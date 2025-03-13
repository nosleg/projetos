package com.example.controller;

import com.example.model.Conta;
import com.example.model.Usuario;
import com.example.repository.ContaRepository;
import com.example.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaRepository contaRepository;

    @GetMapping("/criar")
    public String mostrarFormularioCriacao(Model model) {
        
        model.addAttribute("usuarios", usuarioRepository.findAll());

        if (!model.containsAttribute("mensagem")) {
            model.addAttribute("mensagem", model.asMap().get("mensagem"));
        }

        return "criar-conta";
    }

    @PostMapping("/criar")
    public String criarUsuarioEConta(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String tipo,
            @RequestParam String numeroConta,
            Model model
    ) {
        
        if (usuarioRepository.findByEmail(email) != null) {
            model.addAttribute("erro", "Email já cadastrado.");
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "redirect:/contas/criar";
        }

       
        if (contaRepository.findByNumero(numeroConta) != null) {
            model.addAttribute("erro", "Número da conta já cadastrado.");
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "redirect:/contas/criar";
        }

        
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipo(tipo);

        
        Conta conta = new Conta();
        conta.setNumero(numeroConta);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setUsuario(usuario);

        
        usuario.setConta(conta);

        
        usuarioRepository.save(usuario);
        contaRepository.save(conta);

        model.addAttribute("mensagem", "Usuário e conta criados com sucesso!");
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "redirect:/contas/criar";
    }

    @PostMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id, Model model) {
        
        usuarioRepository.deleteById(id);
        model.addAttribute("mensagem", "Usuário e conta excluídos com sucesso!");
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "criar-conta";
    }
}