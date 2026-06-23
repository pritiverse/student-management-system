package com.example.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentResponseDTO {

    private Long id;
    private String regNo;
    private String name;
    private String department;
    private String departmentCode;
    private String email;
    private String phone;
    private LocalDate dob;
    private LocalDateTime createdAt;
    private Integer enrolledYear;
}
