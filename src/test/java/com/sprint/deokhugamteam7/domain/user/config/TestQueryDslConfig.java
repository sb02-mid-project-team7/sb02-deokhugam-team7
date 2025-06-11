package com.sprint.deokhugamteam7.domain.user.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugamteam7.domain.user.repository.UserQueryRepository;
import com.sprint.deokhugamteam7.domain.user.repository.custom.UserQueryRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class TestQueryDslConfig {

  @PersistenceContext
  private EntityManager em;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }

  @Bean
  public UserQueryRepository userQueryRepository(JPAQueryFactory jpaQueryFactory) {
    return new UserQueryRepositoryImpl(jpaQueryFactory);
  }
}
