package com.example.leaveManagementSystem.service;

import com.example.leaveManagementSystem.dto.*;
import com.example.leaveManagementSystem.exception.InvalidDataException;
import com.example.leaveManagementSystem.exception.UserNotFoundException;

import java.util.List;


public interface LeaveTransactionService {
    ApiResponseDTO applyLeave(LeaveTransactionRequestDTO leaveTransactionRequestDTO, Long userId, Long tenantId) throws UserNotFoundException, InvalidDataException;

    LeaveTransactionResponseDTO getLeaveTransactionsByUserId(Long userId,Long tenant_Id,Long transactionId);

    ApiResponseDTO cancelLeave(Long userId,Long tenant_Id, Long transactionId) throws UserNotFoundException;

    ApiResponseDTO updateLeave(LeaveTransactionUpdateDTO leaveTransactionUpdateDTO , Long userId, Long tenant_Id, Long transactionId) throws UserNotFoundException, InvalidDataException;

    List<LeaveTransactionResponseWithoutWorkflowDTO> getLeaveTransactionsListByUserId(Long tenantId,Long userId, Long approverId,String search,List<String>statuses,String fromDate,String toDate,String sortBy,String sortOrder) throws UserNotFoundException, InvalidDataException;

}
