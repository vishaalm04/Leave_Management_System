package com.example.leaveManagementSystem.service.impl;

import com.example.leaveManagementSystem.constants.ResponseConstants;
import com.example.leaveManagementSystem.dto.*;
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
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.leaveManagementSystem.repository.LeaveTransactionSpecification.*;
import static javax.management.Query.and;


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

        leaveTransactionValidator.validateLeaveDates(leaveTransactionRequestDTO.getStartDate(), leaveTransactionRequestDTO.getEndDate());

        LocalDate startDate = LocalDate.parse(leaveTransactionRequestDTO.getStartDate());
        LocalDate endDate = LocalDate.parse(leaveTransactionRequestDTO.getEndDate());

        EnumLeaveDuration startDateType = EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedStartDateType());
        EnumLeaveDuration endDateType = EnumLeaveDuration.valueOf(leaveTransactionRequestDTO.getAppliedEndDateType());

        leaveTransactionValidator.validateDuplicateLeave(user, leaveType, startDate, startDateType, endDate, endDateType);
        leaveTransactionValidator.validateLeaveDuration(leaveTransactionRequestDTO.getAppliedStartDateType(), leaveTransactionRequestDTO.getAppliedEndDateType());

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
    public LeaveTransactionResponseDTO getLeaveTransactionsByUserId(Long userId, Long tenant_Id, Long transactionId) {
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
    public ApiResponseDTO cancelLeave(Long userId, Long tenant_Id, Long transactionId) throws UserNotFoundException {
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
        TenantEntity tenantEntity = leaveTransactionValidator.validateTenant(tenantId);

        Optional<LeaveTransactionEntity> leaveTransactionOpt = leaveTransactionRepository.findById(transactionId);

        if (leaveTransactionOpt.isEmpty()) {
            return new ApiResponseDTO(HttpStatus.NOT_FOUND.value(), ResponseConstants.TRANSACTION_NOT_FOUND);
        }

        LeaveTransactionEntity leaveTransaction = leaveTransactionOpt.get();

        // Ensure the leave belongs to the user
        if (!leaveTransaction.getUser().getId().equals(user.getId())) {
            return new ApiResponseDTO(HttpStatus.FORBIDDEN.value(), "Unauthorized to update this leave");
        }
        if (!leaveTransaction.getTenantId().equals(tenantEntity.getId())) {
            return new ApiResponseDTO(HttpStatus.FORBIDDEN.value(), "Unauthorized to update this leave");
        }

        if (!leaveTransaction.getLeaveStatus().equals(EnumLeaveStatus.PENDING)) {
            return new ApiResponseDTO(HttpStatus.BAD_REQUEST.value(), "Leave Status can't be updated");
        }

        if (leaveTransactionUpdateDTO.getLeaveStatus() != null &&
                leaveTransactionUpdateDTO.getLeaveStatus().equalsIgnoreCase("CANCELLED")) {

            leaveTransaction.setLeaveStatus(EnumLeaveStatus.CANCELLED);
            leaveTransactionRepository.save(leaveTransaction);
            return new ApiResponseDTO(HttpStatus.OK.value(), "Leave request has been cancelled.");
        }

        if (leaveTransactionUpdateDTO.getStartDate() != null) {
            leaveTransactionValidator.validateLeaveDates(leaveTransactionUpdateDTO.getStartDate(), leaveTransactionUpdateDTO.getEndDate());
            leaveTransaction.setStartDate(LocalDate.parse(leaveTransactionUpdateDTO.getStartDate()));
        }

        if (leaveTransactionUpdateDTO.getEndDate() != null) {
            leaveTransactionValidator.validateLeaveDates(leaveTransactionUpdateDTO.getStartDate(), leaveTransactionUpdateDTO.getEndDate());
            leaveTransaction.setEndDate(LocalDate.parse(leaveTransactionUpdateDTO.getEndDate()));
        }

        if (leaveTransactionUpdateDTO.getAppliedStartDateType() != null) {
            leaveTransaction.setAppliedStartDateType(EnumLeaveDuration.valueOf(leaveTransactionUpdateDTO.getAppliedStartDateType()));
        }

        if (leaveTransactionUpdateDTO.getAppliedEndDateType() != null) {
            leaveTransaction.setAppliedEndDateType(EnumLeaveDuration.valueOf(leaveTransactionUpdateDTO.getAppliedEndDateType()));
        }
        if (leaveTransactionUpdateDTO.getRemarks() != null && !leaveTransactionUpdateDTO.getRemarks().trim().isEmpty()) {
            leaveTransaction.setRemarks(leaveTransactionUpdateDTO.getRemarks().trim());
        }

        leaveTransactionRepository.save(leaveTransaction);
        return new ApiResponseDTO(HttpStatus.OK.value(), ResponseConstants.LEAVE_UPDATED);
    }

    @Override
    public List<LeaveTransactionResponseWithoutWorkflowDTO> getLeaveTransactionsListByUserId(
            Long tenantId, Long userId, Long approverId, String search,List<String>statuses,String fromDate,String toDate,String sortBy,String sortOrder) throws InvalidDataException {

       if(statuses!=null && !statuses.isEmpty()){
           for(String status :statuses){
               try{
                   EnumLeaveStatus.valueOf(status.toUpperCase());
               }catch(IllegalArgumentException e){
                   throw new InvalidDataException(ResponseConstants.INVALID_FILTER_OPTION);
               }
           }
       }
        Specification<LeaveTransactionEntity> specification =
                LeaveTransactionSpecification.getFilteredTransactions(tenantId, userId, approverId, search,statuses,fromDate,toDate,sortBy,sortOrder);

        // Fetch filtered records from DB
        List<LeaveTransactionEntity> leaveTransactions = leaveTransactionRepository.findAll(specification);

        //entities to DTO using the mapper
        return leaveTransactions.stream()
                .map(leaveTransactionMapper::toResponseDTOWithoutWorkflow)
                .collect(Collectors.toList());
    }







}
