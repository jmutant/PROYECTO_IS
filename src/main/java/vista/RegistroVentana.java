package vista;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import modelo.AutenticacionServicio;
import modelo.UsuarioDuplicadoException;

/**
 * Ventana de registro (público, o creación de Administradores si modoAdministrador = true).
 * El diseño y los campos están en {@link RegistroPanel}; aquí solo se arma la ventana y se
 * conecta el formulario con el servicio de autenticación.
 */
public class RegistroVentana extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient AutenticacionServicio autenticacionServicio;
    private final boolean modoAdministrador;
    private final RegistroPanel panel;

    public RegistroVentana(AutenticacionServicio autenticacionServicio) {
        this(autenticacionServicio, false);
    }

    /** @param modoAdministrador true para crear un usuario Administrador (el selector de rol solo muestra Administrador). */
    public RegistroVentana(AutenticacionServicio autenticacionServicio, boolean modoAdministrador) {
        super(modoAdministrador
                ? "Campus Express - Crear Administrador"
                : "Campus Express - Registro de usuario");
        this.autenticacionServicio = autenticacionServicio;
        this.modoAdministrador = modoAdministrador;
        this.panel = new RegistroPanel(modoAdministrador);
        construirInterfaz();
    }

    private void construirInterfaz() {
        setUndecorated(true); // la "X" del diseño reemplaza al botón Cancelar y al cierre de la ventana
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        aplicarFondoTransparente(); // debe hacerse antes de mostrar la ventana

        panel.setAccionRegistrar(this::registrar);
        panel.setAccionCerrar(this::dispose);

        setContentPane(panel);
        getRootPane().setDefaultButton(panel.getBotonRegistrar());
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cerrarRegistro");
        getRootPane().getActionMap().put("cerrarRegistro", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    // Ventana con fodno transparente
    private void aplicarFondoTransparente() {
        boolean transparente = false;
        try {
            GraphicsDevice pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (pantalla.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
                setBackground(new Color(0, 0, 0, 0));
                getRootPane().setOpaque(false);
                transparente = true;
            }
        } catch (RuntimeException ex) {
            transparente = false; // si algo falla, queda el fondo normal
        }
        panel.setFondoTransparente(transparente);
    }

    private void registrar() {
        try {
            if (modoAdministrador) {
                autenticacionServicio.registrarAdministrador(
                        panel.getNombre(),
                        panel.getApellido(),
                        panel.getCedula(),
                        panel.getUsername(),
                        panel.getPassword()
                );
            } else {
                autenticacionServicio.registrarUsuario(
                        panel.getNombre(),
                        panel.getApellido(),
                        panel.getCedula(),
                        panel.getRolSeleccionado(),
                        panel.getUsername(),
                        panel.getPassword()
                );
            }

            JOptionPane.showMessageDialog(this,
                    modoAdministrador
                            ? "Administrador creado correctamente."
                            : "Usuario registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            panel.limpiar();
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

    public void mostrar() {
        setVisible(true);
        panel.enfocarPrimerCampo();
    }
}
