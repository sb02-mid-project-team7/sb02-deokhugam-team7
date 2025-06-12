package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, UUID> {
  List<UserScore> findAllByPeriodAndDateOrderByScoreDesc(Period period, LocalDate date);
  List<UserScore> findAllByPeriodAndDate(Period period, LocalDate date);
}