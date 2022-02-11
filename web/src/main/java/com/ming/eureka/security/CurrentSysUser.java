package com.ming.eureka.security;

import com.ming.eureka.model.entity.sysuser.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;

/**
 * 登录用户保存对象，扩展自springsecurity自身的用户信息表。添加了页面判断权限的方法
 *
 * @author lazier
 */
public class CurrentSysUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 4849885214720256507L;

    /**
     * 数据库保存的用户对象
     */
    private long sysUserId;

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

    private SysUser.Role role;

    public CurrentSysUser(SysUser sysUser) {
        super(sysUser.getLoginName(), sysUser.getPassword(),
                AuthorityUtils.createAuthorityList(sysUser.getPermissionList()));

        this.sysUserId = sysUser.getId();
        this.role = sysUser.getRole();
        this.lastLoginIp = sysUser.getLastLoginIp();
        this.lastLoginTime = sysUser.getLastLoginDate();
        this.lastLoginAddress = sysUser.getLastLoginAddress();
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
        return this.getUsername().equals("admin");
    }

    /**
     * 判断是否具有指定权限
     *
     * @param right 权限名称
     * @return
     */
    public boolean hasRights(String right) {
        if (this.isSuperUser()) {
            return true;
        }
        for (GrantedAuthority auth : this.getAuthorities()) {
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

    public long getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(long sysUserId) {
        this.sysUserId = sysUserId;
    }

    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    public void setLastLoginAddress(String lastLoginAddress) {
        this.lastLoginAddress = lastLoginAddress;
    }

    public SysUser.Role getRole() {
        return role;
    }

    public void setRole(SysUser.Role role) {
        this.role = role;
    }
}
