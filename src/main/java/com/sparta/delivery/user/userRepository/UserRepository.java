package com.sparta.delivery.user.userRepository;

import com.sparta.delivery.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
