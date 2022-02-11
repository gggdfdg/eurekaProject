/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ming.eureka.business.DynamicSpecifications;
import com.ming.eureka.business.SearchFilter;
import com.ming.eureka.business.SearchParam;
import com.ming.eureka.business.SearchPredicate;
import com.ming.eureka.model.entity.IdEntity;
import com.ming.eureka.model.entity.file.ConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * service 基类
 *
 * @author lll 2015年5月28日
 */
@Transactional(readOnly = true)
@Slf4j
public class BaseService {
    static {
        DozerBeanMapper mapper = (DozerBeanMapper) Reflections.getFieldValue(
                new BeanMapper(), "dozer");
        if (CollectionUtils.isEmpty(mapper.getMappingFiles())) {
            mapper.setMappingFiles(Lists.newArrayList("globalConfig.xml"));
        }
    }

    public static String HOSTNAME;
    @Value("${eureka.instance.ipAddress:127.0.0.1}")
    public void setHostName(String hostName) {
        HOSTNAME = hostName;
    }

    /** application-common.properties 配置信息 */
    @Autowired
    protected ConfigInfo configInfo;

    @Autowired
    protected EntityManagerFactory emf;
    @PersistenceContext
    protected EntityManager entityManager;

    @Transactional(readOnly = false)
    public <T extends IdEntity> void persist(T entity) {
        this.entityManager.persist(entity);
    }

    public <T extends IdEntity> void refresh(T entity) {
        this.entityManager.refresh(entity);
    }

    @Transactional(readOnly = false)
    public <T extends IdEntity> T merge(T entity) {
        return this.entityManager.merge(entity);
    }

    @Transactional(readOnly = false)
    public <T extends IdEntity> Collection<T> batchSave(Collection<T> entities) {
        return this.batchSave(entities, entityManager, true);
    }

