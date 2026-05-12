package com.colheplus.repository;

import com.colheplus.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByTokenSessao(String tokenSessao);

    boolean existsByEmail(String email);
}
