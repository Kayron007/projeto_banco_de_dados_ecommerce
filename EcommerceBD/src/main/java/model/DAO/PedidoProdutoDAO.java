package model.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Pedido;
import model.Produto;
import model.PedidoProduto;

public class PedidoProdutoDAO extends EntidadeBaseDAO<PedidoProduto> {

    private Connection connection;

    public PedidoProdutoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void inserir(PedidoProduto pedidoProduto) throws SQLException {
        try {
            Long novoId = gerarIdUnico("cliente", "id");
            pedidoProduto.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        String sql = "INSERT INTO pedido_produto (fk_ID_pedido, fk_ID_produto, Quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pedidoProduto.getId_pedido().getId());
            stmt.setLong(2, pedidoProduto.getId_produtp().getId());
            stmt.setInt(3, pedidoProduto.getQuantidade());
            stmt.setDouble(4, pedidoProduto.getPrecoUnitario());

            int linhas = stmt.executeUpdate();

            if (linhas == 0) {
                throw new SQLException("Falha ao inserir relação pedido-produto, nenhuma linha afetada.");
            }

            System.out.println("[DAO] PedidoProduto inserido com sucesso!");
        }
    }

    @Override
    public void deletar(PedidoProduto pedidoProduto) {
        String sql = "DELETE FROM pedido_produto WHERE fk_ID_pedido = ? AND fk_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pedidoProduto.getId_pedido().getId());
            stmt.setLong(2, pedidoProduto.getId_produtp().getId());
            stmt.executeUpdate();
            System.out.println("[DAO] PedidoProduto deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar pedido_produto: " + e.getMessage());
        }
    }

    @Override
    public void alterar(PedidoProduto pedidoProduto) {
        String sql = "UPDATE pedido_produto SET Quantidade = ?, preco_unitario = ? WHERE fk_ID_pedido = ? AND fk_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoProduto.getQuantidade());
            stmt.setDouble(2, pedidoProduto.getPrecoUnitario());
            stmt.setLong(3, pedidoProduto.getId_pedido().getId());
            stmt.setLong(4, pedidoProduto.getId_produtp().getId());
            stmt.executeUpdate();

            System.out.println("[DAO] PedidoProduto alterado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar pedido_produto: " + e.getMessage());
        }
    }

    private PedidoProduto montarPedidoProduto(ResultSet rs) throws SQLException {
        Long idPedido = rs.getLong("fk_ID_pedido");
        Long idProduto = rs.getLong("fk_ID_produto");
        int quantidade = rs.getInt("Quantidade");
        double precoUnitario = rs.getDouble("preco_unitario");

        PedidoDAO pedidoDAO = new PedidoDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        Produto produto = produtoDAO.buscarPorId(idProduto);

        return new PedidoProduto(idPedido, pedido, produto, quantidade, quantidade);
    }

    @Override
    public PedidoProduto buscarPorId(Long id) throws SQLException {
        // Como a chave é composta (pedido + produto), não faz sentido buscar só por um id
        throw new UnsupportedOperationException("Use buscarPorChaveComposta(Long idPedido, Long idProduto)");
    }

    public PedidoProduto buscarPorChaveComposta(Long idPedido, Long idProduto) throws SQLException {
        String sql = "SELECT * FROM pedido_produto WHERE fk_ID_pedido = ? AND fk_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            stmt.setLong(2, idProduto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarPedidoProduto(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<PedidoProduto> listar() {
        List<PedidoProduto> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_produto";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(montarPedidoProduto(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar pedido_produto: " + e.getMessage());
        }

        return lista;
    }

    // Método auxiliar: listar todos os produtos de um pedido específico
    public List<PedidoProduto> listarPorPedido(Long idPedido) {
        List<PedidoProduto> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_produto WHERE fk_ID_pedido = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarPedidoProduto(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar produtos do pedido: " + e.getMessage());
        }

        return lista;
    }
}
