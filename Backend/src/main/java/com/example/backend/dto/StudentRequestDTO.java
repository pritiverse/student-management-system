package com.example.backend.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRequestDTO {

    private String name;
    private String department;
    private String email;
    private String phone;
    private LocalDate dob;
}