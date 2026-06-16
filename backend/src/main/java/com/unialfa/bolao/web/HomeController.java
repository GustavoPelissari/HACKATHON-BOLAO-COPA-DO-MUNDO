package com.unialfa.bolao.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Redireciona a raiz para o painel administrativo. */
@Controller
public class HomeController {

    @GetMapping("/")
    public String raiz() {
        return "redirect:/admin/dashboard";
    }
}
