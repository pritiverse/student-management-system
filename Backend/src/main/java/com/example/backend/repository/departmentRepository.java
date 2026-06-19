package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Department;
public interface departmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
    Optional<Department> findByName(String name);
    String findCodeByNameIgnoreCase(String name);
    Optional<Department> findByNameIgnoreCase(String name);
    long countByCode(String code);




}