package com.fontoura.projetointeligente.api.controllers;

import com.fontoura.projetointeligente.api.Response;
import com.fontoura.projetointeligente.api.dtos.FuncionarioDto;
import com.fontoura.projetointeligente.api.entities.Funcionario;
import com.fontoura.projetointeligente.api.services.FuncionarioService;
import com.fontoura.projetointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin("*")
public class FuncionarioController {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    public FuncionarioController() {}

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id,
                                                              @Valid @RequestBody FuncionarioDto funcionarioDto,
                                                              BindingResult result)
            throws NoSuchAlgorithmException{
        log.info("Atualizando funcionário: {}", funcionarioDto.toString());
        Response<FuncionarioDto> response = new Response<>();

        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
        if (!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionário não encontrado"));
        }

        this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);

        if (result.hasErrors()) {
            log.error("Erro validadno funcionário: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.funcionarioService.persistir(funcionario.get());
        response.setData(this.converterFuncionariDto(funcionario.get()));

        return ResponseEntity.ok(response);
    }

    private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto, BindingResult result)
            throws NoSuchAlgorithmException {
        funcionario.setNome(funcionarioDto.getNome());

        if (!funcionario.getEmail().equals(funcionario.getEmail())) {
            this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
                    .ifPresent(func -> result.addError(new ObjectError("Email", "Email já existente")));
            funcionario.setEmail(funcionario.getEmail());
        }

        funcionario.setQtdHorasAlmoco(null);
        funcionarioDto.getQtdHorasAlmoco()
                .ifPresent(qtdHoraAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHoraAlmoco)));

        funcionario.setQtdHorasTrabalhoDia(null);
        funcionarioDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhoDia((Float.valueOf(qtdHorasTrabDia))));

        funcionario.setValorHora(null);
        funcionarioDto.getValorHora()
                .ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        if (funcionarioDto.getSenha().isPresent()) {
            funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
        }
    }

    private FuncionarioDto converterFuncionariDto(Funcionario funcionario) {
        FuncionarioDto funcionarioDto = new FuncionarioDto();
        funcionarioDto.setId(funcionario.getId());
        funcionarioDto.setEmail(funcionario.getEmail());
        funcionarioDto.setNome(funcionario.getNome());
        funcionario.getQtdHorasAlmocoOpt().ifPresent(
                qtdHorasAlomoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlomoco)))
        );
        funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
                qtdHorasTrabDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia)))
        );
        funcionario.getValorHoraOpt().ifPresent(
                valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString()))
        );
        return funcionarioDto;
    }

}
