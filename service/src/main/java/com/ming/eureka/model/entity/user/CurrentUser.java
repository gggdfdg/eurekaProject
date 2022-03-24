package com.ming.eureka.model.entity.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 登录用户保存对象，扩展自springsecurity自身的用户信息表。添加了页面判断权限的方法
 *
 * @author lazier
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 4849885214720256507L;

    /**
     * 数据库保存的用户对象
     */
    private long userId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 上次登录IP
     */
    private String lastLoginIp;

    /**
     * 上次登录地址
     */
    private String lastLoginAddress;

    private Long tenantId;

    private User user;

    private boolean isAnalog = false;

    public boolean isAnalog() {
        return isAnalog;
    }

    public void setAnalog(boolean analog) {
        isAnalog = analog;
    }

    public CurrentUser(User user) {
        super(user.getLoginName(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getPermissionList()));
        this.userId = user.getId();
        this.tenantId = user.getTenantId();
        this.roleName = user.getRole().getName();
        this.lastLoginIp = user.getLastLoginIp();
        this.lastLoginTime = user.getLastLoginDate();
        this.lastLoginAddress = user.getLastLoginAddress();
        this.user = user;
    }

    public static void addRight(String right) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority(right));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    /**
     * 判断是否具有相关的权限组
     *
     * @param rights
     * @return
     */
    public boolean hasRights(String... rights) {
        for (String right : rights) {
            if (!this.hasRights(right)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为超级用户
     */
    public boolean isSuperUser() {
        return super.getUsername().split("@")[0].equals("admin");
    }

    /**
     * 判断是否具有指定权限
     *
     * @param right 权限名称
     * @return
     */
    public boolean hasRights(String right) {
        Collection<? extends GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority auth : auths) {
            if (auth.getAuthority().equals(right)) {
                return true;
            }
        }
        return false;
    }

    // get,set
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    public void setLastLoginAddress(String lastLoginAddress) {
        this.lastLoginAddress = lastLoginAddress;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

}
