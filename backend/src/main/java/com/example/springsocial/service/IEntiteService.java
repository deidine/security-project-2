package com.example.springsocial.service;

import com.example.springsocial.model.Entite;

public interface IEntiteService extends IService<Entite, Long>{
    void deleteById(Integer facultyId);
}
