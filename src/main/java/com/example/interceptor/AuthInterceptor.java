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

       
        if (usuarioLogado == null) {
            response.sendRedirect("/login"); 
            return false; 
        }

        
        String tipoUsuario = usuarioLogado.getTipo(); 

        
        String requestURI = request.getRequestURI();

        if ("ADMIN".equals(tipoUsuario) && requestURI.contains("/contas/transacoes")) {
            
            response.sendRedirect("/contas/criar");
            return false;
        }

        if ("CLIENTE".equals(tipoUsuario) && requestURI.contains("/contas/criar")) {
            
            response.sendRedirect("/contas/transacoes");
            return false;
        }

        return true; 
    }
}