/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import config.ConexionBD;
import modelo.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public String insertar(Reserva reserva) {
        // verificar traslape
        if (hayTraslapeEnBD(reserva)) {
            return "El recurso ya está reservado en ese horario.";
        }

        String sql = """
            INSERT INTO reserva (id_usuario, id_recurso, inicio, fin, motivo, estado)
            VALUES (?, ?, ?, ?, ?, 'PENDIENTE')
            """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reserva.getUsuario().getId());
            ps.setInt(2, reserva.getRecurso().getId());
            ps.setTimestamp(3, Timestamp.valueOf(reserva.getInicio()));
            ps.setTimestamp(4, Timestamp.valueOf(reserva.getFin()));
            ps.setString(5, reserva.getMotivo());
            ps.executeUpdate();
            return null; // null = sin error

        } catch (SQLException e) {
            return "Error al crear reserva: " + e.getMessage();
        }
    }

    public boolean cambiarEstado(int id, EstadoReserva nuevoEstado) {
        String sql = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    // Lista reservas(admin)
    public List<Reserva> listarTodas(List<Recurso> recursos, List<Usuario> usuarios) {
        return listarConFiltro(null, recursos, usuarios);
    }

    // Lista reservas usuario
    public List<Reserva> listarPorUsuario(int idUsuario, List<Recurso> recursos, List<Usuario> usuarios) {
        return listarConFiltro(idUsuario, recursos, usuarios);
    }

    private List<Reserva> listarConFiltro(Integer idUsuario, List<Recurso> recursos, List<Usuario> usuarios) {
        List<Reserva> lista = new ArrayList<>();

        String sql = "SELECT id_reserva, id_usuario, id_recurso, inicio, fin, motivo, estado " +
                     "FROM reserva ";
        if (idUsuario != null) sql += "WHERE id_usuario = ? ";
        sql += "ORDER BY inicio DESC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (idUsuario != null) ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idR = rs.getInt("id_recurso");
                    int idU = rs.getInt("id_usuario");

                    Recurso recurso = recursos.stream()
                            .filter(r -> r.getId() == idR).findFirst().orElse(null);
                    Usuario usuario = usuarios.stream()
                            .filter(u -> u.getId() == idU).findFirst().orElse(null);

                    if (recurso == null || usuario == null) continue;

                    LocalDateTime inicio = rs.getTimestamp("inicio").toLocalDateTime();
                    LocalDateTime fin    = rs.getTimestamp("fin").toLocalDateTime();

                    Reserva reserva = new Reserva(
                            rs.getInt("id_reserva"),
                            usuario, recurso, inicio, fin,
                            rs.getString("motivo")
                    );
                    reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                    lista.add(reserva);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        }
        return lista;
    }

    private boolean hayTraslapeEnBD(Reserva reserva) {
        String sql = """
            SELECT COUNT(*) FROM reserva
            WHERE id_recurso = ?
              AND estado NOT IN ('CANCELADA', 'RECHAZADA')
              AND inicio < ?
              AND fin > ?
            """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reserva.getRecurso().getId());
            ps.setTimestamp(2, Timestamp.valueOf(reserva.getFin()));
            ps.setTimestamp(3, Timestamp.valueOf(reserva.getInicio()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar traslape: " + e.getMessage());
        }
        return false;
    }
}