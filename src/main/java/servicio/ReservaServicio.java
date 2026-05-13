/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

/**
 *
 * @author allan
 */
import modelo.Reserva;
import modelo.Recurso;
import modelo.EstadoReserva;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaServicio {

    private List<Reserva> reservas = new ArrayList<>();
    private int contadorId = 1;

    // Verifica si existe traslape con otras reservas
  
    public boolean hayTraslape(Recurso recurso, LocalDateTime inicio, LocalDateTime fin) {
        for (Reserva r : reservas) {
            if (r.getRecurso().getId() == recurso.getId()
                    && r.getEstado() != EstadoReserva.CANCELADA) {
                // Hay traslape si el nuevo rango se superpone con el existente
                boolean traslapa = inicio.isBefore(r.getFin()) && fin.isAfter(r.getInicio());
                if (traslapa) return true;
            }
        }
        return false;
    }

    public String agregar(Reserva reserva) {
        if (reserva.getFin().isBefore(reserva.getInicio()) ||
            reserva.getFin().isEqual(reserva.getInicio())) {
            return "La fecha de fin debe ser posterior a la de inicio.";
        }
        if (hayTraslape(reserva.getRecurso(), reserva.getInicio(), reserva.getFin())) {
            return "El recurso ya está reservado en ese horario.";
        }
        reservas.add(reserva);
        contadorId++;
        return null; // null = no errores
    }

    public boolean cancelar(int id) {
        for (Reserva r : reservas) {
            if (r.getId() == id) {
                r.setEstado(EstadoReserva.CANCELADA);
                return true;
            }
        }
        return false;
    }
    
    public String aprobar(int id) {
    for (Reserva r : reservas) {
        if (r.getId() == id) {
            if (r.getEstado() != EstadoReserva.PENDIENTE) {
                return "Solo se pueden aprobar reservas en estado PENDIENTE.";
            }
            r.setEstado(EstadoReserva.APROBADA);
            return null;
        }
    }
    return "Reserva no encontrada.";
}

public String rechazar(int id) {
    for (Reserva r : reservas) {
        if (r.getId() == id) {
            if (r.getEstado() != EstadoReserva.PENDIENTE) {
                return "Solo se pueden rechazar reservas en estado PENDIENTE.";
            }
            r.setEstado(EstadoReserva.RECHAZADA);
            return null;
        }
    }
    return "Reserva no encontrada.";
}

    public List<Reserva> listar() {
        return reservas;
    }

    public int siguienteId() {
        return contadorId;
    }
}