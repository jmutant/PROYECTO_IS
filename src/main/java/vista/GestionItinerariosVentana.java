package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

public class GestionItinerariosVentana extends JFrame implements GestionItinerariosVista {

    private static final long serialVersionUID = 1L;    
    // Botones de navegación y acciones
    private final JButton botonRegresar = new JButton("Regresar");
    private final JButton botonGuardar = new JButton("Guardar Itinerario");
    private final JButton botonLimpiar = new JButton("Limpiar");
    private final JButton botonBuscarPlaca = new JButton("Buscar Placa");
    private final JButton botonBuscarConductor = new JButton("Buscar Conductor");

    // Campos del formulario
    private final JTextField campoOrigen = new JTextField();
    private final JTextField campoDestino = new JTextField();
    private final JTextField campoHorario = new JTextField();
    private final JComboBox<String> comboTipoRuta = new JComboBox<>(new String[]{"Directa", "Urbana", "Campus Interno"});
    
    // Campos de búsqueda
    private final JTextField campoPlacaBusqueda = new JTextField();
    private final JTextField campoConductorBusqueda = new JTextField();

    public GestionItinerariosVentana() {
        super("Campus Express - Gestionar Itinerarios");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Datos del Itinerario", TitledBorder.LEFT, TitledBorder.TOP));

        panelFormulario.add(new JLabel(" Origen:"));
        panelFormulario.add(campoOrigen);

        panelFormulario.add(new JLabel(" Destino:"));
        panelFormulario.add(campoDestino);

        panelFormulario.add(new JLabel(" Horario:"));
        panelFormulario.add(campoHorario);

        panelFormulario.add(new JLabel(" Tipo de Ruta:"));
        panelFormulario.add(comboTipoRuta);

        JPanel panelBusqueda = new JPanel(new GridLayout(2, 3, 8, 8));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asignación de Recursos", TitledBorder.LEFT, TitledBorder.TOP));

        panelBusqueda.add(new JLabel(" Buscar Vehículo (Placa):"));
        panelBusqueda.add(campoPlacaBusqueda);
        panelBusqueda.add(botonBuscarPlaca);

        panelBusqueda.add(new JLabel(" Buscar Conductor (Nombre):"));
        panelBusqueda.add(campoConductorBusqueda);
        panelBusqueda.add(botonBuscarConductor);

        // Contenedor Central para Formulario + Búsqueda
        JPanel panelContenedorCentral = new JPanel(new BorderLayout(10, 10));
        panelContenedorCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelContenedorCentral.add(panelFormulario, BorderLayout.NORTH);
        panelContenedorCentral.add(panelBusqueda, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        // Estilo básico al botón principal
        botonGuardar.setBackground(new Color(40, 167, 69));
        botonGuardar.setForeground(Color.WHITE);

        panelBotones.add(botonRegresar);
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonGuardar);

        // Agregar todo al marco principal
        setContentPane(new JPanel(new BorderLayout()));
        getContentPane().add(panelContenedorCentral, BorderLayout.CENTER);
        getContentPane().add(panelBotones, BorderLayout.SOUTH);
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        botonRegresar.addActionListener(e -> accion.run());
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