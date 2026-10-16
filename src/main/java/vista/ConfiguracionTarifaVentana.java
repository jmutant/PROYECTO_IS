package vista;

import java.awt.Dimension;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class ConfiguracionTarifaVentana extends JFrame implements ConfiguracionTarifaVista {

    // El diseño de la pantalla vive en TarifaPanel; aquí se toman sus componentes
    private final TarifaPanel panel = new TarifaPanel();

    private final JTextField txtMontoTarifaBase = panel.getTxtMontoTarifaBase();
    private final JTextField txtPctEstudiante = panel.getTxtPctEstudiante();
    private final JTextField txtPctEmpleado = panel.getTxtPctEmpleado();
    private final JComboBox<String> comboRutaExtraurbana = panel.getComboRuta();
    private final JButton btnGuardarTarifa = panel.getBotonGuardar();
    private final JButton btnEliminarTarifa = panel.getBotonEliminar();
    private final JButton btnRegresar = panel.getBotonCerrar(); // la cruz de la esquina superior derecha
    private final JTable tablaTarifasCalculadas = panel.getTabla();
    private final DefaultTableModel modeloTabla = panel.getModeloTabla();

    public ConfiguracionTarifaVentana() {
        super("Campus Express - Configuración de Tarifa Base Municipal");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(820, 460));
        setLocationRelativeTo(null);

        // Datos iniciales de demostración visual
        modeloTabla.addRow(new Object[]{"Estudiante", "-", "-"});
        modeloTabla.addRow(new Object[]{"Empleado / Profesor", "-", "-"});
        modeloTabla.addRow(new Object[]{"Público General", "0% Subsidio (Paga 100%)", "-"});
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