package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

// mantener estilo de listas dezplegables
public final class EstiloCombo {

    // Mismos colores que SelectorOpciones (lista desplegable gris redondeada)
    private static final Color GRIS = new Color(217, 217, 217);
    private static final Color GRIS_HOVER = new Color(198, 198, 198);
    private static final Color TEXTO = new Color(20, 20, 20);
    private static final Color PULGAR = new Color(150, 150, 150);
    private static final Color TRANSPARENTE = new Color(0, 0, 0, 0);
    private static final int ARCO_LISTA = 36;
    private static final String FONDO_DEFECTO = "/imagenes/appButton.png";

    private EstiloCombo() { }

    
    public static <T> void aplicar(JComboBox<T> combo, boolean flecha, boolean centrado,
            Function<Object, String> texto) {
        aplicar(combo, flecha, centrado, texto, FONDO_DEFECTO);
    }

    
    public static <T> void aplicar(JComboBox<T> combo, boolean flecha, boolean centrado,
            Function<Object, String> texto, String rutaFondo) {
        combo.setOpaque(false);
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setForeground(Color.BLACK);
        combo.setMaximumRowCount(6);
        combo.setRenderer(new RendererCombo(combo, centrado, texto));
        combo.setUI(new ComboUI(flecha, rutaFondo));
    }// aveces falla la imagen x alguna razón

    // ------------------------------------------------------------------ renderer

    private static final class RendererCombo extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        private final JComboBox<?> combo;
        private final boolean centrado;
        private final transient Function<Object, String> texto;
        private boolean pastilla = false;

