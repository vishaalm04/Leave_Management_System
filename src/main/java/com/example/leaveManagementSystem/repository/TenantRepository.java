package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity,Long> {

}
