package dev.weewee.userservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    
    List<AddressEntity> findByUserId(Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
}

