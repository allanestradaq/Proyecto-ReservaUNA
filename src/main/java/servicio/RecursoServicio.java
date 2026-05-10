/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

/**
 *
 * @author allan
 */
import modelo.Recurso;
import java.util.ArrayList;
import java.util.List;

public class RecursoServicio {

    private List<Recurso> recursos = new ArrayList<>();
    private int contadorId = 1;

    public void agregar(Recurso r) {
        recursos.add(r);
        contadorId++;
    }

    public List<Recurso> listar() {
        return recursos;
    }

    public Recurso buscarPorId(int id) {
        for (Recurso r : recursos) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    public boolean eliminar(int id) {
        return recursos.removeIf(r -> r.getId() == id);
    }

    public int siguienteId() {
        return contadorId;
    }
}