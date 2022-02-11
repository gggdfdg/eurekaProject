package com.ming.eureka.model.entity.config;

import com.ming.eureka.model.entity.IdTimeTenantEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 服务器信息
 */
@Getter
@Setter
@Entity
@Table(name = "b_sys_config")
public class SysConfig extends IdTimeTenantEntity {

    public static final int LOCK_STATUS_LOCK = 0;
    public static final int LOCK_STATUS_NORMAL = 1;
    // 锁定状态(0:锁定; 1:正常; )
    @Column(columnDefinition = "INT default 1")
    private int lockStatus;

    @Override
    public void prepareForInsert() {

    }

}
