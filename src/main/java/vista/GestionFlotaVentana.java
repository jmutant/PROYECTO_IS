package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;


import modelo.EstadoUnidad;
import modelo.Unidad;


 //Ventana Swing para la gestión del inventario de flota vehicular.

public class GestionFlotaVentana extends JFrame implements GestionFlotaVista {

    private static final long serialVersionUID = 1L;

    // Campos de formulario
    private final JTextField txtPlaca = new JTextField(10);
    private final JTextField txtModelo = new JTextField(10);
    private final JTextField txtCapacidad = new JTextField(10);
    private final JComboBox<EstadoUnidad> comboEstado = new JComboBox<>(EstadoUnidad.values());

    // Botones de acción
    private final JButton botonRegistrar = new JButton("Registrar Unidad");
    private final JButton botonModificar = new JButton("Modificar Estado");
    private final JButton botonRegresar = new JButton("Regresar");

    // Configuración de tabla (celdas no editables directamente)
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"Placa", "Modelo", "Capacidad", "Estado"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaUnidades = new JTable(modeloTabla);


     // Constructor principal: Inicializa el título y la estructura visual de la ventana.

    public GestionFlotaVentana() {
        super("Campus Express - Gestión de Flota");
        construirInterfaz();
        configurarSeleccionTabla();
        aplicarFiltroPlaca();
    }


     // Ensambla los componentes gráficos dentro del marco de la ventana Swing.

    private void construirInterfaz() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setSize(620, 500);
    setLocationRelativeTo(null);

    // Panel principal
    JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
    panelPrincipal.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

    //  Formulario de entrada
    JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 8, 8));
    panelFormulario.add(new JLabel("Placa:"));
    panelFormulario.add(txtPlaca);
    panelFormulario.add(new JLabel("Modelo:"));
    panelFormulario.add(txtModelo);
    panelFormulario.add(new JLabel("Capacidad:"));
    panelFormulario.add(txtCapacidad);
    panelFormulario.add(new JLabel("Estado:"));
    panelFormulario.add(comboEstado);

    //  Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    panelBotones.add(botonRegistrar);
    panelBotones.add(botonModificar);
    panelBotones.add(botonRegresar);

    //  Agrupar Formulario + Botones en la sección superior
    JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
    panelSuperior.add(panelFormulario, BorderLayout.CENTER);
    panelSuperior.add(panelBotones, BorderLayout.SOUTH);

    //  Agregar todo al panel principal
    panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
    panelPrincipal.add(new JScrollPane(tablaUnidades), BorderLayout.CENTER);

    // Asignar el panel principal como el contenido de la ventana
    this.setContentPane(panelPrincipal);
}


     // Listener para capturar el clic sobre la tabla y cargar sus datos en los inputs del formulario.

    private void configurarSeleccionTabla() {
        tablaUnidades.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaUnidades.getSelectedRow();
                if (fila != -1) {
                    txtPlaca.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtModelo.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtCapacidad.setText(modeloTabla.getValueAt(fila, 2).toString());
                    comboEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3));
                    txtPlaca.setEditable(false); // La clave primaria no se modifica durante una actualización
                }
            }
        });
    }

    private void aplicarFiltroPlaca() {
    ((AbstractDocument) txtPlaca.getDocument()).setDocumentFilter(new DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text != null && text.matches("[a-zA-Z0-9]+")) {
                super.insertString(fb, offset, text.toUpperCase(), attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("[a-zA-Z0-9]+")) {
                super.replace(fb, offset, length, text.toUpperCase(), attrs);
            } else if (text != null && text.isEmpty()) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    } ); 
}
    @Override
    public String getPlaca() {
        return txtPlaca.getText().trim();
    }

    @Override
    public String getModelo() {
        return txtModelo.getText().trim();
    }

    @Override
    public int getCapacidad() {
        try {
            return Integer.parseInt(txtCapacidad.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public EstadoUnidad getEstado() {
        return (EstadoUnidad) comboEstado.getSelectedItem();
    }

    @Override
    public void setAccionRegistrar(Runnable accion) {
        botonRegistrar.addActionListener(e -> accion.run());
    }

    // Modificar estado de la unidad luego de registrada
    @Override
    public void setAccionModificarEstado(Runnable accion) {
        botonModificar.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        botonRegresar.addActionListener(e -> accion.run());
    }

    // Muestra lista de unidades
    @Override
    public void mostrarUnidades(List<Unidad> unidades) {
        modeloTabla.setRowCount(0);
        if (unidades != null) {
            for (Unidad u : unidades) {
                modeloTabla.addRow(new Object[]{u.getPlaca(), u.getModelo(), u.getCapacidad(), u.getEstado()});
            }
        }
    }

    // Mensaje de registro exitoso
    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // MENSAJE DE ERROR EN REGISTRO
    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Limpiar el formulario luego de cada accion
    @Override
    public void limpiarFormulario() {
        txtPlaca.setText("");
        txtModelo.setText("");
        txtCapacidad.setText("");
        comboEstado.setSelectedIndex(0);
        txtPlaca.setEditable(true);
        tablaUnidades.clearSelection();
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