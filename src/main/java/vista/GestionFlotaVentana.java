package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.WindowConstants;
import modelo.EstadoUnidad;
import modelo.Unidad;

/** Vista Swing de HU-002. Los datos que muestra provienen del repositorio persistente del modelo. */
public class GestionFlotaVentana extends JFrame implements GestionFlotaVista {

    private static final long serialVersionUID = 1L;

    private final JTextField campoPlaca = new JTextField();
    private final JTextField campoModelo = new JTextField();
    private final JSpinner campoCapacidad = new JSpinner(new SpinnerNumberModel(20, 1, 500, 1));
    private final JComboBox<EstadoUnidad> comboEstado = new JComboBox<>(EstadoUnidad.values());
    private final JButton botonRegistrar = new JButton("Registrar unidad");
    private final JButton botonCerrar = new JButton("Cerrar");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Placa", "Modelo", "Capacidad", "Estado"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private Runnable accionRegistrar = () -> { };

    public GestionFlotaVentana() {
        super("Campus Express - Gestión de Flota");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(720, 470);
        setLocationRelativeTo(null);

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos de la unidad"));
        formulario.add(new JLabel("Placa:"));
        formulario.add(campoPlaca);
        formulario.add(new JLabel("Modelo:"));
        formulario.add(campoModelo);
        formulario.add(new JLabel("Capacidad:"));
        formulario.add(campoCapacidad);
        formulario.add(new JLabel("Estado operativo:"));
        formulario.add(comboEstado);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(botonCerrar);
        acciones.add(botonRegistrar);
        botonRegistrar.addActionListener(e -> accionRegistrar.run());
        botonCerrar.addActionListener(e -> cerrar());

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(acciones, BorderLayout.SOUTH);

        tabla.setFillsViewportHeight(true);

        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(superior, BorderLayout.NORTH);
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);
        setContentPane(contenido);
    }

    @Override
    public String getPlaca() {
        return campoPlaca.getText();
    }

    @Override
    public String getModelo() {
        return campoModelo.getText();
    }

    @Override
    public int getCapacidad() {
        return ((Number) campoCapacidad.getValue()).intValue();
    }

    @Override
    public EstadoUnidad getEstado() {
        return (EstadoUnidad) comboEstado.getSelectedItem();
    }

    @Override
    public void setAccionRegistrar(Runnable accion) {
        accionRegistrar = accion;
    }

    @Override
    public void mostrarUnidades(List<Unidad> unidades) {
        modeloTabla.setRowCount(0);
        for (Unidad unidad : unidades) {
            modeloTabla.addRow(new Object[]{unidad.getPlaca(), unidad.getModelo(), unidad.getCapacidad(),
                    unidad.getEstado().getEtiqueta()});
        }
    }

    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Gestión de Flota", JOptionPane.INFORMATION_MESSAGE);
        campoPlaca.setText("");
        campoModelo.setText("");
        campoCapacidad.setValue(20);
        comboEstado.setSelectedItem(EstadoUnidad.ACTIVO);
        campoPlaca.requestFocusInWindow();
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Gestión de Flota", JOptionPane.ERROR_MESSAGE);
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
