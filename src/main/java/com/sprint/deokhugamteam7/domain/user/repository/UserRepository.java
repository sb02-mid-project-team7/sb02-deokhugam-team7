package com.sprint.deokhugamteam7.domain.user.repository;

import com.sprint.deokhugamteam7.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}
