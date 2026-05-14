/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package app;

import Dao.RecursoDAO;
import Dao.ReservaDAO;
import Dao.UsuarioDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Recurso;
import modelo.Usuario;
import vista.AprobacionReservasVista;
import vista.RecursosVista;
import vista.ReservaVista;
import vista.UsuariosVista;

import java.util.List;

public class MainApp extends Application {

    // DAO
    private final UsuarioDAO usuarioDAO   = new UsuarioDAO();
    private final RecursoDAO recursoDAO   = new RecursoDAO();
    private final ReservaDAO reservaDAO   = new ReservaDAO();

    @Override
    public void start(Stage primaryStage) {
        mostrarLogin(primaryStage);
    }

    // login
    public void mostrarLogin(Stage stage) {
        Label titulo = new Label("ReservaUNA");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Inicio de sesión");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        TextField campoCorreo = new TextField();
        campoCorreo.setPromptText("Correo");
        campoCorreo.setMaxWidth(280);

        PasswordField campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Contraseña");
        campoContrasena.setMaxWidth(280);

        Label mensaje = new Label();
        mensaje.setStyle("-fx-text-fill: red;");

        Button btnEntrar = new Button("Entrar");
        btnEntrar.setMinWidth(280);

        btnEntrar.setOnAction(e -> {
            String correo = campoCorreo.getText().trim();
            String clave  = campoContrasena.getText().trim();

            if (correo.isEmpty() || clave.isEmpty()) {
                mensaje.setText("Complete todos los campos.");
                return;
            }

            // Autenticación DB
            Usuario usuario = usuarioDAO.autenticar(correo, clave);

            if (usuario == null) {
                mensaje.setText("Correo o contraseña incorrectos.");
            } else {
                mostrarMenuPrincipal(stage, usuario);
            }
        });

        campoContrasena.setOnAction(e -> btnEntrar.fire());

        VBox layout = new VBox(10, titulo, subtitulo,
                new Label("Correo:"), campoCorreo,
                new Label("Contraseña:"), campoContrasena,
                btnEntrar, mensaje);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(30));
        layout.setMaxWidth(320);

        VBox contenedor = new VBox(layout);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(40));

        stage.setScene(new Scene(contenedor, 440, 360));
        stage.setTitle("ReservaUNA — Login");
        stage.show();
    }

    // menu
    private void mostrarMenuPrincipal(Stage stage, Usuario usuario) {
        Label titulo = new Label("ReservaUNA");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoUsuario = new Label("Sesión: " + usuario.getNombre()
                + "  |  Rol: " + usuario.getRol());
        infoUsuario.setStyle("-fx-text-fill: #444; -fx-font-size: 12px;");

        VBox botones = new VBox(10);
        botones.setAlignment(Pos.CENTER);

        Button btnReservas = boton("📅  Mis reservas");
        btnReservas.setOnAction(e -> {
            List<Recurso> recursos = recursoDAO.listar();
            List<Usuario> usuarios = usuarioDAO.listar();
            ReservaVista rv = new ReservaVista(
                    stage, usuario, recursos, usuarios, reservaDAO);
            rv.mostrar();
            agregarBotonVolver(stage, usuario);
        });
        botones.getChildren().add(btnReservas);

        if (usuario.getRol().equals("ADMIN")) {

            Button btnRecursos = boton("🏫  Gestionar recursos");
            btnRecursos.setOnAction(e -> {
                RecursosVista rv = new RecursosVista(stage, usuario, recursoDAO);
                rv.mostrar();
                agregarBotonVolver(stage, usuario);
            });

            Button btnUsuarios = boton("👤  Gestionar usuarios");
            btnUsuarios.setOnAction(e -> {
                UsuariosVista uv = new UsuariosVista(stage, usuarioDAO);
                uv.mostrar();
                agregarBotonVolver(stage, usuario);
            });

            Button btnAprobacion = boton("✔  Aprobar / rechazar reservas");
            btnAprobacion.setOnAction(e -> {
                List<Recurso> recursos = recursoDAO.listar();
                List<Usuario> usuarios = usuarioDAO.listar();
                AprobacionReservasVista av = new AprobacionReservasVista(
                        stage, reservaDAO, recursos, usuarios);
                av.mostrar();
                agregarBotonVolver(stage, usuario);
            });

            botones.getChildren().addAll(btnRecursos, btnUsuarios, btnAprobacion);
        }

        Button btnSalir = boton("🚪  Cerrar sesión");
        btnSalir.setStyle("-fx-base: #e0e0e0;");
        btnSalir.setOnAction(e -> mostrarLogin(stage));
        botones.getChildren().add(btnSalir);

        VBox raiz = new VBox(12, titulo, infoUsuario, new Separator(), botones);
        raiz.setPadding(new Insets(30));
        raiz.setAlignment(Pos.TOP_CENTER);

        int alto = usuario.getRol().equals("ADMIN") ? 370 : 230;
        stage.setScene(new Scene(raiz, 380, alto));
        stage.setTitle("ReservaUNA — Menú principal");
    }

    private Button boton(String texto) {
        Button b = new Button(texto);
        b.setMinWidth(280);
        b.setAlignment(Pos.CENTER_LEFT);
        return b;
    }

    private void agregarBotonVolver(Stage stage, Usuario usuario) {
        Scene escenaActual = stage.getScene();
        if (escenaActual.getRoot() instanceof VBox raiz) {
            Button btnVolver = new Button("← Volver al menú");
            btnVolver.setOnAction(e -> mostrarMenuPrincipal(stage, usuario));
            raiz.getChildren().add(btnVolver);
            VBox.setMargin(btnVolver, new Insets(5, 15, 15, 15));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}