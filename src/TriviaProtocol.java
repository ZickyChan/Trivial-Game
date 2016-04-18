import java.util.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author s3479778
 */
public class TriviaProtocol {
    private static final int START = 0;
    private static final int READY = 1;
    private static final int CONTINUE = 2;
    private static final int RESPONSE = 3;
    private static final int ENDTOPIC = 4;
    private static final int FINISH = 5;
    private String dbURL = "jdbc:derby://localhost:1527/trivial";
    
    private int state = START;
    protected int quesNo = 0;
    private int count = 0;
    private int totalCount = 0;
    private int right = 0;
    private int totalRight = 0;
    private int life = 1;
    
    private String[] questions = new String[5];
    private LinkedList ques;
    
    private String[] ans = new String[5];
    private LinkedList answers;
    
    private String[] topic = new String[6];
    private String theOutput;
    
    public void getQuestion(String a) throws SQLException{
        questions = new String[5];
        ans = new String[5];
        storeQuesAns(a);
        ques = new LinkedList(Arrays.asList(questions));
        answers = new LinkedList(Arrays.asList(ans));
        Random ran = new Random();
        int ranNum = ran.nextInt();
        Collections.shuffle(ques, new Random(ranNum));
        Collections.shuffle(answers, new Random(ranNum));
    }
    public String processInput(String input, String a) throws SQLException, Exception{
        if (life > 0){
            if (state == START){
                getQuestion(a);
                theOutput = "" + ques.get(quesNo);
                totalCount++;
                state = RESPONSE;       
            }
            else if (state == RESPONSE){
                if (totalCount<18){
                    if (count < 2){
                        if (input.equalsIgnoreCase((String) answers.get(quesNo))){
                            right();                  
                            quesNo++;
                            count++;
                            right++;
                            totalRight++;
                            //totalCount++;
                        }
                        else {
                            count++;
                            wrong();
                            quesNo++;
                            //totalCount++;
                        }
                        //theOutput = "" + ques.get(quesNo);
                    }
                    else{
                        if (input.equalsIgnoreCase((String) answers.get(quesNo))){
                            right++;
                            totalRight++;
                            //totalCount++;
                            right();
                        }
                        else{
                            wrong();
                        }
                        state = ENDTOPIC;
                        }
                }
                else{
                    if (input.equalsIgnoreCase((String) answers.get(quesNo))){
                            right++;
                            totalRight++;
                            //totalCount++;
                            right();
                        }
                        else{
                            wrong();
                        }
                       state = FINISH;
                }
            }
            else if (state == FINISH){
                // open the sound file as a Java input stream
                String gongFile = "clap.wav";
                InputStream in = new FileInputStream(gongFile);

                // create an audiostream from the inputstream
                AudioStream audioStream = new AudioStream(in);

                // play the audio clip with the audioplayer class
                AudioPlayer.player.start(audioStream);
                theOutput = "This is the end of the game. You got " + totalRight + " answers. Restart a new game?";
                quesNo = 0;
                count = 0;
                right = 0;
                totalCount = 0;
                state = START;
            }
            else if (state == READY){
                theOutput = "" + ques.get(quesNo);
                totalCount++;
                state = RESPONSE;
            }
            else if(state == ENDTOPIC){
                    theOutput = "You finished this topic. You got " + right + " answers. Try another topic?";
                    if(life<3){
                        life++;
                    }
                    quesNo = 0;
                    count = 0;
                    right = 0;
                    state = START;
            }
        }
        else{
            theOutput = "lose"; 
            life = 1;
            state=START;
            count = 0;
            right = 0;
            quesNo = 0;
            totalCount = 0;
        } 
        return theOutput;     
    }
    public void right() throws Exception{
        state = READY;
        theOutput = "right";
        // open the sound file as a Java input stream
        String soundFile = "DING.wav";
        InputStream in = new FileInputStream(soundFile);

         // create an audiostream from the inputstream
         AudioStream audioStream = new AudioStream(in);

         // play the audio clip with the audioplayer class
         AudioPlayer.player.start(audioStream);
    }
    public void wrong() throws Exception{
        life--;
        state = READY;
        theOutput = "wrong";
        // open the sound file as a Java input stream
        String soundFile = "wrong.wav";
        InputStream in = new FileInputStream(soundFile);

         // create an audiostream from the inputstream
         AudioStream audioStream = new AudioStream(in);

         // play the audio clip with the audioplayer class
         AudioPlayer.player.start(audioStream);
    }
    public String[] getTopic() throws SQLException{
        Statement state = null;
        try{
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            //Connecting to database
            Connection conn = DriverManager.getConnection(dbURL,"programming2","programming2");
            
            //Prepare to execute a query
            state = conn.createStatement();
            //Create a sql command
            String query = "SELECT DISTINCT TOPIC FROM PROGRAMMING2.TRIVIAL";
          
            //Store the data that get from database
            ResultSet rs = state.executeQuery(query);
            while (rs.next()){
                String section = rs.getString("Topic");
                for(int i=0;i<topic.length;i++){
                    if(topic[i] == null){
                        topic[i] = section;
                        break;
                    }
                }
            }      
        }
        catch (SQLException e ) {
            e.printStackTrace();
        } 
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (state != null) { 
                state.close();               
            }
        }
        return topic;
    } 
    public void storeQuesAns(String a) throws SQLException{
        Statement state = null;
        try{
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            //Connecting to database
            Connection conn = DriverManager.getConnection(dbURL,"programming2","programming2");
            
            //Prepare to execute a query
            state = conn.createStatement();
            //Create a sql command
            String query = "SELECT Questions,Answers FROM PROGRAMMING2.TRIVIAL WHERE TOPIC='" + a +"'";
          
            //Store the data that get from database
            ResultSet rs = state.executeQuery(query);
            while (rs.next()){
                String ques = rs.getString("Questions");
                String answer = rs.getString("Answers");
                for(int i=0;i<questions.length;i++){
                    if(questions[i] == null){
                        questions[i] = ques;
                        break;
                    }
                }
                for(int i=0;i<ans.length;i++){
                    if(ans[i] == null){
                        ans[i] = answer;
                        
                        break;
                    }
                }
            }
        }
        catch (SQLException e ) {
            e.printStackTrace();
        } 
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (state != null) { 
                state.close(); 
            }
        }
    }
    public void setTotalRight(int a){
        totalRight = a;
    }
}
