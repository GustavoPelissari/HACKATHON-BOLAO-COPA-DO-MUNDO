package com.unialfa.bolao.service;

import com.unialfa.bolao.api.dto.PalpiteDtos.PalpiteRequest;
import com.unialfa.bolao.domain.Palpite;
import com.unialfa.bolao.domain.Partida;
import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.exception.NegocioException;
import com.unialfa.bolao.repository.PalpiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Registro, edicao e listagem de palpites (RF-020 a RF-024, regra 4.2). */
@Service
public class PalpiteService {

    private final PalpiteRepository palpiteRepository;
    private final PartidaService partidaService;

    public PalpiteService(PalpiteRepository palpiteRepository, PartidaService partidaService) {
        this.palpiteRepository = palpiteRepository;
        this.partidaService = partidaService;
    }

    public List<Palpite> meusPalpites(Usuario usuario) {
        return palpiteRepository.findByUsuarioOrderByPartidaDataHoraDesc(usuario);
    }

    /**
     * Cria ou atualiza o palpite do usuario para a partida. Um usuario tem
     * no maximo um palpite por partida; reenviar substitui o anterior (RF-020, RF-021).
     */
    @Transactional
    public Palpite registrarOuEditar(Usuario usuario, PalpiteRequest req) {
        Partida partida = partidaService.buscarPorId(req.partidaId());

        // Regra 4.2: bloqueia registro/edicao apos o inicio oficial da partida.
        if (!LocalDateTime.now().isBefore(partida.getDataHora())) {
            throw new NegocioException(
                    "Os palpites para esta partida estao encerrados (a partida ja iniciou).");
        }

        Palpite palpite = palpiteRepository.findByUsuarioAndPartida(usuario, partida)
                .orElseGet(() -> {
                    Palpite novo = new Palpite();
                    novo.setUsuario(usuario);
                    novo.setPartida(partida);
                    return novo;
                });

        palpite.setGolsMandante(req.golsMandante());
        palpite.setGolsVisitante(req.golsVisitante());

        return palpiteRepository.save(palpite);
    }
}
