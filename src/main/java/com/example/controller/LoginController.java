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
        return "login"; // Retorna a tela de login
    }

    @PostMapping("/login")
    public String fazerLogin(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            Model model
    ) {
        // Busca o usuário no banco de dados pelo email
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            // Usuário não existe
            model.addAttribute("error", "Usuário não existe.");
            return "login"; // Volta para a tela de login com mensagem de erro
        }

        if (!usuario.getSenha().equals(senha)) {
            // Senha incorreta
            model.addAttribute("error", "Senha incorreta.");
            return "login"; // Volta para a tela de login com mensagem de erro
        }

        // Autenticação bem-sucedida
        session.setAttribute("usuarioLogado", usuario);
        session.setAttribute("email", usuario.getEmail()); // Armazena o email na sessão
        session.setAttribute("tipo", usuario.getTipo()); // Armazena o tipo de usuário na sessão

        // Redireciona com base no tipo de usuário
        if ("ADMIN".equals(usuario.getTipo())) {
            return "redirect:/contas/criar"; // Redireciona para a tela de criar conta
        } else {
            return "redirect:/contas/transacoes"; // Redireciona para a tela de transações
        }
    }
    @GetMapping("/logout")
    public String fazerLogout(HttpSession session) {
        session.removeAttribute("usuarioLogado"); // Remove o usuário da sessão
        session.removeAttribute("email"); // Remove o email da sessão
        session.removeAttribute("tipo"); // Remove o tipo de usuário da sessão
        return "redirect:/login"; // Redireciona para a tela de login
    }
}