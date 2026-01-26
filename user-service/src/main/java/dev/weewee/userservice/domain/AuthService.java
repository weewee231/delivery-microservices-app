package dev.weewee.userservice.domain;

import dev.weewee.api.http.auth.AuthResponseDto;
import dev.weewee.api.http.auth.LoginRequestDto;
import dev.weewee.api.http.auth.RefreshTokenRequestDto;
import dev.weewee.api.http.user.CreateUserRequestDto;
import dev.weewee.api.http.user.UserRole;
import dev.weewee.userservice.domain.db.UserEntity;
import dev.weewee.userservice.domain.db.UserMapper;
import dev.weewee.userservice.domain.db.UserRepository;
import dev.weewee.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDto register(CreateUserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with email already exists");
        }

        var entity = userMapper.toEntity(request);
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setRole(UserRole.CUSTOMER);

        var saved = userRepository.save(entity);
        log.info("Registered new user: id={}, email={}", saved.getId(), saved.getEmail());

        return generateAuthResponse(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        log.info("User logged in: id={}, email={}", user.getId(), user.getEmail());
        return generateAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken) || jwtService.isTokenExpired(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        log.info("Token refreshed for user: id={}", user.getId());
        return generateAuthResponse(user);
    }

    private AuthResponseDto generateAuthResponse(UserEntity user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .tokenType("Bearer")
                .user(AuthResponseDto.UserInfoDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole())
                        .build())
                .build();
    }
}

