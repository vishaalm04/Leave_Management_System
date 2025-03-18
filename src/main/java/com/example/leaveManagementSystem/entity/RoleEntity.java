package com.example.leaveManagementSystem.entity;

import com.example.leaveManagementSystem.enumeration.EnumStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RoleEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "name",nullable = false, length = 50)
        private String name;

        @ManyToOne
        @JoinColumn(name = "tenant_id", nullable = false)
        private TenantEntity tenantId;

        @Enumerated(EnumType.STRING) // Ensures it's stored as a string in DB
        @Column(name = "status")
        private EnumStatus status;

        @Column(name="created_by",nullable = false, length = 50, updatable = false)
        private String createdBy;

        @Column(name="updated_by",nullable = false, length = 25)
        private String updatedBy;

        @CreatedDate
        @Column(name="created_at",nullable = false)
        private LocalDateTime createdAt;

        @LastModifiedDate
        @Column(name="updated_at",nullable = false)
         private LocalDateTime updatedAt;
    }


