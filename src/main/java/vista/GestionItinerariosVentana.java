package vista;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import modelo.Conductor;
import modelo.Itinerario;
import modelo.TipoRuta;
import modelo.Unidad;

public class GestionItinerariosVentana extends JFrame implements GestionItinerariosVista {

    private static final long serialVersionUID = 1L;

    // Convierte un DayOfWeek (MONDAY, TUESDAY...) a su nombre en español con inicial mayúscula
    private static String nombreDia(DayOfWeek dia) {
        return ItinerariosPanel.nombreDia(dia);
    }

    // El diseño de la pantalla vive en ItinerariosPanel; aquí se toman sus componentes
    private final ItinerariosPanel panel = new ItinerariosPanel();

    private final JButton botonRegresar = panel.getBotonRegresar();
    private final JButton botonRegistrar = panel.getBotonRegistrar();
    private final JButton botonModificar = panel.getBotonModificar();
    private final JButton botonEliminar = panel.getBotonEliminar();

    private final JTextField campoOrigen = panel.getCampoOrigen();
    private final JTextField campoDestino = panel.getCampoDestino();
    private final JComboBox<String> cbHora = panel.getCbHora();
    private final JComboBox<String> cbMinuto = panel.getCbMinuto();

    private final JComboBox<DayOfWeek> comboDiaSemana = panel.getComboDiaSemana();
    private final JComboBox<TipoRuta> comboTipoRuta = panel.getComboTipoRuta();
    private final JComboBox<Unidad> comboUnidades = panel.getComboUnidades();
    private final JComboBox<Conductor> comboConductores = panel.getComboConductores();
    private final javax.swing.JTable tablaItinerarios = panel.getTabla();
    private final javax.swing.table.DefaultTableModel modeloTabla = panel.getModeloTabla();
    private final List<Itinerario> listaItinerariosActual = new ArrayList<>();

