-- ===========================================
-- TABELA FORNECEDOR
-- ===========================================
CREATE TABLE fornecedor (
  ID_fornecedor BIGINT PRIMARY KEY,
  Nome varchar(80) NOT NULL,
  Tipo_de_material varchar(50) DEFAULT NULL,
  cnpj char(14) NOT NULL,   -- apenas números
  cep char(8) DEFAULT NULL,
  Email varchar(100) DEFAULT NULL,

  UNIQUE KEY uq_fornecedor_cnpj (cnpj),
  UNIQUE KEY uq_fornecedor_email (Email),

  CONSTRAINT chk_fornecedor_cep CHECK ((cep IS NULL OR regexp_like(cep, '^[0-9]{8}$'))),
  CONSTRAINT chk_fornecedor_cnpj CHECK (regexp_like(cnpj, '^[0-9]{14}$'))
);

-- ===========================================
-- TABELA PAGAMENTO
-- ===========================================
CREATE TABLE pagamento (
  ID_pagamento BIGINT PRIMARY KEY,
  Forma_de_pagamento varchar(50) DEFAULT NULL,
  Status varchar(20) DEFAULT NULL
);

-- ===========================================
-- TABELA PRODUTO
-- ===========================================
CREATE TABLE produto (
  ID_produto BIGINT NOT NULL AUTO_INCREMENT, -- Mantem AUTO_INCREMENT porque produtos sao inseridos em massa e nao sao dados criticos
  Descricao varchar(500) DEFAULT NULL,
  Quantidade int DEFAULT NULL,
  Tamanho varchar(5) DEFAULT NULL,
  Preco decimal(10,2) DEFAULT NULL,
  Categoria varchar(20) DEFAULT NULL,
  PRIMARY KEY (ID_produto)
);

-- ===========================================
-- TABELA CLIENTE (SEM TABELA PESSOA)
-- ===========================================
CREATE TABLE cliente (
  ID BIGINT NOT NULL,           -- ID definido automaticamente pela funcao fn_gerar_id_cliente()
  Tipo varchar(50) NOT NULL,
  Nome varchar(50) NOT NULL,
  Email varchar(100) NOT NULL,
  Senha varchar(100) NOT NULL,
  CEP char(8) NOT NULL,         -- sem máscara
  Cidade varchar(20) NOT NULL,
  Logradouro varchar(50) NOT NULL,
  Numero varchar(10) DEFAULT NULL,
  Bairro varchar(20) NOT NULL,
  Estado varchar(20) NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY uq_cliente_email (Email)
);

-- ===========================================
-- TABELA FORNECEDOR_PRODUTO (N:N)
-- ===========================================
CREATE TABLE fornecedor_produto (
  fk_Fornecedor_ID_fornecedor BIGINT NOT NULL,
  fk_Produto_ID_produto BIGINT NOT NULL,
  preco_fornecedor decimal(10,2) NOT NULL,
  prazo_entrega int NOT NULL DEFAULT 0,

  PRIMARY KEY (fk_Fornecedor_ID_fornecedor, fk_Produto_ID_produto),

  CONSTRAINT FK_FORNECEDOR_PRODUTO_FORNECEDOR FOREIGN KEY (fk_Fornecedor_ID_fornecedor)
      REFERENCES fornecedor (ID_fornecedor) ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT FK_FORNECEDOR_PRODUTO_PRODUTO FOREIGN KEY (fk_Produto_ID_produto)
      REFERENCES produto (ID_produto) ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT chk_fp_prazo CHECK (prazo_entrega >= 0),
  CONSTRAINT chk_fp_preco CHECK (preco_fornecedor >= 0)
);

