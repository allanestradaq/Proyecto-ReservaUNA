/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author allan
 */
public class Equipo extends Recurso {

    private String marca;
    private String modelo;
    private String serial;

    public Equipo(int id, String nombre, String descripcion,
                  String marca, String modelo, String serial) {
        super(id, nombre, descripcion);
        this.marca = marca;
        this.modelo = modelo;
        this.serial = serial;
    }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    @Override
    public String getTipo() {
        return "Equipo";
    }
}