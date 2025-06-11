package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import com.sprint.deokhugamteam7.domain.book.repository.custom.RankingBookRepositoryCustom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingBookRepository extends JpaRepository<RankingBook, UUID>,
    RankingBookRepositoryCustom {

}
