package com.example.leaveManagementSystem.mapper;

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

    public ApproveWorkflowEntity toEntity(LeaveTransactionRequestDTO dto, LeaveTransactionEntity leaveTransactionEntity,
                                          TenantEntity tenant, UserEntity approver) {
        if (dto == null || leaveTransactionEntity == null || tenant == null || approver == null) {
            return null;
        }

        ApproveWorkflowEntity entity = new ApproveWorkflowEntity();
        entity.setTenantId(tenant);
        entity.setLeaveTransactionId(leaveTransactionEntity);
        entity.setApprover(approver);
        entity.setWorkflowStatus(EnumWorkflowStatus.PENDING);
        entity.setRemarks(dto.getRemarks());
        entity.setCreatedBy(leaveTransactionEntity.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedBy(leaveTransactionEntity.getCreatedBy());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }

    public LeaveTransactionResponseDTO toResponseDTO(ApproveWorkflowEntity entity) {
        if (entity == null) {
            return null;
        }

        return null;
    }

}
