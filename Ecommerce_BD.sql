CREATE TABLE cliente (
  ID_cliente int NOT NULL AUTO_INCREMENT,
  id_pessoa int NOT NULL,
  Nome varchar(50) NOT NULL,
  cep char(9) NOT NULL,
  Logradouro varchar(50) NOT NULL,
  Numero varchar(10) DEFAULT NULL,
  Bairro varchar(20) NOT NULL,
  Cidade varchar(20) NOT NULL,
  Estado varchar(20) NOT NULL,
  Email varchar(100) NOT NULL,
  PRIMARY KEY (ID_cliente),
  UNIQUE KEY id_pessoa (id_pessoa),
  CONSTRAINT FK_CLIENTE_PESSOA FOREIGN KEY (id_pessoa) REFERENCES pessoa (id_pessoa)
);

CREATE TABLE avaliacao (
  ID_avaliacao int NOT NULL AUTO_INCREMENT,
  Nota tinyint DEFAULT NULL,
  Comentario varchar(100) DEFAULT NULL,
  fk_Cliente_ID_cliente int DEFAULT NULL,
  fk_Produto_ID_produto int DEFAULT NULL,
  PRIMARY KEY (ID_avaliacao),
  KEY FK_AVALIACAO_CLIENTE (fk_Cliente_ID_cliente),
  KEY FK_AVALIACAO_PRODUTO (fk_Produto_ID_produto),
  CONSTRAINT FK_AVALIACAO_CLIENTE FOREIGN KEY (fk_Cliente_ID_cliente) REFERENCES cliente (ID_cliente) ON DELETE CASCADE,
  CONSTRAINT FK_AVALIACAO_PRODUTO FOREIGN KEY (fk_Produto_ID_produto) REFERENCES produto (ID_produto) ON DELETE CASCADE
);

CREATE TABLE fornecedor (
  ID_fornecedor int NOT NULL AUTO_INCREMENT,
  Nome varchar(80) NOT NULL,
  Tipo_de_material varchar(50) DEFAULT NULL,
  cnpj char(14) NOT NULL,
  cep char(8) DEFAULT NULL,
  Email varchar(100) DEFAULT NULL,
  PRIMARY KEY (ID_fornecedor),
  UNIQUE KEY cnpj (cnpj),
  UNIQUE KEY Email (Email),
  CONSTRAINT chk_cep_num CHECK ((cep is null or regexp_like(cep,'^[0-9]{8}$'))),
  CONSTRAINT chk_cnpj_num CHECK (regexp_like(cnpj,'^[0-9]{14}$'))
);

CREATE TABLE notafiscal (
  ID_nota_fiscal int NOT NULL AUTO_INCREMENT,
  Data_de_emissao datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Valor_total decimal(10,2) NOT NULL,
  Imposto decimal(10,2) NOT NULL DEFAULT 0.00,
  Chave_de_acesso char(44) NOT NULL,
  fk_Pedido_ID_pedido int NOT NULL,
  PRIMARY KEY (ID_nota_fiscal),
  UNIQUE KEY Chave_de_acesso (Chave_de_acesso),
  KEY idx_nf_pedido (fk_Pedido_ID_pedido),
  CONSTRAINT FK_NOTAFISCAL_PEDIDO FOREIGN KEY (fk_Pedido_ID_pedido) REFERENCES pedido (ID_pedido) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT chk_nf_chave_44dig CHECK (regexp_like(Chave_de_acesso,'^[0-9]{44}$')),
  CONSTRAINT chk_nf_imposto_nao_neg CHECK ((Imposto >= 0)),
  CONSTRAINT chk_nf_valor_nao_neg CHECK ((Valor_total >= 0))
);

CREATE TABLE fornecedor_produto (
  fk_Fornecedor_ID_fornecedor int NOT NULL,
  fk_Produto_ID_produto int NOT NULL,
  preco_fornecedor decimal(10,2) NOT NULL,
  prazo_entrega int NOT NULL DEFAULT 0,
  PRIMARY KEY (fk_Fornecedor_ID_fornecedor,fk_Produto_ID_produto),
  KEY idx_fp_produto (fk_Produto_ID_produto),
  KEY idx_fp_fornecedor (fk_Fornecedor_ID_fornecedor),
  CONSTRAINT FK_FORNECEDOR_PRODUTO_FORNECEDOR FOREIGN KEY (fk_Fornecedor_ID_fornecedor) REFERENCES fornecedor (ID_fornecedor) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_FORNECEDOR_PRODUTO_PRODUTO FOREIGN KEY (fk_Produto_ID_produto) REFERENCES produto (ID_produto) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT chk_fp_prazo_nao_neg CHECK ((prazo_entrega >= 0)),
  CONSTRAINT chk_fp_preco_nao_neg CHECK ((preco_fornecedor >= 0))
);

CREATE TABLE pagamento (
  ID_pagamento int NOT NULL AUTO_INCREMENT,
  Forma_de_pagamento varchar(50) DEFAULT NULL,
  Status varchar(20) DEFAULT NULL,
  PRIMARY KEY (ID_pagamento)
);

