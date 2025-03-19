package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;
import com.example.leaveManagementSystem.entity.LeaveTypeEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumLeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTransactionRepository extends JpaRepository<LeaveTransactionEntity,Long> {

    List<LeaveTransactionEntity> findByUserAndLeaveTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UserEntity user, LeaveTypeEntity leaveType, LocalDate endDate, LocalDate startDate);

    List<LeaveTransactionEntity> findByUser(UserEntity user);

    List<LeaveTransactionEntity>findByApproveWorkflows_ApproverAndLeaveStatus(UserEntity approver,EnumLeaveStatus status);

}
