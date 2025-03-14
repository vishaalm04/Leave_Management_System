package com.example.leaveManagementSystem.mapper;

import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;

import org.springframework.stereotype.Component;


@Component
public class LeaveTransactionMapper {

    public LeaveTransactionResponseDTO toResponseDTO(LeaveTransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return LeaveTransactionResponseDTO.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .userId(entity.getId())
                .userName(entity.getUserId().getName())
                .leaveTypeId(entity.getLeaveTypeId().getId())
                .leaveType(entity.getLeaveTypeId().getName())
                .startDate(String.valueOf(entity.getStartDate()))
                .appliedStartDateType(String.valueOf(entity.getAppliedStartDateType()))
                .endDate(String.valueOf(entity.getEndDate()))
                .appliedEndDateType(String.valueOf(entity.getAppliedEndDateType()))
                .leaveStatus(String.valueOf(entity.getLeaveStatus()))
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .createdAt(String.valueOf(entity.getCreatedAt()))
                .remarks(entity.getRemarks())
                .build();
    }
}
