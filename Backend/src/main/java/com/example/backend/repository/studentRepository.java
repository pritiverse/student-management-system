package com.example.backend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Department;
import com.example.backend.entity.Student;

public interface studentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT COUNT(s) FROM Student s WHERE s.department.code = :code")
    long countByDepartmentCode(@Param("code") String code);

    List<Student> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT s
        FROM Student s
        JOIN FETCH s.department d
        WHERE (:search IS NULL OR :search = '' OR
               LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(s.regNo) LIKE LOWER(CONCAT('%', :search, '%')) OR
               s.Phone LIKE CONCAT('%', :search, '%'))
          AND (:department IS NULL OR :department = '' OR
               LOWER(d.name) = LOWER(:department))
        ORDER BY s.createdAt DESC
    """)
    List<Student> searchStudents(
            @Param("search") String search,
            @Param("department") String department);


    // Used to prevent duplicate email inserts
    java.util.Optional<Student> findByEmailIgnoreCase(String email);


    List<Student> findByNameContainingIgnoreCase(String name);

    List<Student> findByDepartment_NameContainingIgnoreCase(String name);

    List<Student> findByEmailContainingIgnoreCase(String email);

    List<Student> findByPhoneContainingIgnoreCase(String phone);

    List<Student> findByRegNoContainingIgnoreCase(String regNo);

    List<Student> findByDob(LocalDate dob);

    List<Student> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByDepartment(String department);
    long countByDepartment_Code(String code);

    @Query("""
    SELECT s.regNo
    FROM Student s
    WHERE s.department = :department
    ORDER BY s.regNo DESC
    LIMIT 1
""")
String findLatestRegNoByDepartment(@Param("department") Department department);

Optional<Student> findTopByDepartmentOrderByRegNoDesc(
    Department department);

    @Query("""
        SELECT s.department.id, COUNT(s)
        FROM Student s
        GROUP BY s.department.id
        """)
        List<Object[]> getStudentCountByDepartment();



}
