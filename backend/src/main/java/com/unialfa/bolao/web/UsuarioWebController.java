package com.unialfa.bolao.web;

import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.security.UsuarioDetails;
import com.unialfa.bolao.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Listagem e bloqueio de usuarios no painel admin (RF-045, RF-046). */
@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioWebController {

    private final UsuarioService usuarioService;

    public UsuarioWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model) {
        Page<Usuario> page = usuarioService.listar(busca, PageRequest.of(pagina, 20));
        model.addAttribute("pagina", "usuarios");
        model.addAttribute("usuarios", page.getContent());
        model.addAttribute("busca", busca);
        model.addAttribute("paginaAtual", page.getNumber());
        model.addAttribute("totalPaginas", page.getTotalPages());
        return "admin/usuarios/lista";
    }

    @PostMapping("/{id}/bloqueio")
    public String alternarBloqueio(@PathVariable Long id, RedirectAttributes ra) {
        Usuario usuario = usuarioService.alternarBloqueio(id);
        ra.addFlashAttribute("sucesso",
                "Usuario " + (usuario.isBloqueado() ? "bloqueado" : "desbloqueado") + " com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/perfil")
    public String alternarPerfil(@PathVariable Long id,
                                 @AuthenticationPrincipal UsuarioDetails principal,
                                 RedirectAttributes ra) {
        // Impede que o admin logado altere o proprio perfil e se tranque para fora.
        if (principal != null && principal.getUsuario().getId().equals(id)) {
            ra.addFlashAttribute("erro", "Voce nao pode alterar o seu proprio perfil.");
            return "redirect:/admin/usuarios";
        }
        Usuario usuario = usuarioService.alternarPerfil(id);
        ra.addFlashAttribute("sucesso",
                usuario.getNome() + " agora tem o perfil " + usuario.getPerfil() + ".");
        return "redirect:/admin/usuarios";
    }
}
