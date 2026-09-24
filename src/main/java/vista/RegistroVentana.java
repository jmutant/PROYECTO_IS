package vista;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import modelo.AutenticacionServicio;
import modelo.Rol;
import modelo.UsuarioDuplicadoException;

/** Formulario Swing para registrar usuarios persistentes. */
public class RegistroVentana extends JFrame {
    private static final long serialVersionUID = 1L;

    private final AutenticacionServicio autenticacionServicio;

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtApellido = new JTextField(20);
    private final JTextField txtCedula = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);

    private final JComboBox<Rol> comboRoles = new JComboBox<>(new Rol[] {
            Rol.ESTUDIANTE,
            Rol.EMPLEADO,
            Rol.PROFESOR,
            Rol.PUBLICO_GENERAL,
            Rol.ADMINISTRADOR
    });

    public RegistroVentana(AutenticacionServicio autenticacionServicio) {
        super("Campus Express - Registro de usuario");
        this.autenticacionServicio = autenticacionServicio;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(new EmptyBorder(20, 24, 20, 24));
        agregar(formulario, "Nombre:", txtNombre, 0);
        agregar(formulario, "Apellido:", txtApellido, 1);
        agregar(formulario, "Cédula:", txtCedula, 2);
        agregar(formulario, "Rol:", comboRoles, 3);
        agregar(formulario, "Usuario:", txtUsername, 4);
        agregar(formulario, "Contraseña:", txtPassword, 5);

        JButton botonRegistrar = new JButton("Registrar");
        JButton botonCancelar = new JButton("Cancelar");

        botonRegistrar.addActionListener(e -> registrar());
        botonCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(botonRegistrar);
        botones.add(botonCancelar);

        JPanel principal = new JPanel(new BorderLayout(0, 10));
        principal.add(formulario, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);
        setContentPane(principal);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void agregar(JPanel panel, String texto, java.awt.Component componente, int fila) {
        GridBagConstraints etiqueta = new GridBagConstraints();
        etiqueta.gridx = 0; etiqueta.gridy = fila;
        etiqueta.anchor = GridBagConstraints.WEST;
        etiqueta.insets = new Insets(5, 5, 5, 10);
        panel.add(new JLabel(texto), etiqueta);

        GridBagConstraints campo = new GridBagConstraints();
        campo.gridx = 1; campo.gridy = fila;
        campo.weightx = 1; campo.fill = GridBagConstraints.HORIZONTAL;
        campo.insets = new Insets(5, 5, 5, 5);
        panel.add(componente, campo);
    }

    private void registrar() {
        try {
            /*
             * Este es el flujo solicitado:
             */
            autenticacionServicio.registrarUsuario(
                    txtNombre.getText(),
                    txtApellido.getText(),
                    txtCedula.getText(),
                    (Rol) comboRoles.getSelectedItem(),
                    txtUsername.getText(),
                    new String(txtPassword.getPassword())
            );

            JOptionPane.showMessageDialog(this,
                    "Usuario registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            dispose();

        } catch (UsuarioDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Usuario duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }

    public void mostrar() {
        setVisible(true);
        txtNombre.requestFocusInWindow();
    }
}
