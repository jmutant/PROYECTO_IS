package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class GestionConductoresVentana extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtNombreCompleto;
    private JTextField txtCedula;
    private JTextField txtNumeroLicencia;
    private JButton btnRegistrarConductor;
    private JButton btnEliminarConductor;
    private JTable tablaConductores;
    private DefaultTableModel modeloTabla;

    public GestionConductoresVentana() {
        setTitle("Campus Express - Gestión de Conductores");
        setSize(700, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel central con formulario y tabla
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));

        // Formulario (GridBagLayout para mantener simetría con los campos)
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Nombre Completo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre Completo:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombreCompleto = new JTextField(20);
        panelFormulario.add(txtNombreCompleto, gbc);

        // Fila 1: Cédula
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Cédula:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtCedula = new JTextField(15);
        panelFormulario.add(txtCedula, gbc);

        // Etiqueta de formato para la cédula
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel lblFormatoCedula = new JLabel("Ej: V12345678 ");
        lblFormatoCedula.setForeground(Color.GRAY);
        panelFormulario.add(lblFormatoCedula, gbc);

        // Fila 2: Número de Licencia
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Nº de Licencia:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNumeroLicencia = new JTextField(20);
        panelFormulario.add(txtNumeroLicencia, gbc);

        // Etiqueta de formato para la licencia
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel lblFormatoLicencia = new JLabel("10 dígitos exactos");
        lblFormatoLicencia.setForeground(Color.GRAY);
        panelFormulario.add(lblFormatoLicencia, gbc);

        // Panel para botones de acción (Registrar Conductor y Eliminar)
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRegistrarConductor = new JButton("Registrar Conductor");
        btnEliminarConductor = new JButton("Eliminar");
        panelBotonesAccion.add(btnRegistrarConductor);
        panelBotonesAccion.add(btnEliminarConductor);

        // Agrupando formulario y botones de acción en la parte superior del centro
        JPanel panelNorteCentro = new JPanel(new BorderLayout());
        panelNorteCentro.add(panelFormulario, BorderLayout.CENTER);
        panelNorteCentro.add(panelBotonesAccion, BorderLayout.SOUTH);
        panelCentral.add(panelNorteCentro, BorderLayout.NORTH);

        // Tabla de conductores registrados
        modeloTabla = new DefaultTableModel(new Object[]{"Nombre Completo", "Cédula", "Número de Licencia"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Celdas no editables directamente desde la tabla
            }
        };
        tablaConductores = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaConductores);
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        add(panelPrincipal);
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