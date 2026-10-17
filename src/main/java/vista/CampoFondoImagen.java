package vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;

// Campo de texto con el fondo gris redondeado de appButton.png
public class CampoFondoImagen extends JTextField {
    private static final long serialVersionUID = 1L;

    private final transient RecursosImagen.Escalado fondo;

    public CampoFondoImagen() {
        this("/imagenes/appButton.png");
    }


    public CampoFondoImagen(String rutaFondo) {
        this.fondo = new RecursosImagen.Escalado(rutaFondo);
        setOpaque(false);
        setForeground(Color.BLACK);
        setCaretColor(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);
        super.paintComponent(g2);
        g2.dispose();
    }
}
