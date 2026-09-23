package ve.ucv.campusexpress.vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import ve.ucv.campusexpress.util.Mensajes;

/**
 * Pantalla de inicio de sesión de Campus Express (Swing).
 *
 * <p>Solo contiene componentes visuales. Si el equipo cambia el diseño del prototipo, basta con
 * modificar esta clase mientras siga implementando {@link LoginVista}.</p>
 */
public class LoginVentana extends JFrame implements LoginVista {

    private static final long serialVersionUID = 1L;
    private static final Color COLOR_ERROR = new Color(0xB0, 0x00, 0x20);

    private final JTextField campoUsuario = new JTextField(20);
    private final JPasswordField campoContrasena = new JPasswordField(20);
    private final JButton botonIniciarSesion = new JButton(Mensajes.get("login.boton"));
    private final JLabel etiquetaError = new JLabel(" ", SwingConstants.CENTER);

    public LoginVentana() {
        super(Mensajes.get("app.nombre") + " - " + Mensajes.get("login.titulo"));
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel titulo = new JLabel(Mensajes.get("app.nombre"), SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 26f));

        JLabel subtitulo = new JLabel(Mensajes.get("app.subtitulo"), SwingConstants.CENTER);
        subtitulo.setForeground(Color.GRAY);

        JLabel etiquetaUsuario = new JLabel(Mensajes.get("login.usuario"));
        etiquetaUsuario.setLabelFor(campoUsuario);
        JLabel etiquetaContrasena = new JLabel(Mensajes.get("login.contrasena"));
        etiquetaContrasena.setLabelFor(campoContrasena);

        etiquetaError.setForeground(COLOR_ERROR);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.insets = new Insets(4, 0, 4, 0);
        panel.add(titulo, c);
        c.gridy = 1;
        panel.add(subtitulo, c);
        c.gridy = 2;
        c.insets = new Insets(20, 0, 2, 0);
        panel.add(etiquetaUsuario, c);
        c.gridy = 3;
        c.insets = new Insets(2, 0, 4, 0);
        panel.add(campoUsuario, c);
        c.gridy = 4;
        c.insets = new Insets(10, 0, 2, 0);
        panel.add(etiquetaContrasena, c);
        c.gridy = 5;
        c.insets = new Insets(2, 0, 4, 0);
        panel.add(campoContrasena, c);
        c.gridy = 6;
        c.insets = new Insets(10, 0, 4, 0);
        panel.add(etiquetaError, c);
        c.gridy = 7;
        c.insets = new Insets(4, 0, 0, 0);
        panel.add(botonIniciarSesion, c);

        setContentPane(panel);
        getRootPane().setDefaultButton(botonIniciarSesion); // Enter equivale a presionar el botón
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @Override
    public String getNombreUsuario() {
        return campoUsuario.getText();
    }

    @Override
    public char[] getContrasena() {
        return campoContrasena.getPassword();
    }

    @Override
    public void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
    }

    @Override
    public void limpiarContrasena() {
        campoContrasena.setText("");
        campoContrasena.requestFocusInWindow();
    }

    @Override
    public void setAccionIniciarSesion(Runnable accion) {
        botonIniciarSesion.addActionListener(evento -> accion.run());
    }

    @Override
    public void mostrar() {
        setVisible(true);
        campoUsuario.requestFocusInWindow();
    }

    @Override
    public void cerrar() {
        dispose();
    }
}
