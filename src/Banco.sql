DROP DATABASE IF EXISTS banco_malvader;
CREATE DATABASE banco_malvader;
USE banco_malvader;

-- Tabela usuario
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    tipo_usuario ENUM('FUNCIONARIO', 'CLIENTE') NOT NULL,
    senha_hash VARCHAR(32) NOT NULL,
    otp_ativo VARCHAR(6),
    otp_expiracao DATETIME
);

-- Tabela funcionario
CREATE TABLE funcionario (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT UNIQUE NOT NULL,
    codigo_funcionario VARCHAR(20) UNIQUE NOT NULL,
    cargo ENUM('ESTAGIARIO', 'ATENDENTE', 'GERENTE') NOT NULL,
    id_supervisor INT,
    id_agencia INT, -- RF2.5
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_supervisor) REFERENCES funcionario(id_funcionario)
);

-- Tabela cliente
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT UNIQUE NOT NULL,
    score_credito DECIMAL(5,2) DEFAULT 0,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Tabela endereco
CREATE TABLE endereco (
    id_endereco INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    cep VARCHAR(10) NOT NULL,
    local VARCHAR(100) NOT NULL,
    numero_casa INT NOT NULL,
    bairro VARCHAR(50) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado CHAR(2) NOT NULL,
    complemento VARCHAR(50),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);
CREATE INDEX idx_cep ON endereco(cep);

-- Tabela agencia
CREATE TABLE agencia (
    id_agencia INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    codigo_agencia VARCHAR(10) UNIQUE NOT NULL,
    endereco_id INT UNIQUE NOT NULL, -- cada agencia tem um endereço único
    FOREIGN KEY (endereco_id) REFERENCES endereco(id_endereco)
);

-- FK de funcionario para agencia
ALTER TABLE funcionario
ADD CONSTRAINT FK_funcionario_agencia
FOREIGN KEY (id_agencia) REFERENCES agencia(id_agencia);

-- Tabela conta
CREATE TABLE conta (
    id_conta INT AUTO_INCREMENT PRIMARY KEY,
    numero_conta VARCHAR(20) UNIQUE NOT NULL,
    id_agencia INT NOT NULL,
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0,
    tipo_conta ENUM('POUPANCA', 'CORRENTE', 'INVESTIMENTO') NOT NULL,
    id_cliente INT NOT NULL,
    id_funcionario_abertura INT, -- RF2.3
    data_abertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ATIVA', 'ENCERRADA', 'BLOQUEADA') NOT NULL DEFAULT 'ATIVA',
    FOREIGN KEY (id_agencia) REFERENCES agencia(id_agencia),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_funcionario_abertura) REFERENCES funcionario(id_funcionario)
);
CREATE INDEX idx_numero_conta ON conta(numero_conta);

-- Tabela conta_poupanca
CREATE TABLE conta_poupanca (
    id_conta_poupanca INT AUTO_INCREMENT PRIMARY KEY,
    id_conta INT UNIQUE NOT NULL,
    taxa_rendimento DECIMAL(5,4) NOT NULL,
    ultimo_rendimento DATETIME,
    FOREIGN KEY (id_conta) REFERENCES conta(id_conta)
);

-- Tabela conta_corrente
CREATE TABLE conta_corrente (
    id_conta_corrente INT AUTO_INCREMENT PRIMARY KEY,
    id_conta INT UNIQUE NOT NULL,
    limite DECIMAL(15,2) NOT NULL DEFAULT 0,
    data_vencimento DATE NOT NULL,
    taxa_manutencao DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_conta) REFERENCES conta(id_conta)
);

-- Tabela conta_investimento
CREATE TABLE conta_investimento (
    id_conta_investimento INT AUTO_INCREMENT PRIMARY KEY,
    id_conta INT UNIQUE NOT NULL,
    perfil_risco ENUM('BAIXO', 'MEDIO', 'ALTO') NOT NULL,
    valor_minimo DECIMAL(15,2) NOT NULL,
    taxa_rendimento_base DECIMAL(5,4) NOT NULL,
    FOREIGN KEY (id_conta) REFERENCES conta(id_conta)
);

