/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST     = "1.pgsqlserver.com";
    private static final String PUERTO   = "5432";
    private static final String BASE     = "gamabasis_bdallan";
    private static final String USUARIO  = "gamabasis_bdallan";
    private static final String CLAVE    = "9a52FuaTU#";

    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + BASE;

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
