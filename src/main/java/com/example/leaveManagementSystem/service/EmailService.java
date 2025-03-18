package com.example.leaveManagementSystem.service;

import com.example.leaveManagementSystem.entity.UserEntity;

public interface EmailService {

    void sendLeaveAppliedEmail(UserEntity userId, String startDate, String endDate);
}
