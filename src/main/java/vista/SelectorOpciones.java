package vista;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

/**
 * Lista desplegable de opciones de texto (por ejemplo, los bancos del Pago Móvil).
 * Mismo diseño y comportamiento que SelectorRol: campo gris redondeado, flecha a la derecha
 * (desplegableArrow1.png; desplegableArrow2.png al pasar el mouse o con la lista abierta)
 * y lista que se despliega con resaltado por fila. También funciona con teclado.
 */
public class SelectorOpciones extends JComponent {

    private static final long serialVersionUID = 1L;

    // Medidas del diseño original (maqueta a 1920x1080); todo se escala según el ancho real.
    private static final double ANCHO_REF = 618;
    private static final double ALTO_CERRADO_REF_DEFECTO = 92;
    private static final double FUENTE_REF_DEFECTO = 38;
    private static final double ALTO_FILA_REF = 83;
    private static final double MARGEN_VERTICAL_REF = 10;

    private static final Color GRIS = new Color(217, 217, 217);
    private static final Color GRIS_HOVER = new Color(198, 198, 198);
    private static final Color TEXTO = new Color(20, 20, 20);

    private final String[] opciones;
    private final double altoCerradoRef;
    private double fuenteRef = FUENTE_REF_DEFECTO;
    private final boolean desplegable;
    private int seleccionado = 0;
    private boolean abierto = false;
    private int resaltado = -1;
    private boolean sobreCampo = false;

    private final transient RecursosImagen.Escalado fondoCerrado = new RecursosImagen.Escalado("/imagenes/appButton.png");
    private final transient RecursosImagen.Escalado flechaCerrada = new RecursosImagen.Escalado("/imagenes/desplegableArrow1.png");
    private final transient RecursosImagen.Escalado flechaAbierta = new RecursosImagen.Escalado("/imagenes/desplegableArrow2.png");

    public SelectorOpciones(String... opciones) {
        this(ALTO_CERRADO_REF_DEFECTO, opciones);
    }

