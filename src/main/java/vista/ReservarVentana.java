package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// Ventana "reserva": se abre desde Rutas Disponibles. Por ahora solo tiene el botón para regresar.
public class ReservarVentana extends JFrame implements ReservarVista {

    private static final long serialVersionUID = 1L;

    private static final int ANCHO = 1000;
    private static final int ALTO = 650;

    private final RecursosImagen.Escalado fondo =
            new RecursosImagen.Escalado("/imagenes/MonederoPantalla.png");

    private final BotonImagen botonRegresar = new BotonImagen(
            "/imagenes/cross1.png",
            "/imagenes/cross2.png",
            "Regresar");

    public ReservarVentana() {
        super("Campus Express - Reserva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel fondoPanel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);
                g2.dispose();
            }
        };
        fondoPanel.setOpaque(false);
        fondoPanel.setBorder(new EmptyBorder(20, 55, 55, 55));

        // La X va encima de la tarjeta blanca, alineada a la derecha
        JPanel filaRegresar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaRegresar.setOpaque(false);
        filaRegresar.setBorder(new EmptyBorder(0, 0, 14, 0));
        botonRegresar.setPreferredSize(new Dimension(58, 56));
        filaRegresar.add(botonRegresar);
        fondoPanel.add(filaRegresar, BorderLayout.NORTH);

        // Tarjeta blanca vacía: aquí irá el contenido de la reserva
        JPanel tarjeta = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 42, 42);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        fondoPanel.add(tarjeta, BorderLayout.CENTER);

        setContentPane(fondoPanel);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        botonRegresar.addActionListener(evento -> accion.run());
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
