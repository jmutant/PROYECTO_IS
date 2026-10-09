package vista;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


//Filtro para campos de cédula: solo deja escribir (o pegar) dígitos, con un máximo de 8. El rango válido (10000000 a 99999999) lo valida AutenticacionServicio.

public class FiltroCedula extends DocumentFilter {

    public static final int LONGITUD_MAXIMA = 8;

    @Override
    public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
            throws BadLocationException {
        replace(fb, offset, 0, texto, attrs);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet attrs)
            throws BadLocationException {
        String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String insertado = texto == null ? "" : texto;
        String resultado = actual.substring(0, offset) + insertado + actual.substring(offset + longitud);
        if (resultado.matches("[0-9]{0," + LONGITUD_MAXIMA + "}")) {
            super.replace(fb, offset, longitud, texto, attrs);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
