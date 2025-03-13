package com.example.service;

import com.example.model.Conta;
import com.example.model.Transacao;
import com.example.model.Usuario;
import com.example.repository.ContaRepository;
import com.example.repository.TransacaoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.repository.UsuarioRepository;
import org.springframework.data.jpa.repository.Lock;

import antlr.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {
	
	@Autowired
	private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void criarConta(String numero) {
    	
        if (contaRepository.findByNumero(numero) != null) {
            throw new RuntimeException("Já existe uma conta com esse número.");
        }

        Conta conta = new Conta();
        conta.setNumero(numero);
        contaRepository.save(conta);
    }
    
    public List<Conta> listarContas() {
        return contaRepository.findAll();
    }

    @Transactional
    public void creditar(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumeroWithLock(numeroConta);
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
        
        registrarTransacao(valor, "CREDITO", numeroConta, conta);
    }

    @Transactional
    public void debitar(String numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumeroWithLock(numeroConta);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada.");
        }
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente.");
        }
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
        
        registrarTransacao(valor, "DEBITO", numeroConta, conta);
    }

    @Transactional
    public void transferir(String contaOrigem, String contaDestino, BigDecimal valor) {

        Conta contaOrigemObj = contaRepository.findByNumeroWithLock(contaOrigem);
        if (contaOrigemObj == null) {
            throw new RuntimeException("Conta de origem não encontrada.");
        }
        Conta contaDestinoObj = contaRepository.findByNumeroWithLock(contaDestino);
        if (contaDestinoObj == null) {
            throw new RuntimeException("Conta de destino não encontrada.");
        }

        debitar(contaOrigem, valor);
        creditar(contaDestino, valor);
    }

    public Usuario atualizarUsuarioLogado(Usuario usuarioLogado) {
        Optional<Usuario> usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getId());
        return usuarioAtualizado.orElse(usuarioLogado);
    }
    
    private void registrarTransacao (BigDecimal valor, String tipo, String numeroConta, Conta conta ) {
    	
    	String mensagem;
    	
    	if(tipo == "CREDITO") {
    		mensagem = "Crédito na conta " + numeroConta;
    	}else {
    		mensagem = "Débito na conta " + numeroConta;
    	}
    	
        Transacao transacao = new Transacao();
        transacao.setTipo(tipo);
        transacao.setValor(valor);
        transacao.setData(LocalDateTime.now());
        transacao.setDescricao(mensagem);
        transacao.setConta(conta);
        transacaoRepository.save(transacao);
    }

}