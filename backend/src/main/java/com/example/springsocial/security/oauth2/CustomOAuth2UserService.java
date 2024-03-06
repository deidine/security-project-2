package com.example.springsocial.security.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.springsocial.exception.OAuth2AuthenticationProcessingException;
import com.example.springsocial.model.AppUserRole;
import com.example.springsocial.model.AuthProvider;
import com.example.springsocial.model.User;
import com.example.springsocial.repository.UserRepository;
import com.example.springsocial.security.UserPrincipal;
import com.example.springsocial.security.oauth2.user.OAuth2UserInfo;
import com.example.springsocial.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the
            // OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
          // Get roles attribute from OAuth2User
        List<String> roles = (List<String>) oAuth2User.getAttributes().get("roles");
     

        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equalsIgnoreCase("github")) {
                oAuth2UserInfo.setEmail(requestEmail(oAuth2UserRequest.getAccessToken().getTokenValue()));

            } else {

                throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
            }
        }
        // else  if (roles != null && !roles.isEmpty()) {
        //     // Convert role strings to AppUserRole enum values
        //     Set<GrantedAuthority> authorities = roles.stream()
        //             .map(AppUserRole::valueOf)
        //             .collect(Collectors.toSet());

        //     // Add roles to OAuth2User
        //     return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "name");
        // } else {
        //     // Handle scenario where roles are missing or empty
        //     throw new OAuth2AuthenticationProcessingException("User roles not found from OAuth2 provider");
        // }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider()
                    .equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

    private String requestEmail(String token) {
        String url = "https://api.github.com/user/emails";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                GithubEmailResponse[] emails = mapper.readValue(response.getBody(), GithubEmailResponse[].class);

                String primaryEmail = "";
                for (GithubEmailResponse email : emails) {
                    if (email.isPrimary()) {
                        primaryEmail = email.getEmail();
                        break;
                    }
                }
                return primaryEmail;
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize JSON response", e);
            }
        } else {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GithubEmailResponse {
        private String email;
        private boolean primary;
        private boolean verified;
        private String visibility;

        public String getEmail() {
            return email;
        }

        public boolean isPrimary() {
            return primary;
        }

        public boolean isVerified() {
            return verified;
        }

        public String getVisibility() {
            return visibility;
        }
    }
}