    /**
     * @param altoCerradoRef alto del campo cerrado en la misma escala de la maqueta (ancho = 618);
     *                       por defecto 92. Sirve para igualar la altura de otros campos del formulario.
     */
    public SelectorOpciones(double altoCerradoRef, String... opciones) {
        if (opciones == null || opciones.length == 0) {
            throw new IllegalArgumentException("Se necesita al menos una opción.");
        }
        this.opciones = opciones.clone();
        this.altoCerradoRef = altoCerradoRef;
        this.desplegable = opciones.length > 1;

        setFocusable(true);
        setOpaque(false);
        setPreferredSize(new Dimension((int) ANCHO_REF, (int) altoCerradoRef));
        if (desplegable) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (!desplegable) {
                    return;
                }
                if (!abierto) {
                    abrir();
                } else {
                    int fila = filaEn(e.getY());
                    if (fila >= 0) {
                        seleccionado = fila;
                    }
                    cerrar();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                sobreCampo = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sobreCampo = false;
                resaltado = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (abierto) {
                    int fila = filaEn(e.getY());
                    if (fila != resaltado) {
                        resaltado = fila;
                        repaint();
                    }
                }
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                cerrar();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        if (desplegable) {
                            if (abierto) {
                                confirmarResaltado();
                            } else {
                                abrir();
                            }
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_DOWN:
                        mover(1);
                        e.consume();
                        break;
                    case KeyEvent.VK_UP:
                        mover(-1);
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (abierto) {
                            confirmarResaltado();
                            e.consume();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (abierto) {
                            cerrar();
                            e.consume();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public String getOpcionSeleccionada() {
        return opciones[seleccionado];
    }

    /** Tamaño de la letra en la escala de la maqueta (ancho = 618); por defecto 38. */
    public void setFuenteRef(double fuenteRef) {
        this.fuenteRef = fuenteRef;
        repaint();
    }

    public int getIndiceSeleccionado() {
        return seleccionado;
    }

    public void setIndiceSeleccionado(int indice) {
        if (indice >= 0 && indice < opciones.length) {
            seleccionado = indice;
            repaint();
        }
    }

    /** Vuelve a la primera opción y cierra la lista. */
    public void reiniciar() {
        seleccionado = 0;
        cerrar();
        repaint();
    }

    public boolean isAbierto() {
        return abierto;
    }
    // Tamaño (en píxeles) que debe tener el componente cuando está cerrado, para un ancho dado
    public int getAlturaCerrada(int ancho) {
        return (int) Math.round(altoCerradoRef * ancho / ANCHO_REF);
    }

    // Tamaño que debe tener el componente cuando está desplegado, para un ancho dado
    public int getAlturaAbierta(int ancho) {
        double ref = MARGEN_VERTICAL_REF + ALTO_FILA_REF * opciones.length;
        return (int) Math.round(ref * ancho / ANCHO_REF);
    }

    // ------------------------------------------------------------------ estado

    private void abrir() {
        abierto = true;
        resaltado = seleccionado;
        pedirNuevoLayout();
    }

    private void cerrar() {
        if (!abierto) {
            return;
        }
        abierto = false;
        resaltado = -1;
        pedirNuevoLayout();
    }

    private void confirmarResaltado() {
        if (resaltado >= 0) {
            seleccionado = resaltado;
        }
        cerrar();
        repaint();
    }

    private void mover(int delta) {
        if (abierto) {
            int base = resaltado < 0 ? seleccionado : resaltado;
            resaltado = Math.max(0, Math.min(opciones.length - 1, base + delta));
        } else {
            seleccionado = Math.max(0, Math.min(opciones.length - 1, seleccionado + delta));
        }
        repaint();
    }

    /** El padre (con layout manual) recalcula el tamaño del selector según esté abierto o cerrado. */
    private void pedirNuevoLayout() {
        Container padre = getParent();
        if (padre != null) {
            padre.doLayout();
            padre.repaint();
        }
        repaint();
    }

    private int filaEn(int y) {
        double u = getWidth() / ANCHO_REF;
        double fila = (y / u - MARGEN_VERTICAL_REF / 2) / ALTO_FILA_REF;
        int i = (int) Math.floor(fila);
        return (i >= 0 && i < opciones.length) ? i : -1;
    }

    // ------------------------------------------------------------------ dibujo

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        double u = w / ANCHO_REF;
        int altoCerrado = (int) Math.round(altoCerradoRef * u);
        g2.setFont(new Font("Arial", Font.PLAIN, Math.max(1, (int) Math.round(fuenteRef * u))));

        if (!abierto) {
            g2.drawImage(fondoCerrado.obtener(w, altoCerrado), 0, 0, null);
            dibujarTextoCentrado(g2, opciones[seleccionado], w, altoCerrado / 2);
        } else {
            int arco = altoCerrado;
            g2.setColor(GRIS);
            g2.fillRoundRect(0, 0, w, getHeight(), arco, arco);

            for (int i = 0; i < opciones.length; i++) {
                int arribaFila = (int) Math.round((MARGEN_VERTICAL_REF / 2 + ALTO_FILA_REF * i) * u);
                int altoFila = (int) Math.round(ALTO_FILA_REF * u);
                if (i == resaltado) {
                    g2.setColor(GRIS_HOVER);
                    int margen = (int) Math.round(24 * u);
                    int arcoFila = (int) Math.round(60 * u);
                    g2.fillRoundRect(margen, arribaFila + 2, w - 2 * margen, altoFila - 4, arcoFila, arcoFila);
                }
                dibujarTextoCentrado(g2, opciones[i], w, arribaFila + altoFila / 2);
            }
        }

        if (desplegable) {
            int ax = (int) Math.round(522 * u);
            int ay = (altoCerrado - (int) Math.round(34 * u)) / 2;
            int aw = (int) Math.round(52 * u);
            int ah = (int) Math.round(34 * u);
            RecursosImagen.Escalado flecha = (abierto || sobreCampo) ? flechaAbierta : flechaCerrada;
            g2.drawImage(flecha.obtener(aw, ah), ax, ay, null);
        }
        g2.dispose();
    }

    private void dibujarTextoCentrado(Graphics2D g2, String texto, int ancho, int centroY) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (ancho - fm.stringWidth(texto)) / 2;
        int y = centroY + (fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(TEXTO);
        g2.drawString(texto, x, y);
    }
}
