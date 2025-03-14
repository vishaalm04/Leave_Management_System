package com.example.leaveManagementSystem.service.impl;

import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.entity.*;
import com.example.leaveManagementSystem.enumeration.EnumLeaveStatus;
import com.example.leaveManagementSystem.enumeration.EnumWorkflowStatus;
import com.example.leaveManagementSystem.mapper.ApproveWorkFlowMapper;
import com.example.leaveManagementSystem.mapper.LeaveTransactionMapper;
import com.example.leaveManagementSystem.repository.*;
import com.example.leaveManagementSystem.service.LeaveTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class LeaveTransactionServiceImpl implements LeaveTransactionService {

    @Autowired
    private LeaveTransactionRepository leaveTransactionRepository;

    @Autowired
    private ApproveWorkflowRepository approveWorkflowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveTransactionMapper leaveTransactionMapper;

    @Autowired
    private ApproveWorkFlowMapper approveWorkFlowMapper;

    @Override
    public LeaveTransactionResponseDTO applyLeave(LeaveTransactionRequestDTO leaveTransactionRequestDTO, Long userId, Long tenantId) {
        try {
            // Validate User
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            TenantEntity tenantEntity = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

            // Validate Leave Type
            LeaveTypeEntity leaveType = leaveTypeRepository.findById(leaveTransactionRequestDTO.getLeaveTypeId())
                    .orElseThrow(() -> new RuntimeException("Leave Type not found with ID: " + leaveTransactionRequestDTO.getLeaveTypeId()));

            // Validate Approver
            UserEntity approver = userRepository.findById(leaveTransactionRequestDTO.getApproverId())
                    .orElseThrow(() -> new RuntimeException("Approver not found with ID: " + leaveTransactionRequestDTO.getApproverId()));

            LocalDate startDate = LocalDate.parse(leaveTransactionRequestDTO.getStartDate());
            LocalDate endDate = LocalDate.parse(leaveTransactionRequestDTO.getEndDate());

            if (startDate.isAfter(endDate)) {
                throw new RuntimeException("Start date cannot be after end date.");
            }
            // Prevent duplicate leave applications
            Optional<LeaveTransactionEntity> existingLeave = leaveTransactionRepository.findByUserIdAndLeaveTypeIdAndStartDateAndEndDate(user, leaveType, startDate, endDate);
            if (existingLeave.isPresent()) {
                throw new RuntimeException("Leave application for the same period and leave type already exists.");
            }

            LeaveTransactionEntity leaveTransactionEntity = new LeaveTransactionEntity();
            leaveTransactionEntity.setTenantId(tenantId);
            leaveTransactionEntity.setUserId(user);
            leaveTransactionEntity.setLeaveTypeId(leaveType);
            leaveTransactionEntity.setApprover(approver);
            leaveTransactionEntity.setStartDate(startDate);
            leaveTransactionEntity.setAppliedStartDateType(leaveTransactionRequestDTO.getAppliedStartDateType());
            leaveTransactionEntity.setEndDate(endDate);
            leaveTransactionEntity.setAppliedEndDateType(leaveTransactionRequestDTO.getAppliedEndDateType());
            leaveTransactionEntity.setRemarks(leaveTransactionRequestDTO.getRemarks());
            leaveTransactionEntity.setLeaveStatus(EnumLeaveStatus.PENDING);
            leaveTransactionEntity.setStatus("ACTIVE");
            leaveTransactionEntity.setCreatedBy(user.getName());

            leaveTransactionEntity = leaveTransactionRepository.save(leaveTransactionEntity);


            ApproveWorkflowEntity approveWorkflowEntity = approveWorkFlowMapper.toEntity(
                    leaveTransactionRequestDTO, leaveTransactionEntity,tenantEntity,approver
            );
            approveWorkflowRepository.save(approveWorkflowEntity);


            return leaveTransactionMapper.toResponseDTO(leaveTransactionEntity);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}