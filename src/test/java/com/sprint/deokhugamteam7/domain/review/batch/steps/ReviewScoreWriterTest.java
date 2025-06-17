package com.sprint.deokhugamteam7.domain.review.batch.steps;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.review.batch.step.ReviewScoreWriter;
import com.sprint.deokhugamteam7.domain.review.entity.RankingReview;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.RankingReviewRepository;
import com.sprint.deokhugamteam7.domain.review.repository.custom.ReviewRepositoryCustom;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewScoreWriterTest {

  @Mock
  private RankingReviewRepository rankingReviewRepository;

  @Mock
  private ReviewRepositoryCustom reviewRepositoryCustom;

  private ReviewScoreWriter writer;

  @BeforeEach
  void setUp() {
    // periodStr 는 toUpperCase 되어 Period.DAILY 로 변환됩니다
    writer = new ReviewScoreWriter(rankingReviewRepository, reviewRepositoryCustom, "daily");
  }

  @Test
  @DisplayName("기존 엔트리가 없으면 모두 saveAll 한다")
  void write_savesAllNew_whenNoExisting() {
    // given
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    Review rev1 = mock(Review.class);
    Review rev2 = mock(Review.class);
    when(rev1.getId()).thenReturn(id1);
    when(rev2.getId()).thenReturn(id2);

    RankingReview new1 = mock(RankingReview.class);
    RankingReview new2 = mock(RankingReview.class);
    when(new1.getReview()).thenReturn(rev1);
    when(new2.getReview()).thenReturn(rev2);

    // no existing entries
    when(reviewRepositoryCustom.findAllByReviewIdInAndPeriod(
        Set.of(id1, id2), Period.DAILY))
        .thenReturn(Map.of());

    Chunk<RankingReview> chunk = new Chunk<>(List.of(new1, new2));

    // when
    writer.write(chunk);

    // then
    ArgumentCaptor<List<RankingReview>> captor = ArgumentCaptor.forClass(List.class);
    verify(rankingReviewRepository).saveAll(captor.capture());
    List<RankingReview> saved = captor.getValue();
    assertThat(saved).containsExactly(new1, new2);
  }

  @Test
  @DisplayName("기존 엔트리가 있으면 해당 엔트리를 update 후 saveAll 한다")
  void write_updatesExistingAndSaves() {
    // given
    UUID idExisting = UUID.randomUUID();
    UUID idNew      = UUID.randomUUID();

    Review revExisting = mock(Review.class);
    Review revNew      = mock(Review.class);
    when(revExisting.getId()).thenReturn(idExisting);
    when(revNew.getId()).thenReturn(idNew);

    RankingReview existing = mock(RankingReview.class);
    RankingReview incoming = mock(RankingReview.class);
    when(existing.getReview()).thenReturn(revExisting);
    when(incoming.getReview()).thenReturn(revExisting); // same ID
    when(incoming.getScore()).thenReturn(42.0);

    RankingReview newEntry = mock(RankingReview.class);
    when(newEntry.getReview()).thenReturn(revNew);

    // stub existing lookup
    when(reviewRepositoryCustom.findAllByReviewIdInAndPeriod(
        Set.of(idExisting, idNew), Period.DAILY))
        .thenReturn(Map.of(idExisting, existing));

    Chunk<RankingReview> chunk = new Chunk<>(List.of(incoming, newEntry));

    // when
    writer.write(chunk);

    // then
    // existing.update(score) 가 호출되었는지 검증
    verify(existing).update(42.0);

    // saveAll 호출된 리스트 검증
    ArgumentCaptor<List<RankingReview>> captor = ArgumentCaptor.forClass(List.class);
    verify(rankingReviewRepository).saveAll(captor.capture());
    List<RankingReview> saved = captor.getValue();
    assertThat(saved).containsExactly(existing, newEntry);
  }

  @Test
  @DisplayName("mixed case: 기존 및 신규 엔트리 모두 처리 후 saveAll 한다")
  void write_handlesMixedEntries() {
    // given
    UUID idA = UUID.randomUUID();
    UUID idC = UUID.randomUUID();

    Review rA = mock(Review.class);
    Review rC = mock(Review.class);
    when(rA.getId()).thenReturn(idA);
    when(rC.getId()).thenReturn(idC);

    RankingReview existing = mock(RankingReview.class);
    RankingReview updA     = mock(RankingReview.class);
    RankingReview newC     = mock(RankingReview.class);

    when(existing.getReview()).thenReturn(rA);
    when(updA.getReview()).thenReturn(rA);
    when(updA.getScore()).thenReturn(7.7);
    when(newC.getReview()).thenReturn(rC);

    // stub은 Set.of(idA, idC)로 변경!
    when(reviewRepositoryCustom.findAllByReviewIdInAndPeriod(
        Set.of(idA, idC), Period.DAILY))
        .thenReturn(Map.of(idA, existing));

    // chunk에는 updA, newC 만 담는다
    Chunk<RankingReview> chunk = new Chunk<>(List.of(updA, newC));

    // when
    writer.write(chunk);

    // then
    verify(existing).update(7.7);

    ArgumentCaptor<List<RankingReview>> captor = ArgumentCaptor.forClass(List.class);
    verify(rankingReviewRepository).saveAll(captor.capture());
    List<RankingReview> saved = captor.getValue();
    assertThat(saved).containsExactly(existing, newC);
  }
}