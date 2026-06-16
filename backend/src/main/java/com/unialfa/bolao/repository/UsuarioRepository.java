package com.unialfa.bolao.repository;

import com.unialfa.bolao.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    /** Ranking geral: ordena por pontuacao e aplica os criterios de desempate (RF-032, RF-034). */
    Page<Usuario> findAllByOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(Pageable pageable);

    /** Busca usuarios por nome ou e-mail para a listagem do admin (RF-045). */
    Page<Usuario> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nome, String email, Pageable pageable);

    long countByUltimoAcessoAfter(LocalDateTime momento);
}
