package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

// bara de desplazamiento
public final class BarraDesplazamientoUI extends BasicScrollBarUI {
    private final Color colorPista;
    private final Color colorControl;
    private final boolean controlCortoSiNoHayRecorrido;


    public BarraDesplazamientoUI(Color colorPista, Color colorControl, boolean controlCortoSiNoHayRecorrido) {
        this.colorPista = colorPista;
        this.colorControl = colorControl;
        this.controlCortoSiNoHayRecorrido = controlCortoSiNoHayRecorrido;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return botonVacio();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return botonVacio();
    }

    private static JButton botonVacio() {
        JButton b = new JButton();
        Dimension cero = new Dimension(0, 0);
        b.setPreferredSize(cero);
        b.setMinimumSize(cero);
        b.setMaximumSize(cero);
        return b;
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        int ancho = Math.max(10, scrollbar.getWidth() - scrollbar.getInsets().left - scrollbar.getInsets().right);
        return new Dimension(ancho, ancho);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle pista = trackRect;
        g2.setColor(colorPista);
        g2.fillRoundRect(pista.x, pista.y, pista.width, pista.height, pista.width, pista.width);
        Rectangle pulgar = thumbRect;
        g2.setColor(colorControl);
        if (controlCortoSiNoHayRecorrido && (pulgar.isEmpty() || pulgar.height >= pista.height)) {
            int alto = Math.min(pista.height, Math.round(pista.width * 1.3f));
            g2.fillRoundRect(pista.x, pista.y, pista.width, alto, pista.width, pista.width);
        } else if (!pulgar.isEmpty()) {
            g2.fillRoundRect(pulgar.x, pulgar.y, pulgar.width, pulgar.height, pulgar.width, pulgar.width);
        }
        g2.dispose();
    }
}
