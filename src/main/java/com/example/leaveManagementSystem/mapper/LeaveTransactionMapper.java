package com.example.leaveManagementSystem.mapper;

import com.example.leaveManagementSystem.dto.ApproveWorkflowResponseDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.entity.ApproveWorkflowEntity;
import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;

import com.example.leaveManagementSystem.entity.LeaveTypeEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumLeaveDuration;
import com.example.leaveManagementSystem.enumeration.EnumLeaveStatus;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;


@Component
public class LeaveTransactionMapper {

    public LeaveTransactionResponseDTO toResponseDTO(LeaveTransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return LeaveTransactionResponseDTO.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .leaveTypeId(entity.getLeaveType().getId())
                .leaveType(entity.getLeaveType().getName())
                .approverId(entity.getApprover() != null ? entity.getApprover().getId() : null)
                .approverName(entity.getApprover() != null ? entity.getApprover().getName() : null)
                .startDate(String.valueOf(entity.getStartDate()))
                .appliedStartDateType(String.valueOf(entity.getAppliedStartDateType()))
                .endDate(String.valueOf(entity.getEndDate()))
                .appliedEndDateType(String.valueOf(entity.getAppliedEndDateType()))
                .leaveStatus(String.valueOf(entity.getLeaveStatus()))
                .createdBy(entity.getCreatedBy())
                .createdAt(String.valueOf(entity.getCreatedAt()))
                .remarks(entity.getRemarks())
                .workflows(entity.getApproveWorkflows() != null ?
                        entity.getApproveWorkflows().stream().map(this::toWorkflowResponseDTO).collect(Collectors.toList()) : null)
                .build();
    }
    public LeaveTransactionEntity toEntity(LeaveTransactionRequestDTO leaveTransactionRequestDTO, UserEntity user, LeaveTypeEntity leaveType, Long tenantId, LocalDate startDate, LocalDate endDate) {
        if (leaveTransactionRequestDTO == null || user == null || leaveType == null) {
            return null;
        }

        LeaveTransactionEntity leaveTransactionEntity = new LeaveTransactionEntity();
        leaveTransactionEntity.setTenantId(tenantId);
        leaveTransactionEntity.setUser(user);
        leaveTransactionEntity.setLeaveType(leaveType);
        leaveTransactionEntity.setApprover(!leaveType.getIsApprover() && leaveType.getIsReviewer() ? user.getReviewer() : user.getApprover());
        leaveTransactionEntity.setStartDate(startDate);
        leaveTransactionEntity.setAppliedStartDateType(EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedStartDateType()));
        leaveTransactionEntity.setEndDate(endDate);
        leaveTransactionEntity.setAppliedEndDateType(EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedEndDateType()));
        leaveTransactionEntity.setRemarks(leaveTransactionRequestDTO.getRemarks());
        leaveTransactionEntity.setLeaveStatus(EnumLeaveStatus.PENDING);
        leaveTransactionEntity.setCreatedBy(user.getName());

        return leaveTransactionEntity;
    }
    private ApproveWorkflowResponseDTO toWorkflowResponseDTO(ApproveWorkflowEntity entity) {
        return ApproveWorkflowResponseDTO.builder()
                .id(entity.getId())
                .approverId(entity.getApprover().getId())
                .approverName(entity.getApprover().getName())
                .workflowStatus(String.valueOf(entity.getWorkflowStatus()))
                .remarks(entity.getRemarks())
                .createdBy(entity.getCreatedBy())
                .createdAt(String.valueOf(entity.getCreatedAt()))
                .build();
    }
}
