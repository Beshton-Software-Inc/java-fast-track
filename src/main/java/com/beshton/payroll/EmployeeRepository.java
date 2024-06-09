package com.beshton.payroll;

import org.springframework.data.jpa.repository.JpaRepository;

interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
/*

Application -> JPARepository -> JPA -> JDBC -> Database (H2/MySQL/Postgres/...)

 */