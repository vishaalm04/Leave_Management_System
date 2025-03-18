package com.example.leaveManagementSystem.service;

import com.example.leaveManagementSystem.dto.ApiResponseDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionUpdateDTO;
import com.example.leaveManagementSystem.exception.InvalidDataException;
import com.example.leaveManagementSystem.exception.UserNotFoundException;

import java.util.List;


public interface LeaveTransactionService {
    ApiResponseDTO applyLeave(LeaveTransactionRequestDTO leaveTransactionRequestDTO, Long userId, Long tenantId) throws UserNotFoundException, InvalidDataException;

    LeaveTransactionResponseDTO getLeaveTransactionsByUserId(Long userId,Long tenant_Id,Long transactionId);

    ApiResponseDTO cancelLeave(Long userId,Long tenant_Id, Long transactionId) throws UserNotFoundException;

    ApiResponseDTO updateLeave(LeaveTransactionUpdateDTO leaveTransactionUpdateDTO , Long userId, Long tenant_Id, Long transactionId) throws UserNotFoundException, InvalidDataException;

    List<LeaveTransactionResponseDTO> getLeaveTransactionsListByUserId(Long userId) throws UserNotFoundException;

}
