package vista;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import modelo.EstadoUnidad;
import modelo.Unidad;

// Ventana Swing para la gestión del inventario de flota vehicular.
// El diseño de la pantalla vive en FlotaPanel; esta clase conecta la pantalla con el controlador.
public class GestionFlotaVentana extends JFrame implements GestionFlotaVista {

    private static final long serialVersionUID = 1L;

    private final FlotaPanel panel = new FlotaPanel();

    public GestionFlotaVentana() {
        super("Campus Express - Gestión de Flota");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(760, 430));
        setLocationRelativeTo(null);
    }

    public JTable getTablaUnidades() {
        return panel.getTabla();
    }

    public DefaultTableModel getModeloTabla() {
        return panel.getModeloTabla();
    }

    @Override
    public String getPlaca() {
        return panel.getPlaca();
    }

    @Override
    public String getModelo() {
        return panel.getModelo();
    }

    @Override
    public int getCapacidad() {
        try {
            return Integer.parseInt(panel.getCapacidadTexto());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public EstadoUnidad getEstado() {
        return panel.getEstado();
    }

    @Override
    public void setAccionRegistrar(Runnable accion) {
        panel.setAccionRegistrar(accion);
    }

    @Override
    public void setAccionModificarEstado(Runnable accion) {
        panel.setAccionGuardar(accion);
    }

    // La cruz de la esquina superior derecha cierra la pantalla y vuelve al panel anterior
    @Override
    public void setAccionRegresar(Runnable accion) {
        panel.setAccionCerrar(accion);
    }

    @Override
    public void mostrarUnidades(List<Unidad> unidades) {
        DefaultTableModel modelo = panel.getModeloTabla();
        modelo.setRowCount(0);
        if (unidades != null) {
            for (Unidad u : unidades) {
                modelo.addRow(new Object[]{u.getPlaca(), u.getModelo(), u.getCapacidad(), u.getEstado()});
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
    public void limpiarFormulario() {
        panel.limpiarFormulario();
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
