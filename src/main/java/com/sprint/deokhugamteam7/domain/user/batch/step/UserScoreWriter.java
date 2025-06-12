package com.sprint.deokhugamteam7.domain.user.batch.step;

import com.sprint.deokhugamteam7.domain.user.entity.UserScore;
import com.sprint.deokhugamteam7.domain.user.repository.UserScoreRepository;
import java.util.UUID;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class UserScoreWriter implements ItemWriter<UserScore> {

  private final UserScoreRepository userScoreRepository;

  public UserScoreWriter(UserScoreRepository userScoreRepository) {
    this.userScoreRepository = userScoreRepository;
  }

  @Override
  public void write(Chunk<? extends UserScore> chunk) throws Exception {
    for (UserScore score : chunk) {
      UUID id = score.getId();

      if (id == null) {
        id = UUID.randomUUID();
        score.updateId(id); // JPA의 save가 아니라 네이티브 쿼리(upsertUserScore)를 사용할 경우 @GeneratedValue가 적용되지 않아서 수동으로 설정함
      }

      userScoreRepository.upsertUserScore(
          id,
          score.getUser().getId(),
          score.getPeriod().name(),
          score.getScore(),
          score.getReviewScoreSum(),
          score.getLikeCount(),
          score.getCommentCount()
      );
    }
  }
}