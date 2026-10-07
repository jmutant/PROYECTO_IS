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

    // Nombres de los días en español (índice 0 = lunes ... 6 = domingo)
    private static final String[] DIAS_ES =
            {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    // Convierte un DayOfWeek (MONDAY, TUESDAY...) a su nombre en español con inicial mayúscula
    private static String nombreDia(DayOfWeek dia) {
        return DIAS_ES[dia.getValue() - 1]; // DayOfWeek: 1 = lunes ... 7 = domingo
    }

    // Componentes visuales
    private final JButton botonRegresar = new JButton("Regresar");
    private final JButton botonRegistrar = new JButton("Registrar Itinerario");
    private final javax.swing.JButton botonModificar = new JButton("Modificar");
    private final javax.swing.JButton botonEliminar = new JButton("Eliminar");
    
    private final JTextField campoOrigen = new JTextField();
    private final JTextField campoDestino = new JTextField();
    private final JComboBox<String> cbHora = new JComboBox<>(generarHoras());
    private final JComboBox<String> cbMinuto = new JComboBox<>(new String[]{"00", "15", "30", "45"});
    
    private final JComboBox<DayOfWeek> comboDiaSemana = new JComboBox<>(DayOfWeek.values());
    private final JComboBox<TipoRuta> comboTipoRuta = new JComboBox<>(TipoRuta.values());
    private final JComboBox<Unidad> comboUnidades = new JComboBox<>();
    private final JComboBox<Conductor> comboConductores = new JComboBox<>();
    private javax.swing.JTable tablaItinerarios;
    private javax.swing.table.DefaultTableModel modeloTabla;
    private final List<Itinerario> listaItinerariosActual = new ArrayList<>();

    public GestionItinerariosVentana() {
        super("Campus Express - Gestionar Itinerarios");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        java.awt.Dimension tamanoComboHora = new java.awt.Dimension(50, 20);
        cbHora.setPreferredSize(tamanoComboHora);
        cbMinuto.setPreferredSize(tamanoComboHora);

        javax.swing.JPanel panelPrincipal = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        panelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---------------- FORMULARIO (ARRIBA) ----------------
        javax.swing.JPanel panelFormulario = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 8, 4, 8);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Fila 0: Origen
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Origen:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelFormulario.add(campoOrigen, gbc);

        // Fila 1: Destino
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Destino:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelFormulario.add(campoDestino, gbc);

        // Fila 2: Día de la Semana
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Día de la Semana:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboDiaSemana.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DayOfWeek dia) {
                    setText(nombreDia(dia));
                }
                return this;
            }
        });
        panelFormulario.add(comboDiaSemana, gbc);

        // // Fila 3: Hora Salida (Mejora pedida para el Sprint 2 para eliminar hora manual)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Hora Salida:"), gbc);

        // Contenedor horizontal para la hora y los minutos
        javax.swing.JPanel panelHora = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        cbHora.setPreferredSize(new java.awt.Dimension(50, 20));
        cbMinuto.setPreferredSize(new java.awt.Dimension(50, 20));

        panelHora.add(cbHora);
        panelHora.add(new javax.swing.JLabel(":"));
        panelHora.add(cbMinuto);

        gbc.gridx = 1; gbc.weightx = 0.7;
        panelFormulario.add(panelHora, gbc);

        // Fila 4: Tipo de Ruta
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Tipo de Ruta:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelFormulario.add(comboTipoRuta, gbc);

        // Fila 5: Unidad / Vehículo
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Unidad / Vehículo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboUnidades.setEditable(true);
        panelFormulario.add(comboUnidades, gbc);

        // Fila 6: Conductor
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        panelFormulario.add(new javax.swing.JLabel("Conductor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboConductores.setEditable(true);
        panelFormulario.add(comboConductores, gbc);

        // ---------------- BOTONES (CENTRO) ----------------
        javax.swing.JPanel panelBotones = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));
        panelBotones.add(botonRegistrar);
        panelBotones.add(botonModificar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonRegresar);

        // Panel superior contenedor (Formulario + Botones)
        javax.swing.JPanel panelNorte = new javax.swing.JPanel(new java.awt.BorderLayout());
        panelNorte.add(panelFormulario, java.awt.BorderLayout.CENTER);
        panelNorte.add(panelBotones, java.awt.BorderLayout.SOUTH);

        // ---------------- TABLA DE ITINERARIOS (ABAJO) ----------------
        String[] columnas = {"Origen", "Destino", "Día", "Hora", "Tipo", "Unidad", "Conductor"};
        modeloTabla = new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace la tabla no editable
            }
        };
        tablaItinerarios = new javax.swing.JTable(modeloTabla);
        javax.swing.JScrollPane scrollTabla = new javax.swing.JScrollPane(tablaItinerarios);

        // Unir paneles al principal
        panelPrincipal.add(panelNorte, java.awt.BorderLayout.NORTH);
        panelPrincipal.add(scrollTabla, java.awt.BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    // Método auxiliar de horas (Parte de la mejora)
    private static String[] generarHoras() {
        String[] horas = new String[24];
        for (int i = 0; i < 24; i++) {
            horas[i] = String.format("%02d", i); // "00", "01", ..., "23"
        }
        return horas;
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

    @Override
    public Unidad getUnidad() {
        return (Unidad) comboUnidades.getSelectedItem();
    }

    @Override
    public Conductor getConductor() {
        return (Conductor) comboConductores.getSelectedItem();
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
        comboUnidades.setSelectedItem(itinerario.getPlacaUnidad());
        comboConductores.setSelectedItem(itinerario.getLicenciaConductor());
    }

    @Override
    public void cerrar() {
        dispose();
    }
}