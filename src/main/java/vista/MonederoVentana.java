package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//import modelo.MonederoServicio;   pude que si se use despues pero por ahora no hace falta que llame a la clase MonederoServicio
import modelo.Usuario;

//Pantalla del monedero virtual, basada en los recursos gráficos entregados
public class MonederoVentana extends JFrame implements MonederoVista {

    private static final long serialVersionUID = 1L;

    private static final int ANCHO = 1000;
    private static final int ALTO = 650;
    private static final Color AZUL = new Color(7, 83, 177);
    private static final Color GRIS_PANEL = new Color(235, 234, 234);
    private static final Color VERDE = new Color(114, 208, 141);

    private final RecursosImagen.Escalado fondo =
            new RecursosImagen.Escalado("/imagenes/MonederoPantalla.png");

    private final BotonImagen botonRecargar = new BotonImagen(
            "/imagenes/RecargaSaldo1.png",
            "/imagenes/RecargaSaldo2.png",
            "Recargar saldo");

    private final BotonImagen botonCerrar = new BotonImagen(
            "/imagenes/cross1.png",
            "/imagenes/cross2.png",
            "Cerrar monedero");

    private final JLabel labelSaldo = new JLabel("0.00", SwingConstants.RIGHT);

    public MonederoVentana(Usuario usuario) {
        super("Campus Express - Monedero Virtual");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setUndecorated(true);
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
        fondoPanel.setBorder(new EmptyBorder(20, 55, 55, 55));
        fondoPanel.setOpaque(false);

        // La X va ENCIMA de la tarjeta blanca (sobre el fondo), alineada a la derecha
        JPanel filaCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaCerrar.setOpaque(false);
        filaCerrar.setBorder(new EmptyBorder(0, 0, 14, 0));
        botonCerrar.setPreferredSize(new Dimension(58, 56));
        filaCerrar.add(botonCerrar);
        fondoPanel.add(filaCerrar, BorderLayout.NORTH);

        JPanel tarjeta = new JPanel(new BorderLayout(0, 22)) {
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
        tarjeta.setBorder(new EmptyBorder(28, 24, 25, 24));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JPanel saldoPanel = new JPanel(new BorderLayout());
        saldoPanel.setOpaque(false);
        saldoPanel.setBorder(new EmptyBorder(12, 10, 0, 10));

        JLabel saldoDisponible = new EtiquetaRedondeada("Saldo Disponible", AZUL, 30);
        saldoDisponible.setFont(new Font("Arial", Font.BOLD, 25));
        saldoDisponible.setForeground(Color.WHITE);
        saldoDisponible.setBorder(BorderFactory.createEmptyBorder(16, 30, 16, 30));

        JPanel etiquetaWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        etiquetaWrap.setOpaque(false);
        etiquetaWrap.add(saldoDisponible);
        saldoPanel.add(etiquetaWrap, BorderLayout.NORTH);

        JPanel cajaSaldo = new PanelRedondeado(new BorderLayout(), GRIS_PANEL, 42);
        cajaSaldo.setBorder(new EmptyBorder(30, 25, 30, 25));

        labelSaldo.setFont(new Font("Arial", Font.PLAIN, 64));
        labelSaldo.setForeground(VERDE);

        JLabel bs = new JLabel("Bs");
        bs.setFont(new Font("Arial", Font.PLAIN, 52));
        bs.setForeground(Color.BLACK);
        bs.setBorder(new EmptyBorder(8, 6, 0, 0));

        JPanel lectura = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        lectura.setOpaque(false);
        lectura.add(labelSaldo);
        lectura.add(bs);

        cajaSaldo.add(lectura, BorderLayout.CENTER);
        saldoPanel.add(cajaSaldo, BorderLayout.CENTER);

        cabecera.add(saldoPanel, BorderLayout.CENTER);
        tarjeta.add(cabecera, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        acciones.setOpaque(false);
        botonRecargar.setPreferredSize(new Dimension(390, 110));
        acciones.add(botonRecargar);
        tarjeta.add(acciones, BorderLayout.SOUTH);

        fondoPanel.add(tarjeta, BorderLayout.CENTER);
        setContentPane(fondoPanel);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
    }

    @Override
    public void setAccionRecargarSaldo(Runnable accion) {
        botonRecargar.addActionListener(evento -> accion.run());
    }

    @Override
    public void setAccionCerrar(Runnable accion) {
        botonCerrar.addActionListener(evento -> accion.run());
    }

    @Override
    public void mostrarSaldo(String saldo) {
        labelSaldo.setText(saldo);
    }

    public void mostrarSaldo(double saldo) {
        labelSaldo.setText(String.valueOf(saldo));
    }

    @Override
    public MonederoRecargaDatos solicitarDatosRecarga(Usuario usuario) {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(new EmptyBorder(5, 5, 5, 5));

        JTextField telefono = new JTextField(14);
        JTextField cedula = new JTextField(12);
        JTextField banco = new JTextField(18);
        JTextField referencia = new JTextField(15);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        agregarCampo(formulario, gbc, 0, "Teléfono (11 dígitos):", telefono);
        agregarCampo(formulario, gbc, 1, "Cédula (8 dígitos):", cedula);
        agregarCampo(formulario, gbc, 2, "Banco:", banco);
        agregarCampo(formulario, gbc, 3, "Referencia (13 dígitos):", referencia);

        JLabel monto = new JLabel("Monto fijo de recarga: Bs 1.200,00");
        monto.setFont(monto.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formulario.add(monto, gbc);

        JOptionPane optionPane = new JOptionPane(
                formulario,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);

        JDialog dialogo = optionPane.createDialog(this, "Recargar saldo");
        dialogo.setModal(true);
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        Object seleccion = optionPane.getValue();
        if (!(seleccion instanceof Integer) || ((Integer) seleccion) != JOptionPane.OK_OPTION) {
            return null;
        }

        return new MonederoRecargaDatos(
                telefono.getText(),
                cedula.getText(),
                banco.getText(),
                referencia.getText());
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
                            String etiqueta, JTextField campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    @Override
    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos de recarga inválidos",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void cerrar() {
        dispose();
    }

    //Panel con fondo de color y esquinas redondeadas
    private static class PanelRedondeado extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color color;
        private final int arco;

        PanelRedondeado(java.awt.LayoutManager layout, Color color, int arco) {
            super(layout);
            this.color = color;
            this.arco = arco;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    //Etiqueta con fondo de color y esquinas redondeadas
    private static class EtiquetaRedondeada extends JLabel {
        private static final long serialVersionUID = 1L;
        private final Color colorFondo;
        private final int arco;

        EtiquetaRedondeada(String texto, Color colorFondo, int arco) {
            super(texto, SwingConstants.CENTER);
            this.colorFondo = colorFondo;
            this.arco = arco;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}