package com.challenge.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudService<T, ID> {
    Page<T> getAll(Pageable pageable);
    T getById(ID id);
    T create(T t);
    T update(T t, ID id);
    void delete(ID id);
}
