package org.example.repository;

import java.util.Optional;

import org.example.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.jws.soap.SOAPBinding;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
