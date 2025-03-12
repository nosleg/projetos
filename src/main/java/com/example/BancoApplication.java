package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class BancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancoApplication.class, args);
    }

    /**
     * Bean de exemplo para exibir uma mensagem inicial.
     * Este método é opcional e pode ser removido se não for necessário.
     */
    @Bean
    public String mensagemInicial() {
        return "Bem-vindo ao Sistema Bancário!";
    }
}