package com.unialfa.bolao.repository;

import com.unialfa.bolao.domain.Palpite;
import com.unialfa.bolao.domain.Partida;
import com.unialfa.bolao.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PalpiteRepository extends JpaRepository<Palpite, Long> {

    Optional<Palpite> findByUsuarioAndPartida(Usuario usuario, Partida partida);

    List<Palpite> findByUsuarioOrderByPartidaDataHoraDesc(Usuario usuario);

    List<Palpite> findByPartida(Partida partida);

    long count();
}
