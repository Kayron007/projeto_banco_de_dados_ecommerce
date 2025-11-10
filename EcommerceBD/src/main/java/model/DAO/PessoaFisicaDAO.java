package model.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Cliente;
import model.PessoaFisica;

public class PessoaFisicaDAO extends EntidadeBaseDAO<PessoaFisica> {

    private Connection connection;

    public PessoaFisicaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void inserir(PessoaFisica pf) throws SQLException {
        try {
            Long novoId = gerarIdUnico("cliente", "id");
            pf.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO pessoa_fisica (fk_ID_cliente, cpf, rg) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pf.getId_cliente().getId());
            stmt.setString(2, pf.getCpf());
            stmt.setString(3, pf.getRg());
            stmt.executeUpdate();
            System.out.println("[DAO] Pessoa Física inserida com sucesso!");
        }
    }

    @Override
    public void alterar(PessoaFisica pf) {
        String sql = "UPDATE pessoa_fisica SET cpf = ?, rg = ? WHERE fk_ID_cliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pf.getCpf());
            stmt.setString(2, pf.getRg());
            stmt.setLong(3, pf.getId_cliente().getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Pessoa Física alterada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar pessoa física: " + e.getMessage());
        }
    }

    @Override
    public void deletar(PessoaFisica pf) {
        String sql = "DELETE FROM pessoa_fisica WHERE fk_ID_cliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pf.getId_cliente().getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Pessoa Física deletada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar pessoa física: " + e.getMessage());
        }
    }

    private PessoaFisica montarPessoaFisica(ResultSet rs) throws SQLException {
        Long idCliente = rs.getLong("fk_ID_cliente");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente cliente = cd.buscarPorId(idCliente);

        String cpf = rs.getString("cpf");
        String rg = rs.getString("rg");

        return new PessoaFisica(idCliente, cliente, cpf, rg);
    }

    @Override
    public PessoaFisica buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM pessoa_fisica WHERE fk_ID_cliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarPessoaFisica(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<PessoaFisica> listar() {
        List<PessoaFisica> lista = new ArrayList<>();
        String sql = "SELECT * FROM pessoa_fisica";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(montarPessoaFisica(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar pessoas físicas: " + e.getMessage());
        }

        return lista;
    }
}
