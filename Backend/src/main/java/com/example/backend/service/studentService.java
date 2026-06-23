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

    public String generateRegNo(Department dept, int yr) {

        
        String year = String.format("%02d", yr % 100);

        String deptCode = dept.getCode();

        Optional<Student> latestStudent =
                repo.findTopByDepartmentAndEnrolledYearOrderByRegNoDesc(dept, yr);

        int nextNumber = 1;

        if (latestStudent.isPresent()) {
            String regNo = latestStudent.get().getRegNo();
            String numericPart = regNo.substring(regNo.length() - 3);
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

    // Basic validations
    if (request.getName() == null || request.getName().trim().isEmpty()) {
        throw new IllegalStateException("Student name is required");
    }

    if (normalizedEmail == null || !com.example.backend.validation.ValidationUtil.isValidEmail(normalizedEmail)) {
        throw new IllegalStateException("Enter a valid email address");
    }

    if (request.getPhone() == null || !com.example.backend.validation.ValidationUtil.isValidIndianPhone(request.getPhone())) {
        throw new IllegalStateException("Enter a valid Indian phone number");
    }

    if (request.getDob() == null || !com.example.backend.validation.ValidationUtil.isValidDob(request.getDob())) {
        throw new IllegalStateException("Enter a valid DOB");
    }

    if (request.getEnrolledYear() == null || !com.example.backend.validation.ValidationUtil.isValidEnrolledYear(request.getEnrolledYear())) {
        throw new IllegalStateException("Enter a valid enrolled year");
    }

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
    student.setEnrolledYear(request.getEnrolledYear());

    String regNo = generateRegNo(dept,request.getEnrolledYear());
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
            .createdAt(saved.getCreatedAt())
            .enrolledYear(saved.getEnrolledYear())
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
                            .createdAt(saved.getCreatedAt())
                            .enrolledYear(saved.getEnrolledYear())
                            .build();
                    return dto;
                })
                .toList();
    }


    public long getStudentCount() {
        return repo.count();
    }
    
    public List<StudentResponseDTO> getStudents(
        String search,
        String department) {

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        String normalizedDepartment = (department == null || department.isBlank()) ? null : department.trim();

        return repo.searchStudents(normalizedSearch, normalizedDepartment)
                .stream()
                .map(this::toResponseDTO)
                .toList();
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

    // Validations (same rules as create)
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
        throw new IllegalStateException("Student name is required");
    }

    if (email == null || !com.example.backend.validation.ValidationUtil.isValidEmail(email)) {
        throw new IllegalStateException("Enter a valid email address");
    }

    if (dto.getPhone() == null || !com.example.backend.validation.ValidationUtil.isValidIndianPhone(dto.getPhone())) {
        throw new IllegalStateException("Enter a valid Indian phone number");
    }

    if (dto.getDob() == null || !com.example.backend.validation.ValidationUtil.isValidDob(dto.getDob())) {
        throw new IllegalStateException("Enter a valid DOB");
    }

    if (dto.getEnrolledYear() == null || !com.example.backend.validation.ValidationUtil.isValidEnrolledYear(dto.getEnrolledYear())) {
        throw new IllegalStateException("Enter a valid enrolled year");
    }

    existing.setName(dto.getName());
    existing.setEmail(email);
    existing.setPhone(dto.getPhone());
    existing.setDob(dto.getDob());

    Integer oldEnrolledYear = existing.getEnrolledYear();

    Department newDepartment = deptRepo
            .findByName(dto.getDepartment())
            .orElseThrow(() ->
                    new RuntimeException("Department not found"));

    boolean departmentChanged =
            existing.getDepartment() == null ||
            !existing.getDepartment().getId().equals(newDepartment.getId());

    boolean enrolledYearChanged =
            oldEnrolledYear == null ||
            !oldEnrolledYear.equals(dto.getEnrolledYear());

    if (departmentChanged) {
        existing.setDepartment(newDepartment);
    }

    if (enrolledYearChanged) {
        existing.setEnrolledYear(dto.getEnrolledYear());
    }

    if (departmentChanged || enrolledYearChanged) {
        String newRegNo = generateRegNo(
                departmentChanged ? newDepartment : existing.getDepartment(),
                dto.getEnrolledYear());
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
            .createdAt(saved.getCreatedAt())
            .enrolledYear(saved.getEnrolledYear())
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
                .createdAt(student.getCreatedAt())
                .enrolledYear(student.getEnrolledYear())
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
