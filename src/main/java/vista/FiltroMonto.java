package vista;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


//Filtro para el campo Monto: solo deja escribir (o pegar) números positivos.
//Acepta dígitos y un único separador decimal (punto o coma) con máximo 2 decimales.
//Cualquier otro carácter (letras, signo "-", espacios, etc.) se rechaza.
//La coma se convierte en punto. El valor mínimo (mayor que cero) lo valida MonederoServicio.

public class FiltroMonto extends DocumentFilter {

    // Hasta 7 enteros: el saldo máximo del monedero es 9.999.999,99
    private static final String PATRON = "[0-9]{0,7}([.][0-9]{0,2})?";

    @Override
    public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
            throws BadLocationException {
        replace(fb, offset, 0, texto, attrs);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet attrs)
            throws BadLocationException {
        String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String insertado = texto == null ? "" : texto.replace(',', '.');
        String resultado = actual.substring(0, offset) + insertado + actual.substring(offset + longitud);
        if (resultado.matches(PATRON)) {
            super.replace(fb, offset, longitud, insertado, attrs);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
