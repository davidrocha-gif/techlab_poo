package Techlab.dao;

import Techlab.db.ConexaoBanco;
import Techlab.interfaces.IConexaoBD;
import Techlab.interfaces.IPersistencia;
import Techlab.negocio.AtivoTI;
import Techlab.negocio.Servidor;
import Techlab.negocio.EstacaoTrabalho;
import Techlab.negocio.DispositivoRede;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AtivoDAO implements IPersistencia<AtivoTI> {

    private final IConexaoBD conexaoBD = new ConexaoBanco();

    private static final String COLUNAS = "id, patrimonio, nome, categoria, status, data_aquisicao, "
            + "detalhe_especifico, sistema_operacional, capacidade_ram, usuario_alocado, numero_portas";

    @Override
    public void salvar(AtivoTI ativo) throws SQLException {
        String sql = "INSERT INTO ativos (patrimonio, nome, categoria, status, data_aquisicao, "
                + "detalhe_especifico, sistema_operacional, capacidade_ram, usuario_alocado, numero_portas) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherParametros(stmt, ativo);
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    ativo.setid(chaves.getInt(1));
                }
            }
            System.out.println("[LOG DAO] Ativo cadastrado com sucesso: " + ativo.getcodigoPatrimonio());
        }
    }

    @Override
    public AtivoTI buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ativos WHERE id = ?";
        AtivoTI ativoEncontrado = null;

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ativoEncontrado = mapearResultSetParaObjeto(rs);
                }
            }
        }
        return ativoEncontrado;
    }

    public AtivoTI buscarPorPatrimonio(String patrimonio) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ativos WHERE patrimonio = ?";
        AtivoTI ativoEncontrado = null;

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, patrimonio);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ativoEncontrado = mapearResultSetParaObjeto(rs);
                }
            }
        }
        return ativoEncontrado;
    }

    @Override
    public List<AtivoTI> listarTodos() throws SQLException {
        List<AtivoTI> lista = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM ativos ORDER BY id ASC";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSetParaObjeto(rs));
            }
        }
        return lista;
    }

    @Override
    public void atualizar(AtivoTI ativo) throws SQLException {
        String sql = "UPDATE ativos SET patrimonio = ?, nome = ?, categoria = ?, status = ?, "
                + "data_aquisicao = ?, detalhe_especifico = ?, sistema_operacional = ?, "
                + "capacidade_ram = ?, usuario_alocado = ?, numero_portas = ? WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherParametros(stmt, ativo);
            stmt.setInt(11, ativo.getid());

            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Nenhum ativo encontrado com o ID " + ativo.getid());
            }
            System.out.println("[LOG DAO] Ativo ID " + ativo.getid() + " atualizado.");
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM ativos WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Nenhum ativo encontrado com o ID " + id);
            }
            System.out.println("[LOG DAO] Ativo ID " + id + " removido.");
        }
    }

    // mesma ordem do insert
    private void preencherParametros(PreparedStatement stmt, AtivoTI ativo) throws SQLException {
        String detalhe = null;
        String sistemaOperacional = null;
        Integer ram = null;
        String usuario = null;
        Integer portas = null;

        if (ativo instanceof Servidor) {
            Servidor s = (Servidor) ativo;
            detalhe = s.getIpEstatico();
            sistemaOperacional = s.getSistemaOperacional();
            ram = s.getCapacidadeRAM();
        } else if (ativo instanceof EstacaoTrabalho) {
            EstacaoTrabalho e = (EstacaoTrabalho) ativo;
            detalhe = e.getDepartamento();
            usuario = e.getUsuarioAlocado();
        } else if (ativo instanceof DispositivoRede) {
            portas = ((DispositivoRede) ativo).getNumeroPortas();
        }

        stmt.setString(1, ativo.getcodigoPatrimonio());
        stmt.setString(2, ativo.getmodelo());
        stmt.setString(3, ativo.getClass().getSimpleName());
        stmt.setString(4, ativo.getstatus());
        stmt.setString(5, ativo.getdataAquisicao() == null ? null : ativo.getdataAquisicao().toString());
        stmt.setString(6, detalhe);
        stmt.setString(7, sistemaOperacional);
        stmt.setObject(8, ram);
        stmt.setString(9, usuario);
        stmt.setObject(10, portas);
    }

    private AtivoTI mapearResultSetParaObjeto(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String patrimonio = rs.getString("patrimonio");
        String modelo = rs.getString("nome");
        String categoria = rs.getString("categoria");
        String status = rs.getString("status");
        String dataTexto = rs.getString("data_aquisicao");
        String detalhe = rs.getString("detalhe_especifico");

        LocalDateTime data = (dataTexto == null) ? null : LocalDateTime.parse(dataTexto);

        switch (categoria) {
            case "Servidor":
                return new Servidor(id, patrimonio, modelo, status, data, detalhe,
                        rs.getString("sistema_operacional"), rs.getInt("capacidade_ram"));
            case "EstacaoTrabalho":
                return new EstacaoTrabalho(id, patrimonio, modelo, status, data, detalhe,
                        rs.getString("usuario_alocado"));
            case "DispositivoRede":
                return new DispositivoRede(id, patrimonio, modelo, status, data,
                        rs.getInt("numero_portas"));
            default:
                throw new SQLException("Categoria de ativo desconhecida: " + categoria);
        }
    }
}
