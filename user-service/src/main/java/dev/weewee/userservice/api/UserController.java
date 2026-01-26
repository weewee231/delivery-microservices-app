package dev.weewee.userservice.api;

import dev.weewee.api.http.user.*;
import dev.weewee.userservice.domain.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user")
    public UserDto createUser(@Valid @RequestBody CreateUserRequestDto request) {
        log.info("Creating user: email={}", request.getEmail());
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public UserDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDto request) {
        log.info("Updating user: id={}", id);
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    public void deleteUser(@PathVariable Long id) {
        log.info("Deleting user: id={}", id);
        userService.deleteUser(id);
    }

    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add address to user")
    public AddressDto addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody CreateAddressRequestDto request) {
        log.info("Adding address for user: userId={}", userId);
        return userService.addAddress(userId, request);
    }

    @GetMapping("/{userId}/addresses")
    @Operation(summary = "Get all user addresses")
    public List<AddressDto> getUserAddresses(@PathVariable Long userId) {
        return userService.getUserAddresses(userId);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user address")
    public void deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Deleting address: userId={}, addressId={}", userId, addressId);
        userService.deleteAddress(userId, addressId);
    }

    @PutMapping("/{userId}/addresses/{addressId}/default")
    @Operation(summary = "Set address as default")
    public AddressDto setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Setting default address: userId={}, addressId={}", userId, addressId);
        return userService.setDefaultAddress(userId, addressId);
    }
}

