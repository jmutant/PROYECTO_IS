package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;

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

public class ConfiguracionTarifaVentana extends JFrame implements ConfiguracionTarifaVista {

    private JTextField txtMontoTarifaBase;
    private JTextField txtPctEstudiante;
    private JTextField txtPctEmpleado;
    private JComboBox<String> comboRutaExtraurbana;
    private JButton btnGuardarTarifa;
    private JButton btnEliminarTarifa;
    private JButton btnRegresar;
    private JTable tablaTarifasCalculadas;
    private DefaultTableModel modeloTabla;

    public ConfiguracionTarifaVentana() {
        super("Campus Express - Configuración de Tarifa Base Municipal");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(700, 520);
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
        panelFormulario.add(new JLabel("Tipo de Ruta:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        String[] rutas = {"Seleccionar...", "Urbana", "Extraurbana"};
        comboRutaExtraurbana = new JComboBox<>(rutas);
        comboRutaExtraurbana.setPreferredSize(new Dimension(250, 28));
        panelFormulario.add(comboRutaExtraurbana, gbc);

        // Tarifa Base (T_base)
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Tarifa Base Municipal en Bs:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtMontoTarifaBase = new JTextField();
        txtMontoTarifaBase.setPreferredSize(new Dimension(250, 28));
        panelFormulario.add(txtMontoTarifaBase, gbc);

        // % Estudiante (Rango 10% - 20%)
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("% Subsidio Estudiante (10% - 20%):"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        txtPctEstudiante = new JTextField(""); // Valor por defecto en rango
        txtPctEstudiante.setPreferredSize(new Dimension(250, 26));
        panelFormulario.add(txtPctEstudiante, gbc);

        // % Empleado / Profesor (Rango 30% - 60%)
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("% Subsidio Empleado/Prof. (30% - 60%):"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtPctEmpleado = new JTextField(""); // Valor por defecto en rango
        txtPctEmpleado.setPreferredSize(new Dimension(250, 26));
        panelFormulario.add(txtPctEmpleado, gbc);

        // Botón Guardar
        // Panel contenedor para los botones de acción (Eliminar y Guardar)
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnEliminarTarifa = new JButton("Eliminar Tarifa");
        btnEliminarTarifa.setBackground(new Color(220, 53, 69)); // Rojo alerta
        btnEliminarTarifa.setForeground(Color.BLACK);

        btnGuardarTarifa = new JButton("Guardar / Actualizar");
        btnGuardarTarifa.setBackground(new Color(40, 167, 69)); // Verde éxito
        btnGuardarTarifa.setForeground(Color.BLACK);

        panelBotonesAccion.add(btnEliminarTarifa);
        panelBotonesAccion.add(btnGuardarTarifa);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(panelBotonesAccion, gbc);

        // ---------------- 2. TABLA DE SUBSIDIOS Y DESCUENTOS (CENTRO) ----------------
        String[] columnas = {"Tipo de Usuario", "Porcentaje de Subsidio / Descuento", "Monto a Pagar (Bs)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };

        // Datos iniciales de demostración visual
        modeloTabla.addRow(new Object[]{"Estudiante", "-"});
        modeloTabla.addRow(new Object[]{"Empleado / Profesor",  "-"});
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
    @Override
    public String getMontoTarifaBase() {
        return txtMontoTarifaBase.getText().trim();
    }

    @Override
    public String getPctEstudiante() {
        return txtPctEstudiante.getText().trim();
    }

    @Override
    public String getPctEmpleado() {
        return txtPctEmpleado.getText().trim();
    }

    @Override
    public String getRutaSeleccionada() {
        return (String) comboRutaExtraurbana.getSelectedItem();
    }

    @Override
    public void setAccionGuardarTarifa(Runnable accion) {
        btnGuardarTarifa.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        btnRegresar.addActionListener(e -> accion.run());
    }

    @Override
    public void mostrarTarifasCalculadas(double pctEstudiante, double montoEstudiante, double pctEmpleado, double montoEmpleado, double montoPublico) {
        if (modeloTabla != null) {
            modeloTabla.setRowCount(0); // Limpiar tabla antes de rellenar
            
            // Fila 1: Estudiante (Orden estricto)
            modeloTabla.addRow(new Object[]{
                "Estudiante",
                String.format("%.2f%%", pctEstudiante),
                String.format("%.2f", montoEstudiante)
            });
            
            // Fila 2: Empleado / Profesor (Orden estricto)
            modeloTabla.addRow(new Object[]{
                "Empleado / Profesor",
                String.format("%.2f%%", pctEmpleado),
                String.format("%.2f", montoEmpleado)
            });
            
            // Fila 3: Público General (Fijo 100%)
            modeloTabla.addRow(new Object[]{
                "Público General",
                "100%",
                String.format("%.2f", montoPublico)
            });
        }
    }

    @Override
    public void setAccionEliminarTarifa(Runnable accion) {
        btnEliminarTarifa.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionSeleccionarRuta(ItemListener listener) {
        comboRutaExtraurbana.addItemListener(listener);
    }

    @Override
    public void limpiarCampos() {
        comboRutaExtraurbana.setSelectedIndex(0);
        txtMontoTarifaBase.setText("");
        txtPctEstudiante.setText("");
        txtPctEmpleado.setText("");
        
        // Limpiar tabla o poner guiones
        if (modeloTabla != null) {
            modeloTabla.setRowCount(0);
            modeloTabla.addRow(new Object[]{"Estudiante", "Configurable (10% - 20%)", "-"});
            modeloTabla.addRow(new Object[]{"Empleado / Profesor", "Configurable (30% - 60%)", "-"});
            modeloTabla.addRow(new Object[]{"Público General", "100% (Sin subsidio)", "-"});
        }
    }

    @Override
    public void setMontoTarifaBase(String monto) {
        txtMontoTarifaBase.setText(monto); // Cambia "campoTarifaBase" por el nombre de tu variable real si es distinto
    }

    @Override
    public void setPctEstudiante(String porcentaje) {
        txtPctEstudiante.setText(porcentaje); // Cambia por el nombre real de tu campo de estudiante
    }

    @Override
    public void setPctEmpleado(String porcentaje) {
        txtPctEmpleado.setText(porcentaje); // Cambia por el nombre real de tu campo de empleado
    }

    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
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