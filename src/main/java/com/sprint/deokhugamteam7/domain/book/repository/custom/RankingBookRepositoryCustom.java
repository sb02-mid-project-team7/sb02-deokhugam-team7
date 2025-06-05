package com.sprint.deokhugamteam7.domain.book.repository.custom;

import com.sprint.deokhugamteam7.domain.book.dto.condition.BookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.condition.PopularBookCondition;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponseBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.response.CursorPageResponsePopularBookDto;

public interface RankingBookRepositoryCustom {

  CursorPageResponseBookDto findAllByKeyword(BookCondition condition);

  CursorPageResponsePopularBookDto findPopularBooks(PopularBookCondition condition);
}
