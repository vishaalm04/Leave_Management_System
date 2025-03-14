package com.example.leaveManagementSystem.service;

import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;



public interface LeaveTransactionService {
    LeaveTransactionResponseDTO applyLeave(LeaveTransactionRequestDTO leaveTransactionRequestDTO,Long userId,Long tenantId);
}
