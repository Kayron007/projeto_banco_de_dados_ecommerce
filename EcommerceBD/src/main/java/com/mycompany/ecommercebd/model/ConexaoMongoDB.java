package com.mycompany.ecommercebd.model;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Classe responsável pela conexão com MongoDB
 * Complementa o MySQL para dados semi-estruturados (eventos e cache)
 */
public class ConexaoMongoDB {
    
    // String de conexão padrão do MongoDB (localhost, porta 27017)
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    
    // Nome do banco de dados criado pelo script
    private static final String DATABASE_NAME = "EcommerceRealtime";
    
    // Cliente singleton para reutilizar conexões
    private static MongoClient mongoClient;
    
    /**
     * Estabelece conexão com MongoDB e retorna o database
     * Reutiliza a conexão existente se já estiver aberta
     * 
     * @return Database do MongoDB
     */
    public static MongoDatabase conectar() {
        try {
            // Cria cliente apenas uma vez (singleton)
            if (mongoClient == null) {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                System.out.println("✅ Conexão MongoDB estabelecida");
            }
            return mongoClient.getDatabase(DATABASE_NAME);
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar no MongoDB: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Fecha a conexão com MongoDB
     * Deve ser chamado ao encerrar a aplicação
     */
    public static void fechar() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            System.out.println("🔒 Conexão MongoDB fechada");
        }
    }
    
    /**
     * Testa a conexão com MongoDB
     * Útil para debug e validação
     * 
     * @return true se conectou com sucesso
     */
    public static boolean testarConexao() {
        try {
            MongoDatabase db = conectar();
            if (db != null) {
                // Tenta listar coleções como teste
                db.listCollectionNames().first();
                System.out.println("✅ MongoDB: Conexão testada com sucesso!");
                System.out.println("📊 Coleções disponíveis:");
                for (String nome : db.listCollectionNames()) {
                    System.out.println("   - " + nome);
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Falha no teste de conexão MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}