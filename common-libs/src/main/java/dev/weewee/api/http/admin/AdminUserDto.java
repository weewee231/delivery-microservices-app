package dev.weewee.api.http.admin;

import dev.weewee.api.http.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private boolean blocked;
    private long ordersCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastOrderAt;
}

