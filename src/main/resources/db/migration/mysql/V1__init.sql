CREATE TABLE `ponto_inteligente`.`empresa` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `cnpj` VARCHAR(255) NOT NULL,
  `data_atualizacao` DATETIME NOT NULL,
  `data_criacao` DATETIME NOT NULL,
  `razao_social` VARCHAR(255) NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `ponto_inteligente`.`funcionario` (
  `id` BIGINT(20) NOT NULL,
  `cpf` VARCHAR(255) NOT NULL,
  `data_atualizacao` DATETIME NOT NULL,
  `data_criacao` DATETIME NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `nome` VARCHAR(255) NOT NULL,
  `perfil` VARCHAR(255) NOT NULL,
  `qtd_horas_almoco` FLOAT NULL DEFAULT NULL,
  `qtd_horas_trabalho_dia` FLOAT NULL DEFAULT NULL,
  `senha` VARCHAR(255) NOT NULL,
  `valor_hora` DECIMAL(19,2) NULL DEFAULT NULL,
  `empresa_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `ponto_inteligente`.`lancamento` (
  `id` BIGINT(20) NOT NULL,
  `data` DATETIME NOT NULL,
  `data_atualizacao` DATETIME NOT NULL,
  `data_criacao` DATETIME NOT NULL,
  `descricao` VARCHAR(255) NULL DEFAULT NULL,
  `localizacao` VARCHAR(255) NULL DEFAULT NULL,
  `tipo` VARCHAR(45) NOT NULL,
  `funcionario_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `funcionario`
    ADD CONSTRAINT `FK4cm1kg523jlopyexjbi6y54j` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`);

ALTER TABLE `lancamento`
    ADD CONSTRAINT `FK46i4k5vl8wah7feutwe9kbpi4` FOREIGN KEY (`funcionario_id`) REFERENCES `funcionario` (`id`);
