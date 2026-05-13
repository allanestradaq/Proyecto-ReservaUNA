/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Espacio;
import modelo.Equipo;
import modelo.Usuario;
import servicio.RecursoServicio;
import servicio.ReservaServicio;
import vista.AprobacionReservasVista;
import vista.RecursosVista;
import vista.ReservaVista;
import vista.UsuariosVista;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private RecursoServicio recursoServicio = new RecursoServicio();
    private ReservaServicio reservaServicio = new ReservaServicio();
    private List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        cargarDatosIniciales();
        mostrarLogin(primaryStage);
    }

    private void cargarDatosIniciales() {
        usuarios.add(new Usuario(1, "Administrador", "admin@una.ac.cr", "admin123", "ADMIN"));
        usuarios.add(new Usuario(2, "María González", "maria@una.ac.cr", "pass123", "USUARIO"));

        recursoServicio.agregar(new Espacio(recursoServicio.siguienteId(),
                "Aula 101", "Aula general planta baja", 30, "Edificio A", "Aula"));
        recursoServicio.agregar(new Equipo(recursoServicio.siguienteId(),
                "Proyector BenQ", "Proyector HDMI portátil", "BenQ", "MX505", "SN-00123"));
    }

    //login
 
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
            String clave = campoContrasena.getText().trim();

            if (correo.isEmpty() || clave.isEmpty()) {
                mensaje.setText("Complete todos los campos.");
                return;
            }

            Usuario encontrado = null;
            for (Usuario u : usuarios) {
                if (u.getCorreo().equals(correo) && u.getContrasena().equals(clave)) {
                    encontrado = u;
                    break;
                }
            }

            if (encontrado == null) {
                mensaje.setText("Correo o contraseña incorrectos.");
            } else {
                mostrarMenuPrincipal(stage, encontrado);
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

    //menu
    private void mostrarMenuPrincipal(Stage stage, Usuario usuario) {
        Label titulo = new Label("ReservaUNA");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoUsuario = new Label("Sesión: " + usuario.getNombre()
                + "  |  Rol: " + usuario.getRol());
        infoUsuario.setStyle("-fx-text-fill: #444; -fx-font-size: 12px;");

        VBox botones = new VBox(10);
        botones.setAlignment(Pos.CENTER);

        // Opción disponible para todos los roles
        Button btnReservas = boton("📅  Mis reservas");
        btnReservas.setOnAction(e -> {
            ReservaVista rv = new ReservaVista(
                    stage, usuario, recursoServicio, reservaServicio);
            rv.mostrar();
            agregarBotonVolver(stage, usuario);
        });
        botones.getChildren().add(btnReservas);

        // exclucivo admin
        if (usuario.getRol().equals("ADMIN")) {

            Button btnRecursos = boton("🏫  Gestionar recursos");
            btnRecursos.setOnAction(e -> {
                RecursosVista rv = new RecursosVista(stage, usuario, recursoServicio);
                rv.mostrar();
                agregarBotonVolver(stage, usuario);
            });

            Button btnUsuarios = boton("👤  Gestionar usuarios");
            btnUsuarios.setOnAction(e -> {
                UsuariosVista uv = new UsuariosVista(stage, usuarios);
                uv.mostrar();
                agregarBotonVolver(stage, usuario);
            });

            Button btnAprobacion = boton("✔  Aprobar / rechazar reservas");
            btnAprobacion.setOnAction(e -> {
                AprobacionReservasVista av =
                        new AprobacionReservasVista(stage, reservaServicio);
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