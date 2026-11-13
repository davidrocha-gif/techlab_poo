package Techlab.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConexaoBD {
    Connection obterConexao() throws SQLException;
    void inicializarBanco();
}
