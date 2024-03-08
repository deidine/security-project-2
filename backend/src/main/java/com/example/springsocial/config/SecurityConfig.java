package com.example.springsocial.config;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.example.springsocial.security.*;
import com.example.springsocial.security.oauth2.CustomOAuth2UserService;
import com.example.springsocial.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.springsocial.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.example.springsocial.security.oauth2.OAuth2AuthenticationSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    /*
     * By default, Spring OAuth2 uses
     * HttpSessionOAuth2AuthorizationRequestRepository to save
     * the authorization request. But, since our service is stateless, we can't save
     * it in
     * the session. We'll save the request in a Base64 encoded cookie instead.
     */
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (Boolean.parseBoolean(env.getRequiredProperty("security.disable.csrf")))
            http.csrf().disable();
        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers("/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                .permitAll()
                .antMatchers(env.getRequiredProperty("security.uri.white-list").split(",")).permitAll()

                .antMatchers(HttpMethod.POST, "/**/entite/save").hasRole("CLIENT")
                .antMatchers(HttpMethod.GET, "/auth/users").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/**/entite/delete/*").hasRole("ADMIN")
                .antMatchers("/auth/**",
                        "/oauth2/**", "/api/entite/mougatta",
                        "/api/entite/cities")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().accessDeniedPage("/login");

        http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);
        http.headers().contentSecurityPolicy("script-src 'self'");

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    // i have alredy configure cros in http.cros() this is for cros acess origin
    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    // return new WebMvcConfigurer() {
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // registry.addMapping("/greeting-javaconfig").allowedOrigins("http://localhost:9000");
    // }
    // };
    // }
    // fro multuple ip
    // registry.addMapping("/**").allowedOrigins("http://domain1.com",
    // "http://domain2.com");

    // @Override
    // public void addCorsMappings(final CorsRegistry registry) {
    // registry.addMapping("/**")
    // .allowedOrigins(clientPort)
    // .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(),
    // HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.HEAD.name())
    // .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
    // .exposedHeaders(JwtHeader)
    // .allowCredentials(true)
    // .maxAge(86400);
    // }

    // @Bean
    // public Customizer<RateLimiterConfig> rateLimiterCustomizer() {
    // return rateLimiterConfig -> rateLimiterConfig
    // .limitForPeriod(10) // Maximum allowed requests within the defined duration
    // .limitRefreshPeriod(Duration.ofMinutes(1)); // Duration for the rate limit
    // window
    // }

}
