// ================================================================
// Script de configuracao do MongoDB para complementar o MySQL
// ================================================================

use('EcommerceRealtime'); // Seleciona (ou cria) a base dedicada a dados semi estruturados

db.createCollection( // Cria a colecao que armazena eventos de pedido
  'pedido_eventos', // Define o nome descritivo da colecao
  { // Inicio das opcoes de criacao
    validator: { // Configura um validador para evitar documentos com formato invalido
      $jsonSchema: { // Especifica um schema JSON para os eventos
        bsonType: 'object', // O documento precisa ser um objeto
        required: ['pedidoId', 'clienteId', 'tipoEvento', 'payload', 'dataEvento'], // Campos obrigatorios para rastrear eventos
        properties: { // Descreve cada propriedade aceita
          pedidoId: { bsonType: 'long', description: 'ID do pedido vindo do MySQL' }, // Vincula ao pedido relacional
          clienteId: { bsonType: 'long', description: 'ID do cliente relacional' }, // Permite agrupar eventos por cliente
          tipoEvento: { bsonType: 'string', description: 'Tipo do evento registrado' }, // Explica o motivo do evento
          origem: { bsonType: 'string', description: 'Fonte do evento dentro do sistema' }, // Aponta o subsistema que gerou o evento
          payload: { bsonType: 'object', description: 'Dados flexiveis associados ao evento' }, // Guarda dados semi estruturados
          dataEvento: { bsonType: 'date', description: 'Momento exato do evento' } // Garante ordenacao temporal
        } // Finaliza descricao das propriedades
      } // Conclui a definicao do schema JSON
    }, // Termina a secao de validacao
    collation: { locale: 'pt', strength: 1 } // Usa collation em portugues para ordenacoes e comparacoes
  } // Termina objeto de configuracao da colecao
); // Finaliza criacao de pedido_eventos

db.pedido_eventos.createIndex( // Cria um indice composto para consultas por pedido ordenadas por data
  { pedidoId: 1, dataEvento: -1 }, // Ordena por pedido crescente e por data decrescente
  { name: 'idx_pedido_data' } // Nomeia o indice para facilitar manutencao
); // Finaliza criacao do indice

db.pedido_eventos.createIndex( // Adiciona indice para buscas por cliente e tipo de evento
  { clienteId: 1, tipoEvento: 1 }, // Agrupa eventos do cliente e filtra rapidamente por acao
  { name: 'idx_cliente_tipo' } // Nome do indice secundario
); // Termina criacao do segundo indice

db.createCollection( // Cria colecao para cache de catalogo dinamico
  'catalogo_cache', // Nome da colecao de produtos
  { // Inicio das opcoes
    capped: false, // Permite crescimento ilimitado do cache
    collation: { locale: 'pt', strength: 1 } // Mantem a mesma collation para consistencia
  } // Final das opcoes
); // Finaliza criacao da colecao catalogo_cache

db.catalogo_cache.createIndex( // Cria indice textual para buscas rapidas por descricao
  { descricao: 'text', categorias: 1 }, // Indexa descricao e categorias simultaneamente
  { name: 'idx_texto_catalogo', default_language: 'portuguese' } // Configura lingua portuguesa para stemming
); // Conclui criacao do indice textual

db.pedido_eventos.insertMany( // Insere eventos de exemplo para demonstrar o fluxo
  [ // Inicio do array de documentos
    { // Primeiro documento de evento
      pedidoId: NumberLong('20241115001'), // ID do pedido referenciado no MySQL
      clienteId: NumberLong('20241115010'), // ID do cliente associado
      tipoEvento: 'pedido_criado', // Tipo do evento
      origem: 'checkout', // Subsistema que disparou o evento
      payload: { valor: 399.9, itens: 3, canal: 'web' }, // Dados flexiveis relacionados ao pedido
      dataEvento: new Date('2025-11-15T17:30:00Z') // Momento do evento
    }, // Finaliza primeiro evento
    { // Segundo evento relacionado ao mesmo pedido
      pedidoId: NumberLong('20241115001'), // Mesmo ID para manter o encadeamento
      clienteId: NumberLong('20241115010'), // Mesmo cliente
      tipoEvento: 'pagamento_confirmado', // Evento de confirmacao
      origem: 'pagamentos', // Origem do evento
      payload: { meio: 'cartao', autorizacao: 'A1B2C3' }, // Detalhes flexiveis do pagamento
      dataEvento: new Date('2025-11-15T17:32:15Z') // Data aproximada da confirmacao
    }, // Finaliza segundo evento
    { // Terceiro evento para outro pedido
      pedidoId: NumberLong('20241116005'), // Novo pedido
      clienteId: NumberLong('20241115011'), // Outro cliente
      tipoEvento: 'pedido_cancelado', // Evento de cancelamento
      origem: 'suporte', // Origem do cancelamento
      payload: { motivo: 'cliente desistiu', canal: 'chat' }, // Motivo armazenado de forma flexivel
      dataEvento: new Date('2025-11-16T12:05:00Z') // Data do cancelamento
    } // Finaliza terceiro evento
  ] // Final do array
); // Conclui insert de exemplo

