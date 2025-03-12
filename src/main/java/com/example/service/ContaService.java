package com.example.service;

import com.example.model.Conta;
import com.example.model.Transacao;
import com.example.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Transactional
    public Conta criarConta(String numero) {
        Conta conta = new Conta();
        conta.setNumero(numero);
        return contaRepository.save(conta);
    }

    @Transactional
    public void creditar(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumero(numeroConta);
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    @Transactional
    public void debitar(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumero(numeroConta);
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }

    @Transactional
    public void transferir(String contaOrigem, String contaDestino, BigDecimal valor) {
        debitar(contaOrigem, valor);
        creditar(contaDestino, valor);
    }



}