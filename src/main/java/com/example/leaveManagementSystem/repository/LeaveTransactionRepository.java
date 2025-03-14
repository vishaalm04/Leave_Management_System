package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;
import com.example.leaveManagementSystem.entity.LeaveTypeEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LeaveTransactionRepository extends JpaRepository<LeaveTransactionEntity,Long> {

    Optional<LeaveTransactionEntity> findByUserIdAndLeaveTypeIdAndStartDateAndEndDate(
            UserEntity userId,
            LeaveTypeEntity leaveTypeId,
            LocalDate startDate,
            LocalDate endDate
    );

}
