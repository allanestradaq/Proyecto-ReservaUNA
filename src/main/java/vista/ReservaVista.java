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
import modelo.EstadoReserva;
import modelo.Recurso;
import modelo.Reserva;
import modelo.Usuario;
import servicio.RecursoServicio;
import servicio.ReservaServicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReservaVista {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Stage stage;
    private Usuario usuarioActual;
    private RecursoServicio recursoServicio;
    private ReservaServicio reservaServicio;
    private ObservableList<Reserva> listaObservable;

    public ReservaVista(Stage stage, Usuario usuarioActual,
                         RecursoServicio recursoServicio,
                         ReservaServicio reservaServicio) {
        this.stage = stage;
        this.usuarioActual = usuarioActual;
        this.recursoServicio = recursoServicio;
        this.reservaServicio = reservaServicio;
        this.listaObservable = FXCollections.observableArrayList();
    }

    public void mostrar() {
        //  nueva reserva
        ComboBox<Recurso> comboRecurso = new ComboBox<>();
        comboRecurso.getItems().addAll(recursoServicio.listar());
        comboRecurso.setPromptText("Seleccione un recurso");
        comboRecurso.setMaxWidth(Double.MAX_VALUE);

        TextField campoInicio = new TextField();
        campoInicio.setPromptText("dd/MM/yyyy HH:mm");

        TextField campoFin = new TextField();
        campoFin.setPromptText("dd/MM/yyyy HH:mm");

        TextField campoMotivo = new TextField();
        campoMotivo.setPromptText("Motivo de la reserva");

        Label mensajeError = new Label();
        mensajeError.setStyle("-fx-text-fill: red;");
        Label mensajeOk = new Label();
        mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnReservar = new Button("Crear reserva");
        btnReservar.setMaxWidth(Double.MAX_VALUE);

        //  Lista 
        ListView<Reserva> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(250);
        lista.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reserva r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("#%d [%s] %s | %s → %s | %s",
                            r.getId(), r.getEstado(),
                            r.getRecurso().getNombre(),
                            r.getInicio().format(FORMATO),
                            r.getFin().format(FORMATO),
                            r.getMotivo()));
                    switch (r.getEstado()) {
                        case PENDIENTE -> setStyle("-fx-text-fill: #b06000;");
                        case APROBADA  -> setStyle("-fx-text-fill: #1a7a1a;");
                        case RECHAZADA -> setStyle("-fx-text-fill: #aa0000;");
                        case CANCELADA -> setStyle("-fx-text-fill: #888888;");
                    }
                }
            }
        });

        Button btnCancelar = new Button("Cancelar reserva seleccionada");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);

        // Acciones
        btnReservar.setOnAction(e -> {
            mensajeError.setText("");
            mensajeOk.setText("");

            Recurso recurso  = comboRecurso.getValue();
            String inicioStr = campoInicio.getText().trim();
            String finStr    = campoFin.getText().trim();
            String motivo    = campoMotivo.getText().trim();

            if (recurso == null || inicioStr.isEmpty()
                    || finStr.isEmpty() || motivo.isEmpty()) {
                mensajeError.setText("Todos los campos son obligatorios.");
                return;
            }

            LocalDateTime inicio, fin;
            try {
                inicio = LocalDateTime.parse(inicioStr, FORMATO);
                fin    = LocalDateTime.parse(finStr, FORMATO);
            } catch (DateTimeParseException ex) {
                mensajeError.setText("Formato incorrecto. Use dd/MM/yyyy HH:mm");
                return;
            }

            Reserva nueva = new Reserva(reservaServicio.siguienteId(),
                    usuarioActual, recurso, inicio, fin, motivo);

            String error = reservaServicio.agregar(nueva);
            if (error != null) {
                mensajeError.setText(error);
            } else {
                refrescarLista();
                campoInicio.clear();
                campoFin.clear();
                campoMotivo.clear();
                comboRecurso.setValue(null);
                mensajeOk.setText("Reserva creada. Pendiente de aprobación.");
            }
        });

        btnCancelar.setOnAction(e -> {
            mensajeError.setText("");
            mensajeOk.setText("");
            Reserva sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mensajeError.setText("Seleccione una reserva de la lista.");
                return;
            }
            if (sel.getEstado() == EstadoReserva.CANCELADA) {
                mensajeError.setText("La reserva ya está cancelada.");
                return;
            }
            reservaServicio.cancelar(sel.getId());
            refrescarLista();
            mensajeOk.setText("Reserva #" + sel.getId() + " cancelada.");
        });

        refrescarLista();

        // pantalla
        VBox formulario = new VBox(8,
                new Label("Recurso:"), comboRecurso,
                new Label("Fecha inicio:"), campoInicio,
                new Label("Fecha fin:"), campoFin,
                new Label("Motivo:"), campoMotivo,
                mensajeError, mensajeOk, btnReservar);
        formulario.setPadding(new Insets(10));
        formulario.setMinWidth(260);

        VBox panelLista = new VBox(8,
                new Label("Mis reservas:"), lista, btnCancelar);
        panelLista.setPadding(new Insets(10));

        HBox contenido = new HBox(20, formulario, panelLista);
        contenido.setPadding(new Insets(20));

        Label titulo = new Label("Gestión de Reservas — " + usuarioActual.getNombre());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, contenido);
        raiz.setPadding(new Insets(15));

        stage.setScene(new Scene(raiz, 860, 540));
        stage.setTitle("ReservaUNA — Reservas");
    }

    // reservas del usuario actual
    private void refrescarLista() {
        listaObservable.setAll(
            reservaServicio.listar().stream()
                .filter(r -> r.getUsuario().getId() == usuarioActual.getId())
                .toList()
        );
    }
}