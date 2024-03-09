package com.example.springsocial.security.oauth2;

import com.example.springsocial.config.AppProperties;
import com.example.springsocial.exception.BadRequestException;
import com.example.springsocial.model.User;
import com.example.springsocial.security.CustomUserDetailsService;
import com.example.springsocial.security.TokenProvider;
import com.example.springsocial.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.example.springsocial.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// this class will give the front end the tokrn redirect by the oauth2 provider
// and the clone the infornmation
// and store it in the localstorage
// i get the user from authentification and i take the specivice user in
// database then give to frontend
// https://localhost:4200/oauth2/redirect?user=com.example.springsocial.model.User@1ae32efa&token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NyIsImlhdCI6MTcwOTk0MDAzMywiZXhwIjoxNzEwODA3NjMzfQ.Sqei8QheNLAn_kQDSz4f6SKd1oG201DJ-HzJSN1QKH3KLpJoD7_P6HyTVXnH_-3q-l5bmyBxQKFyphTiof8bsg
// i need also to authentificate the user

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private AppProperties appProperties;

    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties,
            HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException(
                    "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        log.info("preincipale information" + authentication.getPrincipal().toString());
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        authentication.getPrincipal();
        User user2 = userDetailsService.loadUserByUsername2(authentication.getName());
        
        
        // authenticationManager.authenticate(
        // new UsernamePasswordAuthenticationToken(user2.getEmail(),
        // user2.getPassword()));

        String token = tokenProvider.createToken(authentication);
        log.warn(UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("roles", user2.getAppUserRoles())
                .queryParam("email", user2.getEmail())
                .queryParam("username", user2.getName())

                .build().toUriString());
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("roles", user2.getAppUserRoles())
                .queryParam("email", user2.getEmail())
                .queryParam("username", user2.getName())

                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want
                    // to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
