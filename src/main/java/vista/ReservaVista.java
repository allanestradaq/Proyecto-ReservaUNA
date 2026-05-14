/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import Dao.ReservaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservaVista {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Stage stage;
    private Usuario usuarioActual;
    private List<Recurso> recursos;
    private List<Usuario> usuarios;
    private ReservaDAO reservaDAO;
    private ObservableList<Reserva> listaObservable;

    public ReservaVista(Stage stage, Usuario usuarioActual,
                         List<Recurso> recursos, List<Usuario> usuarios,
                         ReservaDAO reservaDAO) {
        this.stage = stage;
        this.usuarioActual = usuarioActual;
        this.recursos = recursos;
        this.usuarios = usuarios;
        this.reservaDAO = reservaDAO;
        this.listaObservable = FXCollections.observableArrayList();
    }

    public void mostrar() {
        ComboBox<Recurso> comboRecurso = new ComboBox<>();
        comboRecurso.getItems().addAll(recursos);
        comboRecurso.setPromptText("Seleccione un recurso");
        comboRecurso.setMaxWidth(Double.MAX_VALUE);

        TextField campoInicio = new TextField(); campoInicio.setPromptText("dd/MM/yyyy HH:mm");
        TextField campoFin    = new TextField(); campoFin.setPromptText("dd/MM/yyyy HH:mm");
        TextField campoMotivo = new TextField(); campoMotivo.setPromptText("Motivo");

        Label mensajeError = new Label(); mensajeError.setStyle("-fx-text-fill: red;");
        Label mensajeOk    = new Label(); mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnReservar = new Button("Crear reserva");
        btnReservar.setMaxWidth(Double.MAX_VALUE);

        ListView<Reserva> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(250);
        lista.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reserva r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText(null); setStyle(""); return; }
                setText(String.format("#%d [%s] %s | %s → %s | %s",
                        r.getId(), r.getEstado(), r.getRecurso().getNombre(),
                        r.getInicio().format(FORMATO), r.getFin().format(FORMATO),
                        r.getMotivo()));
                switch (r.getEstado()) {
                    case PENDIENTE -> setStyle("-fx-text-fill: #b06000;");
                    case APROBADA  -> setStyle("-fx-text-fill: #1a7a1a;");
                    case RECHAZADA -> setStyle("-fx-text-fill: #aa0000;");
                    case CANCELADA -> setStyle("-fx-text-fill: #888888;");
                }
            }
        });

        Button btnCancelar = new Button("Cancelar reserva seleccionada");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);

        btnReservar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            Recurso recurso  = comboRecurso.getValue();
            String inicioStr = campoInicio.getText().trim();
            String finStr    = campoFin.getText().trim();
            String motivo    = campoMotivo.getText().trim();

            if (recurso == null || inicioStr.isEmpty() || finStr.isEmpty() || motivo.isEmpty()) {
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

            if (!fin.isAfter(inicio)) {
                mensajeError.setText("La fecha fin debe ser posterior al inicio.");
                return;
            }

            Reserva nueva = new Reserva(0, usuarioActual, recurso, inicio, fin, motivo);
            String error = reservaDAO.insertar(nueva);

            if (error != null) {
                mensajeError.setText(error);
            } else {
                refrescarLista();
                campoInicio.clear(); campoFin.clear();
                campoMotivo.clear(); comboRecurso.setValue(null);
                mensajeOk.setText("Reserva creada. Pendiente de aprobación.");
            }
        });

        btnCancelar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            Reserva sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) { mensajeError.setText("Seleccione una reserva."); return; }
            if (sel.getEstado() == EstadoReserva.CANCELADA) {
                mensajeError.setText("La reserva ya está cancelada.");
                return;
            }
            reservaDAO.cambiarEstado(sel.getId(), EstadoReserva.CANCELADA);
            refrescarLista();
            mensajeOk.setText("Reserva #" + sel.getId() + " cancelada.");
        });

        refrescarLista();

        VBox formulario = new VBox(8,
                new Label("Recurso:"), comboRecurso,
                new Label("Fecha inicio:"), campoInicio,
                new Label("Fecha fin:"), campoFin,
                new Label("Motivo:"), campoMotivo,
                mensajeError, mensajeOk, btnReservar);
        formulario.setPadding(new Insets(10)); formulario.setMinWidth(260);

        VBox panelLista = new VBox(8,
                new Label("Mis reservas:"), lista, btnCancelar);
        panelLista.setPadding(new Insets(10));

        Label titulo = new Label("Mis Reservas — " + usuarioActual.getNombre());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, new HBox(20, formulario, panelLista));
        raiz.setPadding(new Insets(15));

        stage.setScene(new Scene(raiz, 860, 540));
        stage.setTitle("ReservaUNA — Reservas");
    }

    private void refrescarLista() {
        listaObservable.setAll(
                reservaDAO.listarPorUsuario(usuarioActual.getId(), recursos, usuarios));
    }
}