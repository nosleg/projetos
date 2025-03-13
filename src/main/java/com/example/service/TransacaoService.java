package com.example.service;

import java.math.BigDecimal;

public interface TransacaoService {
	void creditar(String numeroConta, BigDecimal valor);

	void debitar(String numeroConta, BigDecimal valor);

	void transferir(String contaOrigem, String contaDestino, BigDecimal valor);
}
