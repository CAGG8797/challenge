package com.challenge.api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ExtendedCrudRepository<T, ID> extends JpaRepository<T, ID> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.active = true")
    Page<T> findAll(Pageable pageable);

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.active = true")
    Optional<T> findById(ID id);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.active = false WHERE e.id = :id")
    void softDeleteById(ID id);
}
