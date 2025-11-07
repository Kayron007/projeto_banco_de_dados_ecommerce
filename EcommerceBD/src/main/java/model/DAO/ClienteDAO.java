package model.DAO;

import model.Cliente;
import model.Pessoa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {
    private Connection connection;

    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    public void inserir(Cliente cliente) throws SQLException {
        // PASSO 1: Normaliza dados
        cliente.normalizar();
        
        // PASSO 2: Valida dados
        cliente.validar();
        if (cliente.getSenha() != null) {
            cliente.validarSenha();
        }
        
        // PASSO 3: Verifica se email já existe
        if (emailExistente(cliente.getEmail())) {
            throw new SQLException("Email já cadastrado no sistema!");
        }
        
        // PASSO 4: Gera ID único (método herdado de EntidadeBase)
        cliente.gerarIdUnico(connection);
        
        System.out.println("[DAO] ID gerado: " + cliente.getId());
        
        // PASSO 5: Insere no banco
        String sql = "INSERT INTO cliente (id, id_pessoa, nome, cep, logradouro, " +
                     "numero, bairro, estado, email, senha) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cliente.getId());
            stmt.setLong(2, cliente.getId_pessoa().getId());
            stmt.setString(3, cliente.getNome());
            stmt.setString(4, cliente.getCep());
            stmt.setString(5, cliente.getLogradouro());
            stmt.setString(6, cliente.getNumero());
            stmt.setString(7, cliente.getBairro());
            stmt.setString(8, cliente.getEstado());
            stmt.setString(9, cliente.getEmail());
            stmt.setString(10, cliente.getSenha());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir cliente, nenhuma linha afetada");
            }
            
            System.out.println("[DAO] Cliente inserido com sucesso!");
        }
    }

    /**
     * Busca cliente por ID.
     * 
     * @param id ID do cliente
     * @return Cliente encontrado ou null se não existir
     * @throws SQLException se houver erro no banco
     */
    public Cliente buscarPorId(Long id) throws SQLException {
        String sql = "SELECT c.id, c.id_pessoa, c.nome, c.cep, c.logradouro, " +
                     "c.numero, c.bairro, c.estado, c.email, c.senha " +
                     "FROM cliente c " +
                     "WHERE c.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarCliente(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Busca cliente por email.
     * 
     * @param email Email do cliente
     * @return Cliente encontrado ou null se não existir
     * @throws SQLException se houver erro no banco
     */
    public Cliente buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT c.id, c.id_pessoa, c.nome, c.cep, c.logradouro, " +
                     "c.numero, c.bairro, c.estado, c.email, c.senha " +
                     "FROM cliente c " +
                     "WHERE c.email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarCliente(rs);
                }
            }
        }
        
        return null;
    }
    
    private boolean emailExistente(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Cliente montarCliente(ResultSet rs) throws SQLException {
        // Cria objeto Pessoa apenas com ID (simplificado)
        // Em produção, você pode fazer JOIN e buscar dados completos da pessoa
        Pessoa pessoa = new Pessoa();
        pessoa.setId(rs.getLong("id_pessoa"));
        
        // Extrai os valores de cada atributo
        Long id = rs.getLong("id");
        String nome = rs.getString("Nome");
        String email = rs.getString("Email");
        String senha = rs.getString("Senha");
        String cep = rs.getString("cep");
        String logradouro = rs.getString("Logradouro");
        String numero = rs.getString("Numero");
        String bairro = rs.getString("Bairro");
        String estado = rs.getString("Estado");

        return new Cliente(id, pessoa, nome, email, senha, cep, logradouro, numero, bairro, estado);
    }
}