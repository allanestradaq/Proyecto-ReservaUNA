/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author allan
 */
import java.time.LocalDateTime;

public class Reserva {

    private int id;
    private Usuario usuario;
    private Recurso recurso;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String motivo;
    private EstadoReserva estado;

    public Reserva(int id, Usuario usuario, Recurso recurso,
                   LocalDateTime inicio, LocalDateTime fin, String motivo) {
        this.id = id;
        this.usuario = usuario;
        this.recurso = recurso;
        this.inicio = inicio;
        this.fin = fin;
        this.motivo = motivo;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Recurso getRecurso() { return recurso; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public String getMotivo() { return motivo; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    @Override
    public String toString() {
        return recurso.getNombre() + " | " + inicio + " → " + fin + " [" + estado + "]";
    }
}
