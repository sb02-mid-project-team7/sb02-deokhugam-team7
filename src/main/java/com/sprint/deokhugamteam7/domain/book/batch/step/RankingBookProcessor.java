package com.sprint.deokhugamteam7.domain.book.batch.step;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.BookActivity;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class RankingBookProcessor implements ItemProcessor<BookActivity,RankingBook> {

  private final BookRepository bookRepository;
  private final Period period;

  public RankingBookProcessor(
      BookRepository bookRepository,
      @Value("#{jobParameters['period']}") String periodStr
  ) {
    this.bookRepository = bookRepository;
    this.period = Period.valueOf(periodStr.toUpperCase());
  }

  @Override
  public RankingBook process(BookActivity bookActivity) {
//    Book book = bookRepository.findById(bookActivity.bookId()).orElseThrow(
//        () -> new BookException(ErrorCode.BOOK_NOT_FOUND)
//    );
    int reviewCount = bookActivity.reviewCount();
    if (reviewCount > 0) {
      double rating = (double) bookActivity.totalRating() / reviewCount;
      double score = (reviewCount * 0.4) + (rating * 0.6);
      return RankingBook.create(period, rating, score);
    } else {
      return RankingBook.create(period);
    }
  }

}
