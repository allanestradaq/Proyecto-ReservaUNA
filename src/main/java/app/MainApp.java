/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package app;

/**
 *
 * @author allan
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import modelo.Espacio;
import modelo.Equipo;
import modelo.Usuario;
import servicio.RecursoServicio;
import servicio.ReservaServicio;
import vista.LoginVista;
import vista.RecursosVista;

import java.util.ArrayList;
import java.util.List;
import vista.ReservaVista;

public class MainApp extends Application {

    // Servicios compartidos durante toda la sesión
    private final RecursoServicio recursoServicio = new RecursoServicio();
    private final ReservaServicio reservaServicio = new ReservaServicio();
    private final List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        cargarDatosIniciales();
        mostrarLogin(primaryStage);
    }

    /**
     * Datos de prueba para poder iniciar sesión y probar el sistema.
     */
    private void cargarDatosIniciales() {
        usuarios.add(new Usuario(1, "Administrador", "admin@una.ac.cr", "admin123", "ADMIN"));
        usuarios.add(new Usuario(2, "María González", "maria@una.ac.cr", "pass123", "USUARIO"));

        recursoServicio.agregar(new Espacio(recursoServicio.siguienteId(),
                "Aula 101", "Aula general planta baja", 30, "Edificio A", "Aula"));
        recursoServicio.agregar(new Equipo(recursoServicio.siguienteId(),
                "Proyector BenQ", "Proyector HDMI portátil", "BenQ", "MX505", "SN-00123"));
    }

    public void mostrarLogin(Stage stage) {
        LoginVista login = new LoginVista(stage, usuarios) {
            // Sobrescribe el comportamiento interno de abrirMenuPrincipal
        };

        // Redefine el comportamiento del login directamente aquí
        javafx.geometry.Insets padding = new Insets(30);

        Label titulo = new Label("ReservaUNA — Inicio de sesión");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField campoCorreo = new TextField();
        campoCorreo.setPromptText("Correo");

        PasswordField campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Contraseña");

        Label mensaje = new Label();
        mensaje.setStyle("-fx-text-fill: red;");

        Button btnEntrar = new Button("Entrar");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);

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

        VBox layout = new VBox(12, titulo, campoCorreo, campoContrasena, btnEntrar, mensaje);
        layout.setPadding(padding);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setMaxWidth(320);

        VBox contenedor = new VBox(layout);
        contenedor.setAlignment(javafx.geometry.Pos.CENTER);
        contenedor.setPadding(new Insets(40));

        Scene escena = new Scene(contenedor, 420, 320);
        stage.setTitle("ReservaUNA — Login");
        stage.setScene(escena);
        stage.show();
    }

    private void mostrarMenuPrincipal(Stage stage, Usuario usuario) {
        Label titulo = new Label("Bienvenido/a, " + usuario.getNombre()
                + "  |  Rol: " + usuario.getRol());
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Button btnRecursos = new Button("Gestionar Recursos");
        Button btnReservas = new Button("Gestionar Reservas");
        Button btnSalir = new Button("Cerrar sesión");

        btnRecursos.setMinWidth(200);
        btnReservas.setMinWidth(200);
        btnSalir.setMinWidth(200);

        // Solo el admin puede gestionar recursos
        btnRecursos.setDisable(!usuario.getRol().equals("ADMIN"));

        btnRecursos.setOnAction(e -> {
            RecursosVista rv = new RecursosVista(stage, usuario, recursoServicio);
            rv.mostrar();
            agregarBotonVolver(stage, usuario);
        });

        btnReservas.setOnAction(e -> {
            ReservaVista resv = new ReservaVista(stage, usuario, recursoServicio, reservaServicio);
            resv.mostrar();
            agregarBotonVolver(stage, usuario);
        });

        btnSalir.setOnAction(e -> mostrarLogin(stage));

        VBox menu = new VBox(14, titulo, btnRecursos, btnReservas, btnSalir);
        menu.setPadding(new Insets(40));
        menu.setAlignment(javafx.geometry.Pos.CENTER);

        Scene escena = new Scene(menu, 380, 280);
        stage.setScene(escena);
        stage.setTitle("ReservaUNA — Menú principal");
    }

    /**
     * Agrega un botón "Volver al menú" en la parte inferior de la escena actual.
     */
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