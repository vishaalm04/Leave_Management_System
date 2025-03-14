package com.example.leaveManagementSystem.entity;


import com.example.leaveManagementSystem.enumeration.EnumWorkflowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approve_workflow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveWorkflowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenantId;  // Reference to Tenant Entity

    @ManyToOne
    @JoinColumn(name = "leave_transaction_id", nullable = false, referencedColumnName = "id")
    private LeaveTransactionEntity leaveTransactionId;  // Reference to LeaveTransaction Entity

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private UserEntity approver;  // Reference to Approver User

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_status", nullable = false, length = 20)
    private EnumWorkflowStatus workflowStatus; // ENUM (PENDING, APPROVED, REJECTED)

    @Column(name="remarks",columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", nullable = false, length = 25)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

