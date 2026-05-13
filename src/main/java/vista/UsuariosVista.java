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
import modelo.Usuario;
import java.util.List;

public class UsuariosVista {

    private Stage stage;
    private List<Usuario> usuarios;
    private ObservableList<Usuario> listaObservable;
    private int contadorId;

    public UsuariosVista(Stage stage, List<Usuario> usuarios) {
        this.stage = stage;
        this.usuarios = usuarios;
        this.listaObservable = FXCollections.observableArrayList(usuarios);
        this.contadorId = usuarios.stream().mapToInt(Usuario::getId).max().orElse(0) + 1;
    }

    public void mostrar() {
        TextField campoNombre = new TextField();
        campoNombre.setPromptText("Nombre completo");

        TextField campoCorreo = new TextField();
        campoCorreo.setPromptText("Correo");

        PasswordField campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Contraseña");

        ComboBox<String> comboRol = new ComboBox<>();
        comboRol.getItems().addAll("USUARIO", "ADMIN");
        comboRol.setValue("USUARIO");

        Label mensajeError = new Label();
        mensajeError.setStyle("-fx-text-fill: red;");

        Label mensajeOk = new Label();
        mensajeOk.setStyle("-fx-text-fill: green;");

        Button btnAgregar = new Button("Agregar usuario");
        btnAgregar.setMaxWidth(Double.MAX_VALUE);

        ListView<Usuario> lista = new ListView<>(listaObservable);
        lista.setPrefHeight(220);

        Button btnEliminar = new Button("Eliminar seleccionado");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);

        btnAgregar.setOnAction(e -> {
            mensajeError.setText("");
            mensajeOk.setText("");

            String nombre = campoNombre.getText().trim();
            String correo = campoCorreo.getText().trim();
            String clave = campoContrasena.getText().trim();

            if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                mensajeError.setText("Todos los campos son obligatorios.");
                return;
            }
            if (!correo.contains("@")) {
                mensajeError.setText("El correo no tiene un formato válido.");
                return;
            }
            boolean duplicado = usuarios.stream()
                    .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo));
            if (duplicado) {
                mensajeError.setText("Ya existe un usuario con ese correo.");
                return;
            }

            usuarios.add(new Usuario(contadorId++, nombre, correo, clave, comboRol.getValue()));
            listaObservable.setAll(usuarios);
            campoNombre.clear();
            campoCorreo.clear();
            campoContrasena.clear();
            comboRol.setValue("USUARIO");
            mensajeOk.setText("Usuario agregado correctamente.");
        });

        btnEliminar.setOnAction(e -> {
            mensajeError.setText("");
            mensajeOk.setText("");
            Usuario seleccionado = lista.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mensajeError.setText("Seleccione un usuario de la lista.");
                return;
            }
            long admins = usuarios.stream().filter(u -> u.getRol().equals("ADMIN")).count();
            if (seleccionado.getRol().equals("ADMIN") && admins == 1) {
                mensajeError.setText("No se puede eliminar el único administrador.");
                return;
            }
            usuarios.remove(seleccionado);
            listaObservable.setAll(usuarios);
            mensajeOk.setText("Usuario eliminado.");
        });

        VBox formulario = new VBox(8,
                new Label("Nombre:"), campoNombre,
                new Label("Correo:"), campoCorreo,
                new Label("Contraseña:"), campoContrasena,
                new Label("Rol:"), comboRol,
                mensajeError, mensajeOk, btnAgregar);
        formulario.setPadding(new Insets(10));
        formulario.setMinWidth(250);

        VBox panelLista = new VBox(8,
                new Label("Usuarios registrados:"), lista, btnEliminar);
        panelLista.setPadding(new Insets(10));

        HBox contenido = new HBox(20, formulario, panelLista);
        contenido.setPadding(new Insets(20));

        Label titulo = new Label("Gestión de Usuarios");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox raiz = new VBox(10, titulo, contenido);
        raiz.setPadding(new Insets(15));

        stage.setScene(new Scene(raiz, 720, 460));
        stage.setTitle("ReservaUNA — Usuarios");
    }
}