CREATE TABLE pedido (
  ID_pedido int NOT NULL AUTO_INCREMENT,
  Data_do_pedido datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Status enum('pedido realizado','pagamento pendente','pagamento efetuado','pedido em preparação','enviado','entregue','cancelado') NOT NULL DEFAULT 'pedido realizado',
  Valor_total decimal(10,2) NOT NULL,
  fk_Cliente_ID_cliente int NOT NULL,
  fk_Pagamento_ID_pagamento int DEFAULT NULL,
  PRIMARY KEY (ID_pedido),
  KEY idx_pedido_cliente (fk_Cliente_ID_cliente),
  KEY idx_pedido_pagamento (fk_Pagamento_ID_pagamento),
  CONSTRAINT FK_PEDIDO_CLIENTE FOREIGN KEY (fk_Cliente_ID_cliente) REFERENCES cliente (ID_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT FK_PEDIDO_PAGAMENTO FOREIGN KEY (fk_Pagamento_ID_pagamento) REFERENCES pagamento (ID_pagamento) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT chk_pedido_valor_nao_negativo CHECK ((Valor_total >= 0))
);

CREATE TABLE pedido_produto (
  fk_ID_pedido int NOT NULL,
  fk_ID_produto int NOT NULL,
  Quantidade int NOT NULL,
  preco_unitario decimal(10,2) NOT NULL,
  PRIMARY KEY (fk_ID_pedido,fk_ID_produto),
  KEY idx_pp_produto (fk_ID_produto),
  CONSTRAINT FK_PEDIDO_PRODUTO_PEDIDO FOREIGN KEY (fk_ID_pedido) REFERENCES pedido (ID_pedido) ON DELETE CASCADE,
  CONSTRAINT FK_PEDIDO_PRODUTO_PRODUTO FOREIGN KEY (fk_ID_produto) REFERENCES produto (ID_produto) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_pp_preco_nao_neg CHECK ((preco_unitario >= 0)),
  CONSTRAINT chk_pp_qtd_pos CHECK ((Quantidade > 0))
);

CREATE TABLE pessoa (
  id_pessoa int NOT NULL AUTO_INCREMENT,
  tipo enum('cliente','fornecedor') NOT NULL,
  PRIMARY KEY (id_pessoa)
);

CREATE TABLE pessoa_juridica (
  fk_ID_cliente int NOT NULL,
  cnpj char(18) NOT NULL,
  IE varchar(20) NOT NULL,
  PRIMARY KEY (fk_ID_cliente),
  UNIQUE KEY cnpj (cnpj),
  CONSTRAINT FK_PJ_CLIENTE FOREIGN KEY (fk_ID_cliente) REFERENCES cliente (ID_cliente) ON DELETE CASCADE,
  CONSTRAINT chk_pj_cnpj_mask CHECK (regexp_like(cnpj,'^[0-9]{2}.[0-9]{3}.[0-9]{3}/[0-9]{4}-[0-9]{2}$'))
);

CREATE TABLE pessoa_fisica (
  fk_ID_cliente int NOT NULL,
  cpf char(14) NOT NULL,
  rg varchar(15) NOT NULL,
  PRIMARY KEY (fk_ID_cliente),
  UNIQUE KEY cpf (cpf),
  UNIQUE KEY rg (rg),
  CONSTRAINT FK_PF_CLIENTE FOREIGN KEY (fk_ID_cliente) REFERENCES cliente (ID_cliente) ON DELETE CASCADE,
  CONSTRAINT chk_pf_cpf_mask CHECK (regexp_like(cpf,'^[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}$'))
);

CREATE TABLE produto (
  ID_produto int NOT NULL AUTO_INCREMENT,
  Descricao varchar(500) DEFAULT NULL,
  Quantidade int DEFAULT NULL,
  Tamanho varchar(5) DEFAULT NULL,
  Preco decimal(10,2) DEFAULT NULL,
  Categoria varchar(20) DEFAULT NULL,
  PRIMARY KEY (ID_produto)
);

CREATE TABLE telefone (
  id_telefone int NOT NULL AUTO_INCREMENT,
  id_fornecedor int DEFAULT NULL,
  id_cliente int DEFAULT NULL,
  numero char(11) NOT NULL,
  tipo enum('celular','fixo','whatsapp','comercial') DEFAULT 'celular',
  PRIMARY KEY (id_telefone),
  UNIQUE KEY uq_fornecedor_num (id_fornecedor,numero),
  UNIQUE KEY uq_cliente_num (id_cliente,numero),
  KEY idx_tel_cliente (id_cliente),
  CONSTRAINT FK_TELEFONE_CLIENTE FOREIGN KEY (id_cliente) REFERENCES cliente (ID_cliente) ON DELETE CASCADE,
  CONSTRAINT FK_TELEFONE_FORNECEDOR FOREIGN KEY (id_fornecedor) REFERENCES fornecedor (ID_fornecedor) ON DELETE CASCADE,
  CONSTRAINT chk_tel_11 CHECK (regexp_like(numero,'^[0-9]{11}$')),
  CONSTRAINT chk_um_dono CHECK ((id_fornecedor is not null) <> (id_cliente is not null))
);
