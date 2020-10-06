package org.example.services;

import org.example.exeptions.UserNotFoundException;
import org.example.models.User;
import org.example.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public List<User>findAll(){
        return userRepo.findAll();
    }

    public User findById(Long userId) {
        Optional<User>userOptional=userRepo.findById(userId);
        if(userOptional.isPresent())
            return userOptional.get();
        throw new UserNotFoundException();
    }




}
