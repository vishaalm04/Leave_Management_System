package com.example.leaveManagementSystem.mapper;

import com.example.leaveManagementSystem.dto.ApproveWorkflowResponseDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.entity.ApproveWorkflowEntity;
import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;
import com.example.leaveManagementSystem.entity.TenantEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumWorkflowStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ApproveWorkFlowMapper {

    public ApproveWorkflowEntity toEntity(LeaveTransactionRequestDTO leaveTransactionRequestDTO, LeaveTransactionEntity leaveTransactionEntity,
                                          TenantEntity tenant, UserEntity approver) {
        if (leaveTransactionRequestDTO == null || leaveTransactionEntity == null || tenant == null || approver == null) {
            return null;
        }

        ApproveWorkflowEntity entity = new ApproveWorkflowEntity();
        entity.setTenantId(tenant);
        entity.setLeaveTransactionId(leaveTransactionEntity);
        entity.setApprover(approver);
        entity.setWorkflowStatus(EnumWorkflowStatus.PENDING);
        entity.setRemarks(leaveTransactionRequestDTO.getRemarks());
        entity.setCreatedBy(approver.getName());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedBy(approver.getName());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }

    public ApproveWorkflowResponseDTO toResponseDTO(ApproveWorkflowEntity entity) {
        if (entity == null) {
            return null;
        }
        return ApproveWorkflowResponseDTO.builder()
                .id(entity.getId())
                .approverId(entity.getApprover().getId())
                .approverName(entity.getApprover().getName())
                .leaveTransactionId(entity.getLeaveTransactionId() != null ? entity.getLeaveTransactionId().getId() : null)
                .workflowStatus(String.valueOf(entity.getWorkflowStatus()))
                .remarks(entity.getRemarks())
                .createdBy(entity.getCreatedBy())
                .createdAt(String.valueOf(entity.getCreatedAt()))
                .createdBy(entity.getCreatedBy())
                .createdAt(String.valueOf(entity.getCreatedAt()))
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(String.valueOf(entity.getUpdatedAt()))
                .build();
    }

}
