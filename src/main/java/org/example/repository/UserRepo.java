package org.example.repository;

import org.example.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.jws.soap.SOAPBinding;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
