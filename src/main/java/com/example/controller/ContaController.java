package com.example.controller;

import com.example.model.Conta;
import com.example.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping("/criar")
    public String mostrarFormularioCriacao(Model model) {
        model.addAttribute("conta", new Conta());
        return "criar-conta";
    }

    @PostMapping("/criar")
    public String criarConta(@RequestParam String numero) {
        contaService.criarConta(numero);
        return "redirect:/contas";
    }

    @PostMapping("/creditar")
    public String creditar(@RequestParam String numeroConta, @RequestParam BigDecimal valor) {
        contaService.creditar(numeroConta, valor);
        return "redirect:/contas";
    }

    @PostMapping("/debitar")
    public String debitar(@RequestParam String numeroConta, @RequestParam BigDecimal valor) {
        contaService.debitar(numeroConta, valor);
        return "redirect:/contas";
    }

    @PostMapping("/transferir")
    public String transferir(@RequestParam String contaOrigem, @RequestParam String contaDestino, @RequestParam BigDecimal valor) {
        contaService.transferir(contaOrigem, contaDestino, valor);
        return "redirect:/contas";
    }
}