-- ===========================================
-- TABELA PEDIDO
-- ===========================================
CREATE TABLE pedido (
  ID_pedido BIGINT NOT NULL, -- Recebe identificador personalizado pela funcao fn_gerar_id_pedido()
  Data_do_pedido datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Status enum('pedido realizado','pagamento pendente','pagamento efetuado','pedido em preparação','enviado','entregue','cancelado')
      NOT NULL DEFAULT 'pedido realizado',
  Valor_total decimal(10,2) NOT NULL DEFAULT 0.00, -- Inicia zerado e e atualizado pelos gatilhos de itens

  fk_Cliente_id BIGINT NOT NULL,
  fk_Pagamento_ID_pagamento BIGINT DEFAULT NULL,

  PRIMARY KEY (ID_pedido), -- Chave gerada sem AUTO_INCREMENT

  CONSTRAINT FK_PEDIDO_CLIENTE FOREIGN KEY (fk_Cliente_id)
      REFERENCES cliente (id) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT FK_PEDIDO_PAGAMENTO FOREIGN KEY (fk_Pagamento_ID_pagamento)
      REFERENCES pagamento (ID_pagamento) ON DELETE SET NULL ON UPDATE CASCADE,

  CONSTRAINT chk_pedido_valor CHECK (Valor_total >= 0)
);

-- ===========================================
-- TABELA NOTA FISCAL
-- ===========================================
CREATE TABLE notafiscal (
  ID_nota_fiscal BIGINT NOT NULL AUTO_INCREMENT,
  Data_de_emissao datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Valor_total decimal(10,2) NOT NULL,
  Imposto decimal(10,2) NOT NULL DEFAULT 0.00,
  Chave_de_acesso char(44) NOT NULL,

  fk_Pedido_ID_pedido BIGINT NOT NULL,

  PRIMARY KEY (ID_nota_fiscal),
  UNIQUE KEY uq_nf_chave (Chave_de_acesso),

  CONSTRAINT FK_NOTAFISCAL_PEDIDO FOREIGN KEY (fk_Pedido_ID_pedido)
      REFERENCES pedido (ID_pedido) ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT chk_nf_chave CHECK (regexp_like(Chave_de_acesso, '^[0-9]{44}$')),
  CONSTRAINT chk_nf_imposto CHECK (Imposto >= 0),
  CONSTRAINT chk_nf_valor CHECK (Valor_total >= 0)
);

-- ===========================================
-- TABELA PEDIDO_PRODUTO (N:N)
-- ===========================================
CREATE TABLE pedido_produto (
  fk_ID_pedido BIGINT NOT NULL,
  fk_ID_produto BIGINT NOT NULL,
  Quantidade int NOT NULL,
  preco_unitario decimal(10,2) NOT NULL,

  PRIMARY KEY (fk_ID_pedido, fk_ID_produto),

  CONSTRAINT FK_PEDIDO_PRODUTO_PEDIDO FOREIGN KEY (fk_ID_pedido)
      REFERENCES pedido (ID_pedido) ON DELETE CASCADE,

  CONSTRAINT FK_PEDIDO_PRODUTO_PRODUTO FOREIGN KEY (fk_ID_produto)
      REFERENCES produto (ID_produto) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_pp_preco CHECK (preco_unitario >= 0),
  CONSTRAINT chk_pp_qtd CHECK (Quantidade > 0)
);

-- ===========================================
-- TABELA PESSOA JURIDICA (CLIENTE PJ)
-- ===========================================
CREATE TABLE pessoa_juridica (
  fk_ID_cliente BIGINT NOT NULL,
  cnpj char(14) NOT NULL,          -- somente números
  IE varchar(20) NOT NULL,

  PRIMARY KEY (fk_ID_cliente),
  UNIQUE KEY uq_pj_cnpj (cnpj),

  CONSTRAINT FK_PJ_CLIENTE FOREIGN KEY (fk_ID_cliente)
      REFERENCES cliente (id) ON DELETE CASCADE
);

-- ===========================================
-- TABELA PESSOA FISICA (CLIENTE PF)
-- ===========================================
CREATE TABLE pessoa_fisica (
  fk_ID_cliente BIGINT NOT NULL,
  cpf char(11) NOT NULL,          -- somente números
  rg varchar(15) NOT NULL,

  PRIMARY KEY (fk_ID_cliente),
  UNIQUE KEY uq_pf_cpf (cpf),
  UNIQUE KEY uq_pf_rg (rg),

  CONSTRAINT FK_PF_CLIENTE FOREIGN KEY (fk_ID_cliente)
      REFERENCES cliente (id) ON DELETE CASCADE
);

