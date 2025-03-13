package com.example.controller;

import com.example.model.Conta;
import com.example.service.ContaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {
	
    @Autowired
    private ContaService contaService;

    @GetMapping("/home")
    public String home(Model model) {
        List<Conta> contas = contaService.listarContas();
        model.addAttribute("contas", contas); 
        model.addAttribute("mensagem", "Bem-vindo ao Sistema Bancário!");
        return "home";
    }
}