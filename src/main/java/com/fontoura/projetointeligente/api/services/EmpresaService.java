package com.fontoura.projetointeligente.api.services;

import com.fontoura.projetointeligente.api.entities.Empresa;

import java.util.Optional;

public interface EmpresaService {

    /**
     * Retorna uma empresa dado um CNPJ
     * @param cnpj
     * @return Optional<Empresa>
     */
    Optional<Empresa> buscarPorCnpj(String cnpj);

    /**
     * Cadastra uma nvoa empresa na base da dados
     * @param empresa
     * @return Empresa
     */
    Empresa persistir(Empresa empresa);

}
