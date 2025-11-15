package com.mycompany.ecommercebd.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mycompany.ecommercebd.model.Cliente;

import java.util.ArrayList;

public class ClienteDAO extends EntidadeBaseDAO<Cliente>{
    private Connection connection;

    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void inserir(Cliente cliente) throws SQLException {
        // PASSO 1: Normaliza dados
        cliente.normalizar();
        
        // PASSO 2: Valida dados
        cliente.validar();
        
        // PASSO 3: Verifica se email já existe
        if (emailExistente(cliente.getEmail())) {
            throw new SQLException("Email já cadastrado no sistema!");
        }
        
        // PASSO 4: Gera ID único (método herdado de EntidadeBase)
        try {
            Long novoId = gerarIdUnico("cliente", "id");
            cliente.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("[DAO] ID gerado: " + cliente.getId());
        
        // PASSO 5: Insere no banco
        String sql = "INSERT INTO cliente (ID, Tipo, Nome, Email, Senha, CEP, Cidade, Logradouro, Numero, Bairro, Estado)" +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cliente.getId());
            stmt.setString(2, cliente.getTipo());
            stmt.setString(3, cliente.getNome());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getSenha());
            stmt.setString(6, cliente.getCep());
            stmt.setString(7, cliente.getCidade());
            stmt.setString(8, cliente.getLogradouro());
            stmt.setString(9, cliente.getNumero());
            stmt.setString(10, cliente.getBairro());
            stmt.setString(11, cliente.getEstado());
            
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
        String sql = "SELECT c.id, c.tipo, c.nome, c.cep, c.cidade, c.logradouro, " +
                     "c.numero, c.bairro, c.estado, c.email, c.senha " +
                     "FROM cliente c " +
                     "WHERE c.ID_cliente = ?";
        
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
        String sql = "SELECT c.id, c.tipo, c.nome, c.cep, c.cidade, c.logradouro, " +
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
    
    public boolean emailExistente(String email) throws SQLException {
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
        String tipo = rs.getString("Tipo");
        
        // Extrai os valores de cada atributo
        Long id = rs.getLong("ID");
        String nome = rs.getString("Nome");
        String email = rs.getString("Email");
        String senha = rs.getString("Senha");
        String cep = rs.getString("CEP");
        String cidade = rs.getString("Cidade");
        String logradouro = rs.getString("Logradouro");
        String numero = rs.getString("Numero");
        String bairro = rs.getString("Bairro");
        String estado = rs.getString("Estado");

        return new Cliente(id, tipo, nome, email, senha, cep, cidade, logradouro, numero, bairro, estado);
    }

    @Override
    public void deletar(Cliente obj) {
        String sql = "DELETE FROM cliente WHERE ID_cliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, obj.getId());
            stmt.executeUpdate();

            System.out.println("Cliente deletado com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao deletar cliente: " + e.getMessage());
        }    
    }

    @Override
    public void alterar(Cliente cliente) {
        String sql = "UPDATE cliente SET Tipo = ?, Nome = ?, Email = ?, Senha = ?, CEP = ?, Cidade = ?,"
                + " Logradouro = ?, Numero = ?, Bairro = ?, Estado = ? WHERE ID = ?";
        
        try {
            cliente.normalizar();
            cliente.validar();

            try (PreparedStatement stmt = connection.prepareStatement(sql)){
                
                stmt.setString(1, cliente.getTipo());
                stmt.setString(2, cliente.getNome());
                stmt.setString(3, cliente.getEmail());
                stmt.setString(4, cliente.getSenha());
                stmt.setString(5, cliente.getCep());
                stmt.setString(6, cliente.getCidade());
                stmt.setString(7, cliente.getLogradouro());
                stmt.setString(8, cliente.getNumero());
                stmt.setString(9, cliente.getBairro());
                stmt.setString(10, cliente.getEstado());
                stmt.setLong(11, cliente.getId());
            }
        } catch (Exception e) {
            System.out.println("Erro ao alterar cliente: " + e.getMessage());
        }
    }
    
    @Override
    public List<Cliente> listar () {
        List<Cliente> cliente = new ArrayList<>();
        
        String sql = "SELECT * FROM cliente";
        
        try (Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("ID"));
                c.setNome(rs.getString("Nome"));
                c.setTipo(rs.getString("Tipo"));
                c.setEmail(rs.getString("Email"));
                c.setSenha(rs.getString("Senha"));
                c.setNumero(rs.getString("Numero"));
                c.setCep(rs.getString("CEP"));
                c.setLogradouro(rs.getString("Logradouro"));
                c.setBairro(rs.getString("Bairro"));
                c.setEstado(rs.getString("Estado"));
                c.setCidade(rs.getString("Cidade"));
                
                cliente.add(c);
            }
            
        } catch (Exception e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
        return cliente;
    }
}

