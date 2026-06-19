package com.example.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.dto.StudentRequestDTO;
import com.example.backend.dto.StudentResponseDTO;
import com.example.backend.entity.Department;
import com.example.backend.entity.Student;
import com.example.backend.repository.departmentRepository;
import com.example.backend.repository.studentRepository;

@Service
public class studentService {

    private final studentRepository repo;
    private final departmentRepository deptRepo;

    public studentService(studentRepository repo, departmentRepository deptRepo) {
        this.repo = repo;
        this.deptRepo = deptRepo;
    }

    public String generateRegNo(Department dept) {

        LocalDateTime now = LocalDateTime.now();
        String year = String.format("%02d", now.getYear() % 100);

        String deptCode = dept.getCode();

        

        Optional<Student> latestStudent =
        repo.findTopByDepartmentOrderByRegNoDesc(dept);

int nextNumber = 1;

if (latestStudent.isPresent()) {

    String regNo = latestStudent.get().getRegNo();

    String numericPart =
            regNo.substring(regNo.length() - 3);

    nextNumber = Integer.parseInt(numericPart) + 1;
}
String sequence = String.format("%03d", nextNumber);

        return year + deptCode + sequence;
    }

    public StudentResponseDTO createStudent(StudentRequestDTO request) {

        Department dept = deptRepo.findByNameIgnoreCase(
                request.getDepartment())
                .orElseThrow(() ->
                        new RuntimeException("Department not found"));


    String normalizedEmail = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();

    if (normalizedEmail != null && repo.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
        // will be mapped to 409 by controller
        throw new IllegalStateException("Email already exists: " + normalizedEmail);
    }

    Student student = new Student();

    student.setName(request.getName());
    student.setEmail(normalizedEmail);
    student.setPhone(request.getPhone());
    student.setDob(request.getDob());
    student.setDepartment(dept);
    student.setCreatedAt(LocalDateTime.now());

    String regNo = generateRegNo(dept);
    student.setRegNo(regNo);

    Student saved = repo.save(student);

    return StudentResponseDTO.builder()
            .id(saved.getId())
            .regNo(saved.getRegNo())
            .name(saved.getName())
            .department(saved.getDepartment().getName())
            .departmentCode(saved.getDepartment().getCode())
            .email(saved.getEmail())
            .phone(saved.getPhone())
            .dob(saved.getDob())
            .build();
}



    public List<StudentResponseDTO> getAll() {
        return repo.findAllByOrderByCreatedAtDesc().stream()
                .map(saved -> {
                    StudentResponseDTO dto = StudentResponseDTO.builder()
                            .id(saved.getId())
                            .regNo(saved.getRegNo())
                            .name(saved.getName())
                            .department(saved.getDepartment() != null ? saved.getDepartment().getName() : null)
                            .departmentCode(saved.getDepartment() != null ? saved.getDepartment().getCode() : null)
                            .email(saved.getEmail())
                            .phone(saved.getPhone())
                            .dob(saved.getDob())
                            .build();
                    return dto;
                })
                .toList();
    }


    public long getStudentCount() {
        return repo.count();
    }
    




    public StudentResponseDTO updateStudent(
        Long id,
        StudentRequestDTO dto) {

    Student existing = repo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Student not found"));

    String email = dto.getEmail().trim().toLowerCase();

    Optional<Student> emailOwner =
            repo.findByEmailIgnoreCase(email);

    if (emailOwner.isPresent()
            && !emailOwner.get().getId().equals(id)) {

        throw new IllegalStateException(
                "Email already exists");
    }

    existing.setName(dto.getName());
    existing.setEmail(email);
    existing.setPhone(dto.getPhone());
    existing.setDob(dto.getDob());

    Department newDepartment = deptRepo
            .findByName(dto.getDepartment())
            .orElseThrow(() ->
                    new RuntimeException("Department not found"));

    if (!existing.getDepartment().getId()
            .equals(newDepartment.getId())) {

        existing.setDepartment(newDepartment);

        String newRegNo = generateRegNo(newDepartment);

        existing.setRegNo(newRegNo);
    }

    Student saved = repo.save(existing);

    return StudentResponseDTO.builder()
            .id(saved.getId())
            .regNo(saved.getRegNo())
            .name(saved.getName())
            .department(saved.getDepartment().getName())
            .departmentCode(saved.getDepartment().getCode())
            .email(saved.getEmail())
            .phone(saved.getPhone())
            .dob(saved.getDob())
            .build();
}

    public void deleteStudent(Long id) {

        Student student = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));
    
        repo.delete(student);
    }


    private StudentResponseDTO toResponseDTO(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .regNo(student.getRegNo())
                .name(student.getName())
                .department(student.getDepartment() != null
                        ? student.getDepartment().getName()
                        : null)
                .departmentCode(student.getDepartment() != null
                        ? student.getDepartment().getCode()
                        : null)
                .email(student.getEmail())
                .phone(student.getPhone())
                .dob(student.getDob())
                .build();
    }

    public List<StudentResponseDTO> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StudentResponseDTO> searchByDepartment(String dept) {
        return repo.findByDepartment_NameContainingIgnoreCase(dept)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StudentResponseDTO> searchByEmail(String email) {
        return repo.findByEmailContainingIgnoreCase(email)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StudentResponseDTO> searchByPhone(String phone) {
        return repo.findByPhoneContainingIgnoreCase(phone)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StudentResponseDTO> searchByRegNo(String regNo) {
        return repo.findByRegNoContainingIgnoreCase(regNo)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StudentResponseDTO> filterByDob(LocalDate dob) {
        return repo.findByDob(dob)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StudentResponseDTO> filterByCreatedAt(
            LocalDateTime start,
            LocalDateTime end) {

        return repo.findByCreatedAtBetween(start, end)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<Object[]> getStudentCountByDepartment() {
        return repo.getStudentCountByDepartment();
    }
}
