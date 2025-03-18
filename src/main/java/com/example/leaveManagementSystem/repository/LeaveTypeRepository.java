package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.LeaveTypeEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveTypeEntity, Long> {
    Optional<LeaveTypeEntity> findByIdAndStatus(Long id, EnumStatus enumStatus);
}
