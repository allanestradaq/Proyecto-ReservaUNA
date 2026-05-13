/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Equipo;
import modelo.Espacio;
import modelo.Recurso;
import modelo.Usuario;
import servicio.RecursoServicio;

public class RecursosVista {

    private Stage stage;
    private Usuario usuarioActual;
    private RecursoServicio recursoServicio;
    private ObservableList<Recurso> listaObservable;

    public RecursosVista(Stage stage, Usuario usuarioActual, RecursoServicio recursoServicio) {
        this.stage = stage;
        this.usuarioActual = usuarioActual;
        this.recursoServicio = recursoServicio;
        this.listaObservable = FXCollections.observableArrayList(recursoServicio.listar());
    }

    public void mostrar() {
        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Espacio", "Equipo");
        comboTipo.setValue("Espacio");

        TextField campoNombre = new TextField();
        campoNombre.setPromptText("Nombre del recurso");

        TextField campoDesc = new TextField();
        campoDesc.setPromptText("Descripción");

        // Campos de Espacio
        TextField campoCapacidad   = new TextField();
        campoCapacidad.setPromptText("Capacidad (número)");
        TextField campoUbicacion   = new TextField();
        campoUbicacion.setPromptText("Ubicación");
        TextField campoTipoEspacio = new TextField();
        campoTipoEspacio.setPromptText("Tipo (Aula / Lab)");
        VBox camposEspacio = new VBox(6, campoCapacidad, campoUbicacion, campoTipoEspacio);

        // Campos de Equipo
        TextField campoMarca  = new TextField();
        campoMarca.setPromptText("Marca");
        TextField campoModelo = new TextField();
        campoModelo.setPromptText("Modelo");
        TextField campoSerial = new TextField();
        campoSerial.setPromptText("Serial");
        VBox camposEquipo = new VBox(6, campoMarca, campoModelo, campoSerial);
        camposEquipo.setVisible(false);
        camposEquipo.setManaged(false);

        comboTipo.setOnAction(e -> {
            boolean esEspacio = comboTipo.getValue().equals("Espacio");
            camposEspacio.setVisible(esEspacio);
            camposEspacio.setManaged(esEspacio);
            camposEquipo.setVisible(!esEspacio);
            camposEquipo.setManaged(!esEspacio);
        });

        Label mensajeError = new Label();
        mensajeError.setStyle("-fx-text-fill: red;");
        Label mensajeOk = new Label();
        mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnAgregar = new Button("Agregar recurso");
        btnAgregar.setMaxWidth(Double.MAX_VALUE);

        ListView<Recurso> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(220);

        Button btnEliminar = new Button("Eliminar seleccionado");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);

        btnAgregar.setOnAction(e -> {
            mensajeError.setText("");
            mensajeOk.setText("");
            String nombre = campoNombre.getText().trim();
            String desc   = campoDesc.getText().trim();

            if (nombre.isEmpty() || desc.isEmpty()) {
                mensajeError.setText("Nombre y descripción son obligatorios.");
                return;
            }

            int id = recursoServicio.siguienteId();

            if (comboTipo.getValue().equals("Espacio")) {
                String capStr = campoCapacidad.getText().trim();
                String ubic   = campoUbicacion.getText().trim();
                String tipoE  = campoTipoEspacio.getText().trim();
                if (capStr.isEmpty() || ubic.isEmpty() || tipoE.isEmpty()) {
                    mensajeError.setText("Complete todos los campos del espacio.");
                    return;
                }
                try {
                    int cap = Integer.parseInt(capStr);
                    recursoServicio.agregar(new Espacio(id, nombre, desc, cap, ubic, tipoE));
                } catch (NumberFormatException ex) {
                    mensajeError.setText("La capacidad debe ser un número entero.");
                    return;
                }
            } else {
                String marca  = campoMarca.getText().trim();
                String modelo = campoModelo.getText().trim();
                String serial = campoSerial.getText().trim();
                if (marca.isEmpty() || modelo.isEmpty() || serial.isEmpty()) {
                    mensajeError.setText("Complete todos los campos del equipo.");
                    return;
                }
                recursoServicio.agregar(new Equipo(id, nombre, desc, marca, modelo, serial));
            }

            listaObservable.setAll(recursoServicio.listar());
            limpiar(campoNombre, campoDesc, campoCapacidad, campoUbicacion,
                    campoTipoEspacio, campoMarca, campoModelo, campoSerial);
            mensajeOk.setText("Recurso agregado correctamente.");
        });

        btnEliminar.setOnAction(e -> {
            mensajeError.setText("");
            mensajeOk.setText("");
            Recurso sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mensajeError.setText("Seleccione un recurso de la lista.");
                return;
            }
            recursoServicio.eliminar(sel.getId());
            listaObservable.setAll(recursoServicio.listar());
            mensajeOk.setText("Recurso eliminado.");
        });

        VBox formulario = new VBox(8,
                new Label("Tipo:"), comboTipo,
                new Label("Nombre:"), campoNombre,
                new Label("Descripción:"), campoDesc,
                camposEspacio, camposEquipo,
                mensajeError, mensajeOk, btnAgregar);
        formulario.setPadding(new Insets(10));
        formulario.setMinWidth(260);

        VBox panelLista = new VBox(8,
                new Label("Recursos registrados:"), lista, btnEliminar);
        panelLista.setPadding(new Insets(10));

        HBox contenido = new HBox(20, formulario, panelLista);
        contenido.setPadding(new Insets(20));

        Label titulo = new Label("Gestión de Recursos");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, contenido);
        raiz.setPadding(new Insets(15));

        stage.setScene(new Scene(raiz, 720, 500));
        stage.setTitle("ReservaUNA — Recursos");
    }

    private void limpiar(TextField... campos) {
        for (TextField c : campos) c.clear();
    }
}