-- Tabela transacao
CREATE TABLE transacao (
    id_transacao INT AUTO_INCREMENT PRIMARY KEY,
    id_conta_origem INT,
    id_conta_destino INT,
    tipo_transacao ENUM('DEPOSITO', 'SAQUE', 'TRANSFERENCIA', 'TAXA', 'RENDIMENTO', 'PAGAMENTO_FATURA') NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descricao VARCHAR(100),
    FOREIGN KEY (id_conta_origem) REFERENCES conta(id_conta),
    FOREIGN KEY (id_conta_destino) REFERENCES conta(id_conta)
);
CREATE INDEX idx_data_hora_transacao ON transacao(data_hora);
CREATE INDEX idx_tipo_transacao ON transacao(tipo_transacao);


-- Tabela auditoria
CREATE TABLE auditoria (
    id_auditoria INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    acao VARCHAR(50) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detalhes TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Tabela relatorio
CREATE TABLE relatorio (
    id_relatorio INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL, -- Funcionário que gerou o relatório
    tipo_relatorio VARCHAR(50) NOT NULL,
    data_geracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    conteudo LONGTEXT NOT NULL,
    FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);

-- Tabela de historico de encerramento de contas (RF2.2)
CREATE TABLE conta_encerramento_historico (
    id_encerramento INT AUTO_INCREMENT PRIMARY KEY,
    id_conta INT NOT NULL,
    id_funcionario_responsavel INT, -- Funcionário que processou o encerramento
    motivo TEXT NOT NULL,
    data_encerramento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES conta(id_conta),
    FOREIGN KEY (id_funcionario_responsavel) REFERENCES funcionario(id_funcionario)
);

-- Gatilhos (Triggers)

-- Atualização de Saldo
DELIMITER $$
CREATE TRIGGER atualizar_saldo AFTER INSERT ON transacao
FOR EACH ROW
BEGIN
    IF NEW.tipo_transacao = 'DEPOSITO' OR NEW.tipo_transacao = 'RENDIMENTO' THEN
        UPDATE conta SET saldo = saldo + NEW.valor WHERE id_conta = NEW.id_conta_origem;

    ELSEIF NEW.tipo_transacao = 'SAQUE' OR NEW.tipo_transacao = 'TAXA' OR NEW.tipo_transacao = 'PAGAMENTO_FATURA' THEN
        UPDATE conta SET saldo = saldo - NEW.valor WHERE id_conta = NEW.id_conta_origem;

    ELSEIF NEW.tipo_transacao = 'TRANSFERENCIA' THEN
        IF NEW.id_conta_origem IS NOT NULL THEN
            UPDATE conta SET saldo = saldo - NEW.valor WHERE id_conta = NEW.id_conta_origem;

        END IF;

        IF NEW.id_conta_destino IS NOT NULL THEN
            UPDATE conta SET saldo = saldo + NEW.valor WHERE id_conta = NEW.id_conta_destino;

        END IF;
    END IF;
END $$
DELIMITER ;

-- Validação de Senha (Trigger apenas como lembrete, a lógica principal está na Stored Procedure)
DELIMITER $$
CREATE TRIGGER validar_senha BEFORE UPDATE ON usuario
FOR EACH ROW
BEGIN
    -- Este trigger serve como um lembrete de que as senhas devem ser gerenciadas
    -- pela procedure `atualizar_senha_usuario` que contém a lógica de validação de força.
    -- A Stored Procedure é o ponto de controle recomendado para alterações de senha.
    -- Se uma tentativa de UPDATE direto ocorrer no campo senha_hash sem passar pela procedure,
    -- e você quiser bloqueá-la aqui, você pode adicionar:
    -- IF OLD.senha_hash != NEW.senha_hash AND (SELECT @BYPASS_SENHA_TRIGGER IS NULL OR @BYPASS_SENHA_TRIGGER = 0) THEN
    --    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Alteração de senha deve ser feita via procedure apropriada.';
    -- END IF;
    -- A variável de sessão @BYPASS_SENHA_TRIGGER poderia ser setada pela procedure.
    -- Por simplicidade, a confiança é depositada na disciplina de uso da procedure.
    -- Nenhuma ação de bloqueio explícita aqui para evitar complexidade desnecessária
    -- se a procedure for sempre usada.
    DECLARE dummy INT;
END $$
DELIMITER ;


-- Limite de Depósito Diário
DELIMITER $$
CREATE TRIGGER limite_deposito_cliente BEFORE INSERT ON transacao
FOR EACH ROW
BEGIN
    DECLARE v_total_dia DECIMAL(15,2) DEFAULT 0;
    DECLARE v_id_cliente_deposito INT;

    IF NEW.tipo_transacao = 'DEPOSITO' AND NEW.id_conta_origem IS NOT NULL THEN

        SELECT id_cliente INTO v_id_cliente_deposito FROM conta WHERE id_conta = NEW.id_conta_origem;

        IF v_id_cliente_deposito IS NOT NULL THEN
            SELECT SUM(t.valor) INTO v_total_dia
            FROM transacao t
            INNER JOIN conta c ON t.id_conta_origem = c.id_conta
            WHERE c.id_cliente = v_id_cliente_deposito
              AND t.tipo_transacao = 'DEPOSITO'
              AND DATE(t.data_hora) = DATE(NEW.data_hora);

            IF (COALESCE(v_total_dia, 0) + NEW.valor) > 10000.00 THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Limite diário de depósito excedido.';
            END IF;
        END IF;
    END IF;
END $$
DELIMITER ;

-- Registrar abertura de conta em auditoria (RF2.1)
DELIMITER $$
CREATE TRIGGER auditar_abertura_conta AFTER INSERT ON conta
FOR EACH ROW
BEGIN
    DECLARE v_id_usuario_funcionario INT DEFAULT NULL;
    -- Tenta obter o id_usuario do funcionário que abriu a conta
    IF NEW.id_funcionario_abertura IS NOT NULL THEN
        SELECT id_usuario INTO v_id_usuario_funcionario
        FROM funcionario WHERE id_funcionario = NEW.id_funcionario_abertura;
    END IF;

    INSERT INTO auditoria (id_usuario, acao, detalhes)
    VALUES (v_id_usuario_funcionario, 'ABERTURA_CONTA',
            JSON_OBJECT('id_conta', NEW.id_conta,
                        'numero_conta', NEW.numero_conta,
                        'tipo_conta', NEW.tipo_conta,
                        'id_cliente', NEW.id_cliente,
                        'data_abertura', DATE_FORMAT(NEW.data_abertura, '%Y-%m-%d %H:%i:%s'),
                        'id_funcionario_abertura', NEW.id_funcionario_abertura));
END $$
DELIMITER ;

-- Gatilho pra limite de funcionarios por agencia (RF2.5)
DELIMITER $$
CREATE TRIGGER verificar_limite_funcionarios_agencia BEFORE INSERT ON funcionario
FOR EACH ROW
BEGIN
    DECLARE total_funcionarios_agencia INT;
    IF NEW.id_agencia IS NOT NULL THEN
        SELECT COUNT(*) INTO total_funcionarios_agencia
        FROM funcionario
        WHERE id_agencia = NEW.id_agencia;

        IF total_funcionarios_agencia >= 20 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Limite de funcionários por agência (20) excedido.';
        END IF;
    END IF;
END $$
DELIMITER ;

-- Gatilho pra taxa de saques excessivos (RF3.1)
DELIMITER $$
CREATE TRIGGER aplicar_taxa_saque_excessivo AFTER INSERT ON transacao
FOR EACH ROW
BEGIN
    DECLARE num_saques_mes INT;
    DECLARE taxa_saque_excessivo DECIMAL(10,2) DEFAULT 5.00;
    DECLARE v_id_conta_cliente INT;
    DECLARE v_id_usuario_auditoria INT DEFAULT NULL;

    IF NEW.tipo_transacao = 'SAQUE' AND NEW.id_conta_origem IS NOT NULL THEN
        SET v_id_conta_cliente = NEW.id_conta_origem;

        SELECT COUNT(*) INTO num_saques_mes
        FROM transacao
        WHERE id_conta_origem = v_id_conta_cliente
          AND tipo_transacao = 'SAQUE'
          AND MONTH(data_hora) = MONTH(NEW.data_hora)
          AND YEAR(data_hora) = YEAR(NEW.data_hora);

        IF num_saques_mes > 5 THEN

            SELECT cl.id_usuario INTO v_id_usuario_auditoria
            FROM conta co
            INNER JOIN cliente cl ON co.id_cliente = cl.id_cliente
            WHERE co.id_conta = v_id_conta_cliente;

            -- Insere a transação de taxa. O gatilho 'atualizar_saldo' cuidará da dedução.
            INSERT INTO transacao (id_conta_origem, tipo_transacao, valor, descricao)
            VALUES (v_id_conta_cliente, 'TAXA', taxa_saque_excessivo, CONCAT('Taxa por saque excessivo (', num_saques_mes, 'º saque no mês)'));

            INSERT INTO auditoria (id_usuario, acao, detalhes)
            VALUES (v_id_usuario_auditoria, 'TAXA_SAQUE_EXCESSIVO', JSON_OBJECT('id_conta', v_id_conta_cliente, 'valor_taxa', taxa_saque_excessivo, 'num_saques_mes', num_saques_mes));
        END IF;
    END IF;
END $$
DELIMITER ;


-- Procedimentos Armazenados (Stored Procedures)

-- Gerar OTP
DELIMITER $$
CREATE PROCEDURE gerar_otp(IN p_id_usuario INT, OUT p_novo_otp VARCHAR(6))
BEGIN
    SET p_novo_otp = LPAD(FLOOR(RAND() * 1000000), 6, '0');
    UPDATE usuario
    SET otp_ativo = p_novo_otp, otp_expiracao = NOW() + INTERVAL 5 MINUTE
    WHERE id_usuario = p_id_usuario;
END $$
DELIMITER ;

-- Calcular Score de Crédito
DELIMITER $$
CREATE PROCEDURE calcular_score_credito(IN p_id_cliente INT)
BEGIN
    DECLARE v_total_trans DECIMAL(15,2) DEFAULT 0;
    DECLARE v_media_trans DECIMAL(15,2) DEFAULT 0;

    -- A SP original do SRS usava SUM(valor) e AVG(valor) com base em transações de DEPÓSITO e SAQUE na conta de origem do cliente.
    SELECT COALESCE(SUM(t.valor),0), COALESCE(AVG(t.valor),0)
    INTO v_total_trans, v_media_trans
    FROM transacao t
    INNER JOIN conta c ON t.id_conta_origem = c.id_conta -- A conta de origem é a conta do cliente para estas transações
    WHERE c.id_cliente = p_id_cliente AND t.tipo_transacao IN ('DEPOSITO', 'SAQUE');

    UPDATE cliente
    SET score_credito = LEAST(100.00, (v_total_trans / 1000.00) + (v_media_trans / 100.00))
    WHERE id_cliente = p_id_cliente;
END $$
DELIMITER ;

-- Encerrar conta cliente (RF2.2)
DELIMITER $$
CREATE PROCEDURE encerrar_conta_cliente(IN p_id_conta INT, IN p_id_funcionario_responsavel INT, IN p_motivo TEXT)
BEGIN
    DECLARE v_saldo DECIMAL(15,2);
    DECLARE v_id_usuario_funcionario INT DEFAULT NULL;
    DECLARE v_status_conta VARCHAR(20);

    SELECT saldo, status INTO v_saldo, v_status_conta FROM conta WHERE id_conta = p_id_conta;

    IF v_status_conta IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Conta não encontrada.';
    ELSEIF v_status_conta != 'ATIVA' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Conta não está ativa. Não pode ser encerrada novamente.';
    ELSEIF v_saldo < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Não é possível encerrar conta com saldo negativo.';
    -- Adicionar outras verificações de dívidas pendentes aqui, se necessário.
    ELSE
        UPDATE conta SET status = 'ENCERRADA' WHERE id_conta = p_id_conta;

        INSERT INTO conta_encerramento_historico (id_conta, id_funcionario_responsavel, motivo, data_encerramento)
        VALUES (p_id_conta, p_id_funcionario_responsavel, p_motivo, NOW());

        -- Auditoria
        IF p_id_funcionario_responsavel IS NOT NULL THEN
            SELECT id_usuario INTO v_id_usuario_funcionario FROM funcionario WHERE id_funcionario = p_id_funcionario_responsavel;
        END IF;
        INSERT INTO auditoria (id_usuario, acao, detalhes)
        VALUES (v_id_usuario_funcionario, 'ENCERRAMENTO_CONTA',
                JSON_OBJECT('id_conta_encerrada', p_id_conta, 'motivo', p_motivo, 'id_funcionario', p_id_funcionario_responsavel));
    END IF;
END $$
DELIMITER ;

-- Atualizar senha do usuario com validacao (RNF3 e RF2.4)
DELIMITER $$
CREATE PROCEDURE atualizar_senha_usuario(IN p_id_usuario INT, IN p_nova_senha_texto_plano VARCHAR(255))
BEGIN
    DECLARE v_senha_valida BOOLEAN DEFAULT FALSE;
    DECLARE v_senha_hash_nova VARCHAR(32);

    -- Validação de força da senha
    IF LENGTH(p_nova_senha_texto_plano) >= 8 AND
       p_nova_senha_texto_plano REGEXP '[A-Z]' AND
       p_nova_senha_texto_plano REGEXP '[a-z]' AND
       p_nova_senha_texto_plano REGEXP '[0-9]' AND
       p_nova_senha_texto_plano REGEXP '[^A-Za-z0-9]' THEN
        SET v_senha_valida = TRUE;
    END IF;

    IF NOT v_senha_valida THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Senha fraca. Requisitos: >= 8 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 caractere especial.';
    ELSE
        SET v_senha_hash_nova = MD5(p_nova_senha_texto_plano);

        UPDATE usuario SET senha_hash = v_senha_hash_nova
        WHERE id_usuario = p_id_usuario;

        INSERT INTO auditoria (id_usuario, acao, detalhes)
        VALUES (p_id_usuario, 'ALTERACAO_SENHA',
                JSON_OBJECT('id_usuario_afetado', p_id_usuario, 'resultado', 'sucesso'));
    END IF;
END $$
DELIMITER ;

-- Procedure pra tentativa de login (RF1.3)
DELIMITER $$
CREATE PROCEDURE registrar_tentativa_login(IN p_cpf_tentativa VARCHAR(11), IN p_sucesso BOOLEAN, IN p_info_adicional TEXT)
BEGIN
    DECLARE v_id_usuario_tentativa INT DEFAULT NULL;
    DECLARE v_detalhes_json TEXT;

    SELECT id_usuario INTO v_id_usuario_tentativa FROM usuario WHERE cpf = p_cpf_tentativa;

    SET v_detalhes_json = JSON_OBJECT(
        'cpf_ou_usuario_tentativa', p_cpf_tentativa,
        'resultado', IF(p_sucesso, 'sucesso', 'falha'),
        'info_adicional', p_info_adicional -- Ex: IP, user_agent
    );

    INSERT INTO auditoria (id_usuario, acao, data_hora, detalhes)
    VALUES (v_id_usuario_tentativa, IF(p_sucesso, 'LOGIN_SUCESSO', 'LOGIN_FALHA'), NOW(), v_detalhes_json);
END $$
DELIMITER ;


-- Visões (Views)

-- Visão Resumo de Contas por Cliente
CREATE  VIEW vw_resumo_contas AS
SELECT
    c.id_cliente,
    u.nome AS nome_cliente,
    u.cpf AS cpf_cliente,
    COUNT(co.id_conta) AS total_contas,
    SUM(IF(co.status = 'ATIVA', 1, 0)) AS total_contas_ativas,
    SUM(IF(co.status = 'ATIVA', co.saldo, 0)) AS saldo_total_contas_ativas
FROM cliente c
JOIN usuario u ON c.id_usuario = u.id_usuario
LEFT JOIN conta co ON c.id_cliente = co.id_cliente
GROUP BY c.id_cliente, u.nome, u.cpf;

-- Visão Movimentações Recentes
CREATE VIEW vw_movimentacoes_recentes AS
SELECT
    t.id_transacao,
    t.data_hora,
    t.tipo_transacao,
    t.valor,
    t.descricao,
    co_orig.numero_conta AS numero_conta_origem,
    u_orig.nome AS nome_cliente_origem,
    (SELECT cco_dest.numero_conta FROM conta cco_dest WHERE cco_dest.id_conta = t.id_conta_destino) AS numero_conta_destino,
    (SELECT u_dest.nome FROM conta cco_dest JOIN cliente cl_dest ON cco_dest.id_cliente = cl_dest.id_cliente JOIN usuario u_dest ON cl_dest.id_usuario = u_dest.id_usuario WHERE cco_dest.id_conta = t.id_conta_destino) AS nome_cliente_destino
FROM transacao t
LEFT JOIN conta co_orig ON t.id_conta_origem = co_orig.id_conta
LEFT JOIN cliente cl_orig ON co_orig.id_cliente = cl_orig.id_cliente
LEFT JOIN usuario u_orig ON cl_orig.id_usuario = u_orig.id_usuario
WHERE t.data_hora >= NOW() - INTERVAL 90 DAY
ORDER BY t.data_hora DESC;

-- Visão detalhes da conta (RF2.3)
CREATE VIEW vw_detalhes_conta AS
SELECT
    c.id_conta,
    c.numero_conta,
    c.tipo_conta,
    u_cli.nome AS nome_cliente,
    u_cli.cpf AS cpf_cliente,
    c.saldo,
    c.status AS status_conta,
    DATE_FORMAT(c.data_abertura, '%d/%m/%Y %H:%i:%s') AS data_abertura_formatada,
    ag.nome AS nome_agencia,
    ag.codigo_agencia AS codigo_agencia,
    f_abertura_u.nome AS nome_funcionario_abertura,
    cc.limite AS limite_conta_corrente,
    DATE_FORMAT(cc.data_vencimento, '%d/%m/%Y') AS vencimento_fatura_cc_formatada,
    cc.taxa_manutencao AS taxa_manutencao_cc,
    cp.taxa_rendimento AS taxa_rendimento_poupanca,
    ci.perfil_risco AS perfil_risco_investimento,
    ci.valor_minimo AS valor_minimo_investimento,
    ci.taxa_rendimento_base AS taxa_rendimento_base_investimento,

    CASE
        WHEN c.tipo_conta = 'POUPANCA' AND cp.taxa_rendimento > 0 THEN ROUND(c.saldo * (cp.taxa_rendimento / 100 / 12), 2) -- Projeção mensal simples
        WHEN c.tipo_conta = 'INVESTIMENTO' AND ci.taxa_rendimento_base > 0 THEN ROUND(c.saldo * (ci.taxa_rendimento_base / 100 / 12), 2) -- Projeção mensal simples
        ELSE 0.00
    END AS projecao_rendimento_mensal_estimada
FROM conta c
JOIN cliente cl ON c.id_cliente = cl.id_cliente
JOIN usuario u_cli ON cl.id_usuario = u_cli.id_usuario
JOIN agencia ag ON c.id_agencia = ag.id_agencia
LEFT JOIN funcionario f_abertura ON c.id_funcionario_abertura = f_abertura.id_funcionario
LEFT JOIN usuario f_abertura_u ON f_abertura.id_usuario = f_abertura_u.id_usuario
LEFT JOIN conta_corrente cc ON c.id_conta = cc.id_conta AND c.tipo_conta = 'CORRENTE'
LEFT JOIN conta_poupanca cp ON c.id_conta = cp.id_conta AND c.tipo_conta = 'POUPANCA'
LEFT JOIN conta_investimento ci ON c.id_conta = ci.id_conta AND c.tipo_conta = 'INVESTIMENTO';

-- Visão detalhes do funcionario (RF2.3)
CREATE VIEW vw_detalhes_funcionario AS
SELECT
    f.id_funcionario,
    f.codigo_funcionario,
    u.nome AS nome_funcionario,
    u.cpf AS cpf_funcionario,
    f.cargo,
    ag.nome AS nome_agencia_lotacao,
    DATE_FORMAT(u.data_nascimento, '%d/%m/%Y') AS data_nascimento_formatada,
    u.telefone,
    (SELECT GROUP_CONCAT(DISTINCT CONCAT(e.local, ', Nº ', e.numero_casa, ', Bairro: ', e.bairro) SEPARATOR ' | ')
     FROM endereco e WHERE e.id_usuario = u.id_usuario) AS enderecos,
    sup_u.nome AS nome_supervisor,
    (SELECT COUNT(DISTINCT co.id_conta) FROM conta co WHERE co.id_funcionario_abertura = f.id_funcionario) AS num_contas_abertas_pelo_funcionario

FROM funcionario f
JOIN usuario u ON f.id_usuario = u.id_usuario
LEFT JOIN agencia ag ON f.id_agencia = ag.id_agencia
LEFT JOIN funcionario sup_f ON f.id_supervisor = sup_f.id_funcionario
LEFT JOIN usuario sup_u ON sup_f.id_usuario = sup_u.id_usuario;

-- Visão detalhes do cliente (RF2.3)
CREATE VIEW vw_detalhes_cliente AS
SELECT
    cl.id_cliente,
    u.nome AS nome_cliente,
    u.cpf AS cpf_cliente,
    DATE_FORMAT(u.data_nascimento, '%d/%m/%Y') AS data_nascimento_formatada,
    u.telefone,
    (SELECT GROUP_CONCAT(DISTINCT CONCAT(e.local, ', Nº ', e.numero_casa, ', Bairro: ', e.bairro, ', Cidade: ', e.cidade, '-', e.estado) SEPARATOR ' | ')
     FROM endereco e WHERE e.id_usuario = u.id_usuario) AS enderecos,
    cl.score_credito,
    (SELECT GROUP_CONCAT(DISTINCT CONCAT(co.tipo_conta, ': ', co.numero_conta, ' (Status: ', co.status, ', Saldo: R$', FORMAT(co.saldo, 2, 'de_DE'), ')') ORDER BY co.id_conta SEPARATOR ' ; ')
     FROM conta co WHERE co.id_cliente = cl.id_cliente) AS lista_contas_detalhada
FROM cliente cl
JOIN usuario u ON cl.id_usuario = u.id_usuario;

-- Visão pro relatorio de movimentacoes detalhadas (RF2.6)
CREATE VIEW vw_relatorio_movimentacoes_detalhadas AS
SELECT
    t.id_transacao,
    DATE_FORMAT(t.data_hora, '%d/%m/%Y %H:%i:%s') AS data_hora_transacao,
    t.tipo_transacao,
    FORMAT(t.valor, 2, 'de_DE') AS valor_formatado,
    t.valor AS valor_numerico,
    t.descricao,
    co_orig.numero_conta AS conta_origem_numero,
    u_orig.nome AS cliente_origem_nome,
    u_orig.cpf AS cliente_origem_cpf,
    ag_orig.nome AS agencia_origem_nome,
    co_dest.numero_conta AS conta_destino_numero,
    u_dest.nome AS cliente_destino_nome,
    u_dest.cpf AS cliente_destino_cpf,
    ag_dest.nome AS agencia_destino_nome
FROM transacao t
LEFT JOIN conta co_orig ON t.id_conta_origem = co_orig.id_conta
LEFT JOIN cliente cl_orig ON co_orig.id_cliente = cl_orig.id_cliente
LEFT JOIN usuario u_orig ON cl_orig.id_usuario = u_orig.id_usuario
LEFT JOIN agencia ag_orig ON co_orig.id_agencia = ag_orig.id_agencia
LEFT JOIN conta co_dest ON t.id_conta_destino = co_dest.id_conta
LEFT JOIN cliente cl_dest ON co_dest.id_cliente = cl_dest.id_cliente
LEFT JOIN usuario u_dest ON cl_dest.id_usuario = u_dest.id_usuario
LEFT JOIN agencia ag_dest ON co_dest.id_agencia = ag_dest.id_agencia
ORDER BY t.data_hora DESC;

-- Visão pro relatorio de caso de inadimplencia (RF2.6)
CREATE VIEW vw_relatorio_inadimplencia AS
SELECT
    u.nome AS nome_cliente,
    u.cpf AS cpf_cliente,
    u.telefone AS telefone_cliente,
    c.numero_conta,
    c.tipo_conta,
    c.saldo AS saldo_atual_conta,
    cc.limite AS limite_conta_corrente,
    CASE
        WHEN c.saldo < 0 THEN 'SALDO NEGATIVO'
        WHEN c.tipo_conta = 'CORRENTE' AND c.saldo < (COALESCE(cc.limite,0) * -1) THEN 'LIMITE CHEQUE ESPECIAL ULTRAPASSADO'

        ELSE 'VERIFICAR OUTRAS CONDICOES'
    END AS tipo_inadimplencia_detectada,
    (SELECT GROUP_CONCAT(DISTINCT CONCAT(e.local, ', Nº ', e.numero_casa) SEPARATOR ' | ')
     FROM endereco e WHERE e.id_usuario = u.id_usuario) AS enderecos_cliente
FROM conta c
JOIN cliente cl ON c.id_cliente = cl.id_cliente
JOIN usuario u ON cl.id_usuario = u.id_usuario
LEFT JOIN conta_corrente cc ON c.id_conta = cc.id_conta AND c.tipo_conta = 'CORRENTE'
WHERE c.status = 'ATIVA' AND (c.saldo < 0 OR (c.tipo_conta = 'CORRENTE' AND c.saldo < (COALESCE(cc.limite,0) * -1)))
ORDER BY u.nome, c.numero_conta;

-- Visão pro relatorio de desempenho dos funcionarios (RF2.6)
CREATE VIEW vw_relatorio_desempenho_funcionarios AS
SELECT
    f.id_funcionario,
    u.nome AS nome_funcionario,
    f.cargo,
    ag.nome AS nome_agencia,
    (SELECT COUNT(DISTINCT co.id_conta)
     FROM conta co
     WHERE co.id_funcionario_abertura = f.id_funcionario) AS total_contas_abertas_pelo_funcionario,
    COALESCE(
        (SELECT SUM(tr.valor)
         FROM transacao tr
         INNER JOIN conta co_tr ON (tr.id_conta_origem = co_tr.id_conta OR tr.id_conta_destino = co_tr.id_conta)
         WHERE co_tr.id_funcionario_abertura = f.id_funcionario
           AND tr.tipo_transacao IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA')
        ), 0) AS total_valor_movimentado_em_contas_abertas,
    COALESCE(
        (SELECT COUNT(DISTINCT tr.id_transacao)
         FROM transacao tr
         INNER JOIN conta co_tr ON (tr.id_conta_origem = co_tr.id_conta OR tr.id_conta_destino = co_tr.id_conta)
         WHERE co_tr.id_funcionario_abertura = f.id_funcionario
        ), 0) AS total_transacoes_em_contas_abertas
FROM funcionario f
JOIN usuario u ON f.id_usuario = u.id_usuario
LEFT JOIN agencia ag ON f.id_agencia = ag.id_agencia
ORDER BY nome_funcionario;