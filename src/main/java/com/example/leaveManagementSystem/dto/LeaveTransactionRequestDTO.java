package com.example.leaveManagementSystem.dto;

import com.example.leaveManagementSystem.enumeration.EnumLeaveDuration;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveTransactionRequestDTO {


    @NotNull
    private  Long leaveTypeId;

    @NotNull
    private String startDate;

    @NotNull
    private EnumLeaveDuration appliedStartDateType;

    @NotNull
    private String endDate;

    @NotNull
    private EnumLeaveDuration appliedEndDateType;

    @NotNull
    private Long approverId;

    private String remarks;


}
