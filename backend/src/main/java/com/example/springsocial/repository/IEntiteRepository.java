package com.example.springsocial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.springsocial.model.Entite;
 
 
 
/***
 * Faculty.java
 * Entity for the FacultyServiceImplTest
 * @author Mbuso Kotobe (218040385)
 * Date: 14 August 2022
 */
public interface IEntiteRepository extends JpaRepository<Entite, Integer> {


@Query(value=" Select * FROM Entite where entite_id=?1",nativeQuery = true)
 Entite read_Id(Integer id);}
