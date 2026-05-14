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
import modelo.EstadoReserva;
import modelo.Recurso;
import modelo.Reserva;
import modelo.Usuario;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AprobacionReservasVista {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Stage stage;
    private ReservaDAO reservaDAO;
    private List<Recurso> recursos;
    private List<Usuario> usuarios;
    private ObservableList<Reserva> listaObservable;

    public AprobacionReservasVista(Stage stage, ReservaDAO reservaDAO,
                                   List<Recurso> recursos, List<Usuario> usuarios) {
        this.stage = stage;
        this.reservaDAO = reservaDAO;
        this.recursos = recursos;
        this.usuarios = usuarios;
        this.listaObservable = FXCollections.observableArrayList();
    }

    public void mostrar() {
        ComboBox<String> comboFiltro = new ComboBox<>();
        comboFiltro.getItems().addAll("TODAS", "PENDIENTE", "APROBADA", "RECHAZADA", "CANCELADA");
        comboFiltro.setValue("PENDIENTE");

        Button btnFiltrar = new Button("Filtrar");
        HBox barraFiltro = new HBox(10, new Label("Mostrar:"), comboFiltro, btnFiltrar);
        barraFiltro.setPadding(new Insets(0, 0, 8, 0));

        ListView<Reserva> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(280);

        lista.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reserva r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText(null); setStyle(""); return; }
                setText(String.format("[%s] #%d — %s | %s | %s → %s | Motivo: %s",
                        r.getEstado(), r.getId(),
                        r.getUsuario().getNombre(),
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
        });

        Label mensajeError = new Label(); mensajeError.setStyle("-fx-text-fill: red;");
        Label mensajeOk    = new Label(); mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnAprobar  = new Button("✔ Aprobar");
        Button btnRechazar = new Button("✘ Rechazar");
        btnAprobar.setMinWidth(120);
        btnRechazar.setMinWidth(120);
        btnAprobar.setStyle("-fx-base: #4CAF50; -fx-text-fill: white;");
        btnRechazar.setStyle("-fx-base: #f44336; -fx-text-fill: white;");

        btnFiltrar.setOnAction(e -> aplicarFiltro(comboFiltro.getValue()));

        btnAprobar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            Reserva sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) { mensajeError.setText("Seleccione una reserva."); return; }
            if (sel.getEstado() != EstadoReserva.PENDIENTE) {
                mensajeError.setText("Solo se pueden aprobar reservas PENDIENTES.");
                return;
            }
            reservaDAO.cambiarEstado(sel.getId(), EstadoReserva.APROBADA);
            mensajeOk.setText("Reserva #" + sel.getId() + " aprobada.");
            aplicarFiltro(comboFiltro.getValue());
        });

        btnRechazar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            Reserva sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) { mensajeError.setText("Seleccione una reserva."); return; }
            if (sel.getEstado() != EstadoReserva.PENDIENTE) {
                mensajeError.setText("Solo se pueden rechazar reservas PENDIENTES.");
                return;
            }
            reservaDAO.cambiarEstado(sel.getId(), EstadoReserva.RECHAZADA);
            mensajeOk.setText("Reserva #" + sel.getId() + " rechazada.");
            aplicarFiltro(comboFiltro.getValue());
        });

        aplicarFiltro("PENDIENTE");

        Label titulo = new Label("Aprobación de Reservas");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10,
                titulo, barraFiltro, lista,
                new HBox(12, btnAprobar, btnRechazar),
                mensajeError, mensajeOk);
        raiz.setPadding(new Insets(20));

        stage.setScene(new Scene(raiz, 780, 480));
        stage.setTitle("ReservaUNA — Aprobación de reservas");
    }

    private void aplicarFiltro(String filtro) {
        List<Reserva> todas = reservaDAO.listarTodas(recursos, usuarios);
        if (filtro.equals("TODAS")) {
            listaObservable.setAll(todas);
        } else {
            EstadoReserva estado = EstadoReserva.valueOf(filtro);
            listaObservable.setAll(
                    todas.stream().filter(r -> r.getEstado() == estado).toList());
        }
    }
}