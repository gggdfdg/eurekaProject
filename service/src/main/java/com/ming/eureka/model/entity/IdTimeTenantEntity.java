package com.ming.eureka.model.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Index;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class IdTimeTenantEntity extends IdTimeEntity {

	@Index(name = "tenantId")
	@Column(name = "tenant_id", columnDefinition = "bigint default 1")
	private long tenantId = 1;
}
