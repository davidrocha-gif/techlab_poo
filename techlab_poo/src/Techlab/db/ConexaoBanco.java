package Techlab.db;

import Techlab.interfaces.IConexaoBD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco implements IConexaoBD {

    private static final String URL = "jdbc:sqlite:techlab.db";

    public ConexaoBanco() {}

    @Override
    public Connection obterConexao() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL);
        // sqlite exige isso
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conexao;
    }

    @Override
    public void inicializarBanco() {
        String ddlAtivos = "CREATE TABLE IF NOT EXISTS ativos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "patrimonio TEXT NOT NULL UNIQUE, "
                + "nome TEXT NOT NULL, "
                + "categoria TEXT NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'Ativo', "
                + "data_aquisicao TEXT, "
                + "detalhe_especifico TEXT, "
                + "sistema_operacional TEXT, "
                + "capacidade_ram INTEGER, "
                + "usuario_alocado TEXT, "
                + "numero_portas INTEGER);";

        String ddlChamados = "CREATE TABLE IF NOT EXISTS chamados ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "titulo TEXT NOT NULL, "
                + "descricao TEXT NOT NULL, "
                + "prioridade TEXT NOT NULL, "
                + "status TEXT NOT NULL, "
                + "data_abertura TEXT NOT NULL, "
                + "ativo_id INTEGER NOT NULL, "
                + "FOREIGN KEY (ativo_id) REFERENCES ativos(id) ON DELETE CASCADE);";

        try (Connection conexao = obterConexao();
             Statement stmt = conexao.createStatement()) {

            stmt.execute(ddlAtivos);
            stmt.execute(ddlChamados);
            System.out.println("[INFO] Banco de dados 'techlab.db' inicializado e tabelas prontas!");

        } catch (SQLException e) {
            System.err.println("[ERRO CRÍTICO] Falha ao inicializar o banco de dados: " + e.getMessage());
            throw new RuntimeException("Não foi possível inicializar a base de dados.", e);
        }
    }
}