-- ===========================================
-- TABELA TELEFONE
-- ===========================================
CREATE TABLE telefone (
  id_telefone BIGINT NOT NULL AUTO_INCREMENT, -- Auto_increment mantem o cadastro simples e nao compromete requisitos de ID
  id_fornecedor BIGINT DEFAULT NULL,
  id_cliente BIGINT DEFAULT NULL,
  numero char(11) NOT NULL,  -- somente números
  tipo enum('celular','fixo','whatsapp','comercial') DEFAULT 'celular',

  PRIMARY KEY (id_telefone),
  UNIQUE KEY uq_tel_fornecedor (id_fornecedor, numero),
  UNIQUE KEY uq_tel_cliente (id_cliente, numero),

  CONSTRAINT FK_TELEFONE_CLIENTE FOREIGN KEY (id_cliente)
      REFERENCES cliente (id) ON DELETE CASCADE,

  CONSTRAINT FK_TELEFONE_FORNECEDOR FOREIGN KEY (id_fornecedor)
      REFERENCES fornecedor (ID_fornecedor) ON DELETE CASCADE,

  CONSTRAINT chk_tel_numero CHECK (regexp_like(numero, '^[0-9]{11}$')),
  CONSTRAINT chk_tel_apenas_um_dono CHECK ((id_fornecedor IS NOT NULL) <> (id_cliente IS NOT NULL))
);

-- ===========================================
-- TABELA GRUPOS_USUARIOS
-- ===========================================
CREATE TABLE grupos_usuarios ( -- Define grupos para controle de permissao
  id_grupo SMALLINT UNSIGNED PRIMARY KEY, -- Identificador compacto para manutencao manual
  nome varchar(50) NOT NULL UNIQUE, -- Nome do grupo precisa ser unico para garantir buscas deterministicas
  descricao varchar(150) NOT NULL -- Descreve o escopo de acesso de cada grupo
); -- Finaliza criacao de grupos_usuarios

-- ===========================================
-- TABELA USUARIOS
-- ===========================================
CREATE TABLE usuarios ( -- Tabela central de autenticacao
  id_usuario BIGINT PRIMARY KEY, -- Recebe ID customizado gerado via trigger
  login varchar(50) NOT NULL UNIQUE, -- Login exclusivo para cada usuario
  senha_hash varchar(255) NOT NULL, -- Hash da senha para garantir seguranca
  email varchar(100) NOT NULL UNIQUE, -- Email unico para comunicacoes
  ativo tinyint(1) NOT NULL DEFAULT 1, -- Flag simples para ativar ou desativar acessos
  criado_em datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Data de criacao
  ultimo_login datetime DEFAULT NULL, -- Armazena ultimo login
  fk_cliente BIGINT DEFAULT NULL, -- Liga opcionalmente o usuario a um cliente existente

  CONSTRAINT FK_USUARIO_CLIENTE FOREIGN KEY (fk_cliente) -- Mantem integridade com a tabela cliente
      REFERENCES cliente (id) ON DELETE SET NULL ON UPDATE CASCADE
); -- Finaliza a criacao da tabela usuarios

