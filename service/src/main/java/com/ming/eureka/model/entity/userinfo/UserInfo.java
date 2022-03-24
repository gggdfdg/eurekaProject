package com.ming.eureka.model.entity.userinfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ming.eureka.model.entity.IdEntity;
import com.ming.eureka.model.entity.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户基础数据
 */
@Entity
@Table(name = "b_user_baseinfo")
@Getter
@Setter
public class UserInfo extends IdEntity {
    // 关联的用户
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // 性别 0-未设置 1-男 2-女
    private int sex;

    // 生日
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date birthday;

    private String cellPhone;

    // 邮箱
    private String email;

    @PreUpdate
    private void preUpdate() {
    }

    @Override
    public void prepareForInsert() {
    }

}
