package com.example.springsocial.repository;

public interface IRepository <T, I>
{
    T create(T object);
    T read(I id);
    T update(T object);
    boolean delete(I id);
}