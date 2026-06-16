package com.unialfa.bolao.repository;

import com.unialfa.bolao.domain.Selecao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SelecaoRepository extends JpaRepository<Selecao, Long> {

    List<Selecao> findAllByOrderByNomeAsc();

    boolean existsByCodigoFifaIgnoreCase(String codigoFifa);

    Optional<Selecao> findByCodigoFifaIgnoreCase(String codigoFifa);
}
