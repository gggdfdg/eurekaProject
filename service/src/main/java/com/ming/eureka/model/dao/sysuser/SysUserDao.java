package com.ming.eureka.model.dao.sysuser;

import com.ming.eureka.model.BaseDao;
import com.ming.eureka.model.entity.sysuser.SysUser;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 后台用户业务
 */
public interface SysUserDao extends BaseDao<SysUser> {

    @Query("select s from SysUser s where s.loginName=?1 and s.state = 0")
    SysUser findNormalSysUser(String loginName);

    @Query("select s from SysUser s where s.id=?1")
    SysUser findSysUserById(long id);

    @Query("select id from SysUser s where s.id like ?1 ")
    long findSysUserByName(long name);

    @Query("select id from SysUser s where s.loginName like ?1 and s.state = ?2")
    List<Long> findSysUserByNameAndState(String name, long state);

    @Query("select s from SysUser s where s.loginName !=?1")
    List<SysUser> listSysUser(String excludeloginName);

    @Query("select count(s) from SysUser s where s.loginName =?1")
    int countSysUserByLoginName(String loginName);
    
    @Query(nativeQuery=true, value="select IFNULL(SUM(us.use_count),0), IFNULL(SUM(us.quota),0) from b_user_sys us where us.role = 'AGENT'")
    List<Object[]> countQuota();

    @Query("select s from SysUser s where s.role = 'AGENT'")
    List<SysUser> findAllAGENT();
}
