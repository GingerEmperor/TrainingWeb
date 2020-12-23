package org.example.services;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.NotFoundException;
import org.example.models.User;
import org.example.models.enums.Role;
import org.example.models.enums.Status;
import org.example.repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${upload.userImagesPath}")
    private String uploadPath;

    private final GlobalService globalService;

    private final UserRepo userRepo;

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findById(Long userId) {
        return userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("Юзера с таким id не существует " + userId));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(
                () -> new NotFoundException("Юзера с таким username не существует - " + username));
    }

    public void checkIfExistsByUsername(final String username) {
        try {
            findByUsername(username);
        } catch (NotFoundException e) {
            return;
        }
        throw new AlreadyExistsException("Юзер с таким username уже существует - " + username);
    }

    public User save(final User user) {
        userRepo.save(user);
        return user;
    }

    public void delete(final User userToDelete) {
        userRepo.delete(userToDelete);
    }

    public User createUser(String username, String firstName, String lastName,
            String password, String email, String birthDateStr, MultipartFile image) {
        User user = User.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .email(email)
                .birthDate(Date.valueOf(birthDateStr))
                .active(true)
                .status(Status.ACTIVE)
                .roles(Collections.singleton(Role.USER))
                .registeredAt(Date.valueOf(LocalDate.now()))
                .build();

        try {
            user.setImage(globalService.saveImgToPathWithPrefixName(image, uploadPath, username));
        } catch (Exception e) {
            System.out.println("Cannot create with avatar photo");
            e.printStackTrace();
        }

        return user;
    }

    public void deleteById(final Long id) {
        User userToDelete = findById(id);
        new File(uploadPath + "/" + userToDelete.getImage()).delete();
        userRepo.delete(userToDelete);
    }
}
