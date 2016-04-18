/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Asus N56VZ
 */
import javax.swing.*;
import java.awt.*;

public class RoundButton extends JButton {
    public RoundButton(String a){
        setHorizontalTextPosition(SwingConstants.CENTER);            
        setOpaque(false);
        setFocusPainted(false);        
        setBorderPainted(false);
        setContentAreaFilled(false);
        setBackground(new Color(251,248,219));
        setForeground(Color.BLACK);
        setIcon(new ImageIcon("topic.png"));
        setSize(100,50);
        setText(a);
    }
}
