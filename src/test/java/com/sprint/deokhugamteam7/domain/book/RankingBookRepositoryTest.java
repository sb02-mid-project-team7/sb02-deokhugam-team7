package com.sprint.deokhugamteam7.domain.book;

import com.sprint.deokhugamteam7.config.TestAuditingConfig;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class RankingBookRepositoryTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private RankingBookRepository rankingBookRepository;

  //TODO 레포지토리 테스트 코드 작성해야함

}
