package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Conta;

import javax.persistence.LockModeType;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Conta c WHERE c.numero = :numero")
    Conta findByNumeroWithLock(@Param("numero") String numero);
    
    Conta findByNumero(String numero);
}