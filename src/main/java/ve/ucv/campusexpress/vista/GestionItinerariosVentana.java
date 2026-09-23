package ve.ucv.campusexpress.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.WindowConstants;
import ve.ucv.campusexpress.modelo.Conductor;
import ve.ucv.campusexpress.modelo.Itinerario;
import ve.ucv.campusexpress.modelo.TipoRuta;
import ve.ucv.campusexpress.modelo.Unidad;

/** Vista Swing de HU-003. */
public class GestionItinerariosVentana extends JFrame implements GestionItinerariosVista {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final JTextField campoOrigen = new JTextField();
    private final JTextField campoDestino = new JTextField();
    private final JComboBox<OpcionDia> comboDia = new JComboBox<>(new OpcionDia[]{
            new OpcionDia(DayOfWeek.MONDAY, "Lunes"),
            new OpcionDia(DayOfWeek.TUESDAY, "Martes"),
            new OpcionDia(DayOfWeek.WEDNESDAY, "Miércoles"),
            new OpcionDia(DayOfWeek.THURSDAY, "Jueves"),
            new OpcionDia(DayOfWeek.FRIDAY, "Viernes"),
            new OpcionDia(DayOfWeek.SATURDAY, "Sábado"),
            new OpcionDia(DayOfWeek.SUNDAY, "Domingo")});
    private final JTextField campoHora = new JTextField();
    private final JComboBox<TipoRuta> comboTipo = new JComboBox<>(TipoRuta.values());
    private final JComboBox<Unidad> comboUnidad = new JComboBox<>();
    private final JComboBox<Conductor> comboConductor = new JComboBox<>();
    private final JButton botonRegistrar = new JButton("Registrar ruta");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Día", "Hora", "Origen", "Destino", "Tipo", "Unidad", "Conductor"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private Runnable accionRegistrar = () -> { };

    public GestionItinerariosVentana() {
        super("Campus Express - Gestionar Itinerarios");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel formulario = new JPanel(new GridLayout(7, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del itinerario"));
        formulario.add(new JLabel("Origen:"));
        formulario.add(campoOrigen);
        formulario.add(new JLabel("Destino:"));
        formulario.add(campoDestino);
        formulario.add(new JLabel("Día de la semana:"));
        formulario.add(comboDia);
        formulario.add(new JLabel("Hora de salida (HH:mm):"));
        formulario.add(campoHora);
        formulario.add(new JLabel("Tipo de ruta:"));
        formulario.add(comboTipo);
        formulario.add(new JLabel("Unidad:"));
        formulario.add(comboUnidad);
        formulario.add(new JLabel("Conductor:"));
        formulario.add(comboConductor);

        botonRegistrar.addActionListener(e -> accionRegistrar.run());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(botonRegistrar);

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
    public String getOrigen() { return campoOrigen.getText(); }

    @Override
    public String getDestino() { return campoDestino.getText(); }

    @Override
    public DayOfWeek getDiaSemana() { return ((OpcionDia) comboDia.getSelectedItem()).dia; }

    @Override
    public LocalTime getHoraSalida() {
        try {
            return LocalTime.parse(campoHora.getText().trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La hora debe tener el formato HH:mm");
        }
    }

    @Override
    public TipoRuta getTipoRuta() { return (TipoRuta) comboTipo.getSelectedItem(); }

    @Override
    public Unidad getUnidad() { return (Unidad) comboUnidad.getSelectedItem(); }

    @Override
    public Conductor getConductor() { return (Conductor) comboConductor.getSelectedItem(); }

    @Override
    public void setAccionRegistrar(Runnable accion) { accionRegistrar = accion; }

    @Override
    public void mostrarItinerarios(List<Itinerario> itinerarios) {
        modeloTabla.setRowCount(0);
        for (Itinerario it : itinerarios) {
            modeloTabla.addRow(new Object[]{
                    etiquetaDia(it.getDiaSemana()), it.getHoraSalida().format(FORMATO_HORA),
                    it.getOrigen(), it.getDestino(), it.getTipoRuta().getEtiqueta(),
                    it.getPlacaUnidad(), it.getLicenciaConductor()});
        }
    }

    @Override
    public void cargarUnidades(List<Unidad> unidades) {
        comboUnidad.removeAllItems();
        for (Unidad unidad : unidades) {
            comboUnidad.addItem(unidad);
        }
    }

    @Override
    public void cargarConductores(List<Conductor> conductores) {
        comboConductor.removeAllItems();
        for (Conductor conductor : conductores) {
            comboConductor.addItem(conductor);
        }
    }

    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Itinerarios", JOptionPane.INFORMATION_MESSAGE);
        campoOrigen.setText("");
        campoDestino.setText("");
        campoHora.setText("");
        campoOrigen.requestFocusInWindow();
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Itinerarios", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrar() { setVisible(true); }

    private static String etiquetaDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    private static final class OpcionDia {
        private final DayOfWeek dia;
        private final String etiqueta;

        private OpcionDia(DayOfWeek dia, String etiqueta) {
            this.dia = dia;
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }
}
