package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.ApproveWorkflowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApproveWorkflowRepository extends JpaRepository<ApproveWorkflowEntity,Long> {
}
