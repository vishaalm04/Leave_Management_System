package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.RoleEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long> {

    Optional<RoleEntity> findByIdAndStatus(Long id, EnumStatus enumStatus);
}
