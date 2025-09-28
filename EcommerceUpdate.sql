-- NOTA: SUBSTITUIR os 'ex' PELOS ID'S REAIS DO PAGAMENTO E PEDIDO DO BANCO;

/* Sincronizar PREÇO_UNITARIO de pedidos com preço do produto */
START TRANSACTION;
UPDATE pedido_produto pp
JOIN produto p 
	ON pp.fk_ID_produto = p.ID_produto
JOIN pedido pd
	ON pp.fk_ID_pedido = pd.ID_pedido
SET pp.preco_unitario = p.Preco 
WHERE pd.Status IN ('pedido realizado', 'pagamento pendente');
COMMIT;

/* Recalcular Valor_total dos Pedidos a partir dos itens */
START TRANSACTION;
UPDATE pedido p
JOIN (
	SELECT fk_ID_pedido
    AS pid, COALESCE(SUM(preco_unitario * Quantidade), 0) AS total
    FROM pedido_produto
    GROUP BY fk_ID_pedido
) s ON p.ID_pedido = s.pid
SET p.Valor_total = s.total;
COMMIT;

/* Fluxo transacional: Confirmação de pagamento */ 
START TRANSACTION;

-- marcar caso pendente
UPDATE pagamento
SET Status = 'pago'
WHERE ID_pagamento = 20 -- ID_ex
	AND (Status IS NULL OR Status = 'pagamento pendente');

-- atualizar pedido ligado ao pagamento
UPDATE pedido
SET Status = 'pagamento efetuado', fk_Pagamento_ID_pagamento = 20 -- ID_ex
WHERE fk_Pagamento_ID_pagamento = 20; -- ID_ex

-- reduzir estoque conforme itens do pedido 'x'
UPDATE produto p
JOIN pedido_produto pp
	ON p.ID_produto = pp.fk_ID_produto
SET p.Quantidade = p.Quantidade - pp.Quantidade
WHERE pp.fk_ID_pedido = 200;

COMMIT;

/* Transições de status - pedido manuais */
-- pagamento pendente -> pagamento efetuado
UPDATE pedido
SET Status = 'pagamento efetuado'
WHERE ID_pedido = 101 -- ID_ex
	AND Status = 'pagamento pendente';

-- pagamento efetuado -> pedido em preparação
UPDATE pedido
SET Status = 'pedido em preparação'
WHERE ID_pedido = 101 -- ID_ex
	AND Status = 'pagamento efetuado';
    
-- pedido em preparação -> pedido enviado
UPDATE pedido
SET Status = 'enviado'
WHERE ID_pedido = 101 -- ID_ex
	AND Status = 'pedido em preparação';
    
-- pedido enviado -> entregue
UPDATE pedido
SET Status = 'entregue'
WHERE ID_pedido = 101 -- ID_ex
	AND Status = 'enviado';
    
-- cancelar pedido
UPDATE pedido
SET Status = 'cancelado'
WHERE ID_pedido = 101 -- ID_ex
	AND Status <> 'entregue';
    
/* Atualizações de pagamento (estorno/cancelamento) */
-- cancelar pagamento
UPDATE pagamento
SET Status = 'cancelado'
WHERE ID_pagamento = 30 -- ID_ex
	AND Status IN ('pagamento pendente', 'pago');
    
-- estornar valor do pagamento
UPDATE pagamento
SET Status = 'estornado'
WHERE ID_pagamento = 30 -- ID_ex
	AND Status = 'pago';
    
/* NOTA FISCAL: Ajustar Valor_total e atribuir Chave_de_acesso */
/* (pode ser feito isoladamente; se for parte de emissão completa, agrupar em transação) */

UPDATE notafiscal nf
JOIN pedido p
	ON nf.fk_Pedido_ID_pedido = p.ID_pedido
SET nf.Valor_total = p.Valor_total
WHERE nf.ID_nota_fiscal = 10; -- ID_ex

UPDATE notafiscal
SET Chave_de_acesso = '12345678901234567890123456789012345678901234'
WHERE ID_nota_fiscal = 10 -- ID_ex
	AND (Chave_de_acesso IS NULL OR Chave_de_acesso = '');
    
/* Correção de integridade referencial (FKs órfãos) */
/* (isoladas; se fizer várias correções massivas, envolver em transação) */

