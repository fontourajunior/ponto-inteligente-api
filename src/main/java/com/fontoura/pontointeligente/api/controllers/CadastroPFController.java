package com.fontoura.pontointeligente.api.controllers;

import com.fontoura.pontointeligente.api.Response;
import com.fontoura.pontointeligente.api.dtos.CadastroPfDto;
import com.fontoura.pontointeligente.api.entities.Empresa;
import com.fontoura.pontointeligente.api.entities.Funcionario;
import com.fontoura.pontointeligente.api.enums.PerfilEnum;
import com.fontoura.pontointeligente.api.services.EmpresaService;
import com.fontoura.pontointeligente.api.services.FuncionarioService;
import com.fontoura.pontointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Controller
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FuncionarioService funcionarioService;

    public CadastroPFController() {}

    @PostMapping
    public ResponseEntity<Response<CadastroPfDto>> cadastrar(@Valid @RequestBody CadastroPfDto cadastroPfDto,
                                                             BindingResult result) throws NoSuchAlgorithmException {
        log.info("Cadastrando PF: {}", cadastroPfDto.toString());
        Response<CadastroPfDto> response = new Response<>();

        validarDadosExistentes(cadastroPfDto, result);
        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPfDto, result);

        if (result.hasErrors()) {
            log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPfDto.getCnpj());
        empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
        this.funcionarioService.persistir(funcionario);

        response.setData(this.converterCadastrPFDto(funcionario));
        return ResponseEntity.ok(response);
    }

    private void validarDadosExistentes(CadastroPfDto cadastroPfDto, BindingResult result) {
        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPfDto.getCnpj());
        if (!empresa.isPresent()) {
            result.addError(new ObjectError("empresa", "Empresa não cadastrada"));
        }

        this.funcionarioService.buscarPorCpf(cadastroPfDto.getCpf())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));

        this.funcionarioService.buscarPorEmail(cadastroPfDto.getEmail())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }

    private Funcionario converterDtoParaFuncionario(CadastroPfDto cadastroPfDto, BindingResult result)
        throws NoSuchAlgorithmException {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(cadastroPfDto.getNome());
        funcionario.setEmail(cadastroPfDto.getEmail());
        funcionario.setCpf(cadastroPfDto.getCpf());
        funcionario.setPerfil(PerfilEnum.RELE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPfDto.getSenha()));
        cadastroPfDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco ->funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
        cadastroPfDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabDia ->funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));
        cadastroPfDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
        return funcionario;
    }

    private CadastroPfDto converterCadastrPFDto(Funcionario funcionario) {
        CadastroPfDto cadastroPfDto = new CadastroPfDto();
        cadastroPfDto.setId(funcionario.getId());
        cadastroPfDto.setNome(funcionario.getNome());
        cadastroPfDto.setEmail(funcionario.getEmail());
        cadastroPfDto.setCpf(funcionario.getCpf());
        cadastroPfDto.setCnpj(funcionario.getEmpresa().getCnpj());
        funcionario.getQtdHorasAlmocoOpt().ifPresent(
                qtdHorasAlmoco -> cadastroPfDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
                qtdHorasTrabDia -> cadastroPfDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> cadastroPfDto.setValorHora(Optional.of(valorHora.toString())));

        return cadastroPfDto;
    }

}
