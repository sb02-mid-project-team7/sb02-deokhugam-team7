package com.sprint.deokhugamteam7.domain.book.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.batch.step.RankingBookWriter;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
class RankingBookWriterTest {

  @Mock
  private RankingBookRepository rankingBookRepository;

  private RankingBookWriter writer;

  @BeforeEach
  void setUp() {
    // periodStr 는 toUpperCase() 되어 Period enum 으로 변환됩니다
    writer = new RankingBookWriter(rankingBookRepository, "DAILY");
  }

  @Test
  @DisplayName("저장된 엔트리가 없으면 입력된 모든 RankingBook을 saveAll 한다")
  void write_savesAllNewEntries_whenNoExisting() throws Exception {
    // given
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Book book1 = mock(Book.class);
    Book book2 = mock(Book.class);
    when(book1.getId()).thenReturn(id1);
    when(book2.getId()).thenReturn(id2);

    RankingBook newEntry1 = RankingBook.create(book1, Period.DAILY, 1.0, 10.0, 5L);
    RankingBook newEntry2 = RankingBook.create(book2, Period.DAILY, 2.0, 20.0, 8L);

    // 기존 레코드 없음
    when(rankingBookRepository.findAllByBookIdInAndPeriod(
        Set.of(id1, id2), Period.DAILY))
        .thenReturn(List.of());

    Chunk<RankingBook> chunk = new Chunk<>(List.of(newEntry1, newEntry2));

    // when
    writer.write(chunk);

    // then
    // saveAll 에 입력된 리스트가 정확히 newEntry1, newEntry2 여야 함
    ArgumentCaptor<List<RankingBook>> captor = ArgumentCaptor.forClass(List.class);
    verify(rankingBookRepository).saveAll(captor.capture());
    List<RankingBook> saved = captor.getValue();

    assertThat(saved).containsExactly(newEntry1, newEntry2);
  }

  @Test
  @DisplayName("기존 엔트리가 있으면 해당 엔트리를 업데이트 후 saveAll 한다")
  void write_updatesExistingEntries_whenFound() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    Book book = mock(Book.class);
    when(book.getId()).thenReturn(id);

    // existingEntry: DB 에 이미 존재하는 객체
    RankingBook existingEntry = RankingBook.create(book, Period.DAILY, 0.0, 5.0, 2L);

    // newEntry: 업데이트할 값
    RankingBook newEntry = RankingBook.create(book, Period.DAILY, 3.0, 15.0, 6L);

    when(rankingBookRepository.findAllByBookIdInAndPeriod(
        Set.of(id), Period.DAILY))
        .thenReturn(List.of(existingEntry));

    Chunk<RankingBook> chunk = new Chunk<>(List.of(newEntry));

    // when
    writer.write(chunk);

    // then
    // existingEntry 의 필드가 newEntry 값으로 업데이트되었는지 확인
    assertThat(existingEntry.getRating()).isEqualTo(newEntry.getRating());
    assertThat(existingEntry.getScore()).isEqualTo(newEntry.getScore());
    assertThat(existingEntry.getReviewCount()).isEqualTo(newEntry.getReviewCount());

    // saveAll 에는 existingEntry 하나만 포함
    ArgumentCaptor<List<RankingBook>> captor = ArgumentCaptor.forClass(List.class);
    verify(rankingBookRepository).saveAll(captor.capture());
    List<RankingBook> saved = captor.getValue();

    assertThat(saved).containsExactly(existingEntry);
  }

  @Test
  @DisplayName("새로운 엔트리와 기존 엔트리가 섞인 경우, 각각 처리 후 saveAll 한다")
  void write_handlesMixedNewAndExistingEntries() throws Exception {
    // given
    UUID idExisting = UUID.randomUUID();
    UUID idNew      = UUID.randomUUID();
    Book bookExisting = mock(Book.class);
    Book bookNew      = mock(Book.class);
    when(bookExisting.getId()).thenReturn(idExisting);
    when(bookNew.getId()).thenReturn(idNew);

    // DB에 이미 존재하는 엔트리
    RankingBook existingEntry = RankingBook.create(bookExisting, Period.DAILY, 1.0, 10.0, 4L);

    // 입력으로 들어올 “업데이트용” 엔트리 (same bookExisting)
    RankingBook updateEntry = RankingBook.create(bookExisting, Period.DAILY, 2.0, 20.0, 7L);

    // 입력으로 들어올 “신규” 엔트리
    RankingBook newEntry = RankingBook.create(bookNew, Period.DAILY, 3.0, 30.0, 9L);

    // repository stub
    when(rankingBookRepository.findAllByBookIdInAndPeriod(
        Set.of(idExisting, idNew), Period.DAILY))
        .thenReturn(List.of(existingEntry));

    // chunk에는 updateEntry, newEntry 만 담는다
    Chunk<RankingBook> chunk = new Chunk<>(List.of(updateEntry, newEntry));

    // when
    writer.write(chunk);

    // then
    // 1) existingEntry가 updateEntry의 값으로 갱신되었는지
    assertThat(existingEntry.getRating()).isEqualTo(2.0);
    assertThat(existingEntry.getScore()).isEqualTo(20.0);
    assertThat(existingEntry.getReviewCount()).isEqualTo(7L);

    // 2) saveAll 에 전달된 리스트가 [existingEntry, newEntry] 인지
    ArgumentCaptor<List<RankingBook>> captor = ArgumentCaptor.forClass(List.class);
    verify(rankingBookRepository).saveAll(captor.capture());
    List<RankingBook> saved = captor.getValue();
    assertThat(saved).containsExactly(existingEntry, newEntry);
  }
}
