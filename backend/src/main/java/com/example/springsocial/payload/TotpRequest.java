package com.example.springsocial.payload;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
@Getter
@Setter
public class TotpRequest {

    @NotBlank

	private String code;
    @NotBlank

private String email;
}
