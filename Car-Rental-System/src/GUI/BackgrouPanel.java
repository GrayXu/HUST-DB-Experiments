package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BackgrouPanel extends JPanel {

    Image img;

    public BackgrouPanel(String url) throws IOException {
        img= ImageIO.read(new File(url));//载入作为背景
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);//自动进行拉伸
    }

}