-- ===========================================
-- TABELA USUARIO_GRUPO
-- ===========================================
CREATE TABLE usuario_grupo ( -- Liga usuarios aos grupos de permissao
  fk_usuario BIGINT NOT NULL, -- Referencia obrigatoria ao usuario
  fk_grupo SMALLINT UNSIGNED NOT NULL, -- Referencia obrigatoria ao grupo cadastrado

  PRIMARY KEY (fk_usuario, fk_grupo), -- Evita duplicidade de atribuicoes

  CONSTRAINT FK_USUARIO_GRUPO_USUARIO FOREIGN KEY (fk_usuario) -- Mantem consistencia com usuarios
      REFERENCES usuarios (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_USUARIO_GRUPO_GRUPO FOREIGN KEY (fk_grupo) -- Mantem consistencia com grupos
      REFERENCES grupos_usuarios (id_grupo) ON DELETE CASCADE ON UPDATE CASCADE
); -- Finaliza tabela de relacionamento usuario_grupo

-- ===========================================
-- INDICES ESTRATEGICOS
-- ===========================================
CREATE INDEX idx_pedido_cliente_data ON pedido (fk_Cliente_id, Data_do_pedido); -- Otimiza filtros por cliente e janela temporal
CREATE INDEX idx_produto_categoria_preco ON produto (Categoria, Preco); -- Acelera consultas por categoria e faixa de preco
CREATE INDEX idx_nf_pedido ON notafiscal (fk_Pedido_ID_pedido); -- Facilita auditorias de notas vinculadas a pedidos
CREATE INDEX idx_usuario_ativo ON usuarios (ativo, login); -- Permite localizar rapidamente usuarios ativos
CREATE INDEX idx_usuario_grupo ON usuario_grupo (fk_grupo); -- Suporta relatorios por grupo de permissao

-- ===========================================
-- FUNCOES PARA GERAR IDS E CALCULOS DE NEGOCIO
-- ===========================================
DELIMITER $$ -- Ajusta delimitador para funcoes
CREATE FUNCTION fn_gerar_id_cliente() -- Funcao que gera IDs unicos para clientes
RETURNS BIGINT -- Define o tipo de retorno como BIGINT
DETERMINISTIC -- Garante repetibilidade sob o mesmo estado
BEGIN -- Inicia o bloco da funcao
  RETURN UUID_SHORT(); -- Utiliza identificador monotonicamente crescente do MySQL
END $$ -- Encerramento da funcao fn_gerar_id_cliente
DELIMITER ; -- Retorna delimitador padrao

DELIMITER $$ -- Altera delimitador para nova funcao
CREATE FUNCTION fn_gerar_id_fornecedor() -- Funcao especifica para IDs de fornecedor
RETURNS BIGINT -- Tipo de retorno BIGINT
DETERMINISTIC -- Marcacao deterministica
BEGIN -- Inicia bloco
  RETURN UUID_SHORT(); -- Reaproveita gerador interno de IDs
END $$ -- Finaliza fn_gerar_id_fornecedor
DELIMITER ; -- Restaura delimitador ;

DELIMITER $$ -- Ajusta delimitador
CREATE FUNCTION fn_gerar_id_pedido() -- Funcao que gera IDs proprietarios de pedido
RETURNS BIGINT -- Define tipo numerico largo
DETERMINISTIC -- Mantem comportamento deterministico
BEGIN -- Inicio do bloco
  RETURN UUID_SHORT(); -- Garante unicidade sem depender de AUTO_INCREMENT
END $$ -- Finaliza fn_gerar_id_pedido
DELIMITER ; -- Restaura delimitador padrao

DELIMITER $$ -- Ajusta delimitador
CREATE FUNCTION fn_gerar_id_pagamento() -- Funcao para IDs de pagamento
RETURNS BIGINT -- Retorna BIGINT
DETERMINISTIC -- Deterministica para replicacao
BEGIN -- Inicia bloco
  RETURN UUID_SHORT(); -- Mesma estrategia de IDs nao previsiveis
END $$ -- Encerra fn_gerar_id_pagamento
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE FUNCTION fn_gerar_id_usuario() -- Funcao especifica para IDs de usuarios
RETURNS BIGINT -- Tipo BIGINT
DETERMINISTIC -- Deterministica
BEGIN -- Inicio
  RETURN UUID_SHORT(); -- Utiliza fonte global de unicidade
END $$ -- Finaliza fn_gerar_id_usuario
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE FUNCTION fn_calcular_total_pedido(p_id BIGINT) -- Calcula o valor total de um pedido
RETURNS decimal(10,2) -- Retorna valor monetario
DETERMINISTIC -- Valor depende apenas dos dados em pedido_produto
BEGIN -- Inicia bloco
  DECLARE v_total decimal(10,2); -- Variavel local que acumula o total
  SELECT COALESCE(SUM(Quantidade * preco_unitario), 0.00) -- Soma quantidade vezes preco
    INTO v_total -- Joga resultado na variavel
    FROM pedido_produto -- Fonte dos itens
   WHERE fk_ID_pedido = p_id; -- Filtro pelo pedido desejado
  RETURN v_total; -- Retorna o total calculado
END $$ -- Finaliza fn_calcular_total_pedido
DELIMITER ; -- Retorna delimitador padrao

-- ===========================================
-- TRIGGERS
-- ===========================================
DELIMITER $$ -- Ajusta delimitador para trigger
CREATE TRIGGER trg_cliente_define_id -- Define ID automaticamente para clientes
BEFORE INSERT ON cliente -- Executa antes da insercao
FOR EACH ROW -- Dispara por linha
BEGIN -- Inicia bloco
  IF NEW.ID IS NULL OR NEW.ID = 0 THEN -- Confere se nao veio ID
    SET NEW.ID = fn_gerar_id_cliente(); -- Gera ID com a funcao especifica
  END IF; -- Termina condicao
END $$ -- Finaliza trigger
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE TRIGGER trg_fornecedor_define_id -- Define ID para fornecedores
BEFORE INSERT ON fornecedor -- Antes de inserir fornecedor
FOR EACH ROW -- Uma vez por linha
BEGIN -- Inicio
  IF NEW.ID_fornecedor IS NULL OR NEW.ID_fornecedor = 0 THEN -- Confirma ausencia de ID valido
    SET NEW.ID_fornecedor = fn_gerar_id_fornecedor(); -- Preenche ID com funcao dedicada
  END IF; -- Termina condicao
END $$ -- Encerra trigger
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE TRIGGER trg_pedido_define_id -- Define ID para pedidos
BEFORE INSERT ON pedido -- Dispara antes da insercao
FOR EACH ROW -- Por registro
BEGIN -- Inicio
  IF NEW.ID_pedido IS NULL OR NEW.ID_pedido = 0 THEN -- Confere ID
    SET NEW.ID_pedido = fn_gerar_id_pedido(); -- Gera ID customizado
  END IF; -- Final condicao
END $$ -- Fecha trigger
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE TRIGGER trg_pagamento_define_id -- Define ID para pagamentos
BEFORE INSERT ON pagamento -- Antes da insercao
FOR EACH ROW -- Por registro
BEGIN -- Inicio
  IF NEW.ID_pagamento IS NULL OR NEW.ID_pagamento = 0 THEN -- Checa se ID veio vazio
    SET NEW.ID_pagamento = fn_gerar_id_pagamento(); -- Gera ID com funcao especifica
  END IF; -- Fecha condicao
END $$ -- Encerra trigger
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE TRIGGER trg_usuario_define_id -- Garante IDs unicos de usuario
BEFORE INSERT ON usuarios -- Antes de inserir na tabela usuarios
FOR EACH ROW -- Para cada linha
BEGIN -- Inicio
  IF NEW.id_usuario IS NULL OR NEW.id_usuario = 0 THEN -- Confere ID informado
    SET NEW.id_usuario = fn_gerar_id_usuario(); -- Gera ID customizado
  END IF; -- Termina condicao
END $$ -- Finaliza trigger
DELIMITER ; -- Retorna delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE TRIGGER trg_pedido_produto_recalcula_total -- Mantem valor total apos insercoes
AFTER INSERT ON pedido_produto -- Dispara apos inserir item
FOR EACH ROW -- Por registro
BEGIN -- Inicio
  UPDATE pedido -- Atualiza pedido
     SET Valor_total = fn_calcular_total_pedido(NEW.fk_ID_pedido) -- Recalcula total com funcao agregadora
   WHERE ID_pedido = NEW.fk_ID_pedido; -- Filtra pelo pedido alterado
END $$ -- Finaliza trigger
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE TRIGGER trg_pedido_produto_recalcula_total_delete -- Mantem valor total apos exclusoes
AFTER DELETE ON pedido_produto -- Dispara depois de remover item
FOR EACH ROW -- Por registro removido
BEGIN -- Inicio
  UPDATE pedido -- Atualiza pedido afetado
     SET Valor_total = fn_calcular_total_pedido(OLD.fk_ID_pedido) -- Recalcula total apos exclusao
   WHERE ID_pedido = OLD.fk_ID_pedido; -- Seleciona pedido correto
END $$ -- Finaliza trigger
DELIMITER ; -- Restaura delimitador

-- ===========================================
-- VIEWS
-- ===========================================
CREATE OR REPLACE VIEW vw_resumo_pedidos AS -- Visao para relatorios de pedidos
SELECT -- Inicio da projecao
  p.ID_pedido AS codigo_pedido, -- Chave primaria
  c.Nome AS nome_cliente, -- Nome do cliente
  p.Data_do_pedido AS data_pedido, -- Data
  p.Status AS status_pedido, -- Status atual
  COALESCE(pg.Status, 'sem pagamento') AS status_pagamento, -- Status do pagamento
  p.Valor_total AS valor_total -- Valor do pedido
FROM pedido p -- Fonte principal
JOIN cliente c ON c.ID = p.fk_Cliente_id -- Vincula cliente
LEFT JOIN pagamento pg ON pg.ID_pagamento = p.fk_Pagamento_ID_pagamento; -- Vincula pagamento

CREATE OR REPLACE VIEW vw_monitoramento_estoque AS -- Visao para acompanhar estoque
SELECT -- Inicio da projecao
  ID_produto AS codigo_produto, -- Identificador do produto
  Descricao AS descricao_produto, -- Descricao textual
  Quantidade AS quantidade_disponivel, -- Quantidade atual
  CASE -- Classificacao do estoque
    WHEN Quantidade IS NULL THEN 'sem-informacao' -- Sem dados
    WHEN Quantidade < 5 THEN 'critico' -- Critico
    WHEN Quantidade BETWEEN 5 AND 20 THEN 'atencao' -- Atencao
    ELSE 'saudavel' -- Estoque suficiente
  END AS nivel_estoque -- Resultado do CASE
FROM produto; -- Fonte

-- ===========================================
-- PROCEDURES
-- ===========================================
DELIMITER $$ -- Ajusta delimitador
CREATE PROCEDURE sp_criar_pagamento ( -- Procedure para registrar pagamentos
  IN p_pedido BIGINT, -- Pedido alvo
  IN p_forma varchar(50), -- Forma de pagamento
  IN p_status varchar(20) -- Status inicial
) -- Encerra lista de parametros da procedure sp_criar_pagamento
BEGIN -- Inicio
  DECLARE v_pagamento BIGINT; -- Variavel local do pagamento
  SET v_pagamento = fn_gerar_id_pagamento(); -- Gera ID
  INSERT INTO pagamento (ID_pagamento, Forma_de_pagamento, Status) -- Insere pagamento
  VALUES (v_pagamento, p_forma, p_status); -- Valores persistidos
  UPDATE pedido -- Atualiza pedido
     SET fk_Pagamento_ID_pagamento = v_pagamento -- Relaciona pagamento
   WHERE ID_pedido = p_pedido; -- Pedido alvo
END $$ -- Final procedure
DELIMITER ; -- Restaura delimitador

DELIMITER $$ -- Ajusta delimitador
CREATE PROCEDURE sp_atualizar_status_pedido ( -- Procedure para alterar status de pedido
  IN p_pedido BIGINT, -- Pedido alvo
  IN p_status enum('pedido realizado','pagamento pendente','pagamento efetuado','pedido em prepara��ǜo','enviado','entregue','cancelado') -- Status permitido
) -- Encerra a declaracao de parametros
BEGIN -- Inicio
  UPDATE pedido -- Atualiza tabela
     SET Status = p_status -- Ajusta status
   WHERE ID_pedido = p_pedido; -- Pedido
END $$ -- Final procedure
DELIMITER ; -- Restaura delimitador

-- ===========================================
-- USUARIOS E CONTROLE DE ACESSO
-- ===========================================
CREATE USER 'app_admin'@'localhost' IDENTIFIED BY 'Adm!n#2025'; -- Usuario administrador do schema
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON EcommerceBD.* TO 'app_admin'@'localhost'; -- Concede acesso total ao schema

CREATE USER 'app_analyst'@'localhost' IDENTIFIED BY 'Analyst#2025'; -- Usuario somente leitura
GRANT SELECT ON EcommerceBD.* TO 'app_analyst'@'localhost'; -- Permite apenas consultas

CREATE USER 'app_integracao'@'localhost' IDENTIFIED BY 'Integr@2025'; -- Usuario para integracoes externas
GRANT SELECT, INSERT, UPDATE ON EcommerceBD.produto TO 'app_integracao'@'localhost'; -- Permite manter produtos
GRANT SELECT, INSERT ON EcommerceBD.pedido TO 'app_integracao'@'localhost'; -- Permite registrar pedidos

/* Segurança adicional: desabilitar acesso root:
Se não quiser bloquear o root, basta comentar a proxima linha antes de executar o script.*/

ALTER USER 'root'@'localhost' ACCOUNT LOCK; -- Desabilita o acesso via root conforme requisito
