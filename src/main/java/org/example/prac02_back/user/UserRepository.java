package org.example.prac02_back.user;

import org.example.prac02_back.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
