package com.unialfa.bolao.web;

import com.unialfa.bolao.domain.Fase;
import com.unialfa.bolao.domain.Partida;
import com.unialfa.bolao.domain.Selecao;
import com.unialfa.bolao.exception.NegocioException;
import com.unialfa.bolao.service.PartidaService;
import com.unialfa.bolao.service.SelecaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/** CRUD de partidas e lancamento de resultado (RF-043, RF-044). */
@Controller
@RequestMapping("/admin/partidas")
public class PartidaWebController {

    private final PartidaService partidaService;
    private final SelecaoService selecaoService;

    public PartidaWebController(PartidaService partidaService, SelecaoService selecaoService) {
        this.partidaService = partidaService;
        this.selecaoService = selecaoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagina", "partidas");
        model.addAttribute("partidas", partidaService.listar());
        return "admin/partidas/lista";
    }

    @GetMapping("/nova")
    public String novaTela(Model model) {
        model.addAttribute("pagina", "partidas");
        model.addAttribute("partida", new Partida());
        prepararForm(model);
        return "admin/partidas/form";
    }

    @GetMapping("/{id}/editar")
    public String editarTela(@PathVariable Long id, Model model) {
        model.addAttribute("pagina", "partidas");
        model.addAttribute("partida", partidaService.buscarPorId(id));
        prepararForm(model);
        return "admin/partidas/form";
    }

    @PostMapping
    public String salvar(@RequestParam Long mandanteId,
                         @RequestParam Long visitanteId,
                         @RequestParam(required = false) Long id,
                         @RequestParam @org.springframework.format.annotation.DateTimeFormat(
                                 iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
                         LocalDateTime dataHora,
                         @RequestParam Fase fase,
                         @RequestParam(required = false) String estadio,
                         @RequestParam(required = false) String grupo,
                         RedirectAttributes ra) {
        try {
            Partida partida = (id != null) ? partidaService.buscarPorId(id) : new Partida();
            partida.setSelecaoMandante(selecaoService.buscarPorId(mandanteId));
            partida.setSelecaoVisitante(selecaoService.buscarPorId(visitanteId));
            partida.setDataHora(dataHora);
            partida.setFase(fase);
            partida.setEstadio(estadio);
            partida.setGrupo(grupo);
            partidaService.salvar(partida);
            ra.addFlashAttribute("sucesso", "Partida salva com sucesso.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/partidas";
    }

    /** Lancamento/correcao de resultado: dispara o (re)calculo da pontuacao. */
    @PostMapping("/{id}/resultado")
    public String lancarResultado(@PathVariable Long id,
                                  @RequestParam int golsMandante,
                                  @RequestParam int golsVisitante,
                                  RedirectAttributes ra) {
        try {
            partidaService.lancarResultado(id, golsMandante, golsVisitante);
            ra.addFlashAttribute("sucesso", "Resultado lancado e pontuacoes atualizadas.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/partidas";
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes ra) {
        try {
            partidaService.remover(id);
            ra.addFlashAttribute("sucesso", "Partida removida.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/partidas";
    }

    private void prepararForm(Model model) {
        model.addAttribute("selecoes", selecaoService.listar());
        model.addAttribute("fases", Fase.values());
    }
}
