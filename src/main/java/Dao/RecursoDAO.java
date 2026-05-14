/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import config.ConexionBD;
import modelo.Equipo;
import modelo.Espacio;
import modelo.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    public List<Recurso> listar() {
        List<Recurso> lista = new ArrayList<>();
        String sql = "SELECT * FROM recurso WHERE activo = TRUE ORDER BY id_recurso";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar recursos: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Recurso r) {
        String sql = """
            INSERT INTO recurso
              (nombre, descripcion, tipo, capacidad, ubicacion, tipo_espacio, marca, modelo, serial)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getTipo());

            if (r instanceof Espacio e) {
                ps.setInt(4, e.getCapacidad());
                ps.setString(5, e.getUbicacion());
                ps.setString(6, e.getTipoEspacio());
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            } else if (r instanceof Equipo eq) {
                ps.setNull(4, Types.INTEGER);
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setString(7, eq.getMarca());
                ps.setString(8, eq.getModelo());
                ps.setString(9, eq.getSerial());
            }

            ps.executeUpdate();

            // Actualizar el id
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                }
            }
            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar recurso: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE recurso SET activo = FALSE WHERE id_recurso = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar recurso: " + e.getMessage());
            return false;
        }
    }

    private Recurso mapear(ResultSet rs) throws SQLException {
        int id          = rs.getInt("id_recurso");
        String nombre   = rs.getString("nombre");
        String desc     = rs.getString("descripcion");
        String tipo     = rs.getString("tipo");

        if ("Espacio".equals(tipo)) {
            return new Espacio(id, nombre, desc,
                    rs.getInt("capacidad"),
                    rs.getString("ubicacion"),
                    rs.getString("tipo_espacio"));
        } else {
            return new Equipo(id, nombre, desc,
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getString("serial"));
        }
    }
}
