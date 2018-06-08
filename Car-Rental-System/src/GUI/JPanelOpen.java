package GUI;

import javax.swing.*;

/**
 * 一个透明黑字的textfield
 */
public class JPanelOpen extends JPanel {

    public JPanelOpen(){
        this.setBackground(null);
        this.setOpaque(false);
    }

}

class JTextFiledOpen extends JTextField{
    public JTextFiledOpen(){
        this.setBackground(null);
        this.setOpaque(false);
    }
}

class JLabelOpen extends JLabel{
    public JLabelOpen(){

        this.setBackground(null);
        this.setOpaque(false);
    }
}