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
      }

      userScoreRepository.upsertUserScore(
          id,
          score.getUser().getId(),
          score.getPeriod().name(),
          score.getScore(),
          score.getReviewScoreSum(),
          score.getLikeCount(),
          score.getCommentCount(),
          score.getDate()
      );
    }
  }
}