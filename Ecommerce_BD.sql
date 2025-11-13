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
  ID_produto BIGINT NOT NULL AUTO_INCREMENT,
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
  ID BIGINT NOT NULL,           -- ID gerado pelo gerarIdUnico() no Java
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
  ID_pedido BIGINT NOT NULL AUTO_INCREMENT,
  Data_do_pedido datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Status enum('pedido realizado','pagamento pendente','pagamento efetuado','pedido em preparação','enviado','entregue','cancelado')
      NOT NULL DEFAULT 'pedido realizado',
  Valor_total decimal(10,2) NOT NULL,

  fk_Cliente_id BIGINT NOT NULL,
  fk_Pagamento_ID_pagamento BIGINT DEFAULT NULL,

  PRIMARY KEY (ID_pedido),

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
  id_telefone BIGINT NOT NULL AUTO_INCREMENT,
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
