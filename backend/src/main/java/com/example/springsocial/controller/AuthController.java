package com.example.springsocial.controller;

import javax.validation.constraints.NotEmpty;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import com.example.springsocial.exception.BadRequestException;
import com.example.springsocial.exception.UserNotVerifiedException;
import com.example.springsocial.model.ConfirmationToken;
import com.example.springsocial.model.User;
import com.example.springsocial.payload.ApiResponse;
import com.example.springsocial.payload.AuthResponse;
import com.example.springsocial.payload.LoginRequest;
import com.example.springsocial.payload.SignUpRequest;
import com.example.springsocial.payload.SignUpResponse;
import com.example.springsocial.payload.TotpRequest;
import com.example.springsocial.payload.VerifyEmailRequest;
import com.example.springsocial.repository.UserRepository;
import com.example.springsocial.security.CustomUserDetailsService;
import com.example.springsocial.security.TokenProvider;
import com.example.springsocial.service.AuthService;
import com.example.springsocial.service.EmailSenderService;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestController
@RequestMapping(value = "/auth", produces = { MediaType.APPLICATION_JSON_VALUE })
// @RequestMapping(value="api/entite",produces={MediaType.APPLICATION_JSON_VALUE},consumes
// ={MediaType.APPLICATION_JSON_VALUE} )

public class AuthController {

    @Autowired
    private QrGenerator qrGenerator;
    @Autowired
    private CodeVerifier verifier;
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private QrDataFactory qrDataFactory;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/login")
    // @RateLimit(name = "myEndpointLimit", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        if (userDetailsService.isAccountVerified(user.getUsername()) == false) {
            throw new UserNotVerifiedException(user.getUsername() + " is not verified");
        }
        User user2 = userDetailsService.loadUserByUsername2(loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        log.info("  user login" + loginRequest.toString());
        log.info("login");

        return ResponseEntity.ok(new AuthResponse(token, user2.getName(), user2.getAppUserRoles(), user2.getEmail()));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest)
            throws QrGenerationException {
        System.out.println("deidine 5a9 mn houn" + signUpRequest.getAppUserRoles());
        if (authService.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Account already exists on this mail Id.");
        }
        User user = authService.saveUser(signUpRequest);

        if (signUpRequest.getUsing2FA()) {
            QrData data = qrDataFactory.newBuilder().label(user.getEmail()).secret(user.getSecret())
                    .issuer("deidine").build();
            // Generate the QR code image data as a base64 string which can
            // be used in an <img> tag:
            String qrCodeImage = getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
            return ResponseEntity.ok().body(new SignUpResponse(true, qrCodeImage, user.getEmail()));
        }

        ConfirmationToken confirmationToken = authService.createToken(user);

        emailSenderService.sendMail(user.getEmail(), confirmationToken.getConfirmationToken());

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/me")
                .buildAndExpand(user.getId()).toUri();
        log.info("usere enrgisetre");
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @GetMapping("confirm-account")
    public ResponseEntity<?> getMethodName(@RequestParam("token") String token) {

        ConfirmationToken confirmationToken = authService.findByConfirmationToken(token);

        if (confirmationToken == null) {
            throw new BadRequestException("Invalid token");
        }

        User user = confirmationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if ((confirmationToken.getExpiryDate().getTime() -
                calendar.getTime().getTime()) <= 0) {
            return ResponseEntity.badRequest()
                    .body("Link expired. Generate new link from http:s//localhost:4200/login");
        }

        user.setEmailVerified(true);
        authService.save(user);
        return ResponseEntity.ok("Account verified successfully!");
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendVerificationMail(@Valid @RequestBody VerifyEmailRequest emailRequest) {
        if (authService.existsByEmail(emailRequest.getEmail())) {
            if (userDetailsService.isAccountVerified(emailRequest.getEmail())) {
                throw new BadRequestException("Email is already verified");
            } else {
                User user = authService.findByEmail(emailRequest.getEmail());
                ConfirmationToken token = authService.createToken(user);
                emailSenderService.sendMail(user.getEmail(), token.getConfirmationToken());
                return ResponseEntity.ok(new ApiResponse(true, "Verification link is sent on your mail id"));
            }
        } else {
            throw new BadRequestException("Email is not associated with any account");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody LoginRequest loginRequest) {
        if (authService.existsByEmail(loginRequest.getEmail())) {
            if (authService.changePassword(loginRequest.getEmail(), loginRequest.getPassword())) {
                return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
            } else {
                throw new BadRequestException("Unable to change password. Try again!");
            }
        } else {
            throw new BadRequestException("User not found with this email id");
        }
    }

    public String rateLimitFallback(Throwable throwable) {
        // Handle rate limit exceeded scenario
        System.out.println("yiu have execeed rate lime");
        return "rate limite";
    }

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    // @Secured("ROLE_ADMIN")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getCurrentUser() {
        log.info("show user to front");

        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("delete/{userId}")
    // @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        System.out.println("deleting user" + userId);
        User user = userRepository.getById(Integer.parseInt(userId));
        userRepository.delete(user);
        System.out.println("deideine deleted");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify")
    // @PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody TotpRequest request) {
        System.out.println(request.getEmail() + "  " + request.getCode());
        User user = userDetailsService.loadUserByUsername2(request.getEmail());

        if (!verifier.isValidCode(user.getSecret(), request.getCode())) {

            return new ResponseEntity<>(new ApiResponse(false, "Invalid Code!"),
                    HttpStatus.BAD_REQUEST);
        }
        
        user.setEmailVerified(true);
        authService.save(user);
        System.out.println("verify tocken");
        // String jwt = tokenProvider.createToken(user, true);
        return ResponseEntity.ok(new ApiResponse(true,"Account verified successfully!"));
    }

}
