package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.entity.Department;
import com.example.backend.repository.departmentRepository;

@Service
public class departmentService {

    private final departmentRepository repo;

    public departmentService(departmentRepository repo) {
        this.repo = repo;
    }

    public Department create(Department dept) {
        return repo.save(dept);
    }

    public List<Department> getAll() {
        return repo.findAll();
    }

    public Department getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department update(Long id, Department updated) {
        Department existing = getById(id);

        existing.setName(updated.getName());
        existing.setCode(updated.getCode());

        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
    public long getDepartmentCount() {
        return repo.count();
    }
}