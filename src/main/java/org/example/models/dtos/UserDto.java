package org.example.models.dtos;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String username;

    private String firstName;

    private String lastName;

    private Date birthDate;

    private String email;

    private String gender;

    private MultipartFile image;

    private String password;

    private int weight;

    private int height;

    private String country;

    private String city;

}
