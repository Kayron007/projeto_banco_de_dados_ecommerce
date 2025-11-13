package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Produto;

public class ProdutoDAO extends EntidadeBaseDAO<Produto> {

    private Connection connection;

    @Override
    public void inserir(Produto produto) throws SQLException {
        try {
            Long idnovo = gerarIdUnico("produto", "ID_produto");
            produto.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO produto (ID_produto, Descricao, Quantidade, Tamanho, Preco, Categoria) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, produto.getId());
            stmt.setString(2, produto.getDescricao());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setString(4, produto.getTamanho());
            stmt.setDouble(5, produto.getPreco());
            stmt.setString(6, produto.getCategoria());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir produto, nenhuma linha afetada.");
            }

            System.out.println("[DAO] Produto inserido com sucesso!");
        }
    }

    @Override
    public void deletar(Produto produto) {
        String sql = "DELETE FROM produto WHERE ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, produto.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Produto deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar produto: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Produto produto) {
        String sql = "UPDATE produto SET Descricao = ?, Quantidade = ?, Tamanho = ?, Preco = ?, Categoria = ? "
                   + "WHERE ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getDescricao());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setString(3, produto.getTamanho());
            stmt.setDouble(4, produto.getPreco());
            stmt.setString(5, produto.getCategoria());
            stmt.setLong(6, produto.getId());

            stmt.executeUpdate();
            System.out.println("[DAO] Produto alterado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar produto: " + e.getMessage());
        }
    }

    private Produto montarProduto(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_produto");
        String nome = rs.getString("Nome");
        String descricao = rs.getString("Descricao");
        String tamanho = rs.getString("Tamanho");
        String categoria = rs.getString("Categoria");
        Double preco = rs.getDouble("Preco");
        Integer quantidade = rs.getInt("Quantidade");

        return new Produto(id, nome, descricao, tamanho, categoria, preco, quantidade);
    }

    @Override
    public Produto buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarProduto(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();

        String sql = "SELECT * FROM produto";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("ID_produto"));
                p.setDescricao(rs.getString("Descricao"));
                int qtd = rs.getInt("Quantidade");
                p.setQuantidade(rs.wasNull() ? null : qtd);
                p.setTamanho(rs.getString("Tamanho"));
                p.setPreco(rs.getDouble("Preço"));
                p.setCategoria(rs.getString("Categoria"));
                produtos.add(p);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

        return produtos;
    }
}
