package com.example.springsocial.factory;

import java.util.Set;

import com.example.springsocial.model.City;
import com.example.springsocial.model.City2;
import com.example.springsocial.model.Department;
import com.example.springsocial.model.Entite;
 

public class EntiteFactory {
    public static Entite build( City2 Mougattaa, City Wilayas, String affectation  ,String type  ,Set<Department> department ,String entiteName) {
        return Entite.builder()
                .Mougattaa(Mougattaa)
                .Wilayas(Wilayas)
                .affectation(affectation).type(type)
                .departments((Set<Department>) department).entiteName(entiteName)
                .build();
    }

    public static Entite build(long entiteId, String entiteName, Set<Department> departments) {
        return null;
    }
}