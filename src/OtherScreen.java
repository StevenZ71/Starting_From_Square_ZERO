import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OtherScreen extends JPanel {
    private BufferedImage loadScreen;
    public OtherScreen(){
        try {loadScreen = ImageIO.read(getClass().getResourceAsStream("/misc/loadScreen.png"));} catch (IOException e) {}
    }
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(loadScreen,0,0,1400,800,null);
    }
}
