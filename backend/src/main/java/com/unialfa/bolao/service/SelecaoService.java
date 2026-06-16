package com.unialfa.bolao.service;

import com.unialfa.bolao.domain.Selecao;
import com.unialfa.bolao.exception.NegocioException;
import com.unialfa.bolao.exception.RecursoNaoEncontradoException;
import com.unialfa.bolao.repository.SelecaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD de selecoes (RF-041). */
@Service
public class SelecaoService {

    private final SelecaoRepository selecaoRepository;

    public SelecaoService(SelecaoRepository selecaoRepository) {
        this.selecaoRepository = selecaoRepository;
    }

    public List<Selecao> listar() {
        return selecaoRepository.findAllByOrderByNomeAsc();
    }

    public Selecao buscarPorId(Long id) {
        return selecaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Selecao nao encontrada."));
    }

    @Transactional
    public Selecao salvar(Selecao selecao) {
        String codigo = selecao.getCodigoFifa() == null ? "" : selecao.getCodigoFifa().toUpperCase();
        selecao.setCodigoFifa(codigo);

        boolean novo = selecao.getId() == null;
        if (novo && selecaoRepository.existsByCodigoFifaIgnoreCase(codigo)) {
            throw new NegocioException("Ja existe uma selecao com o codigo FIFA " + codigo + ".");
        }
        return selecaoRepository.save(selecao);
    }

    @Transactional
    public void remover(Long id) {
        Selecao selecao = buscarPorId(id);
        selecaoRepository.delete(selecao);
    }
}
