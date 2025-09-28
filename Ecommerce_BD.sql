CREATE DATABASE ECOM;

USE ECOM;

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

CREATE TABLE pagamento (
  ID_pagamento int NOT NULL AUTO_INCREMENT,
  Forma_de_pagamento varchar(50) DEFAULT NULL,
  Status varchar(20) DEFAULT NULL,
  PRIMARY KEY (ID_pagamento)
);

CREATE TABLE pessoa (
  id_pessoa int NOT NULL AUTO_INCREMENT,
  tipo enum('cliente','fornecedor') NOT NULL,
  PRIMARY KEY (id_pessoa)
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


DELIMITER //

CREATE TRIGGER bi_cliente
BEFORE INSERT ON cliente
FOR EACH ROW
BEGIN
  -- Cria a pessoa automaticamente
  INSERT INTO pessoa (tipo) VALUES ('cliente');
  
  -- Atribui o id_pessoa gerado ao novo cliente
  SET NEW.id_pessoa = LAST_INSERT_ID();
END //

DELIMITER ;

-- Scripts de inserção de dados (população do banco):

-- 1. PAGAMENTO
INSERT INTO PAGAMENTO (Forma_de_pagamento, Status) VALUES
('Cartão de Crédito', 'pago'),
('Cartão de Crédito', 'pendente'),
('Cartão de Crédito', 'cancelado'),
('Cartão de Crédito', 'estornado'),
('Cartão de Débito', 'pago'),
('Cartão de Débito', 'pendente'),
('Cartão de Débito', 'cancelado'),
('Cartão de Débito', 'estornado'),
('Boleto', 'pago'),
('Boleto', 'pendente'),
('Boleto', 'cancelado'),
('Boleto', 'estornado'),
('Pix', 'pago'),
('Pix', 'pendente'),
('Pix', 'cancelado'),
('Pix', 'estornado'),
('Transferência', 'pago'),
('Transferência', 'pendente'),
('Transferência', 'cancelado'),
('Transferência', 'estornado');

-- 2. CLIENTE
INSERT INTO CLIENTE (Nome, cep, Logradouro, Numero, Bairro, Cidade, Estado, Email) VALUES
('João Silva', '70000-000', 'Rua A', '10', 'Centro', 'Brasília', 'DF', 'joao@email.com'),
('Maria Souza', '70000-001', 'Rua B', '22', 'Asa Sul', 'Brasília', 'DF', 'maria@email.com'),
('Carlos Pereira', '70000-002', 'Rua C', '55', 'Taguatinga', 'Brasília', 'DF', 'carlos@email.com'),
('Ana Costa', '70000-003', 'Rua D', '11', 'Guará', 'Brasília', 'DF', 'ana@email.com'),
('Pedro Gomes', '70000-004', 'Rua E', '30', 'Ceilândia', 'Brasília', 'DF', 'pedro@email.com'),
('Fernanda Lima', '70000-005', 'Rua F', '14', 'Gama', 'Brasília', 'DF', 'fernanda@email.com'),
('Lucas Almeida', '70000-006', 'Rua G', '41', 'Samambaia', 'Brasília', 'DF', 'lucas@email.com'),
('Paula Rocha', '70000-007', 'Rua H', '12', 'Asa Norte', 'Brasília', 'DF', 'paula@email.com'),
('Ricardo Melo', '70000-008', 'Rua I', '50', 'Sobradinho', 'Brasília', 'DF', 'ricardo@email.com'),
('Juliana Fernandes', '70000-009', 'Rua J', '77', 'Cruzeiro', 'Brasília', 'DF', 'juliana@email.com'),
('Clara Martins', '700110-01', 'Rua K', '21', 'Asa Sul', 'Brasília', 'DF', 'clara.martins@email.com'),
('Bruno Teixeira', '700120-02', 'Rua L', '22', 'Asa Norte', 'Brasília', 'DF', 'bruno.teixeira@email.com'),
('Rafael Duarte', '700130-03', 'Rua M', '23', 'Taguatinga', 'Brasília', 'DF', 'rafael.duarte@email.com'),
('Camila Torres', '700140-04', 'Rua N', '24', 'Guará', 'Brasília', 'DF', 'camila.torres@email.com'),
('Fábio Nunes', '700150-05', 'Rua O', '25', 'Ceilândia', 'Brasília', 'DF', 'fábio.nunes@email.com'),
('Larissa Ramos', '700160-06', 'Rua P', '26', 'Samambaia', 'Brasília', 'DF', 'larissa.ramos@email.com'),
('Gustavo Moreira', '700170-07', 'Rua Q', '27', 'Gama', 'Brasília', 'DF', 'gustavo.moreira@email.com'),
('Beatriz Cunha', '700180-08', 'Rua R', '28', 'Sobradinho', 'Brasília', 'DF', 'beatriz.cunha@email.com'),
('Marcelo Freitas', '700190-09', 'Rua S', '29', 'Cruzeiro', 'Brasília', 'DF', 'marcelo.freitas@email.com'),
('Sofia Oliveira', '700200-00', 'Rua T', '30', 'Núcleo Bandeirante', 'Brasília', 'DF', 'sofia.oliveira@email.com'),
('Thiago Mendes', '700210-01', 'Rua U', '31', 'Asa Sul', 'Brasília', 'DF', 'thiago.mendes@email.com'),
('Carolina Castro', '700220-02', 'Rua V', '32', 'Asa Norte', 'Brasília', 'DF', 'carolina.castro@email.com'),
('Rodrigo Alves', '700230-03', 'Rua W', '33', 'Taguatinga', 'Brasília', 'DF', 'rodrigo.alves@email.com'),
('Mariana Lopes', '700240-04', 'Rua X', '34', 'Guará', 'Brasília', 'DF', 'mariana.lopes@email.com'),
('Diego Pires', '700250-05', 'Rua Y', '35', 'Ceilândia', 'Brasília', 'DF', 'diego.pires@email.com'),
('Helena Faria', '700260-06', 'Rua Z', '36', 'Samambaia', 'Brasília', 'DF', 'helena.faria@email.com'),
('André Carvalho', '700270-07', 'Rua K', '37', 'Gama', 'Brasília', 'DF', 'andré.carvalho@email.com'),
('Patrícia Santos', '700280-08', 'Rua L', '38', 'Sobradinho', 'Brasília', 'DF', 'patrícia.santos@email.com'),
('Eduardo Rocha', '700290-09', 'Rua M', '39', 'Cruzeiro', 'Brasília', 'DF', 'eduardo.rocha@email.com'),
('Juliana Castro', '700300-00', 'Rua N', '40', 'Núcleo Bandeirante', 'Brasília', 'DF', 'juliana.castro@email.com'),
('Renata Azevedo', '700310-01', 'Rua O', '41', 'Asa Sul', 'Brasília', 'DF', 'renata.azevedo@email.com'),
('Felipe Barbosa', '700320-02', 'Rua P', '42', 'Asa Norte', 'Brasília', 'DF', 'felipe.barbosa@email.com'),
('Aline Ferreira', '700330-03', 'Rua Q', '43', 'Taguatinga', 'Brasília', 'DF', 'aline.ferreira@email.com'),
('Maurício Pinto', '700340-04', 'Rua R', '44', 'Guará', 'Brasília', 'DF', 'maurício.pinto@email.com'),
('Priscila Andrade', '700350-05', 'Rua S', '45', 'Ceilândia', 'Brasília', 'DF', 'priscila.andrade@email.com'),
('Daniel Correia', '700360-06', 'Rua T', '46', 'Samambaia', 'Brasília', 'DF', 'daniel.correia@email.com'),
('Isabela Monteiro', '700370-07', 'Rua U', '47', 'Gama', 'Brasília', 'DF', 'isabela.monteiro@email.com'),
('Leandro Ribeiro', '700380-08', 'Rua V', '48', 'Sobradinho', 'Brasília', 'DF', 'leandro.ribeiro@email.com'),
('Tatiane Lopes', '700390-09', 'Rua W', '49', 'Cruzeiro', 'Brasília', 'DF', 'tatiane.lopes@email.com'),
('Vinícius Araújo', '700400-00', 'Rua X', '50', 'Núcleo Bandeirante', 'Brasília', 'DF', 'vinícius.araújo@email.com');

-- 3. PRODUTO
INSERT INTO PRODUTO (Descricao, Quantidade, Tamanho, Preco, Categoria) VALUES
('Camiseta Azul', 50, 'M', 59.90, 'Vestuário'),
('Calça Jeans', 40, '42', 129.90, 'Vestuário'),
('Tênis Esportivo', 30, '41', 199.90, 'Calçados'),
('Bolsa Feminina', 25, 'U', 149.90, 'Acessórios'),
('Relógio Digital', 15, 'U', 299.90, 'Eletrônicos'),
('Fone Bluetooth', 20, 'U', 199.90, 'Eletrônicos'),
('Jaqueta Couro', 10, 'M', 399.90, 'Vestuário'),
('Chinelo', 60, '39', 29.90, 'Calçados'),
('Notebook Gamer', 5, '15"', 4999.90, 'Eletrônicos'),
('Smartphone', 12, '6"', 2499.90, 'Eletrônicos'),
('Camisa Polo', 35, 'M', 79.90, 'Vestuário'),
('Bermuda Jeans', 25, '42', 99.90, 'Vestuário'),
('Tênis Casual', 20, '40', 159.90, 'Calçados'),
('Bolsa de Couro', 15, 'U', 199.90, 'Acessórios'),
('Smartwatch', 10, 'U', 599.90, 'Eletrônicos'),
('Caixa de Som Bluetooth', 18, 'U', 349.90, 'Eletrônicos'),
('Jaqueta Jeans', 22, 'G', 199.90, 'Vestuário'),
('Sandália Feminina', 40, '37', 79.90, 'Calçados'),
('Tablet 10"', 8, '10"', 1499.90, 'Eletrônicos'),
('Mouse Gamer', 30, 'U', 129.90, 'Eletrônicos'),
('Teclado Mecânico', 20, 'U', 299.90, 'Eletrônicos'),
('Monitor 24"', 12, '24"', 899.90, 'Eletrônicos'),
('Camiseta Preta', 50, 'P', 49.90, 'Vestuário'),
('Tênis de Corrida', 18, '42', 219.90, 'Calçados'),
('Relógio Analógico', 14, 'U', 349.90, 'Acessórios'),
('Carteira Masculina', 25, 'U', 89.90, 'Acessórios'),
('Headset Gamer', 15, 'U', 499.90, 'Eletrônicos'),
('HD Externo 1TB', 10, 'U', 399.90, 'Eletrônicos'),
('Câmera Fotográfica', 6, 'U', 2299.90, 'Eletrônicos'),
('Ventilador', 20, 'U', 149.90, 'Eletrodomésticos');

-- 4. TELEFONE
INSERT INTO TELEFONE (id_cliente, numero) VALUES 
  (1, '61999990001'),
  (2, '61999990002'),
  (3, '61999990003'),
  (4, '61999990004'),
  (5, '61999990005'),
  (6, '61999990006'),
  (7, '61999990007'),
  (8, '61999990008'),
  (9, '61999990009'),
  (10, '61999990010'),
  (11, '61999990011'),
  (12, '61999990012'),
  (13, '61999990013'),
  (14, '61999990014'),
  (15, '61999990015'),
  (16, '61999990016'),
  (17, '61999990017'),
  (18, '61999990018'),
  (19, '61999990019'),
  (20, '61999990020'),
  (21, '61999990021'),
  (22, '61999990022'),
  (23, '61999990023'),
  (24, '61999990024'),
  (25, '61999990025'),
  (26, '61999990026'),
  (27, '61999990027'),
  (28, '61999990028'),
  (29, '61999990029'),
  (30, '61999990030'),
  (31, '61999990031'),
  (32, '61999990032'),
  (33, '61999990033'),
  (34, '61999990034'),
  (35, '61999990035'),
  (36, '61999990036'),
  (37, '61999990037'),
  (38, '61999990038'),
  (39, '61999990039'),
  (40, '61999990040');

-- 5. FORNECEDOR
INSERT INTO FORNECEDOR (Nome, Tipo_de_material, cnpj, cep, Email) VALUES
('Fornecedor A', 'Vestuário', '12345678000101', '70000000', 'fornA@email.com'),
('Fornecedor B', 'Calçados', '22345678000102', '70000001', 'fornB@email.com'),
('Fornecedor C', 'Acessórios', '32345678000103', '70000002', 'fornC@email.com'),
('Fornecedor D', 'Eletrônicos', '42345678000104', '70000003', 'fornD@email.com'),
('Fornecedor E', 'Vestuário', '52345678000105', '70000004', 'fornE@email.com'),
('Fornecedor F', 'Calçados', '62345678000106', '70000005', 'fornF@email.com'),
('Fornecedor G', 'Eletrônicos', '72345678000107', '70000006', 'fornG@email.com'),
('Fornecedor H', 'Acessórios', '82345678000108', '70000007', 'fornH@email.com'),
('Fornecedor I', 'Vestuário', '92345678000109', '70000008', 'fornI@email.com'),
('Fornecedor J', 'Eletrônicos', '10345678000110', '70000009', 'fornJ@email.com');

INSERT INTO TELEFONE (id_fornecedor, numero, tipo) VALUES
(1, '61988880001', 'comercial'),
(2, '61988880002', 'comercial'),
(3, '61988880003', 'comercial'),
(4, '61988880004', 'comercial'),
(5, '61988880005', 'comercial'),
(6, '61988880006', 'comercial'),
(7, '61988880007', 'comercial'),
(8, '61988880008', 'comercial'),
(9, '61988880009', 'comercial'),
(10,'61988880010', 'comercial');


-- 6. PESSOA_FISICA
INSERT INTO PESSOA_FISICA (cpf, rg, fk_ID_cliente) VALUES
('111.111.111-11', '1000001', 1),
('222.222.222-22', '1000002', 2),
('333.333.333-33', '1000003', 3),
('444.444.444-44', '1000004', 4),
('555.555.555-55', '1000005', 5),
('666.666.666-66', '1000006', 6),
('777.777.777-77', '1000007', 7),
('888.888.888-88', '1000008', 8),
('999.999.999-99', '1000009', 9),
('000.000.000-00', '1000010', 10),
('010.000.011-11', '2000011', 11),
('010.000.012-12', '2000012', 12),
('010.000.013-13', '2000013', 13),
('010.000.014-14', '2000014', 14),
('010.000.015-15', '2000015', 15),
('010.000.016-16', '2000016', 16),
('010.000.017-17', '2000017', 17),
('010.000.018-18', '2000018', 18),
('010.000.019-19', '2000019', 19),
('010.000.020-20', '2000020', 20),
('010.000.021-21', '2000021', 21),
('010.000.022-22', '2000022', 22),
('010.000.023-23', '2000023', 23),
('010.000.024-24', '2000024', 24),
('010.000.025-25', '2000025', 25),
('010.000.026-26', '2000026', 26),
('010.000.027-27', '2000027', 27),
('010.000.028-28', '2000028', 28),
('010.000.029-29', '2000029', 29),
('010.000.030-30', '2000030', 30),
('010.000.031-31', '2000031', 31),
('010.000.032-32', '2000032', 32),
('010.000.033-33', '2000033', 33),
('010.000.034-34', '2000034', 34),
('010.000.035-35', '2000035', 35),
('010.000.036-36', '2000036', 36),
('010.000.037-37', '2000037', 37),
('010.000.038-38', '2000038', 38),
('010.000.039-39', '2000039', 39),
('010.000.040-40', '2000040', 40);

-- 7. PESSOA_JURIDICA
INSERT INTO PESSOA_JURIDICA (cnpj, IE, fk_ID_cliente) VALUES
('11.111.111/0001-11', 'IS10001', 1),
('22.222.222/0001-22', 'IS10002', 2),
('33.333.333/0001-33', 'IS10003', 3),
('44.444.444/0001-44', 'IS10004', 4),
('55.555.555/0001-55', 'IS10005', 5),
('66.666.666/0001-66', 'IS10006', 6),
('77.777.777/0001-77', 'IS10007', 7),
('88.888.888/0001-88', 'IS10008', 8),
('99.999.999/0001-99', 'IS10009', 9),
('00.000.000/0001-00', 'IS10010', 10),
('21.467.665/0001-11', 'IS30011', 11),
('22.468.666/0001-12', 'IS30012', 12),
('23.469.667/0001-13', 'IS30013', 13),
('24.470.668/0001-14', 'IS30014', 14),
('25.471.669/0001-15', 'IS30015', 15),
('26.472.670/0001-16', 'IS30016', 16),
('27.473.671/0001-17', 'IS30017', 17),
('28.474.672/0001-18', 'IS30018', 18),
('29.475.673/0001-19', 'IS30019', 19),
('30.476.674/0001-20', 'IS30020', 20),
('31.477.675/0001-21', 'IS30021', 21),
('32.478.676/0001-22', 'IS30022', 22),
('33.479.677/0001-23', 'IS30023', 23),
('34.480.678/0001-24', 'IS30024', 24),
('35.481.679/0001-25', 'IS30025', 25),
('36.482.680/0001-26', 'IS30026', 26),
('37.483.681/0001-27', 'IS30027', 27),
('38.484.682/0001-28', 'IS30028', 28),
('39.485.683/0001-29', 'IS30029', 29),
('40.486.684/0001-30', 'IS30030', 30),
('41.487.685/0001-31', 'IS30031', 31),
('42.488.686/0001-32', 'IS30032', 32),
('43.489.687/0001-33', 'IS30033', 33),
('44.490.688/0001-34', 'IS30034', 34),
('45.491.689/0001-35', 'IS30035', 35),
('46.492.690/0001-36', 'IS30036', 36),
('47.493.691/0001-37', 'IS30037', 37),
('48.494.692/0001-38', 'IS30038', 38),
('49.495.693/0001-39', 'IS30039', 39),
('50.496.694/0001-40', 'IS30040', 40);

-- 8. PEDIDO
INSERT INTO PEDIDO (Data_do_pedido, Status, Valor_total, fk_Cliente_ID_cliente, fk_Pagamento_ID_pagamento) VALUES
('2025-09-01', 'pedido realizado', 299.90, 1, 1),
('2025-09-02', 'pagamento pendente', 59.90, 2, 2),
('2025-09-03', 'pagamento efetuado', 129.90, 3, 3),
('2025-09-04', 'pedido em preparação', 399.90, 4, 4),
('2025-09-05', 'enviado', 199.90, 5, 5),
('2025-09-06', 'entregue', 149.90, 6, 6),
('2025-09-07', 'cancelado', 4999.90, 7, 7),
('2025-09-08', 'pedido realizado', 29.90, 8, 8),
('2025-09-09', 'entregue', 2499.90, 9, 9),
('2025-09-10', 'pedido em preparação', 299.90, 10, 10),
('2025-09-11', 'pedido realizado', 160.00, 11, 11),
('2025-09-12', 'pagamento pendente', 170.00, 12, 12),
('2025-09-13', 'pagamento efetuado', 180.00, 13, 13),
('2025-09-14', 'pedido em preparação', 190.00, 14, 14),
('2025-09-15', 'enviado', 50.00, 15, 15),
('2025-09-16', 'entregue', 60.00, 16, 16),
('2025-09-17', 'cancelado', 70.00, 17, 17),
('2025-09-18', 'pedido realizado', 80.00, 18, 18),
('2025-09-19', 'pagamento pendente', 90.00, 19, 19),
('2025-09-20', 'pagamento efetuado', 100.00, 20, 20),
('2025-09-21', 'pedido em preparação', 110.00, 21, 21),
('2025-09-22', 'enviado', 120.00, 22, 22),
('2025-09-23', 'entregue', 130.00, 23, 23),
('2025-09-24', 'cancelado', 140.00, 24, 24),
('2025-09-25', 'pedido realizado', 150.00, 25, 25),
('2025-09-26', 'pagamento pendente', 160.00, 26, 26),
('2025-09-27', 'pagamento efetuado', 170.00, 27, 27),
('2025-09-28', 'pedido em preparação', 180.00, 28, 28),
('2025-09-29', 'enviado', 190.00, 29, 29),
('2025-09-30', 'entregue', 50.00, 30, 30),
('2025-10-01', 'cancelado', 60.00, 31, 31),
('2025-10-02', 'pedido realizado', 70.00, 32, 32),
('2025-10-03', 'pagamento pendente', 80.00, 33, 33),
('2025-10-04', 'pagamento efetuado', 90.00, 34, 34),
('2025-10-05', 'pedido em preparação', 100.00, 35, 35),
('2025-10-06', 'enviado', 110.00, 36, 36),
('2025-10-07', 'entregue', 120.00, 37, 37),
('2025-10-08', 'cancelado', 130.00, 38, 38),
('2025-10-09', 'pedido realizado', 140.00, 39, 39),
('2025-10-10', 'pagamento pendente', 150.00, 40, 40);

-- 9. PEDIDO_PRODUTO
INSERT INTO PEDIDO_PRODUTO (fk_ID_pedido, fk_ID_produto, Quantidade, preco_unitario) 
VALUES 
(41, 1, 2, 59.90), 
(42, 2, 1, 129.90), 
(43, 3, 1, 199.90), 
(44, 4, 1, 149.90), 
(45, 5, 1, 299.90), 
(46, 6, 2, 199.90), 
(47, 7, 1, 399.90), 
(48, 8, 3, 29.90), 
(49, 9, 1, 4999.90), 
(50, 10, 1, 2499.90), 
(51, 11, 3, 129.90), 
(52, 12, 1, 19.90), 
(53, 20, 2, 149.90), 
(54, 13, 2, 29.90), 
(55, 14, 3, 39.90), 
(56, 22, 2, 169.90), 
(57, 15, 1, 49.90), 
(58, 16, 2, 59.90), 
(59, 24, 2, 39.90), 
(60, 17, 3, 69.90), 
(61, 18, 1, 79.90), 
(62, 26, 2, 59.90), 
(63, 19, 2, 89.90), 
(64, 20, 3, 99.90), 
(65, 28, 2, 79.90), 
(66, 21, 1, 109.90), 
(67, 22, 2, 119.90), 
(68, 30, 2, 99.90), 
(69, 23, 3, 129.90), 
(70, 24, 1, 19.90), 
(71, 2, 2, 119.90), 
(72, 25, 2, 29.90), 
(73, 26, 3, 39.90), 
(74, 4, 2, 139.90), 
(75, 27, 1, 49.90), 
(76, 28, 2, 59.90), 
(77, 6, 2, 159.90), 
(78, 29, 3, 69.90), 
(79, 30, 1, 79.90), 
(80, 8, 2, 29.90), 
(80, 2, 3, 99.90);

-- 10. FORNECEDOR_PRODUTO
INSERT INTO FORNECEDOR_PRODUTO (fk_Fornecedor_ID_fornecedor, fk_Produto_ID_produto, preco_fornecedor, prazo_entrega) VALUES
(1, 1, 39.90, 7),
(2, 2, 89.90, 10),
(3, 3, 149.90, 15),
(4, 4, 99.90, 12),
(5, 5, 199.90, 20),
(6, 6, 129.90, 18),
(7, 7, 250.90, 25),
(8, 8, 15.90, 5),
(9, 9, 3999.90, 30),
(10, 10, 1999.90, 28),
(1, 11, 215.00, 16),
(2, 12, 230.00, 17),
(3, 13, 245.00, 18),
(4, 14, 260.00, 19),
(5, 15, 275.00, 20),
(6, 16, 290.00, 21),
(7, 17, 305.00, 22),
(8, 18, 320.00, 23),
(9, 19, 335.00, 24),
(10, 20, 50.00, 25),
(1, 21, 65.00, 26),
(2, 22, 80.00, 27),
(3, 23, 95.00, 28),
(4, 24, 110.00, 29),
(5, 25, 125.00, 5),
(6, 26, 140.00, 6),
(7, 27, 155.00, 7),
(8, 28, 170.00, 8),
(9, 29, 185.00, 9),
(10, 30, 200.00, 10);

-- 11. AVALIACAO
INSERT INTO AVALIACAO (Nota, Comentario, fk_Cliente_ID_cliente, fk_Produto_ID_produto) VALUES
(5, 'Excelente qualidade!', 1, 1),
(4, 'Muito bom produto.', 2, 2),
(3, 'Atendeu às expectativas.', 3, 3),
(5, 'Recomendo!', 4, 4),
(2, 'Não gostei.', 5, 5),
(4, 'Entrega rápida.', 6, 6),
(5, 'Perfeito!', 7, 7),
(3, 'Poderia ser melhor.', 8, 8),
(5, 'Muito útil.', 9, 9),
(4, 'Ótima compra.', 10, 10),
(2, 'Excelente!', 11, 15),
(3, 'Muito bom.', 12, 16),
(4, 'Bom custo-benefício.', 13, 17),
(5, 'Dentro do esperado.', 14, 18),
(1, 'Não gostei.', 15, 19),
(2, 'Recomendo!', 16, 20),
(3, 'Entrega rápida.', 17, 21),
(4, 'Qualidade mediana.', 18, 22),
(5, 'Superou expectativas.', 19, 23),
(1, 'Voltaria a comprar.', 20, 24),
(2, 'Excelente!', 21, 25),
(3, 'Muito bom.', 22, 26),
(4, 'Bom custo-benefício.', 23, 27),
(5, 'Dentro do esperado.', 24, 28),
(1, 'Não gostei.', 25, 29),
(2, 'Recomendo!', 26, 30),
(3, 'Entrega rápida.', 27, 1),
(4, 'Qualidade mediana.', 28, 2),
(5, 'Superou expectativas.', 29, 3),
(1, 'Voltaria a comprar.', 30, 4),
(2, 'Excelente!', 31, 5),
(3, 'Muito bom.', 32, 6),
(4, 'Bom custo-benefício.', 33, 7),
(5, 'Dentro do esperado.', 34, 8),
(1, 'Não gostei.', 35, 9),
(2, 'Recomendo!', 36, 10),
(3, 'Entrega rápida.', 37, 11),
(4, 'Qualidade mediana.', 38, 12),
(5, 'Superou expectativas.', 39, 13),
(1, 'Voltaria a comprar.', 40, 14);

-- 12. NOTAFISCAL
INSERT INTO NOTAFISCAL (Data_de_emissao, Valor_total, Imposto, Chave_de_acesso, fk_Pedido_ID_pedido) VALUES
('2025-09-01', 299.90, 29.99, '00000000000000000000000000000000000000000001', 41),
('2025-09-02', 59.90, 5.99, '00000000000000000000000000000000000000000002', 42),
('2025-09-03', 129.90, 12.99, '00000000000000000000000000000000000000000003', 43),
('2025-09-04', 399.90, 39.99, '00000000000000000000000000000000000000000004', 44),
('2025-09-05', 199.90, 19.99, '00000000000000000000000000000000000000000005', 45),
('2025-09-06', 149.90, 14.99, '00000000000000000000000000000000000000000006', 46),
('2025-09-07', 4999.90, 499.99, '00000000000000000000000000000000000000000007', 47),
('2025-09-08', 29.90, 2.99, '00000000000000000000000000000000000000000008', 48),
('2025-09-09', 2499.90, 249.99, '00000000000000000000000000000000000000000009', 49),
('2025-09-10', 299.90, 29.99, '00000000000000000000000000000000000000000010', 50),
('2025-09-11', 160.00, 16.00, '00000000000000000000000000000000000000000011', 51),
('2025-09-12', 170.00, 17.00, '00000000000000000000000000000000000000000012', 52),
('2025-09-13', 180.00, 18.00, '00000000000000000000000000000000000000000013', 53),
('2025-09-14', 190.00, 19.00, '00000000000000000000000000000000000000000014', 54),
('2025-09-15', 50.00, 5.00, '00000000000000000000000000000000000000000015', 55),
('2025-09-16', 60.00, 6.00, '00000000000000000000000000000000000000000016', 56),
('2025-09-17', 70.00, 7.00, '00000000000000000000000000000000000000000017', 57),
('2025-09-18', 80.00, 8.00, '00000000000000000000000000000000000000000018', 58),
('2025-09-19', 90.00, 9.00, '00000000000000000000000000000000000000000019', 59),
('2025-09-20', 100.00, 10.00, '00000000000000000000000000000000000000000020', 60),
('2025-09-21', 110.00, 11.00, '00000000000000000000000000000000000000000021', 61),
('2025-09-22', 120.00, 12.00, '00000000000000000000000000000000000000000022', 62),
('2025-09-23', 130.00, 13.00, '00000000000000000000000000000000000000000023', 63),
('2025-09-24', 140.00, 14.00, '00000000000000000000000000000000000000000024', 64),
('2025-09-25', 150.00, 15.00, '00000000000000000000000000000000000000000025', 65),
('2025-09-26', 160.00, 16.00, '00000000000000000000000000000000000000000026', 66),
('2025-09-27', 170.00, 17.00, '00000000000000000000000000000000000000000027', 67),
('2025-09-28', 180.00, 18.00, '00000000000000000000000000000000000000000028', 68),
('2025-09-29', 190.00, 19.00, '00000000000000000000000000000000000000000029', 69),
('2025-09-30', 50.00, 5.00, '00000000000000000000000000000000000000000030', 70),
('2025-10-01', 60.00, 6.00, '00000000000000000000000000000000000000000031', 71),
('2025-10-02', 70.00, 7.00, '00000000000000000000000000000000000000000032', 72),
('2025-10-03', 80.00, 8.00, '00000000000000000000000000000000000000000033', 73),
('2025-10-04', 90.00, 9.00, '00000000000000000000000000000000000000000034', 74),
('2025-10-05', 100.00, 10.00, '00000000000000000000000000000000000000000035', 75),
('2025-10-06', 110.00, 11.00, '00000000000000000000000000000000000000000036', 76),
('2025-10-07', 120.00, 12.00, '00000000000000000000000000000000000000000037', 77),
('2025-10-08', 130.00, 13.00, '00000000000000000000000000000000000000000038', 78),
('2025-10-09', 140.00, 14.00, '00000000000000000000000000000000000000000039', 79),
('2025-10-10', 150.00, 15.00, '00000000000000000000000000000000000000000040', 80);