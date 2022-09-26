package org.example.services;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.CanNotCreateException;
import org.example.exeptions.NotFoundException;
import org.example.models.User;
import org.example.models.dtos.UserDto;
import org.example.models.enums.Gender;
import org.example.models.enums.Role;
import org.example.models.enums.Status;
import org.example.repository.UserRepo;
import org.example.utill.MailSender;
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

    private final MailSender mailSender;

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findById(final Long userId) {
        return userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("Юзера с таким id не существует " + userId));
    }

    public User findByUsername(final String username) {
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

    public User createUser(final UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .birthDate(userDto.getBirthDate())
                .gender(Gender.valueOf(userDto.getGender().toUpperCase()))
                .weight(userDto.getWeight())
                .height(userDto.getHeight())
                .city(userDto.getCity())
                .country(userDto.getCountry())
                .active(true)
                .status(Status.ACTIVE)
                .roles(Collections.singleton(Role.USER))
                .registeredAt(Date.valueOf(LocalDate.now()))
                .build();
        user.setBMI(calculateBMI(userDto));

        if (userDto.getImage() != null && !userDto.getImage().isEmpty()) {
            user.setImage(createUserImage(userDto.getUsername(), userDto.getImage()));
        }
        return user;
    }

    private void updateUserImage(final User userToUpdate, final UserDto newUserDto) {
        if (newUserDto.getImage() != null && !newUserDto.getImage().isEmpty()) {
            userToUpdate.setImage(createUserImage(newUserDto.getUsername(), newUserDto.getImage()));
        }
    }

    public User updateUser(final User oldUser, final UserDto userDto) {
        User newUser = User.builder()
                .id(oldUser.getId())
                .username(ofNullable(userDto.getUsername()).orElse(oldUser.getUsername()))
                .firstName(ofNullable(userDto.getFirstName()).orElse(oldUser.getFirstName()))
                .lastName(ofNullable(userDto.getLastName()).orElse(oldUser.getLastName()))
                .password(ofNullable(userDto.getPassword()).orElse(oldUser.getPassword()))
                .email(ofNullable(userDto.getEmail()).orElse(oldUser.getEmail()))
                .birthDate(ofNullable(userDto.getBirthDate()).orElse(oldUser.getBirthDate()))
                .gender(Gender.valueOf(userDto.getGender().toUpperCase()))
                .weight(userDto.getWeight())
                .height(userDto.getHeight())
                .city(ofNullable(userDto.getCity()).orElse(oldUser.getCity()))
                .country(ofNullable(userDto.getCountry()).orElse(oldUser.getCountry()))
                .active(oldUser.isActive())
                .status(Status.ACTIVE)
                .roles(oldUser.getRoles())
                .registeredAt(ofNullable(oldUser.getRegisteredAt()).orElse(Date.valueOf(LocalDate.now())))
                .subscriptions(oldUser.getSubscriptions())
                .followers(oldUser.getFollowers())
                .build();
        newUser.setImage(oldUser.getImage());
        updateUserImage(newUser, userDto);

        newUser.setBMI(calculateBMI(newUser));

        return newUser;
    }

    public User createUserTemplate(final UserDto userDto) {
        return User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
    }

    private String createUserImage(final String username, final MultipartFile image) {
        if (image != null) {
            try {
                return globalService.saveImgToPathWithPrefixName(image, uploadPath, username);
            } catch (Exception e) {
                System.out.println("Cannot create with avatar photo");
                e.printStackTrace();
                throw new CanNotCreateException("Ошибка создания аватара пользователя");
            }
        } else {
            return null;
        }
    }

    public void deleteById(final Long id) {
        User userToDelete = findById(id);
        new File(uploadPath + "/" + userToDelete.getImage()).delete();
        userRepo.delete(userToDelete);
    }

    private double calculateBMI(final UserDto userDto) {
        return userDto.getWeight() / (((userDto.getHeight() / 100.0) * (userDto.getHeight() / 100.0)));
    }

    private double calculateBMI(final User user) {
        return user.getWeight() / (((user.getHeight() / 100.0) * (user.getHeight() / 100.0)));
    }

    private double calculateBMI(double weight, double height) {
        return weight / (((height / 100.0) * (height / 100.0)));
    }

    public void makeFollow(final User requestedUser, final User userToFollow) {
        requestedUser.getSubscriptions().add(userToFollow);
        userToFollow.getFollowers().add(requestedUser);
        save(requestedUser);
        save(userToFollow);
    }

    public void makeUnFollow(final User requestedUser, final User userToUnFollow) {
        requestedUser.getSubscriptions().remove(userToUnFollow);
        userToUnFollow.getFollowers().remove(requestedUser);
        save(requestedUser);
        save(userToUnFollow);
    }

    public void sendActivationEmailTo(User userToSendActivationCode) {
        String message = String.format(
                "Hello, %s! \n"
                        + "Welcome to FitnessWeb."
                        + " Please visit next link to activate your account: http://localhost:8080/activate/%s",
                userToSendActivationCode.getUsername(),
                userToSendActivationCode.getActivationCode()
        );

        mailSender.send(userToSendActivationCode.getEmail(), "ActivationCode", message);
    }

    public boolean activateUser(final String activationCode) {
        final Optional<User> user = userRepo.findByActivationCode(activationCode);
        if (!user.isPresent()) {
            return false;
        }

        final User activatedUser = user.get();
        activatedUser.setActivationCode(null);
        activatedUser.setWasActivatedByEmail(true);

        userRepo.save(activatedUser);

        return true;
    }
}
