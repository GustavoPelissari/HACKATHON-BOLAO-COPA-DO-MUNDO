package com.unialfa.bolao.web;

import com.unialfa.bolao.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Telas de login e dashboard do painel administrativo (RF-040, RF-047). */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("pagina", "dashboard");
        model.addAttribute("indicadores", dashboardService.indicadores());
        return "admin/dashboard";
    }
}
