package com.example.leaveManagementSystem.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApproveWorkflowResponseDTO {

    private Long id;
    private Long approverId;
    private String approverName;
    private Long leaveTransactionId;
    private String workflowStatus;
    private String remarks;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

}
