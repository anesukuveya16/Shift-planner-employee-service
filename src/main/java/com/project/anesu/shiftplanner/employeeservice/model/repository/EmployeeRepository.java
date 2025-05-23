package com.project.anesu.shiftplanner.employeeservice.model.repository;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {}
