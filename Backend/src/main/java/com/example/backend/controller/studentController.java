package com.example.backend.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.StudentRequestDTO;
import com.example.backend.dto.StudentResponseDTO;
import com.example.backend.entity.Student;
import com.example.backend.service.studentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class studentController {
    @Autowired
    private studentService service;

    @PostMapping
    public ResponseEntity<?> createStudent(
        @RequestBody StudentRequestDTO request) {


        try {
            return ResponseEntity.ok(service.createStudent(request));
        } catch (IllegalStateException ex) {
            // used for duplicates (e.g., duplicate email)
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ex.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @GetMapping
    public List<StudentResponseDTO> getStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department) {
        return service.getStudents(search, department);
    }

    @GetMapping("/count")
public long getStudentCount() {
    return service.getStudentCount();
}

    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO dto) {
    
        return service.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {

        service.deleteStudent(id);
        return "Student deleted successfully";
    }

    @GetMapping("/search/name")
    public List<StudentResponseDTO> searchByName(
            @RequestParam String name) {
        return service.searchByName(name);
    }
    
    @GetMapping("/search/department")
    public List<StudentResponseDTO> searchByDepartment(
            @RequestParam String department) {
        return service.searchByDepartment(department);
    }
    
    @GetMapping("/search/email")
    public List<StudentResponseDTO> searchByEmail(
            @RequestParam String email) {
        return service.searchByEmail(email);
    }
    
    @GetMapping("/search/phone")
    public List<StudentResponseDTO> searchByPhone(
            @RequestParam String phone) {
        return service.searchByPhone(phone);
    }
    
    @GetMapping("/search/regno")
    public List<StudentResponseDTO> searchByRegNo(
            @RequestParam String regNo) {
        return service.searchByRegNo(regNo);
    }

    
    @GetMapping("/filter/created")
    public List<StudentResponseDTO> filterByCreatedAt(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return service.filterByCreatedAt(start, end);
    }

    @GetMapping("/count-by-department")
public List<Object[]> getCountByDepartment() {
    return service.getStudentCountByDepartment();
}


}