        RendererCombo(JComboBox<?> combo, boolean centrado, Function<Object, String> texto) {
            this.combo = combo;
            this.centrado = centrado;
            this.texto = texto != null ? texto : v -> v == null ? "" : String.valueOf(v);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, false);
            setText(value == null ? "" : texto.apply(value));
            setFont(combo.getFont());
            setOpaque(false);
            setForeground(TEXTO);
            if (index < 0) {
                // texto del campo cerrado: sin fondo propio, se ve el gris del combo
                pastilla = false;
                int margen = Math.max(6, getFontMetrics(getFont()).getHeight() / 2);
                setHorizontalAlignment(centrado ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, centrado ? 2 : margen, 0, 2));
            } else {
                // filas de la lista: texto centrado y, en la fila resaltada, pastilla gris más oscura
                pastilla = isSelected;
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 22, 0, 22));
                setPreferredSize(new Dimension(10, getFontMetrics(getFont()).getHeight() + 14));
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (pastilla) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GRIS_HOVER);
                int margen = 10;
                int alto = getHeight() - 4;
                g2.fillRoundRect(margen, 2, getWidth() - 2 * margen, alto, alto, alto);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------------------------ UI del combo

    private static final class ComboUI extends BasicComboBoxUI {
        private final boolean flecha;
        private final transient RecursosImagen.Escalado fondo;
        private final transient BufferedImage origenFondo;
        private transient BufferedImage fondoTresPartes;
        private boolean sobre = false;
        private final MouseAdapter seguirMouse = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sobre = true;
                comboBox.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sobre = comboBox.getMousePosition(true) != null;
                comboBox.repaint();
            }
        };

        ComboUI(boolean flecha, String rutaFondo) {
            this.flecha = flecha;
            this.fondo = cargarFondo(rutaFondo);
            this.origenFondo = cargarOrigen(rutaFondo);
        }

        private static BufferedImage cargarOrigen(String ruta) {
            try {
                return RecursosImagen.cargar(ruta);
            } catch (IllegalStateException e) {
                return RecursosImagen.cargar(FONDO_DEFECTO);
            }
        }

        // Si el combo es mucho más ancho que la imagen, estirarla deforma las puntas redondas:
        // se dibujan las dos puntas con su proporción original y solo se estira el centro.
        private BufferedImage fondoParaTamano(int w, int h) {
            double proporcionCombo = (double) w / h;
            double proporcionImagen = (double) origenFondo.getWidth() / origenFondo.getHeight();
            if (proporcionCombo <= proporcionImagen * 1.5) {
                return null;
            }
            if (fondoTresPartes == null || fondoTresPartes.getWidth() != w || fondoTresPartes.getHeight() != h) {
                int sw = origenFondo.getWidth();
                int sh = origenFondo.getHeight();
                int puntaOrigen = sh / 2;
                int puntaDestino = Math.max(1, h / 2);
                BufferedImage compuesta = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = compuesta.createGraphics();
                g.drawImage(RecursosImagen.escalar(origenFondo.getSubimage(0, 0, puntaOrigen, sh), puntaDestino, h),
                        0, 0, null);
                g.drawImage(RecursosImagen.escalar(origenFondo.getSubimage(puntaOrigen, 0, sw - 2 * puntaOrigen, sh),
                        Math.max(1, w - 2 * puntaDestino), h), puntaDestino, 0, null);
                g.drawImage(RecursosImagen.escalar(origenFondo.getSubimage(sw - puntaOrigen, 0, puntaOrigen, sh),
                        puntaDestino, h), w - puntaDestino, 0, null);
                g.dispose();
                fondoTresPartes = compuesta;
            }
            return fondoTresPartes;
        }

        private static RecursosImagen.Escalado cargarFondo(String ruta) {
            try {
                return new RecursosImagen.Escalado(ruta);
            } catch (IllegalStateException e) {
                return new RecursosImagen.Escalado(FONDO_DEFECTO);
            }
        }

        @Override
        protected void installListeners() {
            super.installListeners();
            comboBox.addMouseListener(seguirMouse);
            if (arrowButton != null) {
                arrowButton.addMouseListener(seguirMouse);
            }
            if (editor != null) {
                editor.addMouseListener(seguirMouse);
            }
        }

        @Override
        protected void uninstallListeners() {
            comboBox.removeMouseListener(seguirMouse);
            if (arrowButton != null) {
                arrowButton.removeMouseListener(seguirMouse);
            }
            if (editor != null) {
                editor.removeMouseListener(seguirMouse);
            }
            super.uninstallListeners();
        }

        @Override
        protected JButton createArrowButton() {
            if (!flecha) {
                JButton oculto = new JButton();
                oculto.setVisible(false);
                return oculto;
            }
            return new BotonFlecha();
        }

        // La flecha ocupa un cuadrado a la derecha; el resto es para el texto
        @Override
        protected LayoutManager createLayoutManager() {
            return new LayoutManager() {
                @Override
                public void addLayoutComponent(String name, Component comp) { }

                @Override
                public void removeLayoutComponent(Component comp) { }

                @Override
                public Dimension preferredLayoutSize(Container parent) {
                    return parent.getPreferredSize();
                }

                @Override
                public Dimension minimumLayoutSize(Container parent) {
                    return parent.getMinimumSize();
                }

                @Override
                public void layoutContainer(Container parent) {
                    int w = comboBox.getWidth();
                    int h = comboBox.getHeight();
                    int ancho = flecha ? h : 0;
                    if (arrowButton != null) {
                        arrowButton.setBounds(w - ancho, 0, ancho, h);
                    }
                    if (editor != null && comboBox.isEditable()) {
                        editor.setBounds(0, 0, Math.max(0, w - ancho), h);
                    }
                }
            };
        }

        @Override
        protected Rectangle rectangleForCurrentValue() {
            int ancho = comboBox.getWidth() - (flecha ? comboBox.getHeight() : 0);
            return new Rectangle(0, 0, Math.max(0, ancho), comboBox.getHeight());
        }

        @Override
        protected void configureEditor() {
            super.configureEditor();
            if (editor instanceof JTextField) {
                JTextField campo = (JTextField) editor;
                campo.setOpaque(false);
                campo.setBorder(new EmptyBorder(0, 14, 0, 4));
                campo.setForeground(Color.BLACK);
                campo.setCaretColor(Color.BLACK);
            }
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // el fondo gris ya se dibuja en paint()
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            BufferedImage tresPartes = fondoParaTamano(c.getWidth(), c.getHeight());
            g2.drawImage(tresPartes != null ? tresPartes : fondo.obtener(c.getWidth(), c.getHeight()), 0, 0, null);
            if (!comboBox.isEditable()) {
                paintCurrentValue(g2, rectangleForCurrentValue(), false);
            }
            g2.dispose();
        }

        @Override
        protected ComboPopup createPopup() {
            return new PopupEstilizado(comboBox);
        }

        /** Flecha de desplegableArrow1.png / desplegableArrow2.png (mouse encima o lista abierta). */
        private final class BotonFlecha extends JButton {
            private static final long serialVersionUID = 1L;
            private final transient BufferedImage flecha1 = RecursosImagen.cargar("/imagenes/desplegableArrow1.png");
            private final transient RecursosImagen.Escalado flecha1Esc =
                    new RecursosImagen.Escalado("/imagenes/desplegableArrow1.png");
            private final transient RecursosImagen.Escalado flecha2Esc =
                    new RecursosImagen.Escalado("/imagenes/desplegableArrow2.png");

            BotonFlecha() {
                setOpaque(false);
                setBorder(null);
                setContentAreaFilled(false);
                setFocusPainted(false);
                setFocusable(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int ancho = Math.max(8, Math.round(getHeight() * 0.52f));
                int alto = Math.max(4, Math.round(ancho * (float) flecha1.getHeight() / flecha1.getWidth()));
                boolean activa = sobre || comboBox.isPopupVisible();
                g2.drawImage((activa ? flecha2Esc : flecha1Esc).obtener(ancho, alto),
                        (getWidth() - ancho) / 2, (getHeight() - alto) / 2, null);
                g2.dispose();
            }
        }
    }

    // ------------------------------------------------------------------ lista desplegable

    private static final class PopupEstilizado extends BasicComboPopup {
        private static final long serialVersionUID = 1L;

        PopupEstilizado(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        protected JScrollPane createScroller() {
            JScrollPane sp = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sp.setHorizontalScrollBar(null);
            sp.setBorder(null);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            JScrollBar barra = sp.getVerticalScrollBar();
            barra.setUI(new BarraDesplazamientoUI(TRANSPARENTE, PULGAR, false));
            barra.setPreferredSize(new Dimension(16, 0));
            barra.setBorder(new EmptyBorder(6, 0, 6, 8));
            barra.setOpaque(false);
            return sp;
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();
            setOpaque(false);
            setBorder(new EmptyBorder(8, 0, 8, 0));
        }

        @Override
        protected void configureList() {
            super.configureList();
            list.setOpaque(false);
            list.setBackground(TRANSPARENTE);
            list.setSelectionBackground(TRANSPARENTE);
            list.setSelectionForeground(TEXTO);
        }

        // Fondo gris con las esquinas redondeadas, sin borde
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(GRIS);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARCO_LISTA, ARCO_LISTA);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // sin borde
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.clip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARCO_LISTA, ARCO_LISTA));
            super.paintChildren(g2);
            g2.dispose();
        }

        // Si la lista se abre en una ventana propia (no cabe dentro de la ventana principal), se hace
        // transparente para que las esquinas redondeadas no muestren un fondo cuadrado.
        @Override
        public void show(Component invoker, int x, int y) {
            super.show(invoker, x, y);
            Window ventana = SwingUtilities.getWindowAncestor(this);
            if (ventana != null && ventana != SwingUtilities.getWindowAncestor(comboBox)) {
                try {
                    if (ventana instanceof RootPaneContainer) {
                        ((JComponent) ((RootPaneContainer) ventana).getContentPane()).setOpaque(false);
                    }
                    ventana.setBackground(TRANSPARENTE);
                } catch (RuntimeException e) {
                    // sin soporte de transparencia: queda un fondo rectangular, solo estético
                }
            }
        }
    }
}
