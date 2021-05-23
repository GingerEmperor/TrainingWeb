package org.example.models;

import java.sql.Date;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.example.models.enums.Gender;
import org.example.models.enums.Role;
import org.example.models.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(exclude = {"subscriptions", "followers"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usr")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private Date birthDate;

    private String email;

    private Gender gender;

    private String image;

    private String password;

    private boolean active;

    private int weight;

    private int height;

    private String country;

    private String city;

    private double BMI;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date registeredAt;

    @ManyToMany
    private Set<User> subscriptions;

    @ManyToMany
    private Set<User> followers;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

}
