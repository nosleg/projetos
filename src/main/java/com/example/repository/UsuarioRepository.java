package com.example.repository;

import com.example.model.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    List<Usuario> findAll();
    List<Usuario> findByTipo(String tipo);
    
    Optional<Usuario> findById(Long id);
}