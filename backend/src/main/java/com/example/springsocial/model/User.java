package com.example.springsocial.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role_name", nullable = false)
  @Enumerated(EnumType.STRING)//this for creating string in data base
      //  @CollectionTable(name = "roles", 
      //        joinColumns = @JoinColumn(name="roles_id"))
     
    List<AppUserRole> appUserRoles;
  
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "USING_2FA")
    private boolean using2FA;

    private String secret;
  
    private String providerId;
 
}
