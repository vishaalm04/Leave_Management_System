package com.example.leaveManagementSystem.entity;

import com.example.leaveManagementSystem.enumeration.EnumLeaveDuration;
import com.example.leaveManagementSystem.enumeration.EnumLeaveStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "leave_transaction")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LeaveTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveTypeEntity leaveType;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private UserEntity approver;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "applied_startdate_type", nullable = false, length = 15)
    private EnumLeaveDuration appliedStartDateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "applied_enddate_type", nullable = false, length = 15)
    private EnumLeaveDuration appliedEndDateType;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "approved_startdate_type", nullable = false, length = 15)
    private EnumLeaveDuration approvedStartDateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "approved_enddate_type", nullable = false, length = 15)
    private EnumLeaveDuration approvedEndDateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_status", nullable = false, length = 20)
    private EnumLeaveStatus leaveStatus;

//    @Enumerated(EnumType.STRING) // Ensures it's stored as a string in DB
//    @Column(name = "status")
//    private EnumStatus status;

    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 25)
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "leaveTransactionId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApproveWorkflowEntity> approveWorkflows;


}
