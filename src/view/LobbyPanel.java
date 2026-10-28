package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import constants.IConstants;
import controller.LobbyController;

public class LobbyPanel extends JPanel {
    private static final String BACKGROUND_SVG = "JUMANJI.svg";
    private static final Color FORM_BACKGROUND = new Color(255, 255, 255, 215); // blanco semitransparente

    public JTextField txtNickname;  // apodo
    public JTextField txtMatchCode; // código de 4 caracteres
    public JButton btnCreate;       // crear partida
    public JButton btnJoin;         // unirse a partida
    public JLabel lblCode;          // muestra el código generado mientras espera al oponente
    public JLabel lblError;         // errores del servidor o de validación local

    /** Los botones le piden al LobbyController crear o unirse a una partida. */
    public LobbyPanel(LobbyController controller) {
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("Jungle Attack", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));

        txtNickname = new JTextField(16);
        limitLength(txtNickname, IConstants.NICKNAME_MAX_LENGTH, false);
        txtMatchCode = new JTextField(6);
        limitLength(txtMatchCode, IConstants.MATCH_CODE_LENGTH, true); // el código siempre en mayúsculas

        btnCreate = new JButton("Crear partida");
        btnJoin = new JButton("Unirse a partida");

        lblCode = new JLabel(" ", SwingConstants.CENTER);
        lblCode.setFont(lblCode.getFont().deriveFont(Font.BOLD, 20f));
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(new Color(200, 30, 30));

        btnCreate.addActionListener(e -> {
            clearError();
            controller.createMatch(txtNickname.getText());
        });
        btnJoin.addActionListener(e -> {
            clearError();
            controller.joinMatch(txtNickname.getText(), txtMatchCode.getText());
        });
        // Enter en un campo hace lo mismo que su botón
        txtNickname.addActionListener(e -> btnCreate.doClick());
        txtMatchCode.addActionListener(e -> btnJoin.doClick());

        // El formulario va en un recuadro semitransparente para que se lea encima del fondo
        JPanel form = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FORM_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        form.add(title, c);
        c.gridwidth = 1;
        c.gridy = 1;
        form.add(new JLabel("Apodo:", SwingConstants.RIGHT), c);
        c.gridx = 1;
        form.add(txtNickname, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        form.add(btnCreate, c);
        c.gridwidth = 1;
        c.gridy = 3;
        form.add(new JLabel("Código:", SwingConstants.RIGHT), c);
        c.gridx = 1;
        form.add(txtMatchCode, c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        form.add(btnJoin, c);
        c.gridy = 5;
        form.add(lblCode, c);
        c.gridy = 6;
        form.add(lblError, c);
        add(form); // GridBagLayout sin restricciones lo deja centrado
        setPreferredSize(new Dimension(IConstants.WINDOW_WIDTH, IConstants.WINDOW_HEIGHT));
    }

    /** Fondo JUMANJI estirado al tamaño del panel; si no se pudo leer queda el color normal del panel. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage background = SvgCache.getInstance().getBackground(BACKGROUND_SVG, getWidth(), getHeight());
        if (background != null) {
            g.drawImage(background, 0, 0, null);
        }
    }

    /** Ya se creó la partida: muestra el código y bloquea la pantalla hasta que llegue el oponente. */
    public void showWaiting(String code) {
        lblCode.setText("Código de tu partida: " + code + "  (esperando al oponente...)");
        clearError();
        setInputsEnabled(false);
    }

    /** Muestra un error de validación o del servidor. */
    public void showError(String message) {
        lblError.setText(message == null || message.isBlank() ? " " : message);
    }

    public void clearError() {
        lblError.setText(" ");
    }

    /** Deja la pantalla como nueva (al volver al lobby). */
    public void reset() {
        lblCode.setText(" ");
        clearError();
        txtMatchCode.setText("");
        setInputsEnabled(true);
    }

    private void setInputsEnabled(boolean enabled) {
        txtNickname.setEnabled(enabled);
        txtMatchCode.setEnabled(enabled);
        btnCreate.setEnabled(enabled);
        btnJoin.setEnabled(enabled);
    }

    /** No deja escribir más de maxLength caracteres (y, si se pide, pasa todo a mayúsculas). */
    private static void limitLength(JTextField field, int maxLength, boolean upperCase) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                    throws BadLocationException {
                String incoming = text == null ? "" : (upperCase ? text.toUpperCase() : text);
                int room = maxLength - (fb.getDocument().getLength() - length);
                if (room <= 0 && length == 0) {
                    return;
                }
                if (incoming.length() > room) {
                    incoming = incoming.substring(0, Math.max(0, room));
                }
                super.replace(fb, offset, length, incoming, attr);
            }
        });
    }
}
