package view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LobbyPanel extends JPanel {
    public JTextField txtNickname;  // apodo
    public JTextField txtMatchCode; // código de 4 caracteres
    public JButton btnCreate;       // crear partida
    public JButton btnJoin;         // unirse a partida
    public JLabel lblCode;          // muestra el código generado mientras espera al oponente
    public JLabel lblError;         // errores del servidor o de validación local

}
