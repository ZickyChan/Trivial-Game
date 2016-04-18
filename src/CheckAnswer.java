/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Asus N56VZ
 */
import java.awt.*;
import javax.swing.*;

public class CheckAnswer extends JPanel{
    private ImageIcon check = new ImageIcon("check.png");
    private ImageIcon cross = new ImageIcon("cross.png");
    private JLabel checkLabel = new JLabel();
    private JLabel crossLabel = new JLabel();
    public CheckAnswer(){
        checkLabel.setIcon(check);
        crossLabel.setIcon(cross);
        LayoutManager overlay = new OverlayLayout(this);
        setLayout(overlay);
        add(checkLabel);
        add(crossLabel);
        setBackground(new Color(251,248,219));
        checkLabel.setVisible(false);
        crossLabel.setVisible(false);
    }
    public void check(String a){
        if(a.equalsIgnoreCase("right")){
            checkLabel.setVisible(true);
        }
        else{
            crossLabel.setVisible(true);
        }
    }
    public void disable(){
        checkLabel.setVisible(false);
        crossLabel.setVisible(false);
    }
    
}
