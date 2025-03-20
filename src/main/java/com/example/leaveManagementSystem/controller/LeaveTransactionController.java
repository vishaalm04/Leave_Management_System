package com.example.leaveManagementSystem.controller;

import com.example.leaveManagementSystem.dto.*;
import com.example.leaveManagementSystem.exception.InvalidDataException;
import com.example.leaveManagementSystem.exception.UserNotFoundException;
import com.example.leaveManagementSystem.service.LeaveTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "leaveTransactionController", description = "Leave Request Api")
@RestController
@RequestMapping("/leave")
public class LeaveTransactionController {

    @Autowired
    private LeaveTransactionService leaveTransactionService;

    @Operation(
            summary = "Apply for leave",
            description = "Submit a leave request"
    )
    @ApiResponse(responseCode = "200", description = "Leave request submitted successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping
    public ApiResponseDTO applyLeave(@RequestBody LeaveTransactionRequestDTO leaveTransactionRequestDTO,
                                     @RequestHeader("userId") Long userId,
                                     @RequestHeader("tenantId") Long tenantId) throws UserNotFoundException, InvalidDataException {

        return leaveTransactionService.applyLeave(leaveTransactionRequestDTO, userId, tenantId);
    }

    @GetMapping("/{transactionId}")
    public LeaveTransactionResponseDTO getLeaveTransactionDetails(@RequestHeader("userId") Long userId,
                                                                  @RequestHeader("tenantId") Long tenant_Id,
                                                                  @PathVariable String transactionId) {
        return leaveTransactionService.getLeaveTransactionsByUserId(userId, tenant_Id, Long.valueOf(transactionId));
    }

    @DeleteMapping("/{transactionId}")
    public ApiResponseDTO cancelLeave(@RequestHeader("userId") Long userId,
                                      @RequestHeader("tenantId") Long tenant_Id,
                                      @PathVariable Long transactionId) throws UserNotFoundException {
        return leaveTransactionService.cancelLeave(userId, tenant_Id, transactionId);
    }

    @PutMapping("/{transactionId}")
    public ApiResponseDTO updateLeave(@RequestBody LeaveTransactionUpdateDTO leaveTransactionUpdateDTO,
                                      @RequestHeader("userId") Long userId,
                                      @RequestHeader("tenantId") Long tenant_Id,
                                      @PathVariable Long transactionId) throws UserNotFoundException, InvalidDataException {
        return leaveTransactionService.updateLeave(leaveTransactionUpdateDTO, userId, tenant_Id, transactionId);
    }

    @GetMapping("/list")
    public List<LeaveTransactionResponseWithoutWorkflowDTO> getLeaveTransactionsListByUserId(
            @RequestHeader(name = "tenantId", required = true) Long tenantId,
            @RequestHeader(name = "userId", required = false) Long userId,
            @RequestHeader(name = "approverId", required = false) Long approverId,
            @RequestParam(name = "search", required = false) String search ,
            @RequestParam(name = "statuses", required = false) List<String> statuses,
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortOrder", required = false) String sortOrder) throws UserNotFoundException, InvalidDataException {


        return leaveTransactionService.getLeaveTransactionsListByUserId(tenantId,userId, approverId,search,statuses,fromDate,toDate,sortBy,sortOrder);
    }

}
