package com.unialfa.bolao.service;

import com.unialfa.bolao.domain.*;
import com.unialfa.bolao.exception.NegocioException;
import com.unialfa.bolao.exception.RecursoNaoEncontradoException;
import com.unialfa.bolao.repository.PalpiteRepository;
import com.unialfa.bolao.repository.PartidaRepository;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gestao de partidas, lancamento de resultado e calculo/recalculo de pontuacao
 * (RF-010 a RF-013, RF-030, RF-043, RF-044, e regras 4.1/4.3).
 */
@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final PalpiteRepository palpiteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontuacaoService pontuacaoService;

    public PartidaService(PartidaRepository partidaRepository,
                          PalpiteRepository palpiteRepository,
                          UsuarioRepository usuarioRepository,
                          PontuacaoService pontuacaoService) {
        this.partidaRepository = partidaRepository;
        this.palpiteRepository = palpiteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pontuacaoService = pontuacaoService;
    }

    public List<Partida> listar() {
        return partidaRepository.findAllByOrderByDataHoraAsc();
    }

    public List<Partida> filtrar(Fase fase, StatusPartida status) {
        return partidaRepository.filtrar(fase, status);
    }

    public List<Partida> proximas() {
        return partidaRepository
                .findByStatusAndDataHoraAfterOrderByDataHoraAsc(StatusPartida.AGENDADA, LocalDateTime.now());
    }

    public Partida buscarPorId(Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Partida nao encontrada."));
    }

    @Transactional
    public Partida salvar(Partida partida) {
        if (partida.getSelecaoMandante().getId().equals(partida.getSelecaoVisitante().getId())) {
            throw new NegocioException("Mandante e visitante devem ser selecoes diferentes.");
        }
        return partidaRepository.save(partida);
    }

    @Transactional
    public void remover(Long id) {
        Partida partida = buscarPorId(id);
        if (!palpiteRepository.findByPartida(partida).isEmpty()) {
            throw new NegocioException("Nao e possivel remover uma partida que ja possui palpites.");
        }
        partidaRepository.delete(partida);
    }

    /**
     * Lanca (ou corrige) o resultado de uma partida. Encerra a partida e
     * dispara o (re)calculo da pontuacao de todos os palpites associados,
     * em uma unica transacao (RF-043, RF-044, regra 4.3).
     */
    @Transactional
    public Partida lancarResultado(Long partidaId, int golsMandante, int golsVisitante) {
        if (golsMandante < 0 || golsVisitante < 0) {
            throw new NegocioException("Os gols nao podem ser negativos.");
        }

        Partida partida = buscarPorId(partidaId);
        partida.setGolsMandante(golsMandante);
        partida.setGolsVisitante(golsVisitante);
        partida.setStatus(StatusPartida.ENCERRADA);
        partidaRepository.save(partida);

        recalcularPontuacao(partida);
        return partida;
    }

    /**
     * Recalcula a pontuacao de todos os palpites da partida e atualiza os
     * totais desnormalizados dos usuarios afetados (pontuacao_total e
     * placares_exatos), garantindo consistencia ao corrigir um resultado.
     */
    private void recalcularPontuacao(Partida partida) {
        List<Palpite> palpites = palpiteRepository.findByPartida(partida);

        for (Palpite palpite : palpites) {
            CriterioPontuacao anterior = palpite.getCriterioAplicado();

            CriterioPontuacao novo = pontuacaoService.avaliar(
                    palpite.getGolsMandante(), palpite.getGolsVisitante(),
                    partida.getGolsMandante(), partida.getGolsVisitante());

            Usuario usuario = palpite.getUsuario();

            // Remove o efeito da pontuacao anterior (caso seja uma correcao).
            if (anterior != null) {
                usuario.setPontuacaoTotal(usuario.getPontuacaoTotal() - anterior.getPontos());
                if (anterior == CriterioPontuacao.PLACAR_EXATO) {
                    usuario.setPlacaresExatos(usuario.getPlacaresExatos() - 1);
                }
            }

            // Aplica a nova pontuacao.
            usuario.setPontuacaoTotal(usuario.getPontuacaoTotal() + novo.getPontos());
            if (novo == CriterioPontuacao.PLACAR_EXATO) {
                usuario.setPlacaresExatos(usuario.getPlacaresExatos() + 1);
            }

            palpite.setPontosObtidos(novo.getPontos());
            palpite.setCriterioAplicado(novo);

            palpiteRepository.save(palpite);
            usuarioRepository.save(usuario);
        }
    }
}
