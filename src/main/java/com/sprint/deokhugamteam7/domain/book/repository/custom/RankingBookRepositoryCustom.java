package com.sprint.deokhugamteam7.domain.book.repository.custom;

import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;
import java.time.LocalDateTime;
import java.util.List;

public interface RankingBookRepositoryCustom {

  CursorPageResponseBookDto findAllByKeyword(BookCondition condition);

  CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition condition);

  List<BookActivity> findReviewActivitySummary(LocalDateTime start, LocalDateTime end);

}
