/* SQL Físico - Ecommerce */

/* Tabela de Pagamento */
CREATE TABLE PAGAMENTO (
    ID_pagamento INT AUTO_INCREMENT PRIMARY KEY,
    Forma_de_pagamento VARCHAR(15),
    Status ENUM('pendente','pago','cancelado','estornado')
);

/* Tabela de Cliente */
CREATE TABLE CLIENTE (
    ID_cliente INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(50) NOT NULL,
    cep CHAR(9) NOT NULL,
    Logradouro VARCHAR(50) NOT NULL,
    Numero VARCHAR(10),
    Bairro VARCHAR(20) NOT NULL,
    Cidade VARCHAR(20) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    Email VARCHAR(100) NOT NULL
);

/* Tabela de Produto */
CREATE TABLE PRODUTO (
    ID_produto INT AUTO_INCREMENT PRIMARY KEY,
    Descricao VARCHAR(500),
    Quantidade INT,
    Tamanho VARCHAR(5),
    Preco DECIMAL(10,2),
    Categoria VARCHAR(20)
);

/* Tabela de Avaliação */
CREATE TABLE AVALIACAO (
    ID_avaliacao INT AUTO_INCREMENT PRIMARY KEY,
    Nota TINYINT,
    Comentario VARCHAR(100),
    fk_Cliente_ID_cliente INT,
    fk_Produto_ID_produto INT,
    CONSTRAINT FK_AVALIACAO_CLIENTE FOREIGN KEY (fk_Cliente_ID_cliente)
        REFERENCES CLIENTE(ID_cliente)
        ON DELETE CASCADE,
    CONSTRAINT FK_AVALIACAO_PRODUTO FOREIGN KEY (fk_Produto_ID_produto)
        REFERENCES PRODUTO(ID_produto)
        ON DELETE CASCADE
);

/* Tabela de Fornecedor */
CREATE TABLE FORNECEDOR (
    ID_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(50),
    Tipo_de_material VARCHAR(20),
    cnpj CHAR(14),
    cep CHAR(8),
    Email VARCHAR(50),
    fk_Telefone_Telefone_PK VARCHAR(20),
    CONSTRAINT FK_FORNECEDOR_TELEFONE FOREIGN KEY (fk_Telefone_Telefone_PK)
        REFERENCES TELEFONE(Telefone_PK)
        ON DELETE NO ACTION
);

/* Tabela de Telefone */
CREATE TABLE TELEFONE (
    Telefone_PK VARCHAR(20) NOT NULL PRIMARY KEY,
    fk_ID_cliente INT,
    Telefone VARCHAR(20),
    CONSTRAINT FK_TELEFONE_CLIENTE FOREIGN KEY (fk_ID_cliente)
        REFERENCES CLIENTE(ID_cliente)
);

/* Tabela Pessoa Física */
CREATE TABLE PESSOA_FISICA (
    cpf CHAR(14) NOT NULL,
    rg VARCHAR(15) NOT NULL,
    fk_ID_cliente INT,
    CONSTRAINT FK_PF_CLIENTE FOREIGN KEY (fk_ID_cliente)
        REFERENCES CLIENTE(ID_cliente)
        ON DELETE CASCADE
);

/* Tabela Pessoa Jurídica */
CREATE TABLE PESSOA_JURIDICA (
    cnpj CHAR(18) NOT NULL,
    IE VARCHAR(15) NOT NULL,
    fk_ID_cliente INT,
    CONSTRAINT FK_PJ_CLIENTE FOREIGN KEY (fk_ID_cliente)
        REFERENCES CLIENTE(ID_cliente)
        ON DELETE CASCADE
);

/* Tabela Pedido */
CREATE TABLE PEDIDO (
    ID_pedido INT AUTO_INCREMENT PRIMARY KEY,
    Data_do_pedido DATE,
    Status ENUM("pedido realizado", "pagamento pendente", "pagamento efetuado", "pedido em preparação", "enviado", "entregue", "cancelado"),
    Valor_total DECIMAL(10,2) NOT NULL,
    fk_Cliente_ID_cliente INT,
    fk_Pagamento_ID_pagamento INT,
    CONSTRAINT FK_PEDIDO_CLIENTE FOREIGN KEY (fk_Cliente_ID_cliente)
        REFERENCES CLIENTE(ID_cliente)
        ON DELETE RESTRICT,
    CONSTRAINT FK_PEDIDO_PAGAMENTO FOREIGN KEY (fk_Pagamento_ID_pagamento)
        REFERENCES PAGAMENTO(ID_pagamento)
        ON DELETE RESTRICT
);

/* Tabela Pedido-Produto (N:N) */
CREATE TABLE PEDIDO_PRODUTO (
    fk_ID_pedido INT,
    fk_ID_produto INT,
    Quantidade INT,
    preco_unitario DECIMAL(10,2),
    PRIMARY KEY(fk_ID_pedido, fk_ID_produto),
    CONSTRAINT FK_PEDIDO_PRODUTO_PEDIDO FOREIGN KEY (fk_ID_pedido)
        REFERENCES PEDIDO(ID_pedido),
    CONSTRAINT FK_PEDIDO_PRODUTO_PRODUTO FOREIGN KEY (fk_ID_produto)
        REFERENCES PRODUTO(ID_produto)
);

/* Tabela Fornecedor-Produto (N:N) */
CREATE TABLE FORNECEDOR_PRODUTO (
    fk_Fornecedor_ID_fornecedor INT,
    fk_Produto_ID_produto INT,
    preco_fornecedor DECIMAL(10,2),
    prazo_entrega INT,
    PRIMARY KEY(fk_Fornecedor_ID_fornecedor, fk_Produto_ID_produto),
    CONSTRAINT FK_FORNECEDOR_PRODUTO_FORNECEDOR FOREIGN KEY (fk_Fornecedor_ID_fornecedor)
        REFERENCES FORNECEDOR(ID_fornecedor),
    CONSTRAINT FK_FORNECEDOR_PRODUTO_PRODUTO FOREIGN KEY (fk_Produto_ID_produto)
        REFERENCES PRODUTO(ID_produto)
);

/* Tabela Nota Fiscal */
CREATE TABLE NOTAFISCAL (
    ID_nota_fiscal INT AUTO_INCREMENT PRIMARY KEY,
    Data_de_emissao DATE,
    Valor_total DECIMAL(10,2),
    Imposto DECIMAL(10,2),
    Chave_de_acesso CHAR(44),
    fk_Pedido_ID_pedido INT,
    CONSTRAINT FK_NOTAFISCAL_PEDIDO FOREIGN KEY (fk_Pedido_ID_pedido)
        REFERENCES PEDIDO(ID_pedido)
        ON DELETE CASCADE
);
