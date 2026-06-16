package com.unialfa.bolao.service;

import com.unialfa.bolao.domain.StatusPartida;
import com.unialfa.bolao.repository.PalpiteRepository;
import com.unialfa.bolao.repository.PartidaRepository;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/** Indicadores do dashboard administrativo (RF-047). */
@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final PalpiteRepository palpiteRepository;
    private final PartidaRepository partidaRepository;

    public DashboardService(UsuarioRepository usuarioRepository,
                            PalpiteRepository palpiteRepository,
                            PartidaRepository partidaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.palpiteRepository = palpiteRepository;
        this.partidaRepository = partidaRepository;
    }

    public record Indicadores(
            long totalUsuarios,
            long totalPalpites,
            long partidasPendentes,
            long usuariosAtivos24h
    ) {}

    public Indicadores indicadores() {
        long totalUsuarios = usuarioRepository.count();
        long totalPalpites = palpiteRepository.count();
        // Partidas ainda nao encerradas = pendentes de resultado.
        long partidasPendentes = partidaRepository.countByStatusNot(StatusPartida.ENCERRADA);
        long ativos = usuarioRepository.countByUltimoAcessoAfter(LocalDateTime.now().minusHours(24));
        return new Indicadores(totalUsuarios, totalPalpites, partidasPendentes, ativos);
    }
}
