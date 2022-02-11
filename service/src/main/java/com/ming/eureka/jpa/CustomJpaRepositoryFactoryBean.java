package com.ming.eureka.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * 自定义JpaRepositoryFactoryBean
 */
public class CustomJpaRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable> extends
		JpaRepositoryFactoryBean<R, T, ID> {

	public CustomJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new CustomJpaExecutorFactory(entityManager);
	}

	/**
	 * 自定义 jpa executor factory
	 * 
	 * @param <T>
	 * @param <I>
	 */
	private static class CustomJpaExecutorFactory<T, I extends Serializable> extends JpaRepositoryFactory {

		/**
		 * Simple jpa executor factory constructor
		 * 
		 * @param entityManager
		 *            entity manager
		 */
		public CustomJpaExecutorFactory(EntityManager entityManager) {
			super(entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return CustomJpaRepository.class;
		}
	}
}