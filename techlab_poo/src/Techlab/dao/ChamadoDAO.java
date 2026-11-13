package Techlab.dao;

import Techlab.db.ConexaoBanco;
import Techlab.interfaces.IConexaoBD;
import Techlab.interfaces.IPersistencia;
import Techlab.negocio.AtivoTI;
import Techlab.negocio.ChamadoSuporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChamadoDAO implements IPersistencia<ChamadoSuporte> {

    private final IConexaoBD conexaoBD = new ConexaoBanco();
    private final AtivoDAO ativoDAO = new AtivoDAO();

    private static final String COLUNAS = "id, titulo, descricao, prioridade, status, data_abertura, ativo_id";

    @Override
    public void salvar(ChamadoSuporte chamado) throws SQLException {
        if (chamado.getAtivoRelacionado().getid() == 0) {
            throw new SQLException("O ativo do chamado precisa estar salvo no banco antes.");
        }
        String sql = "INSERT INTO chamados (titulo, descricao, prioridade, status, data_abertura, ativo_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getPrioridade());
            stmt.setString(4, chamado.getStatus());
            stmt.setString(5, chamado.getDataAbertura().toString());
            stmt.setInt(6, chamado.getAtivoRelacionado().getid());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    chamado.setId(chaves.getInt(1));
                }
            }
            System.out.println("[LOG DAO] Chamado #" + chamado.getId() + " registrado.");
        }
    }

    @Override
    public ChamadoSuporte buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM chamados WHERE id = ?";
        ChamadoSuporte encontrado = null;

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    encontrado = mapearResultSetParaObjeto(rs);
                }
            }
        }
        return encontrado;
    }

    @Override
    public List<ChamadoSuporte> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM chamados ORDER BY id ASC";
        return executarLista(sql, null);
    }

    public List<ChamadoSuporte> listarPorAtivo(int ativoId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM chamados WHERE ativo_id = ? ORDER BY id ASC";
        return executarLista(sql, ativoId);
    }

    @Override
    public void atualizar(ChamadoSuporte chamado) throws SQLException {
        String sql = "UPDATE chamados SET titulo = ?, descricao = ?, prioridade = ?, status = ? WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getPrioridade());
            stmt.setString(4, chamado.getStatus());
            stmt.setInt(5, chamado.getId());

            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Nenhum chamado encontrado com o ID " + chamado.getId());
            }
            System.out.println("[LOG DAO] Chamado #" + chamado.getId() + " atualizado.");
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM chamados WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Nenhum chamado encontrado com o ID " + id);
            }
            System.out.println("[LOG DAO] Chamado #" + id + " removido.");
        }
    }

    private List<ChamadoSuporte> executarLista(String sql, Integer parametro) throws SQLException {
        List<ChamadoSuporte> lista = new ArrayList<>();

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (parametro != null) {
                stmt.setInt(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetParaObjeto(rs));
                }
            }
        }
        return lista;
    }

    private ChamadoSuporte mapearResultSetParaObjeto(ResultSet rs) throws SQLException {
        AtivoTI ativo = ativoDAO.buscarPorId(rs.getInt("ativo_id"));

        return new ChamadoSuporte(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("prioridade"),
                rs.getString("status"),
                LocalDateTime.parse(rs.getString("data_abertura")),
                ativo);
    }
}
