package com.example.leaveManagementSystem.dto;

import com.example.leaveManagementSystem.enumeration.EnumLeaveDuration;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveTransactionUpdateDTO {



    private String startDate;

    @Pattern(regexp = "FULL_DAY|FIRST_HALF|SECOND_HALF", message = "Invalid leave duration type")
    private String appliedStartDateType;


    private String endDate;

    @Pattern(regexp = "FULL_DAY|FIRST_HALF|SECOND_HALF", message = "Invalid leave duration type")
    private String appliedEndDateType;

    private String remarks;

    private String leaveStatus;


}
