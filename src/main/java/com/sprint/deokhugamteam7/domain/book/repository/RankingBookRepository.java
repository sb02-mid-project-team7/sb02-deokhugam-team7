package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.custom.RankingBookRepositoryCustom;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingBookRepository extends JpaRepository<RankingBook, UUID>,
    RankingBookRepositoryCustom {

  List<RankingBook> findAllByBookIdInAndPeriod(Collection<UUID> bookIds, Period period);

  List<RankingBook> findAllByPeriodOrderByScoreDesc(Period period);

  void deleteByPeriod(Period period);
}
