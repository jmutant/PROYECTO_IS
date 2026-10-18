package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
//import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import modelo.EstadoUnidad;

// Pantalla Gestion de flotaa
public class FlotaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final double REF_ANCHO = 1021;
    private static final double REF_ALTO = 572;

    // Medidas de la maqueta {x, y, ancho, alto}
    private static final double[] TARJETA = {25, 78, 977, 480};
    private static final double[] CRUZ = {944, 8, 56, 55};
    private static final double[] CAMION = {572, 88, 282, 125};
    private static final double[] BOTON_REGISTRAR = {458, 232, 247, 70};
    private static final double[] BOTON_GUARDAR = {716, 232, 247, 70};
    private static final double ETIQUETA_X = 54;
    private static final double ETIQUETA_ANCHO = 130;
    private static final double CAMPO_X = 190;
    private static final double CAMPO_ANCHO = 248;
    private static final double CAMPO_ALTO = 46;
    private static final double[] FILA_Y = {94, 148, 202, 256};
    private static final String[] TEXTOS_ETIQUETA = {"Placa", "Modelo", "Capacidad", "Estado"};

    private static final double TABLA_X = 36;
    private static final double TABLA_Y = 314;
    private static final double TABLA_ANCHO = 946;
    private static final double TABLA_ALTO = 219;
    private static final double ENCABEZADO_ALTO = 49;
    private static final double SCROLL_X = 46;
    private static final double SCROLL_Y = 366;
    private static final double SCROLL_ANCHO = 930;
    private static final double SCROLL_ALTO = 157;
    private static final double FILA_ALTO = 40;
    private static final double BARRA_ANCHO = 40;
    private static final double BARRA_SEPARACION = 10;
    private static final String[] COLUMNAS = {"Placa", "Modelo", "Capacidad", "Estado"};

    private static final Color AZUL = new Color(0x00, 0x49, 0xAB);
    private static final Color CUERPO_ARRIBA = new Color(216, 212, 211);
    private static final Color CUERPO_ABAJO = new Color(203, 199, 199);
    private static final Color GRIS_FILA = new Color(176, 173, 173);
    private static final Color GRIS_PISTA = new Color(172, 170, 170);

    private final JTextField txtPlaca = new CampoTextoFondo();
    private final JTextField txtModelo = new CampoTextoFondo();
    private final JTextField txtCapacidad = new CampoTextoFondo();
    // Alto del campo igual al de los demás (46/248 del ancho, en la escala 618 del selector)
    private final SelectorOpciones selectorEstado = new SelectorOpciones(
            CAMPO_ALTO / CAMPO_ANCHO * 618, etiquetasEstado());
    private final JLabel[] etiquetas = new JLabel[TEXTOS_ETIQUETA.length];

    private final BotonImagen botonRegistrar =
            new BotonImagen("/imagenes/RegistrarUnidad1.png", "/imagenes/RegistrarUnidad2.png", "Registrar Unidad");
    private final BotonImagen botonGuardar =
            new BotonImagen("/imagenes/GuardarCambios1.png", "/imagenes/GuardarCambios2.png", "Guardar Cambios");
    private final BotonImagen botonCerrar =
            new BotonImagen("/imagenes/cross1.png", "/imagenes/cross2.png", "Cerrar");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JScrollPane scroll = new JScrollPane(tabla,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private final transient RecursosImagen.Escalado fondo =
            new RecursosImagen.Escalado("/imagenes/GestionDeFlotaPantalla.png");
    private final transient RecursosImagen.Escalado camion = new RecursosImagen.Escalado("/imagenes/UnidadTrans.png");
    private final transient Tarjeta tarjeta = new Tarjeta("/imagenes/BlanckWindow.png");

    private double escala = 1;
    private int desfaseX = 0;
    private int desfaseY = 0;

    public FlotaPanel() {
        super(null);
        setOpaque(true);
        setPreferredSize(new Dimension((int) REF_ANCHO, (int) REF_ALTO));

        for (int i = 0; i < etiquetas.length; i++) {
            etiquetas[i] = new JLabel(TEXTOS_ETIQUETA[i], SwingConstants.LEFT);
            etiquetas[i].setForeground(Color.BLACK);
            add(etiquetas[i]);
        }
        add(txtPlaca);
        add(txtModelo);
        add(txtCapacidad);
        add(selectorEstado);
        add(botonRegistrar);
        add(botonGuardar);
        add(botonCerrar);
        add(scroll);
        selectorEstado.setFuenteRef(40);
        // La lista de estados se dibuja por encima de la tabla cuando se despliega
        setComponentZOrder(selectorEstado, 0);

        configurarTabla();
        aplicarFiltroPlaca();
        configurarSeleccionTabla();
    }

    private static String[] etiquetasEstado() {
        return Arrays.stream(EstadoUnidad.values()).map(EstadoUnidad::getEtiqueta).toArray(String[]::new);
    }

    // ------------------------------------------------------------------ acceso desde la ventana

    public JTable getTabla() { return tabla; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public String getPlaca() { return txtPlaca.getText().trim(); }
    public String getModelo() { return txtModelo.getText().trim(); }
    public String getCapacidadTexto() { return txtCapacidad.getText().trim(); }

    public EstadoUnidad getEstado() {
        return EstadoUnidad.values()[selectorEstado.getIndiceSeleccionado()];
    }

    public void setAccionRegistrar(Runnable accion) { botonRegistrar.addActionListener(e -> accion.run()); }
    public void setAccionGuardar(Runnable accion) { botonGuardar.addActionListener(e -> accion.run()); }
    public void setAccionCerrar(Runnable accion) { botonCerrar.addActionListener(e -> accion.run()); }

    public void limpiarFormulario() {
        txtPlaca.setText("");
        txtModelo.setText("");
        txtCapacidad.setText("");
        selectorEstado.reiniciar();
        txtPlaca.setEditable(true);
        tabla.clearSelection();
    }

    // ------------------------------------------------------------------ configuración

    private void configurarTabla() {
        tabla.setTableHeader(null);
        tabla.setOpaque(false);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(false);
        tabla.setRowSelectionAllowed(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultRenderer(Object.class, new RendererFila());
        tabla.setDefaultRenderer(Integer.class, new RendererFila());

        scroll.setOpaque(false);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        JScrollBar barra = scroll.getVerticalScrollBar();
        barra.setUI(new BarraUI());
        barra.setOpaque(false);
        barra.setUnitIncrement(20);
    }

    private void aplicarFiltroPlaca() {
        ((AbstractDocument) txtPlaca.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text != null && text.matches("[a-zA-Z0-9]+")) {
                    super.insertString(fb, offset, text.toUpperCase(), attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("[a-zA-Z0-9]+")) {
                    super.replace(fb, offset, length, text.toUpperCase(), attrs);
                } else if (text != null && text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    // Al hacer clic en una fila se cargan sus datos en el formulario para editarlos
    private void configurarSeleccionTabla() {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtPlaca.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtModelo.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtCapacidad.setText(modeloTabla.getValueAt(fila, 2).toString());
                    Object estado = modeloTabla.getValueAt(fila, 3);
                    if (estado instanceof EstadoUnidad) {
                        selectorEstado.setIndiceSeleccionado(((EstadoUnidad) estado).ordinal());
                    }
                    txtPlaca.setEditable(true);
                }
            }
        });
    }

    // ------------------------------------------------------------------ layout y dibujo

    private int px(double valor) {
        return Math.max(1, (int) Math.round(valor * escala));
    }

    private Rectangle rect(double x, double y, double ancho, double alto) {
        return new Rectangle(desfaseX + (int) Math.round(x * escala),
                desfaseY + (int) Math.round(y * escala), px(ancho), px(alto));
    }

    private Rectangle rect(double[] r) {
        return rect(r[0], r[1], r[2], r[3]);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        escala = Math.min(w / REF_ANCHO, h / REF_ALTO);
        desfaseX = (int) Math.round((w - REF_ANCHO * escala) / 2);
        desfaseY = (int) Math.round((h - REF_ALTO * escala) / 2);

        Font fuenteEtiqueta = new Font("Arial", Font.PLAIN, px(23));
        Font fuenteCampo = new Font("Arial", Font.PLAIN, px(20));
        EmptyBorder margenTexto = new EmptyBorder(0, px(18), 0, px(18));

        JTextField[] campos = {txtPlaca, txtModelo, txtCapacidad};
        for (int i = 0; i < etiquetas.length; i++) {
            etiquetas[i].setFont(fuenteEtiqueta);
            etiquetas[i].setBounds(rect(ETIQUETA_X, FILA_Y[i], ETIQUETA_ANCHO, CAMPO_ALTO));
            Rectangle r = rect(CAMPO_X, FILA_Y[i], CAMPO_ANCHO, CAMPO_ALTO);
            if (i < campos.length) {
                campos[i].setFont(fuenteCampo);
                campos[i].setBorder(margenTexto);
                campos[i].setBounds(r);
            } else {
                r.height = selectorEstado.isAbierto()
                        ? selectorEstado.getAlturaAbierta(r.width)
                        : selectorEstado.getAlturaCerrada(r.width);
                selectorEstado.setBounds(r);
            }
        }

        botonRegistrar.setBounds(rect(BOTON_REGISTRAR));
        botonGuardar.setBounds(rect(BOTON_GUARDAR));
        botonCerrar.setBounds(rect(CRUZ));

        tabla.setRowHeight(px(FILA_ALTO));
        tabla.setFont(new Font("Arial", Font.BOLD, px(16)));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(px(BARRA_ANCHO + BARRA_SEPARACION), 0));
        scroll.getVerticalScrollBar().setBorder(new EmptyBorder(0, px(BARRA_SEPARACION), 0, 0));
        scroll.setBounds(rect(SCROLL_X, SCROLL_Y, SCROLL_ANCHO, SCROLL_ALTO));
        scroll.doLayout();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);

        Rectangle t = rect(TARJETA);
        tarjeta.dibujar(g2, t.x, t.y, t.width, t.height, px(50));

        Rectangle c = rect(CAMION);
        g2.drawImage(camion.obtener(c.width, c.height), c.x, c.y, null);

        dibujarTabla(g2);
        g2.dispose();
    }

    // Fondo gris de la tabla (con borde blanco) y barra azul de encabezado con sus títulos
    private void dibujarTabla(Graphics2D g2) {
        Rectangle cuerpo = rect(TABLA_X, TABLA_Y + ENCABEZADO_ALTO / 2, TABLA_ANCHO, TABLA_ALTO - ENCABEZADO_ALTO / 2);
        int arco = px(46);
        int borde = px(4);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(cuerpo.x, cuerpo.y, cuerpo.width, cuerpo.height, arco, arco);
        g2.setPaint(new GradientPaint(0, cuerpo.y, CUERPO_ARRIBA, 0, cuerpo.y + cuerpo.height, CUERPO_ABAJO));
        g2.fillRoundRect(cuerpo.x + borde, cuerpo.y + borde, cuerpo.width - 2 * borde, cuerpo.height - 2 * borde,
                arco - borde, arco - borde);

        Rectangle enc = rect(TABLA_X, TABLA_Y, TABLA_ANCHO, ENCABEZADO_ALTO);
        g2.setColor(AZUL);
        g2.fillRoundRect(enc.x, enc.y, enc.width, enc.height, enc.height, enc.height);

        g2.setFont(new Font("Arial", Font.BOLD, px(31)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        double anchoColumna = (SCROLL_ANCHO - BARRA_ANCHO - BARRA_SEPARACION) / COLUMNAS.length;
        int baseY = enc.y + (enc.height - fm.getHeight()) / 2 + fm.getAscent();
        for (int i = 0; i < COLUMNAS.length; i++) {
            int centro = desfaseX + (int) Math.round((SCROLL_X + anchoColumna * (i + 0.5)) * escala);
            g2.drawString(COLUMNAS[i], centro - fm.stringWidth(COLUMNAS[i]) / 2, baseY);
        }
    }

    // ------------------------------------------------------------------ componentes propios

    /** Campo de texto con el fondo gris redondeado de appButton.png. */
    private static final class CampoTextoFondo extends JTextField {
        private static final long serialVersionUID = 1L;
        private final transient RecursosImagen.Escalado fondoCampo =
                new RecursosImagen.Escalado("/imagenes/appButton.png");

        CampoTextoFondo() {
            setOpaque(false);
            setForeground(Color.BLACK);
            setCaretColor(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawImage(fondoCampo.obtener(getWidth(), getHeight()), 0, 0, null);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /** Fila de la tabla dibujada como una "píldora" gris (azul cuando está seleccionada). */
    private static final class RendererFila extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private boolean seleccionada;
        private int columna;
        private int ultimaColumna;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            seleccionada = isSelected;
            columna = column;
            ultimaColumna = table.getColumnCount() - 1;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(table.getFont());
            setForeground(isSelected ? Color.WHITE : Color.BLACK);
            setOpaque(false);
            setBorder(null);
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
            g2.setColor(seleccionada ? AZUL : GRIS_FILA);
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

    /** Barra de desplazamiento: pista gris y control azul redondeados, sin flechas. */
    private static final class BarraUI extends BasicScrollBarUI {
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
            int ancho = Math.max(10, scrollbar.getWidth() - scrollbar.getInsets().left);
            return new Dimension(ancho, ancho);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle pista = trackRect;
            g2.setColor(GRIS_PISTA);
            g2.fillRoundRect(pista.x, pista.y, pista.width, pista.height, pista.width, pista.width);
            Rectangle pulgar = thumbRect;
            g2.setColor(AZUL);
            if (pulgar.isEmpty() || pulgar.height >= pista.height) {
                // Nada que desplazar: se muestra el control corto en la parte superior, como en la maqueta
                int alto = Math.min(pista.height, Math.round(pista.width * 1.3f));
                g2.fillRoundRect(pista.x, pista.y, pista.width, alto, pista.width, pista.width);
            } else {
                g2.fillRoundRect(pulgar.x, pulgar.y, pulgar.width, pulgar.height, pulgar.width, pulgar.width);
            }
            g2.dispose();
        }
    }

    /** Dibuja BlanckWindow.png estirada a cualquier tamaño sin deformar las esquinas (9 partes). */
    private static final class Tarjeta {
        private static final int CORTE = 130; // píxeles de cada esquina en la imagen original
        private final BufferedImage origen;
        private BufferedImage[] partes;
        private int cacheW;
        private int cacheH;
        private int cacheEsquina;

        Tarjeta(String ruta) {
            this.origen = RecursosImagen.cargar(ruta);
        }

        void dibujar(Graphics2D g2, int x, int y, int w, int h, int esquina) {
            int d = Math.max(1, Math.min(esquina, Math.min(w, h) / 2));
            if (partes == null || cacheW != w || cacheH != h || cacheEsquina != d) {
                construir(w, h, d);
            }
            int[] dx = {0, d, w - d, w};
            int[] dy = {0, d, h - d, h};
            for (int fila = 0; fila < 3; fila++) {
                for (int col = 0; col < 3; col++) {
                    g2.drawImage(partes[fila * 3 + col], x + dx[col], y + dy[fila], null);
                }
            }
        }

        private void construir(int w, int h, int d) {
            int sw = origen.getWidth();
            int sh = origen.getHeight();
            int[] xs = {0, CORTE, sw - CORTE, sw};
            int[] ys = {0, CORTE, sh - CORTE, sh};
            int[] dx = {0, d, w - d, w};
            int[] dy = {0, d, h - d, h};
            partes = new BufferedImage[9];
            for (int fila = 0; fila < 3; fila++) {
                for (int col = 0; col < 3; col++) {
                    BufferedImage trozo = origen.getSubimage(xs[col], ys[fila],
                            xs[col + 1] - xs[col], ys[fila + 1] - ys[fila]);
                    partes[fila * 3 + col] = RecursosImagen.escalar(trozo,
                            Math.max(1, dx[col + 1] - dx[col]), Math.max(1, dy[fila + 1] - dy[fila]));
                }
            }
            cacheW = w;
            cacheH = h;
            cacheEsquina = d;
        }
    }
}
