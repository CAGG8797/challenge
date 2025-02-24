package com.challenge.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudService<RQ, RS, ID> {
    Page<RS> getAll(Pageable pageable);
    RS getById(ID id);
    RS create(RQ request) throws Exception;
    RS update(ID id, RQ request) throws Exception;
    void delete(ID id) throws Exception;
}
