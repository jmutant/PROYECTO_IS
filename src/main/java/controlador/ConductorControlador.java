package controlador;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import modelo.Conductor;
import modelo.ConductorRepositorio;
import vista.GestionConductoresVentana;

public class ConductorControlador {

    private final GestionConductoresVentana ventana;
    private final ConductorRepositorio conductorRepositorio;

    public ConductorControlador(GestionConductoresVentana ventana, ConductorRepositorio conductorRepositorio) {
        this.ventana = ventana;
        this.conductorRepositorio = conductorRepositorio;

        initController();
        cargarConductoresEnTabla();
    }

    private void initController() {
        ventana.setAccionRegistrar(e -> registrarConductor());
        ventana.setAccionEliminar(e -> eliminarConductor());

        ventana.getTablaConductores().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = ventana.getTablaConductores().getSelectedRow();
                if (fila >= 0) {
                    ventana.setNombreCompleto((String) ventana.getTablaConductores().getValueAt(fila, 0));
                    ventana.setCedula((String) ventana.getTablaConductores().getValueAt(fila, 1));
                    ventana.setNumeroLicencia((String) ventana.getTablaConductores().getValueAt(fila, 2));
                }
            }
        });
    }

    private void registrarConductor() {
        try {
            String nombre = ventana.getNombreCompleto();
            String cedula = ventana.getCedula();
            String licencia = ventana.getNumeroLicencia();

            Conductor conductor = new Conductor(nombre, cedula, licencia);

            if (conductorRepositorio.existePorLicencia(licencia)) {
                JOptionPane.showMessageDialog(ventana, "Ya existe un conductor registrado con este número de licencia.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            conductorRepositorio.guardar(conductor);
            cargarConductoresEnTabla();
            ventana.limpiarCampos();

            JOptionPane.showMessageDialog(ventana, "Conductor registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(ventana, ex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, "Ocurrió un error al guardar el conductor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarConductor() {
        int filaSeleccionada = ventana.getTablaConductores().getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(ventana, "Por favor seleccione un conductor de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = (String) ventana.getTablaConductores().getValueAt(filaSeleccionada, 0);
        String licencia = (String) ventana.getTablaConductores().getValueAt(filaSeleccionada, 2);

        int confirmacion = JOptionPane.showConfirmDialog(
            ventana, 
            "¿Está seguro de eliminar al conductor " + nombre + "?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                conductorRepositorio.eliminar(licencia);
                cargarConductoresEnTabla();
                ventana.limpiarCampos();

                JOptionPane.showMessageDialog(ventana, "Conductor eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, "Ocurrió un error al eliminar el conductor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarConductoresEnTabla() {
        ventana.limpiarTabla();
        for (Conductor c : conductorRepositorio.listarTodos()) {
            ventana.agregarFilaTabla(new Object[]{
                c.getNombreCompleto(),
                c.getCedula(),
                c.getNumeroLicencia()
            });
        }
    }
}