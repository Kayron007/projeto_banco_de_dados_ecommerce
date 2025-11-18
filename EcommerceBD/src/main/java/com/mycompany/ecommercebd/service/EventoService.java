package com.mycompany.ecommercebd.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.ecommercebd.model.ConexaoMongoDB;
import org.bson.Document;
import java.util.Date;

/**
 * Serviço para registrar eventos de pedidos no MongoDB
 * Complementa o fluxo transacional do MySQL com auditoria flexível
 */
public class EventoService {
    
    /**
     * Registra um evento relacionado a um pedido
     * Não interrompe o fluxo principal se falhar (fail-safe)
     * 
     * @param pedidoId ID do pedido no MySQL
     * @param clienteId ID do cliente no MySQL
     * @param tipoEvento Tipo do evento (ex: "pedido_criado", "pagamento_confirmado")
     * @param origem Origem do evento (ex: "checkout", "pagamentos")
     * @param payload Dados flexíveis do evento (Document do MongoDB)
     */
    public static void registrarEvento(Long pedidoId, Long clienteId, 
                                      String tipoEvento, String origem, 
                                      Document payload) {
        try {
            // Conecta no MongoDB
            MongoDatabase db = ConexaoMongoDB.conectar();
            if (db == null) {
                System.err.println("⚠️ MongoDB indisponível - evento não registrado");
                return;
            }
            
            // Obtém a coleção de eventos
            MongoCollection<Document> eventos = db.getCollection("pedido_eventos");
            
            // Cria o documento do evento
            Document evento = new Document()
                .append("pedidoId", pedidoId)
                .append("clienteId", clienteId)
                .append("tipoEvento", tipoEvento)
                .append("origem", origem)
                .append("payload", payload)
                .append("dataEvento", new Date());
            
            // Insere no MongoDB
            eventos.insertOne(evento);
            System.out.println("📝 Evento registrado: " + tipoEvento + " [Pedido: " + pedidoId + "]");
            
        } catch (Exception e) {
            // Não quebra o fluxo principal se MongoDB falhar
            System.err.println("⚠️ Erro ao registrar evento (não crítico): " + e.getMessage());
        }
    }
    
    /**
     * Versão simplificada sem payload
     */
    public static void registrarEvento(Long pedidoId, Long clienteId, 
                                      String tipoEvento, String origem) {
        registrarEvento(pedidoId, clienteId, tipoEvento, origem, new Document());
    }
}