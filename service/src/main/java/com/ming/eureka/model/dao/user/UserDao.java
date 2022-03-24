package com.ming.eureka.model.dao.user;

import com.ming.eureka.KeyValueObj;
import com.ming.eureka.model.entity.user.IUser;
import com.ming.eureka.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserDao extends IUserDao<User> {

    @Query("select u.id from User u where u.state = 'NORMAL'"
            + " and u.lastLoginDate < ?1")
    Page<Long> findNoLoginUsers(Date date, Pageable pageable);

    @Query("select u from User u where u.id = ?1 and u.state = 'NORMAL'")
    User findNormalUser(long userId);

    @Query("select u from User u where u.id = ?1")
    User findUser(long userId);

    /**
     * 查询正常账号 ：根据手机号
     */
    @Query("select u from User u where u.loginName=?1 and u.tenantId=?2 and u.state = 'NORMAL'")
    User findNormalUser(String loginName, long tenantId);

    @Query("select u from User u where u.loginName=?1 and u.tenantId=?2")
    public User findByLoginName(String loginName, long tenantId);

    /**
     * 查询 ：根据昵称
     */
    @Query("select u from User u where u.alias=?1")
    public User findByAlias(String alias);

    /**
     * 查询正常账号
     */
    @Query("select u from User u where (u.loginName = ?1 or u.alias = ?1) and u.state = 'NORMAL'")
    User findByLoginNameOrAlias(String loginName);


    /**
     * 修改状态
     *
     * @param ids
     * @param state
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query("update User u set u.state=?2, updateTime = now() where u.id in ?1")
    public int updateUserState(List<Long> ids, IUser.State state);


    /**
     * 过滤用户，返回用户昵称列表
     *
     * @param userIds
     * @return
     */
    @Query("select new com.ming.eureka.KeyValueObj(u.id, u.alias) "
            + "from User u where id in ?1 and state != 'DELETED' group by id")
    public List<KeyValueObj<Long, String>> filterUserIds(List<Long> userIds);

    /**
     * 查询用户
     *
     * @return
     */
    @Query("select new com.ming.eureka.KeyValueObj(u.id,u.alias) from User u where u.alias like ?1 and u.tenantId=?2")
    public Page<KeyValueObj<Long, String>> findByAliasAndTenantId(String alias, long tenantId, Pageable Pageable);

    /**
     * 过滤用户id
     *
     * @return
     */
    @Query("select u.id "
            + "from User u where id in ?1 and state != 'DELETED'")
    public List<Long> filterUserIds(Set<Long> userIds);

    @Query("select u from User u where tenantId in ?1")
    public List<User> findByTenantIds(long tenantId);

}
