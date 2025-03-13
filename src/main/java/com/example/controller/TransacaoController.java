package com.example.controller;

import com.example.model.Usuario;
import com.example.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.model.Transacao;
import com.example.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
public class TransacaoController {

    private final ContaService contaService;
    
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    public TransacaoController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/contas/transacoes")
    public String mostrarTransacoes(Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

		
        List<Transacao> transacoes = transacaoRepository.findByContaNumero(usuarioLogado.getConta().getNumero());
        model.addAttribute("transacoes", transacoes);

        model.addAttribute("usuarioLogado", usuarioLogado);
        return "transacoes";
    }

    @PostMapping("/contas/creditar")
    public String creditar(
            @RequestParam String numeroConta,
            @RequestParam BigDecimal valor,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("erro", "O valor deve ser positivo.");
                return "redirect:/contas/transacoes";
            }

           
            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            if (usuarioLogado == null) {
                return "redirect:/login";
            }
            if (!usuarioLogado.getConta().getNumero().equals(numeroConta)) {
                redirectAttributes.addFlashAttribute("erro", "Você só pode creditar na sua própria conta.");
                return "redirect:/contas/transacoes";
            }

            contaService.creditar(numeroConta, valor);
            redirectAttributes.addFlashAttribute("mensagem", "Crédito realizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao creditar: " + e.getMessage());
        }
        return "redirect:/contas/transacoes";
    }

    @PostMapping("/contas/debitar")
    public String debitar(
            @RequestParam String numeroConta,
            @RequestParam BigDecimal valor,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
           
            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            if (usuarioLogado == null) {
                return "redirect:/login";
            }

            
            if (!usuarioLogado.getConta().getNumero().equals(numeroConta)) {
                redirectAttributes.addFlashAttribute("erro", "Você só pode debitar da sua própria conta.");
                return "redirect:/contas/transacoes";
            }

            
            contaService.debitar(numeroConta, valor);

            
            usuarioLogado = contaService.atualizarUsuarioLogado(usuarioLogado);
            session.setAttribute("usuarioLogado", usuarioLogado);

            redirectAttributes.addFlashAttribute("mensagem", "Valor debitado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao debitar: " + e.getMessage());
        }
        return "redirect:/contas/transacoes";
    }

    @PostMapping("/contas/transferir")
    public String transferir(
            @RequestParam String contaOrigem,
            @RequestParam String contaDestino,
            @RequestParam BigDecimal valor,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            
            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            if (usuarioLogado == null) {
                return "redirect:/login";
            }

            
            if (!usuarioLogado.getConta().getNumero().equals(contaOrigem)) {
                redirectAttributes.addFlashAttribute("erro", "Você só pode transferir da sua própria conta.");
                return "redirect:/contas/transacoes";
            }

            
            contaService.transferir(contaOrigem, contaDestino, valor);

            
            usuarioLogado = contaService.atualizarUsuarioLogado(usuarioLogado);
            session.setAttribute("usuarioLogado", usuarioLogado);

            redirectAttributes.addFlashAttribute("mensagem", "Transferência realizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao transferir: " + e.getMessage());
        }
        return "redirect:/contas/transacoes";
    }
}