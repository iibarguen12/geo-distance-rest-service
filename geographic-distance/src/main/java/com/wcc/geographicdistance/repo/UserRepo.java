package com.wcc.geographicdistance.repo;

import com.wcc.geographicdistance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
