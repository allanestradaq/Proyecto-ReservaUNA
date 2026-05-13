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
import modelo.Reserva;
import servicio.ReservaServicio;

public class AprobacionReservasVista {

    private Stage stage;
    private ReservaServicio reservaServicio;
    private ObservableList<Reserva> listaObservable;

    public AprobacionReservasVista(Stage stage, ReservaServicio reservaServicio) {
        this.stage = stage;
        this.reservaServicio = reservaServicio;
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
                if (empty || r == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("[%s] #%d — %s | %s | %s → %s | Motivo: %s",
                            r.getEstado(), r.getId(),
                            r.getUsuario().getNombre(),
                            r.getRecurso().getNombre(),
                            r.getInicio().toLocalDate(),
                            r.getFin().toLocalDate(),
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

        Label mensajeError = new Label();
        mensajeError.setStyle("-fx-text-fill: red;");
        Label mensajeOk = new Label();
        mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnAprobar = new Button("✔ Aprobar");
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
            String err = reservaServicio.aprobar(sel.getId());
            if (err != null) mensajeError.setText(err);
            else { mensajeOk.setText("Reserva #" + sel.getId() + " aprobada."); aplicarFiltro(comboFiltro.getValue()); }
        });

        btnRechazar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            Reserva sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) { mensajeError.setText("Seleccione una reserva."); return; }
            String err = reservaServicio.rechazar(sel.getId());
            if (err != null) mensajeError.setText(err);
            else { mensajeOk.setText("Reserva #" + sel.getId() + " rechazada."); aplicarFiltro(comboFiltro.getValue()); }
        });

        aplicarFiltro("PENDIENTE");

        Label titulo = new Label("Aprobación de Reservas");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, barraFiltro, lista,
                new HBox(12, btnAprobar, btnRechazar), mensajeError, mensajeOk);
        raiz.setPadding(new Insets(20));

        stage.setScene(new Scene(raiz, 760, 460));
        stage.setTitle("ReservaUNA — Aprobación de reservas");
    }

    private void aplicarFiltro(String filtro) {
        if (filtro.equals("TODAS")) {
            listaObservable.setAll(reservaServicio.listar());
        } else {
            EstadoReserva estado = EstadoReserva.valueOf(filtro);
            listaObservable.setAll(
                reservaServicio.listar().stream()
                    .filter(r -> r.getEstado() == estado).toList());
        }
    }
}
