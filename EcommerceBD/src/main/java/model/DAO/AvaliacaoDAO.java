/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Avaliacao;
import model.Cliente;
import model.Cliente;
import model.Conexao;
import model.Fornecedor;
import model.Pedido;
import model.Produto;
import model.Fornecedor;
import model.Pedido;
import model.Produto;

/**
 *
 * @author gustavo
 */
public class AvaliacaoDAO extends EntidadeBaseDAO<Avaliacao> {

    private Connection connection;
public class AvaliacaoDAO extends EntidadeBaseDAO<Avaliacao> {

    private Connection connection;

    @Override
    public void inserir(Avaliacao avaliacao) throws SQLException {
        avaliacao.normalizar();
        avaliacao.validar();

        try {
            Long idnovo = gerarIdUnico("avaliacao", "ID_avaliacao");
            avaliacao.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO avaliacao (ID_avaliacao, fk_ID_cliente, fk_ID_produto, comentario, nota) VALUES (?,?,?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliacao.getId());
            stmt.setLong(2, avaliacao.getId_cliente().getId());
            stmt.setLong(3, avaliacao.getId_produto().getId());
            stmt.setString(4, avaliacao.getComentario());
            stmt.setShort(5, avaliacao.getNota());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Falha ao inserir avaliação, nenhuma linha afetada");
        }
    public void inserir(Avaliacao avaliacao) throws SQLException {
        avaliacao.normalizar();
        avaliacao.validar();

        try {
            Long idnovo = gerarIdUnico("avaliacao", "ID_avaliacao");
            avaliacao.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO avaliacao (ID_avaliacao, fk_ID_cliente, fk_ID_produto, comentario, nota) VALUES (?,?,?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliacao.getId());
            stmt.setLong(2, avaliacao.getId_cliente().getId());
            stmt.setLong(3, avaliacao.getId_produto().getId());
            stmt.setString(4, avaliacao.getComentario());
            stmt.setShort(5, avaliacao.getNota());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Falha ao inserir avaliação, nenhuma linha afetada");
        }
    }

    @Override
    public void deletar(Avaliacao avaliacao) {
        String sql = "DELETE FRMO avaliacao WHERE ID_avaliacao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliacao.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao deletar avaliação " + e.getMessage());
        }
    public void deletar(Avaliacao avaliacao) {
        String sql = "DELETE FRMO avaliacao WHERE ID_avaliacao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliacao.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao deletar avaliação " + e.getMessage());
        }
    }

    @Override
    public void alterar(Avaliacao avaliacao) {
        String sql = "UPDATE avaliacao SET comentario = ?, nota = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, avaliacao.getComentario());
            stmt.setShort(2, avaliacao.getNota());
        } catch (Exception e) {
            System.out.println("Erro ao deletar avaliação " + e.getMessage());
        }
    public void alterar(Avaliacao avaliacao) {
        String sql = "UPDATE avaliacao SET comentario = ?, nota = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, avaliacao.getComentario());
            stmt.setShort(2, avaliacao.getNota());
        } catch (Exception e) {
            System.out.println("Erro ao deletar avaliação " + e.getMessage());
        }
    }

    @Override
    public List<Avaliacao> listar() {
        List<Avaliacao> notas = new ArrayList<>();
        String sql = "SELECT * FROM notafiscal";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Avaliacao nf = montarFornecedor(rs);
                notas.add(nf);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar notas fiscais: " + e.getMessage());
        }

        return notas;
    }

    private Avaliacao montarFornecedor(ResultSet rs) throws SQLException {
        // Extrai os valores das colunas do ResultSet
        Long id = rs.getLong("ID_avaliacao");

        Long id_cliente = rs.getLong("fk_ID_cliente");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente c = cd.buscarPorId(id_cliente);

        Long id_produto = rs.getLong("fk_ID_produto");
        ProdutoDAO pd = new ProdutoDAO();
        Produto p = pd.buscarPorId(id_produto);

        String comentario = rs.getString("comentario");
        Short nota = rs.getShort("nota");

        // Retorna um novo objeto Fornecedor
        return new Avaliacao(id, nota, comentario, p, c);
    public List<Avaliacao> listar() {
        List<Avaliacao> notas = new ArrayList<>();
        String sql = "SELECT * FROM notafiscal";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Avaliacao nf = montarFornecedor(rs);
                notas.add(nf);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar notas fiscais: " + e.getMessage());
        }

        return notas;
    }

    private Avaliacao montarFornecedor(ResultSet rs) throws SQLException {
        // Extrai os valores das colunas do ResultSet
        Long id = rs.getLong("ID_avaliacao");

        Long id_cliente = rs.getLong("fk_ID_cliente");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente c = cd.buscarPorId(id_cliente);

        Long id_produto = rs.getLong("fk_ID_produto");
        ProdutoDAO pd = new ProdutoDAO();
        Produto p = pd.buscarPorId(id_produto);

        String comentario = rs.getString("comentario");
        Short nota = rs.getShort("nota");

        // Retorna um novo objeto Fornecedor
        return new Avaliacao(id, nota, comentario, p, c);
    }

    @Override
    public Avaliacao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT a.ID";

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

}
