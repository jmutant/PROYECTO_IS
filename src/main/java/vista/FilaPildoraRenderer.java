package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

// slider 
public final class FilaPildoraRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    private final Color colorSeleccion;
    private final Color colorFila;
    private final boolean primeraColumnaIzquierda;
    private int margenIzquierdo = 0;
    private boolean seleccionada;
    private int columna;
    private int ultimaColumna;

    public FilaPildoraRenderer(Color colorSeleccion, Color colorFila) {
        this(colorSeleccion, colorFila, false);
    }

    /** @param primeraColumnaIzquierda si es true, el texto de la primera columna va a la izquierda (con margen) */
    public FilaPildoraRenderer(Color colorSeleccion, Color colorFila, boolean primeraColumnaIzquierda) {
        this.colorSeleccion = colorSeleccion;
        this.colorFila = colorFila;
        this.primeraColumnaIzquierda = primeraColumnaIzquierda;
    }

    /** Margen (en píxeles) a la izquierda del texto de la primera columna cuando va alineada a la izquierda. */
    public void setMargenIzquierdo(int margenIzquierdo) {
        this.margenIzquierdo = margenIzquierdo;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);
        seleccionada = isSelected;
        columna = column;
        ultimaColumna = table.getColumnCount() - 1;
        boolean izquierda = primeraColumnaIzquierda && column == 0;
        setHorizontalAlignment(izquierda ? SwingConstants.LEFT : SwingConstants.CENTER);
        setFont(table.getFont());
        setForeground(isSelected ? Color.WHITE : Color.BLACK);
        setOpaque(false);
        setBorder(izquierda ? new javax.swing.border.EmptyBorder(0, margenIzquierdo, 0, 0) : null);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int margen = Math.max(1, Math.round(h * 0.04f));
        int alto = h - 2 * margen;
        g2.setColor(seleccionada ? colorSeleccion : colorFila);
        if (columna == 0) {
            g2.fillRoundRect(0, margen, w + alto, alto, alto, alto);
        } else if (columna == ultimaColumna) {
            g2.fillRoundRect(-alto, margen, w + alto, alto, alto, alto);
        } else {
            g2.fillRect(0, margen, w, alto);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
