//package com.example.leaveManagementSystem.controller;
//
//import com.example.leaveManagementSystem.dto.ApiResponseDTO;
//import com.example.leaveManagementSystem.dto.LeaveTransactionRequestDTO;
//import com.example.leaveManagementSystem.exception.InvalidDataException;
//import com.example.leaveManagementSystem.exception.UserNotFoundException;
//import com.example.leaveManagementSystem.service.LeaveTransactionService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class LeaveTransactionControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private LeaveTransactionService leaveTransactionService;
//
//    @InjectMocks
//    private LeaveTransactionController leaveTransactionController;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(leaveTransactionController).build();
//    }
//
//    @Test
//    void testApplyLeave_Success() throws Exception {
//        // Sample Request DTO
//        LeaveTransactionRequestDTO requestDTO = new LeaveTransactionRequestDTO();
//        requestDTO.setLeaveTypeId(1L);
//        requestDTO.setStartDate("2025-03-20");
//        requestDTO.setEndDate("2025-03-22");
//
//        // Mock Response
//        ApiResponseDTO responseDTO = new ApiResponseDTO(200, "Leave request submitted successfully");
//        when(leaveTransactionService.applyLeave(any(LeaveTransactionRequestDTO.class), anyLong(), anyLong()))
//                .thenReturn(responseDTO);
//
//        // Perform Request
//        mockMvc.perform(post("/leave")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("UserId", "1")
//                        .header("TenantId", "101")
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Leave request submitted successfully"));
//
//        // Verify interaction
//        verify(leaveTransactionService, times(1)).applyLeave(any(LeaveTransactionRequestDTO.class), anyLong(), anyLong());
//    }
//
//    @Test
//    void testApplyLeave_UserNotFound() throws Exception {
//        // Sample Request DTO
//        LeaveTransactionRequestDTO requestDTO = new LeaveTransactionRequestDTO();
//        requestDTO.setLeaveTypeId(1L);
//        requestDTO.setStartDate("2025-03-20");
//        requestDTO.setEndDate("2025-03-22");
//
//        // Mock Exception
//        when(leaveTransactionService.applyLeave(any(LeaveTransactionRequestDTO.class), anyLong(), anyLong()))
//                .thenThrow(new UserNotFoundException("User not found"));
//
//        // Perform Request
//        mockMvc.perform(post("/leave")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("UserId", "999") // Non-existing user
//                        .header("TenantId", "101")
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isNotFound()) // 404
//                .andExpect(jsonPath("$.message").value("User not found"));
//
//        // Verify interaction
//        verify(leaveTransactionService, times(1)).applyLeave(any(LeaveTransactionRequestDTO.class), anyLong(), anyLong());
//    }
//
//    @Test
//    void testApplyLeave_InvalidData() throws Exception {
//        // Sample Request DTO
//        LeaveTransactionRequestDTO requestDTO = new LeaveTransactionRequestDTO();
//        requestDTO.setLeaveTypeId(1L);
//        requestDTO.setStartDate("2025-03-25");
//        requestDTO.setEndDate("2025-03-20"); // Invalid date range
//
//        // Mock Exception
//        when(leaveTransactionService.applyLeave(any(LeaveTransactionRequestDTO.class), anyLong(), anyLong()))
//                .thenThrow(new InvalidDataException("Start date cannot be after end date"));
//
//        // Perform Request
//        mockMvc.perform(post("/leave")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("UserId", "1")
//                        .header("TenantId", "101")
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isBadRequest()) // 400
//                .andExpect(jsonPath("$.message").value("Start date cannot be after end date"));
//
//        // Verify interaction
//        verify(leaveTransactionService, times(1)).applyLeave(any(LeaveTransactionRequestDTO.class), anyLong(), anyLong());
//    }
//}
