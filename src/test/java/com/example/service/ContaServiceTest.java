package com.example.service;

import com.example.model.Conta;
import com.example.model.Transacao;
import com.example.model.Usuario;
import com.example.repository.ContaRepository;
import com.example.repository.TransacaoRepository;
import com.example.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private ContaService contaService;

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta();
        conta.setNumero("12345");
        conta.setSaldo(BigDecimal.valueOf(1000));

    }


    @Test
    void testCriarConta() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(null);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        contaService.criarConta("12345");

        // Assert
        verify(contaRepository, times(1)).findByNumero("12345");
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testCriarConta_ContaExistente() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(conta);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contaService.criarConta("12345");
        });
        assertEquals("Já existe uma conta com esse número.", exception.getMessage());
    }

    @Test
    void testListarContas() {
        // Arrange
        when(contaRepository.findAll()).thenReturn(Arrays.asList(conta));

        // Act
        List<Conta> contas = contaService.listarContas();

        // Assert
        assertEquals(1, contas.size());
        assertEquals("12345", contas.get(0).getNumero());
        verify(contaRepository, times(1)).findAll();
    }

    @Test
    void testCreditar() {
        // Arrange
        when(contaRepository.findByNumeroWithLock("12345")).thenReturn(conta);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        contaService.creditar("12345", BigDecimal.valueOf(500));

        // Assert
        assertEquals(BigDecimal.valueOf(1500), conta.getSaldo());
        verify(contaRepository, times(1)).findByNumeroWithLock("12345");
        verify(contaRepository, times(1)).save(any(Conta.class));
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    void testDebitar() {
        // Arrange
        when(contaRepository.findByNumeroWithLock("12345")).thenReturn(conta);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        contaService.debitar("12345", BigDecimal.valueOf(500));

        // Assert
        assertEquals(BigDecimal.valueOf(500), conta.getSaldo());
        verify(contaRepository, times(1)).findByNumeroWithLock("12345");
        verify(contaRepository, times(1)).save(any(Conta.class));
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    void testDebitar_SaldoInsuficiente() {
        // Arrange
        when(contaRepository.findByNumeroWithLock("12345")).thenReturn(conta);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contaService.debitar("12345", BigDecimal.valueOf(1500));
        });
        assertEquals("Saldo insuficiente.", exception.getMessage());
    }
    
    @Test
    void testTransferir() {
        // Arrange
        String contaOrigem = "12345";
        String contaDestino = "67890";
        BigDecimal valor = BigDecimal.valueOf(500);

        // Configuração do mock para a contaOrigem
        Conta contaOrigemObj = new Conta();
        contaOrigemObj.setNumero(contaOrigem);
        contaOrigemObj.setSaldo(BigDecimal.valueOf(1000));

        // Configuração do mock para a contaDestino
        Conta contaDestinoObj = new Conta();
        contaDestinoObj.setNumero(contaDestino);
        contaDestinoObj.setSaldo(BigDecimal.valueOf(500));

        // Mockando o comportamento do contaRepository
        when(contaRepository.findByNumeroWithLock(contaOrigem)).thenReturn(contaOrigemObj);
        when(contaRepository.findByNumeroWithLock(contaDestino)).thenReturn(contaDestinoObj);

        // Mockando o comportamento do save
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        contaService.transferir(contaOrigem, contaDestino, valor);

        // Assert
        // Verifica se o saldo da contaOrigem foi atualizado corretamente
        assertEquals(BigDecimal.valueOf(500), contaOrigemObj.getSaldo());

        // Verifica se o saldo da contaDestino foi atualizado corretamente
        assertEquals(BigDecimal.valueOf(1000), contaDestinoObj.getSaldo());

        // Verifica se não há interações extras com o contaRepository
        verifyNoMoreInteractions(contaRepository);
    }

    @Test
    void testAtualizarUsuarioLogado() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Usuario usuarioAtualizado = contaService.atualizarUsuarioLogado(usuario);

        // Assert
        assertEquals("João", usuarioAtualizado.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }
}