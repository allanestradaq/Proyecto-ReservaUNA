/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author allan
 */
public class Espacio extends Recurso {

    private int capacidad;
    private String ubicacion;
    private String tipoEspacio; // aula, lab, otros

    public Espacio(int id, String nombre, String descripcion,
                   int capacidad, String ubicacion, String tipoEspacio) {
        super(id, nombre, descripcion);
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.tipoEspacio = tipoEspacio;
    }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getTipoEspacio() { return tipoEspacio; }
    public void setTipoEspacio(String tipoEspacio) { this.tipoEspacio = tipoEspacio; }

    @Override
    public String getTipo() {
        return "Espacio";
    }
}