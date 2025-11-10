package model.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Cliente;
import model.Fornecedor;
import model.Telefone;

public class TelefoneDAO extends EntidadeBaseDAO<Telefone> {

    private Connection connection;

    public TelefoneDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void inserir(Telefone tel) throws SQLException {
        try {
            Long novoId = gerarIdUnico("cliente", "id");
            tel.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO telefone (id_fornecedor, id_cliente, numero, tipo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, tel.getId_fornecedor().getId());
            stmt.setLong(2, tel.getId_fornecedor().getId());

            stmt.setString(3, tel.getNumero());
            stmt.setString(4, tel.getTipo());
            stmt.executeUpdate();
            System.out.println("[DAO] Telefone inserido com sucesso!");
        }
    }

    @Override
    public void alterar(Telefone tel) {
        String sql = "UPDATE telefone SET numero = ?, tipo = ? WHERE id_telefone = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tel.getNumero());
            stmt.setString(2, tel.getTipo());
            stmt.setLong(3, tel.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Telefone alterado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar telefone: " + e.getMessage());
        }
    }

    @Override
    public void deletar(Telefone tel) {
        String sql = "DELETE FROM telefone WHERE id_telefone = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, tel.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Telefone deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar telefone: " + e.getMessage());
        }
    }

    private Telefone montarTelefone(ResultSet rs) throws SQLException {
        Long idTel = rs.getLong("id_telefone");
        String numero = rs.getString("numero");
        String tipo = rs.getString("tipo");

        Long idFornecedor = rs.getLong("id_fornecedor");
        FornecedorDAO fd = new FornecedorDAO();
        Fornecedor fornecedor = fd.buscarPorId(idFornecedor);
        
        Long idCliente = rs.getLong("fk_ID_cliente");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente cliente = cd.buscarPorId(idCliente);

        return new Telefone(numero, tipo, fornecedor, cliente, idTel);
    }

    @Override
    public Telefone buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM telefone WHERE id_telefone = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarTelefone(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Telefone> listar() {
        List<Telefone> lista = new ArrayList<>();
        String sql = "SELECT * FROM telefone";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(montarTelefone(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar telefones: " + e.getMessage());
        }

        return lista;
    }
}
