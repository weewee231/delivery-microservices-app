package dev.weewee.userservice.api;

import dev.weewee.api.http.auth.AuthResponseDto;
import dev.weewee.api.http.auth.LoginRequestDto;
import dev.weewee.api.http.auth.RefreshTokenRequestDto;
import dev.weewee.api.http.user.CreateUserRequestDto;
import dev.weewee.userservice.domain.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public AuthResponseDto register(@Valid @RequestBody CreateUserRequestDto request) {
        log.info("Registration request: email={}", request.getEmail());
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get tokens")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Login request: email={}", request.getEmail());
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public AuthResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Token refresh request");
        return authService.refreshToken(request);
    }
}

