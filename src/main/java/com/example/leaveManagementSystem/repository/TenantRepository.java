package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.TenantEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity,Long> {

    Optional<TenantEntity> findByIdAndStatus(Long id, EnumStatus enumStatus);

}
