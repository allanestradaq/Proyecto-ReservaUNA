/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author allan
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.Usuario;
import java.util.List;

public class LoginVista {

    private Stage stage;
    private List<Usuario> usuarios;

    public LoginVista(Stage stage, List<Usuario> usuarios) {
        this.stage = stage;
        this.usuarios = usuarios;
    }

    public void mostrar() {
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
                abrirMenuPrincipal(encontrado);
            }
        });

        VBox layout = new VBox(12, titulo, campoCorreo, campoContrasena, btnEntrar, mensaje);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setMaxWidth(320);

        VBox contenedor = new VBox(layout);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(40));

        Scene escena = new Scene(contenedor, 420, 320);
        stage.setTitle("ReservaUNA — Login");
        stage.setScene(escena);
        stage.show();
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        System.out.println("Sesión iniciada: " + usuario.getNombre());
    }
}