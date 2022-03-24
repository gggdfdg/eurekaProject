package com.ming.eureka.model.service.user;

import com.ming.eureka.CommonResult;
import com.ming.eureka.CommonUtil;
import com.ming.eureka.business.SearchParam;
import com.ming.eureka.model.dao.user.IUserDao;
import com.ming.eureka.model.dao.user.UserDao;
import com.ming.eureka.model.dao.userinfo.UserInfoDao;
import com.ming.eureka.model.entity.user.IUser;
import com.ming.eureka.model.entity.user.IUser.State;
import com.ming.eureka.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * 用户service
 *
 * @author luoxiaomin 2015年11月3日
 */
@Service
public class UserService extends AccountService<User> {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserInfoDao infoDao;

    @Override
    public IUserDao<User> getUserDao() {
        return userDao;
    }

    public User findNormalUser(long userId) {
        return userDao.findNormalUser(userId);
    }

    public User findNormalUser(String loginName) {
        return userDao.findNormalUser(loginName,
                ((User) getCurrentLoginUser()).getTenantId());
    }

    public User getUserByLoginName(String loginName, long tenantId) {
        return userDao.findByLoginName(loginName, tenantId);
    }

    public User getUserByAlias(String alias) {
        return userDao.findByAlias(alias);
    }

    public User findUser(long userId) {
        return userDao.findUser(userId);
    }

    public List<User> findUserBytenantId(long tenantId) {
        return userDao.findByTenantIds(tenantId);
    }

    /**
     * 冻结用户
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult blockUser(long userId) {
        return this.changeUserState(userId, IUser.State.BLOCKED);
    }

    /**
     * 解冻用户
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult unBlockUser(long userId) {
        return this.changeUserState(userId, State.NORMAL);
    }

    /**
     * 修改用户状态
     *
     * @param ids
     * @param state
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult changeUserState(List<Long> ids, State state) {
        for (Long id : ids) {
            changeUserState(id, state);
        }
        return CommonResult.succ();
    }

    /**
     * 修改用户状态
     *
     * @param userId
     * @param state
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult changeUserState(long userId, State state) {
        User user = userDao.findById(userId).orElse(null);
        if (user == null)
            return CommonResult.notFoundError();
        user.setState(state);
        user.setTenantId(user.getTenantId());
        userDao.save(user);
        return CommonResult.succ();
    }

    /**
     * 反向修改用户状态
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult reverseChangeUserState(long userId) {
        User user = userDao.findById(userId).orElse(null);
        if (user == null)
            return CommonResult.notFoundError();
        if (user.getState() == State.BLOCKED) {
            user.setState(State.NORMAL);
        } else {
            user.setState(State.BLOCKED);
        }
        user.setTenantId(user.getTenantId());
        userDao.save(user);
        return CommonResult.succ();
    }

    /**
     * 修改自己的密码
     *
     * @param newPassword
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult modifyPassword(long userId, String newPassword) {
        User user = userDao.findById(userId).orElse(null);

        if (!CommonUtil.validFieldForString(newPassword, 64)) {
            return CommonResult.commError("password错误");
        }
        user.setPlainPassword(newPassword);
        entryptPassword(user);
        user.setTenantId(user.getTenantId());
        userDao.save(user);
        return CommonResult.succ();
    }

    /**
     * 列表查询
     *
     * @return
     */
    public Page<User> findUserPage(SearchParam searchParams, int pageNumber,
                                   int pageSize) {
        Assert.notNull(searchParams, "");
        Pageable pageRequest = this.createPageRequest(pageNumber, pageSize,
                "id", false);

        Specification<User> specT = (Specification<User>) this
                .createSpecification(searchParams, User.class);
        return userDao.findAll(specT, pageRequest);
    }

    /**
     * 批量删除
     *
     * @return
     */
    @Transactional(readOnly = false)
    public CommonResult deleteUsers(Set<Long> userIds) {

        if (CollectionUtils.isEmpty(userIds)) {
            return CommonResult.deleError();
        }

        for (Long userId : userIds) {
            User user = userDao.findById(userId).orElse(null);
            if (user == null) {
                continue;
            }
            userDao.delete(user);
        }
        return CommonResult.succ();
    }

    @Transactional(readOnly = false)
    public void agreeEULA(long userId) {
        User user = this.userDao.findById(userId).orElse(null);
        user.setEulaAgreed(true);
        this.userDao.save(user);
    }

}
