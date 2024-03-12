package com.example.springsocial.payload;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.example.springsocial.model.AppUserRole;

public class SignUpRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
    List<AppUserRole> appUserRoles;
    @NotNull
    private boolean using2FA;

    public List<AppUserRole> getAppUserRoles() {
        return appUserRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUsing2FA() {
        return using2FA;
    }

    public void setUsing2FA(Boolean using2FA) {
        this.using2FA = using2FA;
    }
}