    public GestionItinerariosVentana() {
        super("Campus Express - Gestionar Itinerarios");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(panel);
        pack();
        setMinimumSize(new java.awt.Dimension(820, 460));
        setLocationRelativeTo(null);
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
    public java.time.LocalTime getHoraSalida() {
        try {
            int h = Integer.parseInt((String) cbHora.getSelectedItem());
            int m = Integer.parseInt((String) cbMinuto.getSelectedItem());
            return java.time.LocalTime.of(h, m);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public TipoRuta getTipoRuta() {
        return (TipoRuta) comboTipoRuta.getSelectedItem();
    }

    // Los combos son editables: getSelectedItem() puede devolver un String (texto escrito o placa/licencia
    // cargada desde la tabla) en vez de un Unidad/Conductor. Por eso se busca el objeto en la lista.
    @Override
    public Unidad getUnidad() {
        Object seleccion = comboUnidades.getSelectedItem();
        if (seleccion instanceof Unidad unidad) {
            return unidad;
        }
        String texto = (seleccion != null) ? seleccion.toString() : textoEditor(comboUnidades);
        return buscarUnidad(texto);
    }

    @Override
    public Conductor getConductor() {
        Object seleccion = comboConductores.getSelectedItem();
        if (seleccion instanceof Conductor conductor) {
            return conductor;
        }
        String texto = (seleccion != null) ? seleccion.toString() : textoEditor(comboConductores);
        return buscarConductor(texto);
    }

    private String textoEditor(javax.swing.JComboBox<?> combo) {
        return ((javax.swing.JTextField) combo.getEditor().getEditorComponent()).getText();
    }

    /** Busca en la lista del combo la unidad cuya placa (o texto completo) coincide con lo indicado. */
    private Unidad buscarUnidad(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String buscado = texto.trim();
        javax.swing.ComboBoxModel<Unidad> modelo = comboUnidades.getModel();
        for (int i = 0; i < modelo.getSize(); i++) {
            Unidad u = modelo.getElementAt(i);
            if (u.getPlaca().equalsIgnoreCase(buscado) || u.toString().equalsIgnoreCase(buscado)) {
                return u;
            }
        }
        return null;
    }

    /** Busca en la lista del combo el conductor cuya licencia, nombre o texto completo coincide con lo indicado. */
    private Conductor buscarConductor(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String buscado = texto.trim();
        javax.swing.ComboBoxModel<Conductor> modelo = comboConductores.getModel();
        for (int i = 0; i < modelo.getSize(); i++) {
            Conductor c = modelo.getElementAt(i);
            if (c.getNumeroLicencia().equalsIgnoreCase(buscado)
                    || c.getNombreCompleto().equalsIgnoreCase(buscado)
                    || c.toString().equalsIgnoreCase(buscado)) {
                return c;
            }
        }
        return null;
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
        this.listaItinerariosActual.clear();
        if (itinerarios != null) {
            this.listaItinerariosActual.addAll(itinerarios);
        }

        if (modeloTabla == null) return;
        modeloTabla.setRowCount(0); // Limpia las filas anteriores
        for (Itinerario it : this.listaItinerariosActual) {
            Object[] fila = {
                it.getOrigen(),
                it.getDestino(),
                nombreDia(it.getDiaSemana()),
                it.getHoraSalida(),
                it.getTipoRuta(),
                it.getPlacaUnidad() != null ? it.getPlacaUnidad() : "",
                it.getLicenciaConductor() != null ? it.getLicenciaConductor() : ""
            };
        modeloTabla.addRow(fila);
        }
    }

    @Override
    public void cargarUnidades(List<Unidad> unidades) {
        javax.swing.ComboBoxModel<Unidad> model = comboUnidades.getModel();
        if (model instanceof javax.swing.DefaultComboBoxModel<Unidad> defaultModel) {
            javax.swing.JTextField editor = (javax.swing.JTextField) comboUnidades.getEditor().getEditorComponent();
            String texto = editor.getText();
            int pos = editor.getCaretPosition();

            defaultModel.removeAllElements();
            if (unidades != null) {
                for (Unidad u : unidades) {
                    // FILTRO: Solo agregar las unidades cuya condición/estado sea ACTIVO (Mejora con relación al Sprint 1)
                    if (u.getEstado() == modelo.EstadoUnidad.ACTIVO) {
                        defaultModel.addElement(u);
                    }
                }
            }
            editor.setText(texto);
            try { editor.setCaretPosition(Math.min(pos, texto.length())); } catch (Exception ignored) {}

            // Mantiene el desplegable abierto si hay resultados mientras el usuario escribe.
            if (comboUnidades.isShowing()) {
                boolean tieneUnidades = unidades != null && !unidades.isEmpty();
            comboUnidades.setPopupVisible(tieneUnidades && editor.hasFocus());
            }
        }
    }

    @Override
    public void cargarConductores(List<Conductor> conductores) {
        javax.swing.ComboBoxModel<Conductor> model = comboConductores.getModel();
        if (model instanceof javax.swing.DefaultComboBoxModel<Conductor> defaultModel) {
            javax.swing.JTextField editor = (javax.swing.JTextField) comboConductores.getEditor().getEditorComponent();
            String texto = editor.getText();
            int pos = editor.getCaretPosition();

            defaultModel.removeAllElements();
            if (conductores != null) {
                for (Conductor c : conductores) {
                    defaultModel.addElement(c);
                }
            }
            editor.setText(texto);
            try { editor.setCaretPosition(Math.min(pos, texto.length())); } catch (Exception ignored) {}

            // Mantiene el desplegable abierto si hay resultados mientras el usuario escribe.
            // Solo se toca el popup si el combo ya está en pantalla (ver cargarUnidades).
            if (comboConductores.isShowing()) {
                boolean tieneConductores = conductores != null && !conductores.isEmpty();
                comboConductores.setPopupVisible(tieneConductores && editor.hasFocus());
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

    @Override
    public void setAccionBuscarUnidad(java.util.function.Consumer<String> buscador) {
        javax.swing.JTextField editor = (javax.swing.JTextField) comboUnidades.getEditor().getEditorComponent();
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                buscador.accept(editor.getText());
            }
        });
    }

    @Override
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
    public void setAccionModificar(Runnable accion) {
        botonModificar.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionEliminar(Runnable accion) {
        botonEliminar.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionSeleccionTabla(Consumer<Itinerario> alSeleccionar) {
        tablaItinerarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Itinerario seleccionado = getItinerarioSeleccionado();
                if (seleccionado != null) {
                    alSeleccionar.accept(seleccionado);
                }
            }
        });
    }

    @Override
    public Itinerario getItinerarioSeleccionado() {
        int fila = tablaItinerarios.getSelectedRow();
        if (fila != -1 && listaItinerariosActual != null && fila < listaItinerariosActual.size()) {
            return listaItinerariosActual.get(fila);
        }
        return null;
    }

    @Override
    public void cargarFormulario(Itinerario itinerario) {
        if (itinerario == null) return;

        campoOrigen.setText(itinerario.getOrigen());
        campoDestino.setText(itinerario.getDestino());
        comboDiaSemana.setSelectedItem(itinerario.getDiaSemana());

        // Cargar la hora de salida en los desplegables cbHora y cbMinuto
        if (itinerario.getHoraSalida() != null) {
            String horaStr = String.format("%02d", itinerario.getHoraSalida().getHour());
            String minutoStr = String.format("%02d", itinerario.getHoraSalida().getMinute());
        
            cbHora.setSelectedItem(horaStr);
            cbMinuto.setSelectedItem(minutoStr);
        }

        comboTipoRuta.setSelectedItem(itinerario.getTipoRuta());
        // Se selecciona el objeto real de la lista (no el texto) para que getUnidad()/getConductor() funcionen.
        Unidad unidadActual = buscarUnidad(itinerario.getPlacaUnidad());
        comboUnidades.setSelectedItem(unidadActual != null ? unidadActual : itinerario.getPlacaUnidad());
        Conductor conductorActual = buscarConductor(itinerario.getLicenciaConductor());
        comboConductores.setSelectedItem(conductorActual != null ? conductorActual : itinerario.getLicenciaConductor());
    }

    @Override
    public void cerrar() {
        dispose();
    }
}