db.catalogo_cache.insertMany( // Cria snapshots de produtos para APIs de leitura
  [ // Inicio do array de documentos
    { // Primeiro cache
      produtoId: NumberLong('501'), // ID original do produto MySQL
      descricao: 'Tenis esportivo leve', // Descricao amigavel
      categorias: ['calcados', 'corrida'], // Categorias variadas
      preco: 299.9, // Preco atual usado no front-end
      atualizadoEm: new Date('2025-11-15T16:00:00Z') // Momento da geracao do snapshot
    }, // Termina primeiro documento
    { // Segundo cache
      produtoId: NumberLong('502'), // ID do produto
      descricao: 'Mochila impermeavel 25L', // Descricao
      categorias: ['acessorios', 'aventura'], // Categorias relevantes
      preco: 189.5, // Valor praticado
      atualizadoEm: new Date('2025-11-15T16:05:00Z') // Data da foto do catalogo
    } // Finaliza segundo documento
  ] // Final do array
); // Finaliza insercao de snapshots

const pipelineStatusAtual = [ // Pipeline que obtem o ultimo evento de cada pedido
  { $sort: { pedidoId: 1, dataEvento: -1 } }, // Ordena eventos por pedido e data descendente
  { $group: { _id: '$pedidoId', ultimoEvento: { $first: '$$ROOT' } } }, // Agrupa pelos pedidos pegando o primeiro registro (mais recente)
  { $project: { _id: 0, pedidoId: '$_id', tipoEvento: '$ultimoEvento.tipoEvento', dataEvento: '$ultimoEvento.dataEvento', origem: '$ultimoEvento.origem' } } // Projeta somente campos relevantes
]; // Finaliza declaracao da pipeline

db.pedido_eventos.aggregate( // Executa a pipeline acima
  pipelineStatusAtual, // Informa o array de operacoes
  { allowDiskUse: true } // Autoriza uso de disco para conjuntos grandes
).forEach(printjson); // Mostra os resultados em formato JSON

function registrarEventoPedido(evento) { // Funcao auxiliar para inserir eventos garantindo consistencia
  if (!evento.pedidoId || !evento.clienteId) { // Confere se IDs obrigatorios foram informados
    throw new Error('pedidoId e clienteId sao obrigatorios'); // Lanca erro caso falte informacao
  } // Finaliza validacao
  evento.dataEvento = evento.dataEvento || new Date(); // Define data atual caso nao venha preenchida
  return db.pedido_eventos.insertOne(evento); // Persiste o evento reutilizando o schema configurado
} // Finaliza a funcao registrarEventoPedido

registrarEventoPedido({ // Demonstra o uso da funcao utilitaria
  pedidoId: NumberLong('20241117009'), // ID do pedido monitorado
  clienteId: NumberLong('20241115012'), // ID do cliente
  tipoEvento: 'pedido_em_preparacao', // Evento registrado
  origem: 'logistica', // Subsistema que atualizou o status
  payload: { previsaoEnvio: '2025-11-18' } // Dados flexiveis associados
}); // Executa a insercao com a funcao acima

db.catalogo_cache.aggregate( // Exemplo de pipeline para montar vitrine por categoria
  [ // Inicio do pipeline
    { $unwind: '$categorias' }, // Explode categorias para facilitar filtros
    { $group: { _id: '$categorias', produtos: { $push: { produtoId: '$produtoId', descricao: '$descricao', preco: '$preco' } } } }, // Agrupa por categoria acumulando os produtos
    { $project: { _id: 0, categoria: '$_id', produtos: 1 } } // Projeta nome da categoria e lista de produtos
  ] // Final da pipeline
).forEach(printjson); // Exibe colecao agrupada que pode abastecer paginas estaticas
