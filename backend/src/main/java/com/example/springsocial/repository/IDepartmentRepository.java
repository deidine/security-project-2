package com.example.springsocial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springsocial.model.Department;
 
/***
 * Department.java
 * @author Elvis Ndlangamandla (213063964)
 * Date: 21 August 2022
 */

@Repository
public interface IDepartmentRepository extends JpaRepository<Department, Integer> {
}
