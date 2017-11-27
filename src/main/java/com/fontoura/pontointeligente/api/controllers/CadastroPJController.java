package com.fontoura.pontointeligente.api.controllers;

import com.fontoura.pontointeligente.api.dtos.CadastroPjDto;
import com.fontoura.pontointeligente.api.services.FuncionarioService;
import com.fontoura.pontointeligente.api.Response;
import com.fontoura.pontointeligente.api.entities.Empresa;
import com.fontoura.pontointeligente.api.entities.Funcionario;
import com.fontoura.pontointeligente.api.enums.PerfilEnum;
import com.fontoura.pontointeligente.api.services.EmpresaService;
import com.fontoura.pontointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private EmpresaService empresaService;

    public CadastroPJController() {}

    /**
     * Cadastra uma pessoa jurídica no sistema
     *
     * @param cadastroPjDto
     * @param result
     * @return ResponseEntity<Response<CadastroPjDto>>
     * @throws java.security.NoSuchAlgorithmException
     */
     @PostMapping
    public ResponseEntity<Response<CadastroPjDto>> cadastrar(@Valid @RequestBody CadastroPjDto cadastroPjDto,
                                                             BindingResult result) throws NoSuchAlgorithmException {
         log.info("Cadastrando PJ: {}", cadastroPjDto.toString());
         Response<CadastroPjDto> response = new Response<>();

         validarDadosExistentes(cadastroPjDto, result);
         Empresa empresa = this.converterDtoParaEmpresa(cadastroPjDto);
         Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPjDto, result);

         if (result.hasErrors()) {
             log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
             result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
             return ResponseEntity.badRequest().body(response);
         }

         this.empresaService.persistir(empresa);
         funcionario.setEmpresa(empresa);
         this.funcionarioService.persistir(funcionario);

         response.setData(this.converterCadastroPjDto(funcionario));
         return ResponseEntity.ok(response);
     }

    private void validarDadosExistentes(CadastroPjDto cadastroPjDto, BindingResult result) {
        this.empresaService.buscarPorCnpj(cadastroPjDto.getCnpj())
                .ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente")));

        this.funcionarioService.buscarPorCpf(cadastroPjDto.getCpf())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));

        this.funcionarioService.buscarPorEmail(cadastroPjDto.getEmail())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }

    private Empresa converterDtoParaEmpresa(CadastroPjDto cadastroPjDto) {
         Empresa empresa = new Empresa();
         empresa.setCnpj(cadastroPjDto.getCnpj());
         empresa.setRazaoSocial(cadastroPjDto.getRazaoSocial());
         return empresa;
    }

    private Funcionario converterDtoParaFuncionario(CadastroPjDto cadastroPjDto, BindingResult result)
            throws NoSuchAlgorithmException{
         Funcionario funcionario = new Funcionario();
         funcionario.setNome(cadastroPjDto.getNome());
         funcionario.setEmail(cadastroPjDto.getEmail());
         funcionario.setCpf(cadastroPjDto.getCpf());
         funcionario.setPerfil(PerfilEnum.ROLE_AMDIN);
         funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPjDto.getSenha()));
         return funcionario;
    }

    private CadastroPjDto converterCadastroPjDto(Funcionario funcionario) {
         CadastroPjDto cadastroPjDto = new CadastroPjDto();
         cadastroPjDto.setId(funcionario.getId());
         cadastroPjDto.setNome(funcionario.getNome());
         cadastroPjDto.setEmail(funcionario.getEmail());
         cadastroPjDto.setCpf(funcionario.getCpf());
         cadastroPjDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
         cadastroPjDto.setCnpj(funcionario.getEmpresa().getCnpj());
         return cadastroPjDto;
    }

}
