/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author allan
 */
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Recurso;
import modelo.Reserva;
import modelo.Usuario;
import servicio.RecursoServicio;
import servicio.ReservaServicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReservaVista {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Stage stage;
    private Usuario usuarioActual;
    private RecursoServicio recursoServicio;
    private ReservaServicio reservaServicio;
    private ObservableList<Reserva> listaObservable;

    public ReservaVista(Stage stage, Usuario usuarioActual,
                         RecursoServicio recursoServicio, ReservaServicio reservaServicio) {
        this.stage = stage;
        this.usuarioActual = usuarioActual;
        this.recursoServicio = recursoServicio;
        this.reservaServicio = reservaServicio;
        this.listaObservable = FXCollections.observableArrayList(reservaServicio.listar());
    }

    public void mostrar() {
        // formulario nueva reserva
        ComboBox<Recurso> comboRecurso = new ComboBox<>();
        comboRecurso.getItems().addAll(recursoServicio.listar());
        comboRecurso.setPromptText("Seleccione un recurso");
        comboRecurso.setMaxWidth(Double.MAX_VALUE);

        TextField campoInicio = new TextField();
        campoInicio.setPromptText("Inicio: dd/MM/yyyy HH:mm");

        TextField campoFin = new TextField();
        campoFin.setPromptText("Fin:    dd/MM/yyyy HH:mm");

        TextField campoMotivo = new TextField();
        campoMotivo.setPromptText("Motivo de la reserva");

        Label mensajeError = new Label();
        mensajeError.setStyle("-fx-text-fill: red;");

        Button btnReservar = new Button("Crear reserva");
        btnReservar.setMaxWidth(Double.MAX_VALUE);

        // lista de reservas
        ListView<Reserva> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(220);

        Button btnCancelar = new Button("Cancelar reserva seleccionada");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);

        // acciones
        btnReservar.setOnAction(e -> {
            Recurso recurso = comboRecurso.getValue();
            String inicioStr = campoInicio.getText().trim();
            String finStr = campoFin.getText().trim();
            String motivo = campoMotivo.getText().trim();

            if (recurso == null || inicioStr.isEmpty() || finStr.isEmpty() || motivo.isEmpty()) {
                mensajeError.setText("Todos los campos son obligatorios.");
                return;
            }

            LocalDateTime inicio, fin;
            try {
                inicio = LocalDateTime.parse(inicioStr, FORMATO);
                fin = LocalDateTime.parse(finStr, FORMATO);
            } catch (DateTimeParseException ex) {
                mensajeError.setText("Formato de fecha incorrecto. Use dd/MM/yyyy HH:mm");
                return;
            }

            Reserva nueva = new Reserva(
                    reservaServicio.siguienteId(),
                    usuarioActual, recurso, inicio, fin, motivo
            );

            String error = reservaServicio.agregar(nueva);
            if (error != null) {
                mensajeError.setText(error);
            } else {
                listaObservable.setAll(reservaServicio.listar());
                campoInicio.clear();
                campoFin.clear();
                campoMotivo.clear();
                comboRecurso.setValue(null);
                mensajeError.setText("");
            }
        });

        btnCancelar.setOnAction(e -> {
            Reserva seleccionada = lista.getSelectionModel().getSelectedItem();
            if (seleccionada == null) {
                mensajeError.setText("Seleccione una reserva de la lista.");
                return;
            }
            reservaServicio.cancelar(seleccionada.getId());
            listaObservable.setAll(reservaServicio.listar());
            mensajeError.setText("");
        });

        // layout
        VBox formulario = new VBox(8,
                new Label("Recurso:"), comboRecurso,
                new Label("Fecha inicio:"), campoInicio,
                new Label("Fecha fin:"), campoFin,
                new Label("Motivo:"), campoMotivo,
                mensajeError, btnReservar
        );
        formulario.setPadding(new Insets(10));

        VBox panelLista = new VBox(8,
                new Label("Reservas registradas:"), lista, btnCancelar
        );
        panelLista.setPadding(new Insets(10));

        HBox contenido = new HBox(20, formulario, panelLista);
        contenido.setPadding(new Insets(20));

        Label titulo = new Label("Gestión de Reservas — Usuario: " + usuarioActual.getNombre());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, contenido);
        raiz.setPadding(new Insets(15));

        Scene escena = new Scene(raiz, 820, 520);
        stage.setScene(escena);
        stage.setTitle("ReservaUNA — Reservas");
    }
}
