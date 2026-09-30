package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import modelo.Conductor;
import modelo.Itinerario;
import modelo.TipoRuta;
import modelo.Unidad;

public class GestionItinerariosVentana extends JFrame implements GestionItinerariosVista {

    private static final long serialVersionUID = 1L;

    // Componentes visuales
    private final JButton botonRegresar = new JButton("Regresar");
    private final JButton botonRegistrar = new JButton("Registrar Itinerario");
    
    private final JTextField campoOrigen = new JTextField();
    private final JTextField campoDestino = new JTextField();
    private final JTextField campoHoraSalida = new JTextField("08:00"); // Formato HH:mm
    
    private final JComboBox<DayOfWeek> comboDiaSemana = new JComboBox<>(DayOfWeek.values());
    private final JComboBox<TipoRuta> comboTipoRuta = new JComboBox<>(TipoRuta.values());
    private final JComboBox<Unidad> comboUnidades = new JComboBox<>();
    private final JComboBox<Conductor> comboConductores = new JComboBox<>();

    public GestionItinerariosVentana() {
        super("Campus Express - Gestionar Itinerarios");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        comboUnidades.setEditable(true);
        comboConductores.setEditable(true);

        // 1. PANEL FORMULARIO DE ITINERARIOS
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 3, 3));
        panelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Datos del Itinerario (HU-003)", TitledBorder.LEFT, TitledBorder.TOP));
        
        panelFormulario.add(new JLabel(" Origen:"));
        panelFormulario.add(campoOrigen);

        panelFormulario.add(new JLabel(" Destino:"));
        panelFormulario.add(campoDestino);

        panelFormulario.add(new JLabel(" Día de la Semana:"));
        panelFormulario.add(comboDiaSemana);

        panelFormulario.add(new JLabel(" Hora Salida (HH:mm):"));
        panelFormulario.add(campoHoraSalida);

        panelFormulario.add(new JLabel(" Tipo de Ruta:"));
        panelFormulario.add(comboTipoRuta);

        panelFormulario.add(new JLabel(" Unidad / Vehículo:"));
        panelFormulario.add(comboUnidades);

        panelFormulario.add(new JLabel(" Conductor:"));
        panelFormulario.add(comboConductores);

        // Contenedor Central
        JPanel panelContenedorCentral = new JPanel(new BorderLayout(5, 5));
        panelContenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelContenedorCentral.add(panelFormulario, BorderLayout.CENTER);

        // 2. PANEL DE BOTONES
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        botonRegistrar.setBackground(new Color(69, 167, 69));
        botonRegistrar.setForeground(Color.WHITE);

        panelBotones.add(botonRegresar);
        panelBotones.add(botonRegistrar);

        setContentPane(new JPanel(new BorderLayout()));
        getContentPane().add(panelContenedorCentral, BorderLayout.CENTER);
        getContentPane().add(panelBotones, BorderLayout.SOUTH);
    }

    // --- IMPLEMENTACIÓN DE MÉTODOS DE LA INTERFAZ ---
    @Override
    public String getOrigen() {
        return campoOrigen.getText().trim();
    }

    @Override
    public String getDestino() {
        return campoDestino.getText().trim();
    }

    @Override
    public DayOfWeek getDiaSemana() {
        return (DayOfWeek) comboDiaSemana.getSelectedItem();
    }

    @Override
    public LocalTime getHoraSalida() {
        try {
            return LocalTime.parse(campoHoraSalida.getText().trim());
        } catch (DateTimeParseException e) {
            return null; // El controlador validará si es nulo
        }
    }

    @Override
    public TipoRuta getTipoRuta() {
        return (TipoRuta) comboTipoRuta.getSelectedItem();
    }

    @Override
    public Unidad getUnidad() {
        return (Unidad) comboUnidades.getSelectedItem();
    }

    @Override
    public Conductor getConductor() {
        return (Conductor) comboConductores.getSelectedItem();
    }

    @Override
    public void setAccionRegistrar(Runnable accion) {
        botonRegistrar.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        botonRegresar.addActionListener(e -> accion.run());
    }

    @Override
    public void mostrarItinerarios(List<Itinerario> itinerarios) {
        // Reservado para actualizar tabla o lista si la agregan
    }

    @Override
    public void cargarUnidades(List<Unidad> unidades) {
        comboUnidades.removeAllItems();
        if (unidades != null) {
            for (Unidad u : unidades) {
                comboUnidades.addItem(u);
            }
        }
    }

    @Override
    public void cargarConductores(List<Conductor> conductores) {
        comboConductores.removeAllItems();
        if (conductores != null) {
            for (Conductor c : conductores) {
                comboConductores.addItem(c);
            }
        }
    }

    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrar() {
        setVisible(true);
    }

    public void setAccionBuscarUnidad(java.util.function.Consumer<String> buscador) {
        javax.swing.JTextField editor = (javax.swing.JTextField) comboUnidades.getEditor().getEditorComponent();
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                buscador.accept(editor.getText());
            }
        });
    }

    public void setAccionBuscarConductor(java.util.function.Consumer<String> buscador) {
        javax.swing.JTextField editor = (javax.swing.JTextField) comboConductores.getEditor().getEditorComponent();
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                buscador.accept(editor.getText());
            }
        });
    }

    @Override
    public void cerrar() {
        dispose();
    }
}