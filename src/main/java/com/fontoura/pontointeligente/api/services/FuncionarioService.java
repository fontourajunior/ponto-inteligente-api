package com.fontoura.pontointeligente.api.services;

import com.fontoura.pontointeligente.api.entities.Funcionario;

import java.util.Optional;

public interface FuncionarioService {

    /**
     * Persiste um funcionário na base de dados
     * @param funcionario
     * @return Funcionario
     */
    Funcionario persistir(Funcionario funcionario);

    /**
     * Busca e retorna um funcionário dado um CPF
     * @param cpf
     * @return Optinal<Funcionario>
     */
    Optional<Funcionario> buscarPorCpf(String cpf);

    /**
     * Buscar e retorno uma funcionario dado um email
     * @param email
     * @return
     */
    Optional<Funcionario> buscarPorEmail(String email);

    /**
     * Busca e retorna um funcionário por ID
     * @param id
     * @return Optional<funcionario>
     */
    Optional<Funcionario> buscarPorId(Long id);

}
