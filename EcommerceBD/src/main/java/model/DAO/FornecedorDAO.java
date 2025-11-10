package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Fornecedor;

public class FornecedorDAO extends EntidadeBaseDAO<Fornecedor> {

    private Connection connection;

    @Override
    public void inserir(Fornecedor fornecedor) throws SQLException {
        try {
            Long idnovo = gerarIdUnico("fornecedor", "ID_fornecedor");
            fornecedor.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        String sql = "INSERT INTO fornecedor (ID_fornecedor, Nome, Tipo_de_material, cnpj, cep, Email)"
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, fornecedor.getId());
            stmt.setString(2, fornecedor.getNome());
            stmt.setString(3, fornecedor.getTipoMaterial());
            stmt.setString(4, fornecedor.getCnpj());
            stmt.setString(5, fornecedor.getCep());
            stmt.setString(6, fornecedor.getEmail());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir cliente, nenhuma linha afetada");
            }

            System.out.println("[DAO] Cliente inserido com sucesso!");
        }
    }

    @Override
    public void deletar(Fornecedor fornecedor) {
        String sql = "DELETRE FROM fornecedor WHERE ID_fornecedor = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, fornecedor.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao deletar fornecedor: " + e.getMessage());

        }
    }

    @Override
    public void alterar(Fornecedor fornecedor) {
        String sql = "UPDATE fornecedor SET Nome = ?, Tipo_de_material = ?"
                + "cnpj = ?, cep = ?, Email = ? WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getTipoMaterial());
            stmt.setString(3, fornecedor.getCnpj());
            stmt.setString(4, fornecedor.getCep());
            stmt.setString(5, fornecedor.getEmail());
            stmt.setLong(6, fornecedor.getId());

        } catch (Exception e) {
            System.out.println("Erro ao alterar cliente: " + e.getMessage());

        }
    }

    private Fornecedor montarFornecedor(ResultSet rs) throws SQLException {
        // Extrai os valores das colunas do ResultSet
        Long id = rs.getLong("ID_fornecedor");
        String nome = rs.getString("Nome");
        String tipoDeMaterial = rs.getString("Tipo_de_material");
        String cnpj = rs.getString("CNPJ");
        String cep = rs.getString("CEP");
        String email = rs.getString("Email");

        // Retorna um novo objeto Fornecedor
        return new Fornecedor(id, nome, tipoDeMaterial, cnpj, cep, email);
    }

    @Override
    public Fornecedor buscarPorId(Long id) throws SQLException {
        String sql = "SELECT f.ID_fornecedor, f.nome, f.tipo de material,"
                + "f.cnpj, f.cep, f.Email"
                + "FROM fornecedor f "
                + "WHERE f.ID_fornecedor  = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarFornecedor(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Fornecedor> listar() {
        List<Fornecedor> fornecedor = new ArrayList<>();
        
        String sql = "SELECT * FROM fornecedor";
        
        try (Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            
            while (rs.next()) {
                Fornecedor c = new Fornecedor();
                c.setId(rs.getLong("ID_fornecedor"));
                c.setNome(rs.getString("Nome"));
                c.setTipoMaterial(rs.getString("Tipo_de_material"));
                c.setEmail(rs.getString("Email"));
                c.setCnpj(rs.getString("cnpj"));
                c.setCep(rs.getString("cep"));
                
                fornecedor.add(c);
            }
            
        } catch (Exception e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
        
        return fornecedor;
    }

}
