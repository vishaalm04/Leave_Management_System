package com.example.leaveManagementSystem.service.impl;

import com.example.leaveManagementSystem.constants.ResponseConstants;
import com.example.leaveManagementSystem.dto.ApiResponseDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionUpdateDTO;
import com.example.leaveManagementSystem.entity.*;
import com.example.leaveManagementSystem.enumeration.EnumLeaveDuration;
import com.example.leaveManagementSystem.enumeration.EnumLeaveStatus;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import com.example.leaveManagementSystem.exception.InvalidDataException;
import com.example.leaveManagementSystem.exception.UserNotFoundException;
import com.example.leaveManagementSystem.mapper.ApproveWorkFlowMapper;
import com.example.leaveManagementSystem.mapper.LeaveTransactionMapper;
import com.example.leaveManagementSystem.repository.*;
import com.example.leaveManagementSystem.service.EmailService;
import com.example.leaveManagementSystem.service.LeaveTransactionService;
import com.example.leaveManagementSystem.validation.LeaveTransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class LeaveTransactionServiceImpl implements LeaveTransactionService {

    @Autowired
    private LeaveTransactionRepository leaveTransactionRepository;

    @Autowired
    private ApproveWorkflowRepository approveWorkflowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveTransactionMapper leaveTransactionMapper;

    @Autowired
    private ApproveWorkFlowMapper approveWorkFlowMapper;

    @Autowired
    private LeaveTransactionValidator leaveTransactionValidator;

    @Autowired
    private EmailService emailService;

    @Override
    public ApiResponseDTO applyLeave(LeaveTransactionRequestDTO leaveTransactionRequestDTO, Long userId, Long tenantId) throws UserNotFoundException, InvalidDataException {

        UserEntity user = leaveTransactionValidator.validateUser(userId);
        TenantEntity tenantEntity = leaveTransactionValidator.validateTenant(tenantId);
        LeaveTypeEntity leaveType = leaveTransactionValidator.validateLeaveType(leaveTransactionRequestDTO.getLeaveTypeId());

        LocalDate startDate = LocalDate.parse(leaveTransactionRequestDTO.getStartDate());
        LocalDate endDate = LocalDate.parse(leaveTransactionRequestDTO.getEndDate());

        EnumLeaveDuration startDateType = EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedStartDateType());
        EnumLeaveDuration endDateType = EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedEndDateType());

        leaveTransactionValidator.validateLeaveDates(leaveTransactionRequestDTO.getStartDate(), leaveTransactionRequestDTO.getEndDate());
        leaveTransactionValidator.validateDuplicateLeave(user, leaveType, startDate,startDateType,endDate,endDateType);
        leaveTransactionValidator.validateLeaveDuration(leaveTransactionRequestDTO.getAppliedStartDateType(),leaveTransactionRequestDTO.getAppliedEndDateType());

