package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
  Optional<User> findByIdAndIsDeletedFalse(UUID id);
}
