package vista;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class GestionConductoresVentana extends JFrame {

    private static final long serialVersionUID = 1L;

    // El diseño de la pantalla vive en ConductoresPanel; aquí se toman sus componentes
    private final ConductoresPanel panel = new ConductoresPanel();

    private final JTextField txtNombreCompleto = panel.getTxtNombreCompleto();
    private final JTextField txtCedula = panel.getTxtCedula();
    private final JTextField txtNumeroLicencia = panel.getTxtNumeroLicencia();
    private final JButton btnRegistrarConductor = panel.getBotonRegistrar();
    private final JButton btnEliminarConductor = panel.getBotonEliminar();
    private final JTable tablaConductores = panel.getTabla();
    private final DefaultTableModel modeloTabla = panel.getModeloTabla();

    public GestionConductoresVentana() {
        setTitle("Campus Express - Gestión de Conductores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(820, 460));
        setLocationRelativeTo(null);
        // La cruz de la esquina superior derecha cierra la pantalla
        panel.getBotonCerrar().addActionListener(e -> cerrar());
    }

    public void mostrar() {
        setVisible(true);
    }

    public void cerrar() {
        dispose();
    }

    // Métodos con validaciones estrictas en la interfaz
    public String getNombreCompleto() {
        String nombre = txtNombreCompleto.getText().trim();
        if (nombre.matches(".*\\d.*")) {
            throw new IllegalArgumentException("El nombre completo no puede contener números.");
        }
        return nombre;
    }

    public String getCedula() {
        String cedulaInput = txtCedula.getText().trim().toUpperCase();
        if (!cedulaInput.matches("^V\\d+$")) {
            throw new IllegalArgumentException("La cédula debe iniciar con 'V' seguido de los números (Ej: V12345678).");
        }
        
        try {
            long numeroCedula = Long.parseLong(cedulaInput.substring(1));
            if (numeroCedula < 1_000_000 || numeroCedula > 40_000_000) {
                throw new IllegalArgumentException("La cédula numérica debe estar entre 1.000.000 y 40.000.000.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El número de cédula ingresado no es válido.");
        }
        
        return cedulaInput;
    }

    public String getNumeroLicencia() {
        String licencia = txtNumeroLicencia.getText().trim();
        if (!licencia.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("El número de licencia debe contener exactamente 10 dígitos numéricos y no puede tener signos negativos.");
        }
        return licencia;
    }

    public void limpiarCampos() {
        txtNombreCompleto.setText("");
        txtCedula.setText("");
        txtNumeroLicencia.setText("");
        tablaConductores.clearSelection();
    }

    public void agregarFilaTabla(Object[] fila) {
        modeloTabla.addRow(fila);
    }

    public void limpiarTabla() {
        modeloTabla.setRowCount(0);
    }

    // Métodos para asignar eventos desde el controlador
    public void setAccionRegistrar(ActionListener accion) {
        btnRegistrarConductor.addActionListener(e -> {
            try {
                accion.actionPerformed(e);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void setAccionEliminar(ActionListener accion) {
        btnEliminarConductor.addActionListener(accion);
    }

    public void setAccionSeleccionarTabla(javax.swing.event.ListSelectionListener accion) {
        tablaConductores.getSelectionModel().addListSelectionListener(accion);
    }

    public JTable getTablaConductores() {
        return tablaConductores;
    }

    public void setNombreCompleto(String nombreCompleto) {
        txtNombreCompleto.setText(nombreCompleto);
    }

    public void setCedula(String cedula) {
        txtCedula.setText(cedula);
    }

    public void setNumeroLicencia(String numeroLicencia) {
        txtNumeroLicencia.setText(numeroLicencia);
    }
}