//        LeaveTransactionEntity leaveTransactionEntity = new LeaveTransactionEntity();
//        leaveTransactionEntity.setTenantId(tenantId);
//        leaveTransactionEntity.setUser(user);
//        leaveTransactionEntity.setLeaveType(leaveType);
//        leaveTransactionEntity.setApprover(!leaveType.getIsApprover() && leaveType.getIsReviewer() ? user.getReviewer() : user.getApprover());
//        leaveTransactionEntity.setStartDate(startDate);
//        leaveTransactionEntity.setAppliedStartDateType(EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedStartDateType()));
//        leaveTransactionEntity.setEndDate(endDate);
//        leaveTransactionEntity.setAppliedEndDateType(EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedEndDateType()));
//        leaveTransactionEntity.setRemarks(leaveTransactionRequestDTO.getRemarks());
//        leaveTransactionEntity.setLeaveStatus(EnumLeaveStatus.PENDING);
//        leaveTransactionEntity.setCreatedBy(user.getName());

       // emailService.sendLeaveAppliedEmail(userId,leaveTransactionRequestDTO.getStartDate(), leaveTransactionRequestDTO.getEndDate());
        LeaveTransactionEntity leaveTransactionEntity =
                leaveTransactionMapper.toEntity(leaveTransactionRequestDTO, user, leaveType, tenantId, startDate, endDate);
        leaveTransactionEntity = leaveTransactionRepository.save(leaveTransactionEntity);

        List<ApproveWorkflowEntity> approveWorkflowEntities = new ArrayList<>();
        if (leaveType.getIsApprover() || !leaveType.getIsReviewer()) {
            ApproveWorkflowEntity approveWorkflowEntity = approveWorkFlowMapper.toEntity(
                    leaveTransactionRequestDTO, leaveTransactionEntity, tenantEntity, user.getApprover()
            );
            approveWorkflowEntities.add(approveWorkflowEntity);
        }
        if (leaveType.getIsReviewer()) {
            ApproveWorkflowEntity approveWorkflowEntity = approveWorkFlowMapper.toEntity(
                    leaveTransactionRequestDTO, leaveTransactionEntity, tenantEntity, user.getReviewer()
            );
            approveWorkflowEntities.add(approveWorkflowEntity);
        }

        approveWorkflowRepository.saveAll(approveWorkflowEntities);

        // Send email before processing the leave application
       // emailService.sendLeaveAppliedEmail(user, leaveTransactionRequestDTO.getStartDate(), leaveTransactionRequestDTO.getEndDate());
        return new ApiResponseDTO(HttpStatus.OK.value(), ResponseConstants.LEAVE_APPLIED);
    }

    @Override
    public LeaveTransactionResponseDTO getLeaveTransactionsByUserId(Long userId,Long tenant_Id,Long transactionId) {
        UserEntity user = userRepository.findByIdAndStatus(userId, EnumStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        LeaveTransactionEntity leaveTransaction = leaveTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Leave Transaction not found with ID: " + transactionId));

        if (!leaveTransaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access: Leave Transaction does not belong to this user.");
        }

        return leaveTransactionMapper.toResponseDTO(leaveTransaction);
    }

    @Override
    public ApiResponseDTO cancelLeave(Long userId,Long tenant_Id, Long transactionId) throws UserNotFoundException {
        // Validate user
        UserEntity user = leaveTransactionValidator.validateUser(userId);

        // Retrieve leave transaction
        Optional<LeaveTransactionEntity> leaveTransactionOptional = leaveTransactionRepository.findById(transactionId);

        if (leaveTransactionOptional.isEmpty()) {
            return new ApiResponseDTO(HttpStatus.NOT_FOUND.value(), ResponseConstants.TRANSACTION_NOT_FOUND);
        }

        LeaveTransactionEntity leaveTransaction = leaveTransactionOptional.get();

        // Ensure leave transaction has a valid user
        if (leaveTransaction.getUser() == null || !leaveTransaction.getUser().getId().equals(user.getId())) {
            return new ApiResponseDTO(HttpStatus.FORBIDDEN.value(), "Unauthorized to cancel this leave");
        }

        // Check if leave is already approved or rejected
        if (leaveTransaction.getLeaveStatus() == null ||
                leaveTransaction.getLeaveStatus() == EnumLeaveStatus.APPROVED ||
                leaveTransaction.getLeaveStatus() == EnumLeaveStatus.REJECTED) {
            return new ApiResponseDTO(HttpStatus.BAD_REQUEST.value(), "Cannot cancel an approved or rejected leave");
        }

        // Update status to CANCELLED
        leaveTransaction.setLeaveStatus(EnumLeaveStatus.CANCELLED);

        try {
            leaveTransactionRepository.save(leaveTransaction);
        } catch (Exception e) {
            return new ApiResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while cancelling leave");
        }

        return new ApiResponseDTO(HttpStatus.OK.value(), ResponseConstants.LEAVE_CANCELLED);
    }

    @Override
    public ApiResponseDTO updateLeave(LeaveTransactionUpdateDTO leaveTransactionUpdateDTO, Long userId, Long tenantId, Long transactionId) throws UserNotFoundException, InvalidDataException {

        UserEntity user = leaveTransactionValidator.validateUser(userId);

        Optional<LeaveTransactionEntity> leaveTransactionOpt = leaveTransactionRepository.findById(transactionId);

        if (leaveTransactionOpt.isEmpty()) {
            return new ApiResponseDTO(HttpStatus.NOT_FOUND.value(), ResponseConstants.TRANSACTION_NOT_FOUND);
        }

        LeaveTransactionEntity leaveTransaction = leaveTransactionOpt.get();

        // Ensure the leave belongs to the user
        if (!leaveTransaction.getUser().getId().equals(user.getId())) {
            return new ApiResponseDTO(HttpStatus.FORBIDDEN.value(), "Unauthorized to update this leave");
        }

        if (!leaveTransaction.getLeaveStatus().equals(EnumLeaveStatus.PENDING)) {
            return new ApiResponseDTO(HttpStatus.BAD_REQUEST.value(), "Leave Status can't be updated");
        }

            leaveTransactionValidator.validateLeaveDates(leaveTransactionUpdateDTO.getStartDate(), leaveTransactionUpdateDTO.getEndDate());
            leaveTransaction.setStartDate(LocalDate.parse(leaveTransactionUpdateDTO.getStartDate()));
            leaveTransaction.setEndDate(LocalDate.parse(leaveTransactionUpdateDTO.getEndDate()));
            leaveTransaction.setAppliedStartDateType(EnumLeaveDuration.valueOf(leaveTransactionUpdateDTO.getAppliedEndDateType()));
            leaveTransaction.setAppliedEndDateType(EnumLeaveDuration.valueOf(leaveTransactionUpdateDTO.getAppliedEndDateType()));
            leaveTransactionRepository.save(leaveTransaction);
            return new ApiResponseDTO(HttpStatus.OK.value(), ResponseConstants.LEAVE_UPDATED);
    }

    @Override
    public List<LeaveTransactionResponseDTO> getLeaveTransactionsListByUserId(Long userId) throws UserNotFoundException {
        UserEntity user = leaveTransactionValidator.validateUser(userId);

        List<LeaveTransactionEntity> leaveTransactions = leaveTransactionRepository.findByUser(user);
        return leaveTransactions.stream()
                .map(leaveTransactionMapper::toResponseDTO)
                .toList();
    }
}