-- limpar fk_pagamento inválidos
START TRANSACTION;
UPDATE pedido p
LEFT JOIN pagamento pg
	ON p.fk_Pagamento_ID_pagamento = pg.ID_pagamento
SET p.fk_Pagamento_ID_pagamento = NULL
WHERE pg.ID_pagamento IS NULL
	AND p.fk_Pagamento_ID_pagamento IS NOT NULL;
COMMIT;

/* Reposição/Estorno de estoque */
-- reposição por entrada de mercadoria
UPDATE produto
SET Quantidade = Quantidade + 100 -- Ex
WHERE ID_produto = 5; -- ID_ex

-- estorno por devolução
START TRANSACTION;
UPDATE produto pr
JOIN (
	SELECT fk_ID_produto AS pid, COALESCE(SUM(Quantidade), 0) AS soma_dev
    FROM pedido_produto
    WHERE fk_ID_pedido = 300 -- ID_ex
    GROUP BY fk_ID_produto
) dev ON pr.ID_produto = dev.pid
SET pr.Quantidade = pr.Quantidade + dev.soma_dev;
COMMIT;

/* Aplicar promoção no catálogo e replicar em pedidos não finalizados */
START TRANSACTION;

-- aplicar desconto no catálogo
UPDATE produto
SET Preco = ROUND(COALESCE(Preco, 0) * 0.90, 2) -- Exs
WHERE Categoria = 'Roupas';

-- reaplicar em pedidos não finalizados
UPDATE pedido_produto pp
JOIN produto p
	ON pp.fk_ID_produto = p.ID_produto
JOIN pedido pd
	ON pp.fk_ID_pedido = pd.ID_pedido
SET pp.preco_unitario = p.Preco
WHERE pd.Status IN ('pedido realizado', 'pagamento pendente');

COMMIT;

/* Atualizar fornecedor_produto (preço do fornecedor/prazo de entrega) */
UPDATE fornecedor_produto
SET preco_fornecedor  = 12.50, prazo_entrega = 7 -- Exs
WHERE fk_Fornecedor_ID_fornecedor = 2 -- ID_ex 
	AND fk_Produto_ID_produto = 10; -- ID_ex
    
/* Correções em itens de pedido (quantidade/preço) */
START TRANSACTION;

-- correção do item
UPDATE pedido_produto
SET Quantidade = 3, preco_unitario = 29.90 -- Exs
WHERE fk_ID_pedido = 400 -- ID_ex
	AND fk_ID_produto = 7; -- ID_ex
    
-- recalcular valor_total do pedido x
UPDATE pedido p
JOIN (
	SELECT fk_ID_pedido AS pid, COALESCE(SUM(preco_unitario * Quantidade), 0) AS total
    FROM pedido_produto
    WHERE fk_ID_pedido = 400 -- ID_ex
    GROUP BY fk_ID_pedido
) s ON p.ID_pedido = s.pid
SET p.Valor_total = s.total
WHERE p.ID_pedido = 400; -- ID_ex

COMMIT;

/* Atualização a partir de tabela temporária (feed externo de valores) */
CREATE TEMPORARY TABLE IF NOT EXISTS tmp_precos (
	ID_produto INT PRIMARY KEY,
    novo_preco DECIMAL(10,2)
);

-- inserção manual do feed (substituir por carga real)
INSERT INTO tmp_precos (ID_produto, novo_preco)
VALUES (1, 49.90), (2, 19.90);

-- aplicar preços vindos do feed
START TRANSACTION;
UPDATE produto p
JOIN tmp_precos t
	ON p.ID_produto = t.ID_produto
SET p.Preco = t.novo_preco;
COMMIT;

DROP TEMPORARY TABLE IF EXISTS tmp_precos;

/* Verificações pós-update (consultas de auditoria operacional) */
SELECT p.ID_pedido, p.Valor_total, s.total
FROM pedido p
LEFT JOIN (
	SELECT fk_ID_pedido AS pid, COALESCE(SUM(preco_unitario * Quantidade), 0) AS total
	FROM pedido_produto
    GROUP BY fk_ID_pedido
) s ON p.ID_pedido = s.pid
WHERE p.Valor_total <> COALESCE(s.total, 0);

SELECT ID_produto, Quantidade FROM produto WHERE Quantidade < 0;