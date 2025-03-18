package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByIdAndStatus(Long id, EnumStatus enumStatus);

}
