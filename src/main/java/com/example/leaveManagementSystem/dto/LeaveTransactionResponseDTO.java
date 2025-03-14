package com.example.leaveManagementSystem.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class LeaveTransactionResponseDTO {

    private Long id;
    private Long tenantId;
    private Long userId;
    private String userName;
    private Long leaveTypeId;
    private String leaveType;
    private Long approverId;
    private String approverName;
    private String startDate;
    private String appliedStartDateType;
    private String endDate;
    private String appliedEndDateType;
    private String approvedStartDateType;
    private String approvedEndDateType;
    private String leaveStatus;
    private String status;
    private String remarks;
    private String createdBy;
    private String createdAt;



}
