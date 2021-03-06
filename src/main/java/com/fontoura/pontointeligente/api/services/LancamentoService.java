package com.fontoura.pontointeligente.api.services;

import com.fontoura.pontointeligente.api.entities.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface LancamentoService {

    /**
     * Retorna uam lista paginada de lancamentos de um determinado funcionário
     * @param funcionarioId
     * @param pageRequest
     * @return Page<Lancamento>
     */
    Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest);

    /**
     * Retorna um lançamento por ID
     * @param id
     * @return Optional<Lancamento>
     */
    Optional<Lancamento> buscarPorId(Long id);

    /**
     * Persiste um lançamento na base de dados
     * @param lancamento
     * @return Lancamento
     */
    Lancamento persistir(Lancamento lancamento);

    /**
     * Remove um lançamento da base de dados.
     * @param id
     */
    void remover(Long id);

}
