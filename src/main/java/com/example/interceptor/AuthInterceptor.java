package com.example.interceptor;

import com.example.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Verifica se o usuário está logado
        if (usuarioLogado == null) {
            response.sendRedirect("/login"); // Redireciona para a tela de login
            return false; // Interrompe a execução do controller
        }

        // Obtém o tipo do usuário diretamente do objeto `usuarioLogado`
        String tipoUsuario = usuarioLogado.getTipo(); // Supondo que o tipo do usuário está no campo `tipo`

        // Restringe o acesso com base no tipo de usuário
        String requestURI = request.getRequestURI();

        if ("ADMIN".equals(tipoUsuario) && requestURI.contains("/contas/transacoes")) {
            // ADMIN não pode acessar a tela de transações
            response.sendRedirect("/contas/criar");
            return false;
        }

        if ("CLIENTE".equals(tipoUsuario) && requestURI.contains("/contas/criar")) {
            // CLIENTE não pode acessar a tela de criar conta
            response.sendRedirect("/contas/transacoes");
            return false;
        }

        return true; // Permite a execução do controller
    }
}