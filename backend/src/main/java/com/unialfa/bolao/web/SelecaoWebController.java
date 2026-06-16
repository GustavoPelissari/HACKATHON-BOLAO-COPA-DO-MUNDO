package com.unialfa.bolao.web;

import com.unialfa.bolao.domain.Selecao;
import com.unialfa.bolao.exception.NegocioException;
import com.unialfa.bolao.service.SelecaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CRUD de selecoes no painel admin (RF-041). */
@Controller
@RequestMapping("/admin/selecoes")
public class SelecaoWebController {

    private final SelecaoService selecaoService;

    public SelecaoWebController(SelecaoService selecaoService) {
        this.selecaoService = selecaoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagina", "selecoes");
        model.addAttribute("selecoes", selecaoService.listar());
        return "admin/selecoes/lista";
    }

    @GetMapping("/nova")
    public String novaTela(Model model) {
        model.addAttribute("pagina", "selecoes");
        model.addAttribute("selecao", new Selecao());
        return "admin/selecoes/form";
    }

    @GetMapping("/{id}/editar")
    public String editarTela(@PathVariable Long id, Model model) {
        model.addAttribute("pagina", "selecoes");
        model.addAttribute("selecao", selecaoService.buscarPorId(id));
        return "admin/selecoes/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Selecao selecao, RedirectAttributes ra) {
        try {
            selecaoService.salvar(selecao);
            ra.addFlashAttribute("sucesso", "Selecao salva com sucesso.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/selecoes";
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes ra) {
        try {
            selecaoService.remover(id);
            ra.addFlashAttribute("sucesso", "Selecao removida.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Nao foi possivel remover (pode haver partidas vinculadas).");
        }
        return "redirect:/admin/selecoes";
    }
}
