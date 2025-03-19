package com.example.leaveManagementSystem.validation;

import com.example.leaveManagementSystem.constants.ResponseConstants;
import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;
import com.example.leaveManagementSystem.entity.LeaveTypeEntity;
import com.example.leaveManagementSystem.entity.TenantEntity;
import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.enumeration.EnumLeaveDuration;
import com.example.leaveManagementSystem.enumeration.EnumLeaveStatus;
import com.example.leaveManagementSystem.enumeration.EnumStatus;
import com.example.leaveManagementSystem.exception.DuplicateKeyException;
import com.example.leaveManagementSystem.exception.InvalidDataException;
import com.example.leaveManagementSystem.exception.UserNotFoundException;
import com.example.leaveManagementSystem.repository.LeaveTransactionRepository;
import com.example.leaveManagementSystem.repository.LeaveTypeRepository;
import com.example.leaveManagementSystem.repository.TenantRepository;
import com.example.leaveManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class LeaveTransactionValidator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveTransactionRepository leaveTransactionRepository;

    //regex as a class-level constant
    //private static final String DATE_REGEX = "^(?!0000)\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";

    //Validates if the user exists and is active.
    public UserEntity validateUser(Long userId) throws UserNotFoundException {
        return userRepository.findByIdAndStatus(userId, EnumStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException(ResponseConstants.USER_NOT_FOUND + userId));
    }

    // Validates if the tenant exists and is active.
    public TenantEntity validateTenant(Long tenantId) throws InvalidDataException {
        return tenantRepository.findByIdAndStatus(tenantId, EnumStatus.ACTIVE)
                .orElseThrow(() -> new InvalidDataException(ResponseConstants.TENANT_NOT_FOUND + tenantId));
    }

    //Validates if the leave type exists and is active.
    public LeaveTypeEntity validateLeaveType(Long leaveTypeId) throws InvalidDataException {
        return leaveTypeRepository.findByIdAndStatus(leaveTypeId, EnumStatus.ACTIVE)
                .orElseThrow(() -> new InvalidDataException(ResponseConstants.LEAVE_TYPE_NOT_FOUND + leaveTypeId));
    }

    //Validates start and end dates format.
    public void validateLeaveDates(String startDateStr, String endDateStr) throws InvalidDataException {

        if (startDateStr == null || startDateStr.trim().isEmpty() ||
                endDateStr == null || endDateStr.trim().isEmpty()) {
            throw new NullPointerException("Start date or End date cannot be null or empty.");
        }

        try {
            LocalDate startDate = parseValidDate(startDateStr);
            LocalDate endDate = parseValidDate(endDateStr);

            // Ensure start date is not after end date (Checked Only Once)
            if (startDate.isAfter(endDate)) {
                throw new InvalidDataException(ResponseConstants.INVALID_LEAVE_DATES);
            }

            // Ensure the date is not in the past
            if (startDate.isBefore(LocalDate.now())) {
                throw new InvalidDataException("Invalid start date: " + startDateStr + ". Past dates are not allowed.");
            }

        } catch (DateTimeParseException ex) {
            throw new InvalidDataException("Invalid date format: Expected format is YYYY-MM-DD.");
        }
    }

    private LocalDate parseValidDate(String dateStr) throws InvalidDataException {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Invalid date format: Expected format is YYYY-MM-DD.");
        }
    }

    //Validates if the leave being applied conflicts with any existing leave.
    public void validateDuplicateLeave(UserEntity user, LeaveTypeEntity leaveType,
                                       LocalDate startDate, EnumLeaveDuration startDateType,
                                       LocalDate endDate, EnumLeaveDuration endDateType) throws InvalidDataException {

        if (startDate.equals(endDate) && startDateType != endDateType) {
            throw new InvalidDataException("For the same start and end date, start date type and end date type must be the same.");
        }

        List<LeaveTransactionEntity> conflictingLeaves = leaveTransactionRepository
                .findByUserAndLeaveTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, leaveType, endDate, startDate)
                .stream()
                .filter(leave -> leave.getLeaveStatus() != EnumLeaveStatus.CANCELLED && leave.getLeaveStatus() != EnumLeaveStatus.REJECTED) // Ignore cancelled and rejected leaves
                .toList();

        for (LeaveTransactionEntity existingLeave : conflictingLeaves) {
            LocalDate existingStart = existingLeave.getStartDate();
            LocalDate existingEnd = existingLeave.getEndDate();
            EnumLeaveDuration existingStartType = existingLeave.getAppliedStartDateType();
            EnumLeaveDuration existingEndType = existingLeave.getAppliedEndDateType();

            // Direct Conflict - Full Overlap
            if ((startDate.isBefore(existingEnd) && endDate.isAfter(existingStart))) {
                throw new DuplicateKeyException("Leave already exists for the selected period.");
            }

            // Check for Half-Day Conflicts
            boolean isSameDay = existingStart.equals(startDate) || existingEnd.equals(endDate);
            if (isSameDay) {
                if (existingStartType == EnumLeaveDuration.FULL_DAY ||
                        startDateType == EnumLeaveDuration.FULL_DAY ||
                        existingEndType == EnumLeaveDuration.FULL_DAY ||
                        endDateType == EnumLeaveDuration.FULL_DAY ||
                        (existingStartType == EnumLeaveDuration.SECOND_HALF && startDateType == EnumLeaveDuration.SECOND_HALF) ||
                        (existingEndType == EnumLeaveDuration.FIRST_HALF && endDateType == EnumLeaveDuration.FIRST_HALF)) {
                    throw new DuplicateKeyException("Leave already exists for the selected period.");
                }
            }

            // Different Leave Type Overlap on the same period.
            if (!existingLeave.getLeaveType().equals(leaveType) &&
                    (existingStartType == startDateType || existingEndType == endDateType)) {
                throw new DuplicateKeyException("Leave of a different type already exists for the selected period.");
            }

            //  Prevents a full-day leave from being applied when a half-day leave already exists on that date
            if ((endDate.equals(existingStart) && endDateType == EnumLeaveDuration.FULL_DAY && existingStartType == EnumLeaveDuration.SECOND_HALF) ||
                    (startDate.equals(existingEnd) && startDateType == EnumLeaveDuration.FULL_DAY && existingEndType == EnumLeaveDuration.FIRST_HALF)) {
                throw new DuplicateKeyException("A half-day leave already exists for the selected date.");
            }
        }
    }

    // Validates the leave duration values.
    public void validateLeaveDuration(String startDateType, String endDateType) throws InvalidDataException {
        try {
            EnumLeaveDuration.valueOf(startDateType);
            EnumLeaveDuration.valueOf(endDateType);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(ResponseConstants.INVALID_LEAVE_DURATION);
        }
    }
}
