package com.example.leaveManagementSystem.controller;

import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
import com.example.leaveManagementSystem.dto.LeaveTransactionResponseDTO;
import com.example.leaveManagementSystem.service.LeaveTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}")
public class LeaveTransactionController {

    @Autowired
    private LeaveTransactionService leaveTransactionService;

    @PostMapping
    public LeaveTransactionResponseDTO applyLeave(@RequestBody LeaveTransactionRequestDTO leaveTransactionRequestDTO,
                                                  @RequestHeader("UserId") Long userId,
                                                  @RequestHeader("TenantId") Long tenantId) {
        return leaveTransactionService.applyLeave(leaveTransactionRequestDTO, userId, tenantId);
    }

}
