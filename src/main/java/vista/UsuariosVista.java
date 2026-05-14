/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import Dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Usuario;

public class UsuariosVista {

    private Stage stage;
    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> listaObservable;

    public UsuariosVista(Stage stage, UsuarioDAO usuarioDAO) {
        this.stage = stage;
        this.usuarioDAO = usuarioDAO;
        this.listaObservable = FXCollections.observableArrayList(usuarioDAO.listar());
    }

    public void mostrar() {
        TextField campoNombre     = new TextField(); campoNombre.setPromptText("Nombre completo");
        TextField campoCorreo     = new TextField(); campoCorreo.setPromptText("Correo");
        PasswordField campoClave  = new PasswordField(); campoClave.setPromptText("Contraseña");

        ComboBox<String> comboRol = new ComboBox<>();
        comboRol.getItems().addAll("USUARIO", "ADMIN");
        comboRol.setValue("USUARIO");

        Label mensajeError = new Label(); mensajeError.setStyle("-fx-text-fill: red;");
        Label mensajeOk    = new Label(); mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnAgregar  = new Button("Agregar usuario");
        btnAgregar.setMaxWidth(Double.MAX_VALUE);

        ListView<Usuario> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(220);

        Button btnEliminar = new Button("Eliminar seleccionado");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);

        btnAgregar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            String nombre = campoNombre.getText().trim();
            String correo = campoCorreo.getText().trim();
            String clave  = campoClave.getText().trim();

            if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                mensajeError.setText("Todos los campos son obligatorios.");
                return;
            }
            if (!correo.contains("@")) {
                mensajeError.setText("El correo no tiene un formato válido.");
                return;
            }

            Usuario nuevo = new Usuario(0, nombre, correo, clave, comboRol.getValue());
            if (usuarioDAO.insertar(nuevo)) {
                listaObservable.setAll(usuarioDAO.listar());
                campoNombre.clear(); campoCorreo.clear();
                campoClave.clear();  comboRol.setValue("USUARIO");
                mensajeOk.setText("Usuario agregado correctamente.");
            } else {
                mensajeError.setText("No se pudo agregar el usuario. ¿El correo ya existe?");
            }
        });

        btnEliminar.setOnAction(e -> {
            mensajeError.setText(""); mensajeOk.setText("");
            Usuario sel = lista.getSelectionModel().getSelectedItem();
            if (sel == null) { mensajeError.setText("Seleccione un usuario."); return; }
            if (sel.getRol().equals("ADMIN") && usuarioDAO.contarAdmins() == 1) {
                mensajeError.setText("No se puede eliminar el único administrador.");
                return;
            }
            if (usuarioDAO.eliminar(sel.getId())) {
                listaObservable.setAll(usuarioDAO.listar());
                mensajeOk.setText("Usuario eliminado.");
            } else {
                mensajeError.setText("No se pudo eliminar el usuario.");
            }
        });

        VBox formulario = new VBox(8,
                new Label("Nombre:"), campoNombre,
                new Label("Correo:"), campoCorreo,
                new Label("Contraseña:"), campoClave,
                new Label("Rol:"), comboRol,
                mensajeError, mensajeOk, btnAgregar);
        formulario.setPadding(new Insets(10)); formulario.setMinWidth(250);

        VBox panelLista = new VBox(8,
                new Label("Usuarios registrados:"), lista, btnEliminar);
        panelLista.setPadding(new Insets(10));

        Label titulo = new Label("Gestión de Usuarios");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, new HBox(20, formulario, panelLista));
        raiz.setPadding(new Insets(15));

        stage.setScene(new Scene(raiz, 720, 460));
        stage.setTitle("ReservaUNA — Usuarios");
    }
}