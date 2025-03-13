package com.example.service;

import com.example.model.Conta;
import com.example.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Override
    @Transactional
    public void creditar(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumero(numeroConta);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada.");
        }
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    @Override
    @Transactional
    public void debitar(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumero(numeroConta);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada.");
        }
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente.");
        }
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }

    @Override
    @Transactional
    public void transferir(String contaOrigem, String contaDestino, BigDecimal valor) {
        debitar(contaOrigem, valor);
        creditar(contaDestino, valor);
    }
}