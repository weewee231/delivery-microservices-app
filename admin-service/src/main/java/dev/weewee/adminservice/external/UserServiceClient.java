package dev.weewee.adminservice.external;

import dev.weewee.api.http.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    @Qualifier("userWebClient")
    private final WebClient userWebClient;

    public List<UserDto> getAllUsers() {
        try {
            return userWebClient.get()
                    .uri("/api/users")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Failed to get users from user-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public UserDto getUserById(Long id) {
        try {
            return userWebClient.get()
                    .uri("/api/users/{id}", id)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to get user {} from user-service: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteUser(Long id) {
        try {
            userWebClient.delete()
                    .uri("/api/users/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("Deleted user: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete user {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}

