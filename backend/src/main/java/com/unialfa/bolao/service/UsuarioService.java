package com.unialfa.bolao.service;

import com.unialfa.bolao.api.dto.AuthDtos.AtualizarPerfilRequest;
import com.unialfa.bolao.domain.Perfil;
import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.exception.RecursoNaoEncontradoException;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Operacoes de conta do usuario e gestao pelo admin (RF-004 a RF-006, RF-045, RF-046). */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));
    }

    @Transactional
    public Usuario atualizarPerfil(Usuario usuario, AtualizarPerfilRequest req) {
        usuario.setNome(req.nome().trim());
        usuario.setAvatarUrl(req.avatarUrl());
        return usuarioRepository.save(usuario);
    }

    /** Exclusao de conta com remocao definitiva (RF-006 / LGPD). */
    @Transactional
    public void excluirConta(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    // ----- Operacoes do painel admin -----

    public Page<Usuario> listar(String busca, Pageable pageable) {
        if (busca == null || busca.isBlank()) {
            return usuarioRepository.findAll(pageable);
        }
        return usuarioRepository
                .findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(busca, busca, pageable);
    }

    @Transactional
    public Usuario alternarBloqueio(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setBloqueado(!usuario.isBloqueado());
        return usuarioRepository.save(usuario);
    }

    /** Promove um USER a ADMIN ou rebaixa um ADMIN para USER. */
    @Transactional
    public Usuario alternarPerfil(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setPerfil(usuario.isAdmin() ? Perfil.USER : Perfil.ADMIN);
        return usuarioRepository.save(usuario);
    }
}
