/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import config.ConexionBD;
import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        try (Connection conn = ConexionBD.conectar()) {
            System.out.println("Conexion exitosa: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}