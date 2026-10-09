package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ConfiguracionTarifaVentana extends JFrame {

    private JTextField txtMontoTarifaBase;
    private JComboBox<String> comboRutaExtraurbana;
    private JButton btnGuardarTarifa;
    private JButton btnRegresar;
    private JTable tablaTarifasCalculadas;
    private DefaultTableModel modeloTabla;

    public ConfiguracionTarifaVentana() {
        super("Campus Express - Configuración de Tarifa Base Municipal");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ---------------- 1. FORMULARIO SUPERIOR ----------------
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configuración de Tarifa Base Extraurbana", 
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Ruta Extraurbana
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Ruta Extraurbana:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        String[] rutas = {"Seleccionar...", "Ruta Caracas - Maracay", "Ruta Caracas - Valencia", "Ruta Caracas - Tejerías"};
        comboRutaExtraurbana = new JComboBox<>(rutas);
        comboRutaExtraurbana.setPreferredSize(new Dimension(250, 28));
        panelFormulario.add(comboRutaExtraurbana, gbc);

        // Tarifa Base (T_base)
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Tarifa Base Municipal (T_base) $:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtMontoTarifaBase = new JTextField();
        txtMontoTarifaBase.setPreferredSize(new Dimension(250, 28));
        panelFormulario.add(txtMontoTarifaBase, gbc);

        // Botón Guardar
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        btnGuardarTarifa = new JButton("Guardar Tarifa Base");
        btnGuardarTarifa.setBackground(new Color(40, 167, 69));
        btnGuardarTarifa.setForeground(Color.WHITE);
        panelFormulario.add(btnGuardarTarifa, gbc);

        // ---------------- 2. TABLA DE SUBSIDIOS Y DESCUENTOS (CENTRO) ----------------
        String[] columnas = {"Tipo de Usuario", "Porcentaje de Subsidio / Descuento", "Monto a Pagar ($)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };

        // Datos iniciales de demostración visual
        modeloTabla.addRow(new Object[]{"Estudiante", "80% Subsidio (Paga 20%)", "-"});
        modeloTabla.addRow(new Object[]{"Empleado / Profesor", "50% Subsidio (Paga 50%)", "-"});
        modeloTabla.addRow(new Object[]{"Público General", "0% Subsidio (Paga 100%)", "-"});

        tablaTarifasCalculadas = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaTarifasCalculadas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Estructura de Cobro Solidario por Tipo de Usuario"));

        // ---------------- 3. PANEL INFERIOR (BOTONES DE NAVEGACIÓN) ----------------
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnRegresar = new JButton("Regresar");
        panelInferior.add(btnRegresar);

        // Ensamblar todo en la ventana
        add(panelFormulario, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // Getters y Setters de acciones para conectar con el Controlador
    public String getMontoTarifa() {
        return txtMontoTarifaBase.getText().trim();
    }

    public String getRutaSeleccionada() {
        return (String) comboRutaExtraurbana.getSelectedItem();
    }

    public void setAccionGuardar(Runnable accion) {
        btnGuardarTarifa.addActionListener(e -> accion.run());
    }

    public void setAccionRegresar(Runnable accion) {
        btnRegresar.addActionListener(e -> accion.run());
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }
}