    public <T extends IdEntity> Collection<T> batchSave(Collection<T> entities, EntityManager entityManager, boolean autoFlush) {
        final List<T> savedEntities = new ArrayList<T>(entities.size());
        int i = 0;
        for (T t : entities) {
            savedEntities.add(persistOrMerge(t, entityManager));
            i++;
            if (autoFlush && i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        return savedEntities;
    }

    @Transactional(readOnly = false)
    public List<Future<Integer>> concurrencyBatchSave(List list, ExecutorService executorService) {
        return this.concurrencyBatchSave(list, executorService, 50);
    }

    @Transactional(readOnly = false)
    public List<Future<Integer>> concurrencyBatchSave(List list, ExecutorService executorService, int sliceSize) {

        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();
        BaseService service = this;
        int size = list.size();
        for (int i = 0; i < size; i = i + sliceSize) {
            int toIndex = (i + sliceSize < list.size() ? (i + sliceSize) : list.size());
            List sublist = list.subList(i, toIndex);
            // 使用ExecutorService执行Callable类型的任务，并将结果保存在future变量中
            Future<Integer> future = executorService.submit(new Callable<Integer>() {
                @Override
                @Transactional(readOnly = false)
                public Integer call() throws Exception {
                    EntityManager em = emf.createEntityManager();
                    EntityTransaction transaction = em.getTransaction();
                    transaction.begin();
                    try {
                        service.batchSave(sublist, em, false);
                        em.flush();
                        em.clear();
                        transaction.commit();
                    } catch (Exception e) {
                        transaction.rollback();
                        CommonUtil.logException(log, e);
                        throw e;
                    } finally {
                        em.close();
                    }
                    return toIndex;
                }
            });
            // 将任务执行结果存储到List中
            resultList.add(future);
        }
        executorService.shutdown();
        return resultList;
    }

    @Transactional(readOnly = false)
    public <T extends IdEntity> T persistOrMerge(T t) {
        return this.persistOrMerge(t, entityManager);
    }

    @Transactional(readOnly = false)
    public <T extends IdEntity> T persistOrMerge(T t, EntityManager entityManager) {
        if (t.getId() == null) {
            entityManager.persist(t);
            return t;
        } else {
            return entityManager.merge(t);
        }
    }

    /**
     * 创建动态查询条件组合.
     */
    protected <T> Specification<T> createSpecification(SearchParam searchParam, Specification<T> spec, final Class<T> entityClass) {
        List<SearchFilter> filters = searchParam.getFilters();
        Specification<T> specT = DynamicSpecifications.bySearchFilter(filters, entityClass);

        if (spec != null) {
            return Specification.where(specT).and(spec);
        } else {
            return specT;
        }

    }

    /**
     * 创建动态查询条件组合.
     */
    protected <T> Specification<T> createSpecification(SearchParam searchParam, final Class<T> entityClass) {
        return createSpecification(searchParam, null, entityClass);
    }

    /**
     * 创建动态查询条件组合.
     */
    protected <T> Specification<T> createSpecification(Map<String, Object> searchParams, Specification<T> spec, final Class<T> entityClass) {
        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchParam param = new SearchParam();
        param.setFilters(Arrays.asList(filters.values().toArray(new SearchFilter[filters.values().size()])));
        return createSpecification(param, spec, entityClass);
    }

    /**
     * 创建动态查询条件组合.
     */
    protected <T> Specification<T> createSpecification(Map<String, Object> searchParams, final Class<T> entityClass) {
        return this.createSpecification(searchParams, null, entityClass);
    }

    /**
     * 创建分页请求.
     *
     * @param pageNumber
     *            页号
     * @param pageSize
     *            每页大小
     * @param sortType
     *            排序字段 默认：id 支持按多字段排序 "id:asc,name:desc" "id,name:desc"
     * @param asc
     *            是否升序
     * @return PageRequest
     */
    public PageRequest createPageRequest(int pageNumber, int pageSize, String sortType, boolean asc) {
        Sort sort = createSort(sortType, asc);
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

    public PageRequest createPageRequest(SearchPredicate predicate) {
        Sort sort = createSort(predicate.getSort(), false);
        return PageRequest.of(predicate.getPageNumber() - 1, predicate.getPageSize(), sort);
    }

    /**
     *
     * @param sortType
     *            排序字段 默认：id 支持按多字段排序 "id:asc,name:desc" "id,name:desc"
     * @param asc
     *            是否升序
     * @return
     */
    protected Sort createSort(String sortType, boolean asc) {
        Sort sort = null;
        Direction defaultDirection = asc ? Direction.ASC : Direction.DESC;

        // 设置默认值
        if (StringUtils.isBlank(sortType)) {
            sortType = "id";
        }

        List<Sort.Order> orders = Lists.newArrayList();
        Direction otherDirection;
        for (String sortStr : sortType.split(",")) {
            String sortAndDirection[] = sortStr.split(":");
            if (sortAndDirection.length < 1) {
                continue;
            }
            if (sortAndDirection.length == 2) {
                otherDirection = sortAndDirection[1].equalsIgnoreCase("asc") ? Direction.ASC
                        : Direction.DESC;
            } else {
                otherDirection = defaultDirection;
            }
            orders.add(new Sort.Order(otherDirection, sortAndDirection[0]));
        }
        sort = Sort.by(orders);
        return sort;
    }

    /**
     * 删除
     *
     * @return
     */
    @Transactional(readOnly = false)
    public <T extends IdEntity> CommonResult delete(long id, Class<T> entityClass) {
        return this.delete(Sets.newHashSet(id), entityClass);
    }

    /**
     * 批量删除
     *
     * @return
     */
    @Transactional(readOnly = false)
    public <T extends IdEntity> CommonResult delete(Set<Long> entityIds, Class<T> entityClass) {

        IdEntity entity;
        for (Long entityId : entityIds) {
            entity = this.entityManager.find(entityClass, entityId);
            if (entity == null) {
                continue;
            }

            Field field = ReflectionUtils.findField(entityClass, "user");
            if (field != null) {
                User user = (User) Reflections.getFieldValue(entity, "user");
//				if (this.getCurrentLoginUser().getId() != user.getId()) {
//					log.error("您无权删除该记录！class: {}"+ entityClass +"entity: {}，userId：{}", entity.getId(), user.getId());
//					return CommonResult.commError("您无权操作");
//				}
            }
            this.entityManager.remove(entity);
        }

        return CommonResult.succ();
    }


    /**
     * 统计数量
     * @param entityClass
     * @param spec
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public long countBySpec(Specification spec, Class entityClass) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root root = countQuery.from(entityClass);

        Predicate predicate = spec.toPredicate(root, countQuery, builder);
        countQuery.where(predicate);
        countQuery.distinct(true);
        countQuery.select(builder.count(root));

        List<Long> totals = entityManager.createQuery(countQuery).getResultList();
        Long total = 0L;
        for (Long element : totals) {
            total += element == null ? 0 : element;
        }
        return total;
    }

    /**
     * @return the configInfo
     */
    public ConfigInfo getConfigInfo() {
        return configInfo;
    }

    /**
     * @param configInfo the configInfo to set
     */
    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

//    /**
//     * 获取当前登录用户
//     * @return
//     */
//    public IUser<?> getCurrentLoginUser() {
//        if (!SecureUtil.isAuthenticated()) {
//            return null;
//        }
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            String[] loginNameArray = ((UserDetails)principal).getUsername().split("@");
//            username = loginNameArray[0];
//        } else {
//            username = principal.toString();
//        }
//        return userDao.findByLoginName(username,((CurrentUser)principal).getTenantId());
//    }
}
