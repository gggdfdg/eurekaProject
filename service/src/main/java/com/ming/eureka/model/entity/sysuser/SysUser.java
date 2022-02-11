package com.ming.eureka.model.entity.sysuser;

import com.ming.eureka.model.entity.IdTimeEntity;
import com.ming.eureka.model.entity.sysmenu.SysMenu;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户
 */
@Getter
@Setter
@Entity
@Table(name = "b_user_sys", uniqueConstraints = @UniqueConstraint(columnNames = "loginName"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysUser extends IdTimeEntity implements Serializable {

    private static final long serialVersionUID = -1589111706670673807L;

    // 登录名
    @NotBlank
    @Column(length = 100)
    private String loginName;

    // 昵称
    private String alias;

    // 密码
    private String password;

    // 状态
    private long state;

    //权限
    @Transient
    private List<SysMenu> menuright;

    //上次登录时间
    private Date lastLoginDate;

    //上次登录IP
    private String lastLoginIp;

    //上次登录地址
    private String lastLoginAddress;

    //角色  --超管 --总代 --销代
    @Enumerated(EnumType.STRING)
    private Role role;

    public SysUser() {
        super();
    }

    public SysUser(long id) {
        super();
        this.id = id;
    }

    public SysUser(long id, String loginName, String alias, String password, long state, Date lastLoginDate, String lastLoginIp, String lastLoginAddress, Role role) {
        this.id = id;
        this.loginName = loginName;
        this.alias = alias;
        this.password = password;
        this.state = state;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginIp = lastLoginIp;
        this.lastLoginAddress = lastLoginAddress;
        this.role = role;
    }

    @Override
    public void prepareForInsert() {
        setState(0);
    }


    public String[] getPermissionList() {
        String roles = "";
        if (this.getRole() == null) {
            throw new RuntimeException("该代理角色为空，先去数据库填补角色（SVIP,SAGENT,AGENT）");
        }
        // use for manage admin
        if (this.getRole().weight == 3) {
            roles += ",ROLE_ADMIN";
        } else if (this.getRole().weight == 2) {
            roles += ",ROLE_SAGENT";
        } else if (this.getRole().weight == 1) {
            roles += ",ROLE_AGENT";
        }
        String[] permissionList = StringUtils.split(roles, ",");
        return permissionList;
    }

    public boolean isEnable() {
        return getState() == 0;
    }

    public List<Role> findAllRole() {
        return new ArrayList<Role>() {{
            add(Role.SVIP);
            add(Role.SAGENT);
            add(Role.AGENT);
            add(Role.AUDITOR);
        }};
    }

    public enum Role {
        SVIP("超级管理员", 3), SAGENT("总代", 2), AGENT("代理", 1), AUDITOR("审核专员", 0);
        public String desc;
        public int weight;

        private Role(String desc, int weight) {
            this.desc = desc;
            this.weight = weight;
        }

        public static Role getRole(int weight) {
            switch (weight) {
                case 3:
                    return Role.SVIP;
                case 2:
                    return Role.SAGENT;
                case 1:
                    return Role.AGENT;
                case 0:
                    return Role.AUDITOR;
                default:
                    return null;
            }
        }

        public String getDesc() {
            return this.desc;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}
