package dev.weewee.userservice.domain;

import dev.weewee.api.http.user.*;
import dev.weewee.userservice.domain.db.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(CreateUserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with email already exists");
        }

        var entity = userMapper.toEntity(request);
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setRole(UserRole.CUSTOMER);

        var saved = userRepository.save(entity);
        log.info("Created user: id={}, email={}", saved.getId(), saved.getEmail());
        return userMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        var entity = findUserOrThrow(id);
        return userMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        var entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with email '%s' not found".formatted(email)));
        return userMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequestDto request) {
        var entity = findUserOrThrow(id);
        
        if (request.getFirstName() != null) {
            entity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            entity.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            entity.setPhone(request.getPhone());
        }

        var saved = userRepository.save(entity);
        log.info("Updated user: id={}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        var entity = findUserOrThrow(id);
        userRepository.delete(entity);
        log.info("Deleted user: id={}", id);
    }


    @Transactional
    public AddressDto addAddress(Long userId, CreateAddressRequestDto request) {
        var user = findUserOrThrow(userId);

        var address = userMapper.toEntity(request);
        
        if (user.getAddresses().isEmpty() || request.isDefault()) {
            
            user.getAddresses().forEach(a -> a.setDefault(false));
            address.setDefault(true);
        }

        user.addAddress(address);
        userRepository.save(user);

        log.info("Added address for user: userId={}, addressId={}", userId, address.getId());
        return userMapper.toDto(address);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(Long userId) {
        findUserOrThrow(userId);
        return addressRepository.findByUserId(userId).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        var user = findUserOrThrow(userId);
        
        var address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Address with id '%d' not found for user '%d'".formatted(addressId, userId)));

        user.removeAddress(address);
        addressRepository.delete(address);
        
        log.info("Deleted address: userId={}, addressId={}", userId, addressId);
    }

    @Transactional
    public AddressDto setDefaultAddress(Long userId, Long addressId) {
        var user = findUserOrThrow(userId);
        
        var address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Address with id '%d' not found for user '%d'".formatted(addressId, userId)));

        user.getAddresses().forEach(a -> a.setDefault(false));
        address.setDefault(true);
        
        userRepository.save(user);
        
        log.info("Set default address: userId={}, addressId={}", userId, addressId);
        return userMapper.toDto(address);
    }


    private UserEntity findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User with id '%d' not found".formatted(id)));
    }
}

