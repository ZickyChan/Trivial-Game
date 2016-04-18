/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * Refereneces: http://stackoverflow.com/questions/15025092/border-with-rounded-corners-transparency
                http://alvinalexander.com/java/java-audio-example-java-au-play-sound
 */
/**
 *
 * @author David
 */
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import javax.swing.*;

public class Client extends JFrame {
    protected Socket client; //create socket
    
    protected Timer timer; //create timer
    
    protected JScrollPane jp = new JScrollPane();
    protected DataInputStream inp;
    protected DataOutputStream out;
    protected String input, output;
    protected Color back = new Color(251,248,219);
    protected ImageIcon brain = new ImageIcon ("brain.png");
    
    //Create JPanel
    protected JPanel setTime = new JPanel(new FlowLayout(FlowLayout.LEFT,100,150));
    protected JPanel north = new JPanel();
    protected JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
    protected JPanel question = new JPanel(new FlowLayout(FlowLayout.CENTER));
    protected JPanel key = new JPanel(new FlowLayout(FlowLayout.CENTER));
    protected JPanel bigCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
    protected JPanel center = new JPanel(new GridLayout(2,1,0,100));
    protected JPanel playButton = new JPanel(new GridLayout(2,1,0,0));
    protected JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER, 200, 0));
    protected JPanel right = new JPanel(new GridLayout(3,1,0,0)); //Panel which show the result of player
    protected JPanel life = new JPanel(new GridLayout(3,1,0,0));
    
    //Create an Array instance of CheckAnswer class
    protected CheckAnswer[] check = new CheckAnswer[3];
    protected JLabel key1 = new JLabel("");
    
    //Create JPanel with the round angle
    protected JPanel ans = new JPanel() {
     @Override
     protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(15,15);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();

        //Draws the rounded opaque panel with borders.
        graphics.setColor(new Color(246,141,39));
        graphics.fillRoundRect(100,0, width-200, height -1 , arcs.width, arcs.height);//paint background
        graphics.setColor(back);
        graphics.drawRoundRect(100,0, width-200, height -1 , arcs.width, arcs.height);//paint border
        }
    };
    //Create JPanel for topics
    protected JPanel topic = new JPanel(new GridLayout(2,3,10,10));
    
    //Create JLabel
    protected JLabel image = new JLabel(brain);
    protected JLabel intro = new JLabel("TRIVIAL GAME");
    protected JLabel time = new JLabel("Time left: 30 seconds");
    protected JLabel[] lifeIcon = new JLabel[3];
    
    protected JTextField answer = new JTextField(40);
    
    //Create button
    protected JButton play = new JButton("NEW GAME");
    protected JButton quit = new JButton("EXIT");
    //Create array of JButton content topics
    protected RoundButton[] section = new RoundButton[6];
    
    //create int count life
    protected int countLife = 1;
    public Client(){
        Font font = new Font("Verdana", Font.BOLD, 17);
        Font big = new Font("Verdana", Font.BOLD, 22);
        Font large = new Font("Verdana", Font.BOLD, 45);
        //modify the top of window
        header.add(image);        
        header.setBackground(back);
       
        time.setFont(big);
        setTime.setBackground(back);
        setTime.add(time);
        setTime.setVisible(true);
        
        LayoutManager over = new OverlayLayout(north);
        north.setLayout(over);
        north.add(header);
        north.add(setTime);
        
        //modify the center
        JLabel ques = new JLabel();
        ques.setFont(font);
        question.add(ques);
        question.setBackground(back);
        
        
        key1.setFont(font);
        key.add(key1);
        key.setBackground(back);
        
        
        center.add(question);
        center.add(key);
        center.setBackground(back);
        center.setVisible(false);
        ans.setVisible(false);
        
        button.setBackground(back);
        ActionListener playGame = new NewGame();
        ActionListener quitGame = new Quit();
        play.addActionListener(playGame);
        quit.addActionListener(quitGame);
        button.add(play);
        button.add(quit);
        
        intro.setFont(large);
        intro.setHorizontalAlignment(JLabel.CENTER);
        
        playButton.add(intro);
        playButton.add(button);
        playButton.setBackground(back);
        
        topic.setBackground(back);
        topic.setVisible(false);
        
        bigCenter.add(center); 
        bigCenter.add(playButton);
        bigCenter.add(topic);
        LayoutManager overlay = new OverlayLayout(bigCenter);
        bigCenter.setLayout(overlay);
        
        answer.setFont(big);
        answer.setForeground(Color.white);
        answer.setBackground(new Color(246,141,39));
        answer.setHorizontalAlignment(JTextField.CENTER);
        answer.setBorder(null);
        
        ans.setLayout(new FlowLayout(FlowLayout.CENTER));
        ans.add(answer);
        ans.setBackground(back);
        
        //add checkAnswer Panel to right Panel to show result of player
        right.setBackground(back);
        for (int i=0; i<check.length; i++){
            check[i] = new CheckAnswer();
            right.add(check[i]);
        }
        //setIcon for life Label
        for (int i=0; i<lifeIcon.length; i++){
            lifeIcon[i] = new JLabel();
            lifeIcon[i].setIcon(new ImageIcon("life.png"));
            lifeIcon[i].setVisible(false);
            life.add(lifeIcon[i]);
        }
        life.setBackground(back);
        
        
        add(north,BorderLayout.NORTH);
        add(bigCenter,BorderLayout.CENTER);
        add(ans,BorderLayout.SOUTH);
        add(right,BorderLayout.EAST);
        add(life,BorderLayout.WEST);
        setBackground(back);
        setTitle("Trivial Game");
        setSize(1750,900);
        setLocationRelativeTo(null);
        getContentPane().setBackground(back);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        answer.addActionListener(new ButtonListener());
        
        //add window listener for frame
        try{
        client = new Socket("localhost",8000);
        inp = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());
        addWindowListener(new WindowListener() {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                out.writeUTF("bye");
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override 
        public void windowOpened(WindowEvent e) {}

        @Override 
        public void windowClosed(WindowEvent e) {}

        @Override 
        public void windowIconified(WindowEvent e) {}

        @Override 
        public void windowDeiconified(WindowEvent e) {}

        @Override 
        public void windowActivated(WindowEvent e) {}

        @Override 
        public void windowDeactivated(WindowEvent e) {}

        });
            int count = 0;
            int quesNo = 0;
            while ((input = inp.readUTF()) != null){  
                if(count<section.length){
                    section[count] = new RoundButton(input);
                    section[count].setFont(big);
                    section[count].addActionListener(new ChooseSection());
                    topic.add(section[count]);
                    count++;
                }
                else if((input.equalsIgnoreCase("right")) || (input.equalsIgnoreCase("wrong"))){
                    if(input.equalsIgnoreCase("wrong")){
                        countLife--;
                        for (int i=0;i<lifeIcon.length;i++){
                            lifeIcon[i].setVisible(false);
                        }
                        for (int i=0; i<countLife;i++){
                            lifeIcon[i].setVisible(true);
                        }
                    }
                    time.setText("Time left: 30 seconds");
                    timer.stop();
                    check[quesNo].check(input);
                    quesNo++;
                    output = "next";
                    out.writeUTF(output);
                    out.flush();
                }
                else if (input.length() == 62 && (input.substring(4,12)).equals("finished")){
                    int option = JOptionPane.showOptionDialog(null, input, "Congratulation!!", 
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                                null, new Object[]{"Yes", "Quit Game"}, "Yes");
                        if(option==0){
                            quesNo = 0;
                            output = "change";
                            out.writeUTF(output);
                            out.flush();
                            playButton.setVisible(false);
                            center.setVisible(false);
                            ans.setVisible(false);
                            topic.setVisible(true);
                            for(int i = 0; i<check.length;i++){
                                check[i].disable();
                            }
                            for (int i=0;i<lifeIcon.length;i++){
                                lifeIcon[i].setVisible(false);
                            }
                            if(countLife<3){
                                countLife++;
                            }
                            time.setVisible(false);
                        }
                        else{
                            output = "bye";
                            out.writeUTF(output);
                            out.flush();
                            break;
                            //System.exit(WIDTH);
                        }
                }
                else if (input.length() == 68 && (input.substring(8,15)).equalsIgnoreCase("the end")){
                    int option = JOptionPane.showOptionDialog(null, input, "Congratulation!!", 
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                                null, new Object[]{"Yes", "Quit Game"}, "Yes");
                    if(option==0){
                            restart();               
                            quesNo = 0;
                            output = "restart";
                            out.writeUTF(output);
                            out.flush();
                            for(int i = 0; i<check.length;i++){
                                check[i].disable();
                            }
                            setTime.setVisible(false);
                        }
                        else{
                            output = "bye";
                            out.writeUTF(output);
                            out.flush();
                            break;
                        }
                }
                else if (input.equals("lose")){
                    time.setVisible(false);
                    int option = JOptionPane.showOptionDialog(null, "Sorry you lost!!! Do you want to restart a new game?", "LOSER!!!!!!!!!", 
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                                null, new Object[]{"Yes", "Quit Game"}, "Yes");
                        if(option==0){
                            restart();
                            quesNo = 0;
                            output = "restart";
                            out.writeUTF(output);
                            out.flush();
                            for(int i = 0; i<check.length;i++){
                                check[i].disable();
                            }
                            setTime.setVisible(false);
                        }
                        else{
                            output = "bye";
                            out.writeUTF(output);
                            out.flush();
                            break;
                        }
                }
                else{
                    if (input.equals("bye")){
                    break;
                    }
                    
                    time.setText("Time left: 30 seconds");
                    time.setVisible(true);
                    setTime.setVisible(true);
                    TimeClass tc = new TimeClass(30);  //30 seconds
                    timer = new Timer(1000, tc);    //each 1000 milliseconds - 1 second, ActionListener tc triggered once
                    timer.start();
                    
                    String[] breaker = input.split("\\?");
                    String question = breaker[0];
                    String dash = breaker[1];
                    key1.setText(dash);
                    ques.setText(question + "?");
                }
            }

            client.close();
            inp.close();
            out.close();
            System.exit(WIDTH); 
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
    }
    
    public class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try{
            output = answer.getText().trim();
            answer.setText(null);    
            out.writeUTF(output);
            out.flush();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    public class ChooseSection implements ActionListener{
        public void actionPerformed(ActionEvent e){
            for (int i=0;i<lifeIcon.length;i++){
                lifeIcon[i].setVisible(false);
            }
            for (int i=0; i<countLife;i++){
                lifeIcon[i].setVisible(true);
            }
            playButton.setVisible(false);
            center.setVisible(true);
            ans.setVisible(true);
            setTime.setVisible(true);
            topic.setVisible(false);
            Object j = e.getSource();
            RoundButton l = (RoundButton)j;
            l.setEnabled(false);
            String topic = l.getText();
            try{
            output = topic.trim();
            out.writeUTF(output);
            out.flush();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    public class NewGame implements ActionListener{
        public void actionPerformed(ActionEvent e){
            playButton.setVisible(false);
            center.setVisible(false);
            ans.setVisible(false);
            topic.setVisible(true);
            time.setVisible(false);
        }
    }
    public void restart(){
        playButton.setVisible(false);
        center.setVisible(false);
        ans.setVisible(false);
        topic.setVisible(true);
        time.setText("dsadasd");
        time.setVisible(true);
        setTime.setVisible(true);
        
        countLife = 1;
        for(int i=0; i<section.length;i++){
            section[i].setEnabled(true);
        }
        for(int i=0; i<lifeIcon.length;i++){
            lifeIcon[i].setVisible(false);
        }
    }
    public class Quit implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try {
                out.writeUTF("bye");
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public class TimeClass implements ActionListener {
        int count;
        public TimeClass(int counter){
            count = counter;
        }
        public void actionPerformed(ActionEvent tc){
            count--;
            if(count > 1){
                String a = "Time left: " + count + " seconds";
                time.setText(a);
            }
            else if(count == 1){
                String a = "Time left: " + count + " second";
                time.setText(a);
            }
            else{
                timer.stop();
                try {
                    out.writeUTF("");
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }      
            }
        }
    }    
    public static void main(String[] args){
        new Client();
    }
}
