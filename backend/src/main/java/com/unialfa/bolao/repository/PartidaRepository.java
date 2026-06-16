package com.unialfa.bolao.repository;

import com.unialfa.bolao.domain.Fase;
import com.unialfa.bolao.domain.Partida;
import com.unialfa.bolao.domain.StatusPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

    List<Partida> findAllByOrderByDataHoraAsc();

    /** Filtra partidas por fase e/ou status (parametros nulos sao ignorados) - RF-012. */
    @Query("""
            SELECT p FROM Partida p
            WHERE (:fase IS NULL OR p.fase = :fase)
              AND (:status IS NULL OR p.status = :status)
            ORDER BY p.dataHora ASC
            """)
    List<Partida> filtrar(@Param("fase") Fase fase, @Param("status") StatusPartida status);

    /** Proximas partidas ainda abertas a palpite (RF-013). */
    List<Partida> findByStatusAndDataHoraAfterOrderByDataHoraAsc(StatusPartida status, LocalDateTime momento);

    long countByStatusNot(StatusPartida status);
}
