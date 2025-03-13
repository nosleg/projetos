package com.example.service;

import com.example.model.Conta;
import com.example.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceImplTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private TransacaoServiceImpl transacaoService;

    private Conta conta;

    @BeforeEach
    void setUp() {
        // Configuração comum para os testes
        conta = new Conta();
        conta.setNumero("12345");
        conta.setSaldo(BigDecimal.valueOf(1000));
    }

    @Test
    void testCreditar() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(conta);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        transacaoService.creditar("12345", BigDecimal.valueOf(500));

        // Assert
        assertEquals(BigDecimal.valueOf(1500), conta.getSaldo());
        verify(contaRepository, times(1)).findByNumero("12345");
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testCreditar_ContaNaoEncontrada() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transacaoService.creditar("12345", BigDecimal.valueOf(500));
        });
        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    void testDebitar() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(conta);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        transacaoService.debitar("12345", BigDecimal.valueOf(500));

        // Assert
        assertEquals(BigDecimal.valueOf(500), conta.getSaldo());
        verify(contaRepository, times(1)).findByNumero("12345");
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testDebitar_ContaNaoEncontrada() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transacaoService.debitar("12345", BigDecimal.valueOf(500));
        });
        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    void testDebitar_SaldoInsuficiente() {
        // Arrange
        when(contaRepository.findByNumero("12345")).thenReturn(conta);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transacaoService.debitar("12345", BigDecimal.valueOf(1500));
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
        when(contaRepository.findByNumero(contaOrigem)).thenReturn(contaOrigemObj);
        when(contaRepository.findByNumero(contaDestino)).thenReturn(contaDestinoObj);
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transacaoService.transferir(contaOrigem, contaDestino, valor);

        // Assert
        assertEquals(BigDecimal.valueOf(500), contaOrigemObj.getSaldo()); // Saldo da conta origem
        assertEquals(BigDecimal.valueOf(1000), contaDestinoObj.getSaldo()); // Saldo da conta destino
        verify(contaRepository, times(1)).findByNumero(contaOrigem);
        verify(contaRepository, times(1)).findByNumero(contaDestino);
        verify(contaRepository, times(2)).save(any(Conta.class));
    }

    @Test
    void testTransferir_ContaOrigemNaoEncontrada() {
        // Arrange
        String contaOrigem = "12345";
        String contaDestino = "67890";
        BigDecimal valor = BigDecimal.valueOf(500);

        when(contaRepository.findByNumero(contaOrigem)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transacaoService.transferir(contaOrigem, contaDestino, valor);
        });
        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    void testTransferir_ContaDestinoNaoEncontrada() {
        // Arrange
        String contaOrigem = "12345";
        String contaDestino = "67890";
        BigDecimal valor = BigDecimal.valueOf(500);

        Conta contaOrigemObj = new Conta();
        contaOrigemObj.setNumero(contaOrigem);
        contaOrigemObj.setSaldo(BigDecimal.valueOf(1000));

        when(contaRepository.findByNumero(contaOrigem)).thenReturn(contaOrigemObj);
        when(contaRepository.findByNumero(contaDestino)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transacaoService.transferir(contaOrigem, contaDestino, valor);
        });
        assertEquals("Conta não encontrada.", exception.getMessage());
    }
}