package com.sprint.deokhugamteam7.domain.batch;

import com.sprint.deokhugamteam7.domain.book.batch.schedule.RankingBookSchedule;
import com.sprint.deokhugamteam7.domain.review.batch.schedule.PopularReviewScoreSchedule;
import com.sprint.deokhugamteam7.domain.user.batch.schedule.PowerUserScoreSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchTriggerController {

  private final RankingBookSchedule rankingBookSchedule;
  private final PopularReviewScoreSchedule popularReviewScoreSchedule;
  private final PowerUserScoreSchedule powerUserScoreSchedule;

  @PostMapping("/trigger-all")
  public ResponseEntity<String> triggerAll() {
    // 1) 인기 도서 점수 계산
    rankingBookSchedule.runRankingJob();
    // 2) 리뷰 점수 계산
    popularReviewScoreSchedule.scheduleScore();
    // 3) 파워 유저 점수 계산
    powerUserScoreSchedule.runUserScoreBatchJob();

    return ResponseEntity.ok("All batch jobs have been triggered.");
  }

  @PostMapping("/ranking-book")
  public ResponseEntity<String> triggerRankingBook() {
    rankingBookSchedule.runRankingJob();
    return ResponseEntity.ok("RankingBook job triggered.");
  }

  @PostMapping("/review-score")
  public ResponseEntity<String> triggerReviewScore() {
    popularReviewScoreSchedule.scheduleScore();
    return ResponseEntity.ok("ReviewScore job triggered.");
  }

  @PostMapping("/user-score")
  public ResponseEntity<String> triggerUserScore() {
    powerUserScoreSchedule.runUserScoreBatchJob();
    return ResponseEntity.ok("UserScore job triggered.");
  }
}
