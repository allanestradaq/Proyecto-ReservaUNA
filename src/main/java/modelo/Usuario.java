/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author allan
 */
public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol; // ADMIN o USUARIO

    public Usuario(int id, String nombre, String correo,
                   String contrasena, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }

    @Override
    public String toString() {
        return nombre + " (" + rol + ")";
    }
}
