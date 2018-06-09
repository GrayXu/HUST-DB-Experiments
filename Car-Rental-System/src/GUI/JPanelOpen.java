package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * 一个透明黑字的textfield
 */
public class JPanelOpen extends JPanel {

    public JPanelOpen(){
        this.setBackground(null);
        this.setOpaque(false);
    }

    public JPanelOpen(LayoutManager layout){
        this.setBackground(null);
        this.setOpaque(false);
        this.setLayout(layout);
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

    public JLabelOpen(String in){
        this.setBackground(null);
        this.setOpaque(false);
        this.setText(in);
    }
}