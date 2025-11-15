package com.mycompany.ecommercebd.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Pagamento;
import com.mycompany.ecommercebd.model.Pedido;

public class PedidoDAO extends EntidadeBaseDAO<Pedido> {

    private Connection connection;

    @Override
    public void inserir(Pedido pedido) throws SQLException {
        pedido.validar();

        try {
            Long idnovo = gerarIdUnico("pedido", "ID_pedido");
            pedido.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO pedido (ID_pedido, Data_do_pedido, Status, Valor_total, fk_Cliente_id, fk_Pagamento_ID_pagamento) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pedido.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(pedido.getData())); // LocalDateTime → SQL datetime
            stmt.setString(3, pedido.getStatus());
            stmt.setInt(4, pedido.getValorTotal());
            stmt.setLong(5, pedido.getId_cliente().getId());
            stmt.setLong(6, pedido.getId_pagamento().getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir pedido, nenhuma linha afetada.");
            }

            System.out.println("[DAO] Pedido inserido com sucesso!");
        }
    }

    @Override
    public void deletar(Pedido pedido) {
        String sql = "DELETE FROM pedido WHERE ID_pedido = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pedido.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Pedido deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar pedido: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Pedido pedido) {
        String sql = "UPDATE pedido SET Data_do_pedido = ?,Status = ?, Valor_total = ?, "
                + "fk_Cliente_id = ?, fk_Pagamento_ID_pagamento = ? "
                + "WHERE ID_pedido = ?";
        
        try {
            pedido.validar();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setTimestamp(1, Timestamp.valueOf(pedido.getData()));
                stmt.setString(2, pedido.getStatus());
                stmt.setLong(3, pedido.getValorTotal());
                stmt.setLong(4, pedido.getId_cliente().getId());
                stmt.setLong(5, pedido.getId_pagamento().getId());
                stmt.setLong(6, pedido.getId());

                stmt.executeUpdate();
                System.out.println("[DAO] Pedido alterado com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao alterar pedido: " + e.getMessage());
        }
    }

    private Pedido montarPedido(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_pedido");
        LocalDateTime dataDoPedido = rs.getTimestamp("Data_do_pedido").toLocalDateTime();
        String status = rs.getString("Status");
        int valorTotal = rs.getInt("Valor_total");

        Long Idcliente = rs.getLong("fk_Cliente_id");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente c = cd.buscarPorId(Idcliente);

        Long Idpagamento = rs.getLong("fk_Pagamento_ID_pagamento");
        PagamentoDAO pd = new PagamentoDAO();
        Pagamento p = pd.buscarPorId(Idpagamento);

        return new Pedido(id, dataDoPedido, status, valorTotal, c, p);
    }

    @Override
    public Pedido buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE ID_pedido = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarPedido(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Pedido> listar() {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedido";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getLong("ID_pedido"));
                p.setData(rs.getTimestamp("Data_do_pedido").toLocalDateTime());
                p.setStatus(rs.getString("Status"));
                p.setValorTotal(rs.getInt("Valor_total"));

                Long Idcliente = rs.getLong("fk_Cliente_id");
                ClienteDAO cd = new ClienteDAO(connection);
                Cliente c = cd.buscarPorId(Idcliente);
                p.setId_cliente(c);

                Long Idpagamento = rs.getLong("fk_Pagamento_ID_pagamento");
                PagamentoDAO pd = new PagamentoDAO();
                Pagamento Pa = pd.buscarPorId(Idpagamento);
                p.setId_pagamento(Pa);

                pedidos.add(p);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }

        return pedidos;
    }
}
