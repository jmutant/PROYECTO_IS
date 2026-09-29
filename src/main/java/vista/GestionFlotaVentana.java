package vista;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class GestionFlotaVentana extends JFrame implements GestionFlotaVista {

    private static final long serialVersionUID = 1L;
    private final JButton botonRegresar = new JButton("Regresar");

    public GestionFlotaVentana() {
        super("Campus Express - Gestión de Flota");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Solo se muestra el botón para regresar, centrado en la ventana.
        JPanel principal = new JPanel(new GridBagLayout());
        principal.add(botonRegresar);
        setContentPane(principal);
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        botonRegresar.addActionListener(e -> accion.run());
    }

    @Override
    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void cerrar() {
        dispose();
    }
}
