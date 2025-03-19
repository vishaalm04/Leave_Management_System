package com.example.leaveManagementSystem.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveTransactionRequestDTO {


    @NotNull(message = "Leave Type ID is required")
    private  Long leaveTypeId;

    @NotNull(message = "Start date type is required")
//    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in the format yyyy-MM-dd")
    private String startDate;

    @NotNull(message = "Start date type is required")
    @Pattern(regexp = "FULL_DAY|FIRST_HALF|SECOND_HALF", message = "Invalid leave duration type")
    private String appliedStartDateType;

    @NotNull(message = "End date is required")
//    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in the format yyyy-MM-dd")
    private String endDate;

    @Pattern(regexp = "FULL_DAY|FIRST_HALF|SECOND_HALF", message = "Invalid leave duration type")
    @NotNull(message = "End date type is required")
    private String appliedEndDateType;

    private String remarks;




}
