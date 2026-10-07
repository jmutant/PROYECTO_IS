package vista;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class BotonImagen extends JButton {

    private static final long serialVersionUID = 1L;

    private final transient RecursosImagen.Escalado imagenNormal;
    private final transient RecursosImagen.Escalado imagenHover;

    public BotonImagen(String rutaNormal, String rutaHover, String descripcion) {
        this.imagenNormal = new RecursosImagen.Escalado(rutaNormal);
        this.imagenHover = new RecursosImagen.Escalado(rutaHover);

        setRolloverEnabled(true);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(descripcion);
        getAccessibleContext().setAccessibleName(descripcion);
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean activo = getModel().isRollover() || getModel().isPressed();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage((activo ? imagenHover : imagenNormal).obtener(getWidth(), getHeight()), 0, 0, null);
        g2.dispose();
    }
}
