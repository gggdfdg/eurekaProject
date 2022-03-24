package com.ming.eureka.model.entity.role;

import com.ming.eureka.model.entity.IdTimeTenantEntity;
import com.ming.eureka.model.entity.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.List;

/**
 * 用户表
 */
@Getter
@Setter
@Entity
@Table(name = "b_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends IdTimeTenantEntity {

    private static final long serialVersionUID = -8939614163844810659L;

    // 角色名称
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> users;

    // 对应的权限标示符字符串，格式为：AUTH_A,AUTH_B,AUTH_C
    @NotBlank
    @Column(length = 5000)
    private String rights;

    @Override
    public void prepareForInsert() {

    }
}
