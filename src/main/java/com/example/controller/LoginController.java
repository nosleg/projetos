package com.example.controller;

import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String redirecionarParaLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; 
    }

    @PostMapping("/login")
    public String fazerLogin(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            Model model
    ) {
        
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            
            model.addAttribute("error", "Usuário não existe.");
            return "login"; 
        }

        if (!usuario.getSenha().equals(senha)) {
            
            model.addAttribute("error", "Senha incorreta.");
            return "login"; 
        }

        
        session.setAttribute("usuarioLogado", usuario);
        session.setAttribute("email", usuario.getEmail()); 
        session.setAttribute("tipo", usuario.getTipo()); 

        
        if ("ADMIN".equals(usuario.getTipo())) {
            return "redirect:/contas/criar"; 
        } else {
            return "redirect:/contas/transacoes"; 
        }
    }
    @GetMapping("/logout")
    public String fazerLogout(HttpSession session) {
        session.removeAttribute("usuarioLogado"); 
        session.removeAttribute("email"); 
        session.removeAttribute("tipo"); 
        return "redirect:/login"; 
    }
}