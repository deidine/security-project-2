withHttpOnlyFalse()

http
    .csrf()
        .csrfTokenRepository(new HeaderCsrfTokenRepository())


@RequestMapping("/login")
@PreAuthorize("hasIpAddress('127.0.0.1') or loginAttempts < 3")
public String login() {
    // ...
}


spring.security.user.lockout.enabled=true
spring.security.user.lockout.threshold=3
spring.security.user.lockout.duration=300s
