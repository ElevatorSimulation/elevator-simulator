
import java.lang.Math;
import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Controller extends JPanel implements ActionListener{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//from algorithm
   //constant
   public static final int walk_stair = 20;
   public static final int elev_moving = 2;
   public static final int elev_openclose = 5;
   public static String message = "";
   public static long time;
   //some details
   public static ArrayList<Person> T_personList = new ArrayList<Person>();   //contain created person list(click - personList add)
   public static ArrayList<Person> A_personList = new ArrayList<Person>();   //contain created person list(click - personList add)
   public static ArrayList<Person> B_personList = new ArrayList<Person>();
   public static ArrayList<Person> C_personList = new ArrayList<Person>();
   public static ArrayList<S_Elevator> eleList = new ArrayList<S_Elevator>();
   public static ArrayList<Floor> floorList = new ArrayList<Floor>();
   public static ArrayList<JButton> jbArr = new ArrayList<JButton>(12);//contains 12 jbuttons
   private int choose;
   
   public static Font ttfReal = null;//digital font
   public static Font userBtnFont = null;//font on button(a little bit smaller)
   public static Font elevator = null;//top ELEVATOR font
   public static Font userFont = null;//top ELEVATOR font
   
   public static JTextField t_cFloor; //current floor JTextField
   public static int x;
   int person = 0, personX = 1, personDest = 62;
   public static int total_waiting_time;
   public static int[] TWT = new int[3];
   private static int tot_person = 0;
   Timer tm1 = new Timer();
 	TimerTask tsk1 = new TimerTask(){
 		public void run(){
 			time = System.currentTimeMillis(); 
 			repaint();
 		}
 	};
   //constructor
   public Controller(){
	   
      /* initialize all */
      
//draw
      //read ele.png from outside
      Image img = null;
      try{
         File elevator_image = new File("ele.png");
         img = ImageIO.read(elevator_image);
      }
      catch(IOException e){
      }
      //read digital Font
      Font ttfBase = null;
        try {
            InputStream myStream = new BufferedInputStream(new FileInputStream("DS-DIGIB.ttf"));
            ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
            userFont = ttfBase.deriveFont(Font.PLAIN, 10);
            ttfReal = ttfBase.deriveFont(Font.BOLD, 19);
            elevator = ttfBase.deriveFont(Font.BOLD, 48);
            userBtnFont = ttfBase.deriveFont(Font.BOLD, 25);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("digital font not loaded.");
        }
        //default configuration
        this.setLayout(null);
        
      //set a default layout
      this.setBackground(Color.WHITE);
      
      //create elevator image and add to the main panel(this)
        JLabel elevator_img = new JLabel(new ImageIcon(img));
        elevator_img.setBounds(2, -1, 72, 121);///positioning and sizing ele.png
        elevator_img.validate();
        this.add(elevator_img);
        
        //add buttons and floor Labels
      int btnX = 467, btnY = 627;
      int j = 1, k = 1;
      for(int i = 1; i <= 12; i++){
         Integer in = new Integer(i);
         String s = in.toString();
         
         //create and add button in the button list
         JButton jb = new JButton(s);
         jb.addActionListener(this);//jb actionEvent throws event in this.actionPerformed.
         jbArr.add(jb);
         
         //button positioning and sizing : j i row, k is column
         jb.setBounds(btnX+70*(k-1), btnY - 45*(j-1), 70, 45);
         if(i%4 == 0){
            btnX = 467;
            k = 0;
            j++;
         }
         k++;
         jb.setFont(userBtnFont); //set button font
         this.add(jb);
      }
      
      //add floor panels
      for(int i = 1; i <= 12; i++){
         Floor f = new Floor(i); //create floor panel and set floor number
         f.setBounds(-21, 572-(40*(i-1)), 96, 30);
         f.setFcnt(0);//set number of users awaiting on this floor to 0
         

         //f.setBackground(Color.RED);
         this.add(f);
         floorList.add(f);
      }
      
      
      //current floor JTextField above the buttons
      t_cFloor = new JTextField();
      t_cFloor.setFont(Controller.ttfReal);
      t_cFloor.setBounds(615+50, 492, 30, 30);
      t_cFloor.setHorizontalAlignment((int) CENTER_ALIGNMENT);
      t_cFloor.setText("1");
      this.add(t_cFloor);
      
//      JButton reset = new JButton("Reset");
//      reset.setFont(Controller.ttfReal);
//      reset.setBounds(650, 492, 90, 30);
//      this.add(reset);
      
  		tm1.schedule(tsk1, 0, 1000);
      initialize();   

   }
   
   //draw the background painting
   public void paintComponent(Graphics g){ //(諛곌꼍�뙣�꼸) 洹몃┝ 洹몃━湲�  
      this.setBackground(Color.WHITE);
      //headline
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, 2000, 2000);
      g.setFont(elevator);//set elevator font
      g.setColor(Color.BLACK);
      g.drawString("E L E V A T O R", 80, 40);
      
      
      //floor on the left side
      for(int i = 122; i <= 602; i+=40){
         
         g.setColor(Color.LIGHT_GRAY);
         g.fillRect(-22, i, 96, 10);
         g.setColor(Color.DARK_GRAY);
         g.drawRect(-22, i, 96-1, 10-1);
      }
      
      Graphics2D g2 = (Graphics2D)g;
      g2.setStroke(new BasicStroke(1));   //g2 is bold stroke
      
      //current time and message boxes
      g.setFont(ttfReal);
      
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(463, 47, 288, 26);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(463, 47, 288-1, 26-1);
      
      g.setColor(Color.WHITE);
      g.fillRect(464, 48, 286, 24);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(464, 48, 286-1, 24-1);
      
      g.setColor(Color.BLACK);
      g.drawString("CURRENT TIME : ", 467, 66);
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = dayTime.format(new Date(time));
      g.drawString(str, 586, 66);
      
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(463, 106, 288, 310);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(463, 106, 288-1, 310-1);
      
      g.setColor(Color.WHITE);
      g.fillRect(466, 109, 282, 304);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(466, 109, 282-1, 304-1);
      
      g.setColor(Color.RED);
      g.drawString("MESSAGE", 575, 130);
      g.setColor(Color.black);
      //message with newlines
      g.setFont(userBtnFont);
      int msg_y = 170;
      for(String tmp_msg: message.split("\n")){
    	  g.drawString(tmp_msg, 490, msg_y+=g.getFontMetrics().getHeight());
      }
      g.setFont(ttfReal);
      
      g.setColor(Color.DARK_GRAY);
      g.fillRect(469, 135, 276, 2);
      
      //user gui outer box
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(463, 448, 288, 228);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(463, 448, 288-1, 228-1);
      
      g.setColor(Color.WHITE);
      g.fillRect(466, 451, 282, 222);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(466, 451, 282-1, 222-1);
      
      g.setColor(Color.RED);
      g.drawString("USER GUI", 570, 470);
         
      g.setColor(Color.DARK_GRAY);
      g.fillRect(469, 475, 276, 2);
      
      //current floor inner text and box
      g.setColor(Color.BLACK); 
      g.drawString("Current Floor : ", 473+50, 512);
      g.setFont(userBtnFont);
      
      g.setColor(Color.RED); 
      g.drawString("A", 60, 669);
      g.setColor(Color.BLUE); 
      g.drawString("B", 188, 669);
      g.setColor(Color.GREEN); 
      g.drawString("C", 316, 669);
   }
   

   //the main panel is also a thread, so when it runs, this panel starts every 50ms
   
   
   
   //(incomplete)people moving action
   //when jbutton is clicked, event is thrown in here
   public void actionPerformed(ActionEvent event) {
//      if(person > personDest){
//         personDest -= 10;
//         person = 0;
//      }
//      else{
//         person = person + personX;
//      }
//      repaint();
      Object obj = event.getSource();//event.getSource returns the address of event occurring object
      for(int i = 0; i < 12; i++){
         if(obj == jbArr.get(i) && Integer.parseInt(t_cFloor.getText())-1!=i && Integer.parseInt(t_cFloor.getText())>0 && Integer.parseInt(t_cFloor.getText())<=12){
            Integer crnt_floor = null;
            
            
            
            //get current floor of new user
            if(t_cFloor.getText() == ""); //return error message
            else crnt_floor = Integer.parseInt(t_cFloor.getText());
            
            Person p = new Person(crnt_floor.intValue(), i+1); //create a person(int current floor and button index + 1(dest floor))
            //p.current_elev = 0;
            
            //eleList.get(0).dest[0]=crnt_floor.intValue();
            //eleList.get(0).dest[1]=i+1;
            T_personList.add(p);//new user is added on the list
            
            int ps = p.getStartFloor();
            int pd = p.getDestFloor();
            
            //System.out.println("current floor : "+ps +" dest floor : "+pd); //test if the person is created well
            
            choose = this.select_elev(tot_person, ps, pd);
            floorList.get(crnt_floor.intValue()-1).setNewUser(p);
            //System.out.println("choose elevator : " + choose);
           // System.out.println(eleList.get(0).getDestArray()[0]);//test : display the first destination
//            //set Direction
//            if(crnt_floor < p.getDestFloor()){
//               eleList.get(choose).setDirection(1);
//            }
//            else if(crnt_floor < p.getDestFloor()){
//               eleList.get(choose).setDirection(-1);
//            }
//            else{
//               eleList.get(choose).setDirection(0);
//            }
            if(ps==eleList.get(p.current_elev).current_floor && eleList.get(p.current_elev).OPEN == true && eleList.get(p.current_elev).e_cnt+p.weight<=900){
               eleList.get(p.current_elev).time = 0;
            }
            if(ps==eleList.get(p.current_elev).current_floor && eleList.get(p.current_elev).CLOSE == true && eleList.get(p.current_elev).e_cnt+p.weight<=900){
               eleList.get(p.current_elev).CLOSE = false;
               eleList.get(p.current_elev).OPEN = true;
               eleList.get(p.current_elev).time = 0;
            }
            
            //display
            message_display(tot_person, ps, pd);
            tot_person++;
//            System.out.println("A : "+eleList.get(0).cases);
//            System.out.println("B : "+eleList.get(1).cases);
//            System.out.println("C : "+eleList.get(2).cases);
//            System.out.println("direc : "+eleList.get(p.current_elev).direction);
//            System.out.println("T : "+total_waiting_time);
//            System.out.println("cNT : " + eleList.get(choose).e_cnt);
            break;
         }
//         else if(obj == "RESET"){
//            for(i=0;i<3;i++){
//            	for(int j=0;j<eleList.get(i).personList.size();j++){
//            		eleList.get(i).personList.get(j).hide();
//            		eleList.get(i).personList.remove(j);
//            	}
//            }
//         }
      }
      //call algorithm and choose one(return n)
      
      //find elevator
      
      //move elevator, 0 is demo
      
      
   }
   
   
   public static void main(String[] args) { 
      //create a frame   
      JFrame jf = new JFrame();
      jf.setTitle("Elevator");
      jf.setSize(775, 720);
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.setVisible(true);
      
      //create a base panel
      Controller eb = new Controller();//create 48 users, 3 elevators
      eb.setLayout(null);
      
      //3 elevator classes is created in the constructor
      S_Elevator ele1 = eleList.get(0);
      S_Elevator ele2 = eleList.get(1);
      S_Elevator ele3 = eleList.get(2);
//      eleList.get(0).elev_index=0;
//      eleList.get(1).elev_index=1;
//      eleList.get(2).elev_index=2;
      //person (incomplete) ignore!
//      ArrayList<Person> pList = new ArrayList<Person>();
//      jbArr.get(6).addActionListener(new ActionListener(){
//         public void actionPerformed(ActionEvent e){
//            Person p = new Person("L");
//            p.setBounds(x, 600, 10, 10);
//            eb.add(p);
//            x = x+11;
//            pList.add(p);
//         }
//      });
      
      //set elevator position and add on the main panel - controller()(aka. eb)
      ele1.setBounds(75, 47, 110, 629);
      ele2.setBounds(203, 47, 110, 629);
      ele3.setBounds(331, 47, 110, 629);
      eb.add(ele1);
      eb.add(ele2);
      eb.add(ele3);
      //add on the elevator list
      
//      //create person
//      Person person = new Person();
//      //person.setBounds(0, 434, 20, 20);
//      eb.add(person);
      jf.add(eb);//main Panel
      
      jf.validate();
      
      
      //create and start 3 threads
//      Thread m = new Thread(eb);
//      Thread e1 = new Thread(ele1);
//      Thread e2 = new Thread(ele2);
//      Thread e3 = new Thread(ele3);
      
      //Thread p = new Thread(person);
//      m.start();
//      e1.start();
//      e2.start();
//      e3.start();
      
      
      //do control (from algorithm)
      
      //if click any buttons, create one person class  (max 45, so n = n%45 + 1) 
      
      //when a new person is created, floor class is updated
      
      //(floor class is 12 classes from 1st floor to 12th floor)
      
      //call algorithm and get elevator A or B or C
      
      //set on value to elevator A or B or C
      
   }
   //message display method
   private void message_display(int n, int c, int d){ //n is nth user, c is user current floor and d is user destination floor
	   //initialize message
	   
	   message = "";
      //be careful for n!!! It might be changed into n-1
	   
      String rec, u_wt="";
      //rec is recomment message
      //message is comparison
      //u_wt is user(n).waiting time string

//      
//      Dim Rec As String
//      Dim i As Integer
//      Human(n).Visible = True
//      Human(n).Left = -16
//      Human(n).Text = D
//      F_Cnt(C) += 1
      
//
//        If C = 12 Then
//            Human(n).Top = 145
//        ElseIf C = 11 Then
//            Human(n).Top = 185
//        ElseIf C = 10 Then
//            Human(n).Top = 225
//        ElseIf C = 9 Then
//            Human(n).Top = 265
//        ElseIf C = 8 Then
//            Human(n).Top = 305
//        ElseIf C = 7 Then
//            Human(n).Top = 345
//        ElseIf C = 6 Then
//            Human(n).Top = 385
//        ElseIf C = 5 Then
//            Human(n).Top = 425
//        ElseIf C = 4 Then
//            Human(n).Top = 465
//        ElseIf C = 3 Then
//            Human(n).Top = 505
//        ElseIf C = 2 Then
//            Human(n).Top = 545
//        ElseIf C = 1 Then
//            Human(n).Top = 585
//        End If
//      
//      For i = 1 To 45
//               If Human(i).Visible = True Then
//                   Tot_Waiting_Time += Users(i).Waiting_Time
//               End If
//           Next
      
      //type cast int to String (elevator type)
      String eleType = "NULL";
      u_wt = Integer.toString(T_personList.get(T_personList.size()-1).getWaiting_time());
      if(choose == 0){
    	  eleType = "A";
//    	  u_wt = Integer.toString(A_personList.get(A_personList.size()-1).getWaiting_time());
      }
      else if(choose == 1){
    	  eleType = "B";
//    	  u_wt = Integer.toString(B_personList.get(B_personList.size()-1).getWaiting_time());
      }
      else if(choose == 2){
    	  eleType = "C";
//    	  u_wt = Integer.toString(C_personList.get(C_personList.size()-1).getWaiting_time());
      }
      
      int worst = T_personList.get(T_personList.size()-1).getWaiting_time()+(Math.abs(c-d)-1)*elev_openclose;
      message = "      Elevator : " +  eleType + "\n\n" +
                " Elev : " + u_wt + " ~ " + worst + " seconds" + "\n"+
                " Walk : " + Math.abs(c - d) * walk_stair + " seconds" + "\n" + 
                "           (" + Math.abs(c - d) * walk_stair + " stairs)" + "\n\n" + 
                "RECOMMEND : ";
      //compare waiting time of an elevator with walking stairs
      if(Integer.parseInt(u_wt) > Math.abs(c - d)*walk_stair){
         rec = "WALK";
      }
      else if(Integer.parseInt(u_wt) < Math.abs(c - d)*walk_stair){
         rec = "ELEVATOR";
      }
      else
         rec = "WALK or ELVEATOR";
      message += rec;
      this.repaint();
      
//      if(floorList.get(c-1).get  )
//            If Floors(C, Users(n).Current_Elev, i) = 0 Then
//            Floors(C, Users(n).Current_Elev, i) = n
//            Exit For
//        End If
//    Next
//    For i = 1 To 3
//        If Elev(i).Current_Floor = C And OPEN(i) = True And Users(n).Current_Elev = i And E_Cnt(i) <> 15 Then
//            Doors(i).Enabled = False
//        End If
//        If Elev(i).Current_Floor = C And CLOSE(i) = True And Users(n).Current_Elev = i And E_Cnt(i) <> 15 Then
//            CLOSE(i) = False
//            OPEN(i) = True
//        End If
//    Next

//      for(i = 0; i < 3; i++){
//         if( (eleList.get(i).getCurrentFloor() == c) && (open(i) == true) && eleList.get(i).getPesrons() != 15){
//            doors(i).enabled = false;// what is door? and what is open?
//         }
//      }
      
      
   }
   
   
   //destination positioning algorithm
   //parameter is nth user and i elevator
   private void dest_algorithm(int n, int i){
//      System.out.println("///////dest_algorithm//////");
      int k , j; 
      int[] destArr = eleList.get(i).getDestArray();
//      System.out.println("previous dest[0]" + destArr[0]);
//      System.out.println("previous dest[1]" + destArr[1]);
      //Person user = personList.get(n);
      int destCnt = 2;
//      System.out.println("n : "+n);
      
      //elevator4
	  int j2 = 0;

      //original i is j in here
      int e_cFloor = eleList.get(i).getCurrentFloor();
      int u_start = T_personList.get(n).getStartFloor();
      int u_dest = T_personList.get(n).getDestFloor();
//      System.out.println("ustart: " + u_start + " udest : " + u_dest);

      int cases = eleList.get(i).getCases();
      
//      System.out.println("get direction : " + eleList.get(i).getDirection());
      
      if(cases == 0){//case 0 : 1st and 2nd ele dest is usr start and usr dest
//         System.out.println("if stop, s and d is 0 and 1");
         destArr[0] = u_start;
         destArr[1] = u_dest;
      }//end of case 0
      else if(cases == 1){
         if(e_cFloor != u_start){//ele current floor is not equal to usr start

            //start floor
            for(j = 0; j < 21; j++){//21times
               if(destArr[j] == u_start){
                  destCnt--; //same start
                  break;//exitfor
               }
               if(destArr[j] > u_start){
                  for(k = 21; k > j; k--){
                     destArr[k] = destArr[k-1];
                  }
                  destArr[k] = u_start;
                  break;
               }
               else if(destArr[j] == 0){ // ? 
                  destArr[j] = u_start;
                  break;
               }   
            }//end for
            
            //dest floor
            for(j = 0; j < 21; j++){
               if(destArr[j] == u_dest){//same dest
                  destCnt--;
                  break;
               }
               if(destArr[j] > u_dest){
            	   for(k = 21; k > j; k--){
                       destArr[k] = destArr[k-1];
                    }
                  destArr[k] = u_dest;
                  break;
               }
               else if(destArr[j] == 0){
                  destArr[j] = u_dest;
                  break;
               }   
            }//end for
         }
         else if(e_cFloor == u_start){//current floor is equal to user start
            //dest floor
            for(j = 0; j < 21; j++){
               if(destArr[j] == u_dest){//same dest
                  destCnt--;
                  break;
               }
               if(destArr[j] > u_dest){
            	   for(k = 21; k > j; k--){
                       destArr[k] = destArr[k-1];
                    }
                  destArr[k] = u_dest;
                  break;
               }
               else if(destArr[j] == 0){
                  destArr[j] = u_dest;
                  break;
               }   
            }//end for
         }
      }//end of case 1
      else if(cases == 2 || cases == -2){
//         for(j = 0; j < 21; j++){
//            //not determined
//         }
      }//end of case 2 or -2
      else if(cases == -1){
         if(e_cFloor != u_start){//current floor is not equal to user start
            //start floor
            for(j = 0; j < 21; j++){
               if(destArr[j] == u_start){
                  destCnt--; //same start
                  break;//exitfor
               }
               if(destArr[j] < u_start){
            	   for(k = 21; k > j; k--){
                       destArr[k] = destArr[k-1];
                    }
                  destArr[k] = u_start;
                  break;
               }
               else if(destArr[j] == 0){ // ? 
                  destArr[j] = u_start;
                  break;
               }   
            }//end for
            
            //dest floor
            for(j = 0; j < 21; j++){
               if(destArr[j] == u_dest){//same dest
                  destCnt--;
                  break;
               }
               if(destArr[j] < u_dest){
            	   for(k = 21; k > j; k--){
                       destArr[k] = destArr[k-1];
                    }
                  destArr[k] = u_dest;
                  break;
               }
               else if(destArr[j] == 0){
                  destArr[j] = u_dest;
                  break;
               }   
            }//end for
         }
         else if(e_cFloor == u_start){
            //dest floor
            for(j = 0; j < 21; j++){
               if(destArr[j] == u_dest){//same dest
                  destCnt--;
                  break;
               }
               if(destArr[j] < u_dest){
            	   for(k = 21; k > j; k--){
                       destArr[k] = destArr[k-1];
                    }
                  destArr[k] = u_dest;
                  break;
               }
               else if(destArr[j] == 0){
                  destArr[j] = u_dest;
                  break;
               }   
            }//end for
         }      
      }//end of case -1
      else if(cases == 3){
    	  //here j is j2
    	  
    	  //start floor
    	  if(e_cFloor!=u_start){
	    	  for(j = 0; j < 21; j++){
	    		  if(destArr[j] > destArr[j+1] && destArr[j]!= 0 && destArr[j]!=0){
	    			  //destCnt--;?
	    			  j2 = j;
	    			  break;
	    		  }
	    	  }//end for
	    	  for(j = j2; j < 21; j++ ){
	    		  if(destArr[j] == u_start)
	    			  //destCnt-- ?
	    			  break;
	    		  if(destArr[j] < u_start){
	    			  for(k = 21; k > j; k--){
	                      destArr[k] = destArr[k-1];
	                   }
	    			  destArr[k] = u_start;
	    			  break;
	    		  }//end if
	    		  else if(destArr[j] == 0){
	    			  destArr[j] = u_start;
	    			  break;
	    		  }
	    	  }//end for
    	  }
    	  //dest floor
    	  for(j = 0; j < 21; j++){
    		  if(destArr[j] > destArr[j+1] && destArr[j]!= 0 && destArr[j]!=0){
    			  //destCnt--;?
    			  j2 = j;
    			  break;
    		  }
    	  }//end for
    	  //dest floor
    	  for(j = j2; j < 21; j++ ){
    		  if(destArr[j] == u_dest)
    			  //destCnt-- ?
    			  break;
    		  if(destArr[j] < u_dest){
    			  for(k = 21; k > j; k--){
                      destArr[k] = destArr[k-1];
                   }
    			  destArr[k] = u_dest;
    			  break;
    		  }//end if
    		  else if(destArr[j] == 0){
    			  destArr[j] = u_dest;
    			  break;
    		  }
    	  }//end for
//    	  for(j = 0; j < 21; j++){
//    		  if(destArr[j]== 0){
//    			  //destCnt--;?
//    			  j2 = j+1;
//    			  break;
//    		  }
//    	  }//end for
//    	  for(j = j2; j < 21; j++ ){
//    		  if(destArr[j] == u_start)
//    			  //destCnt-- ?
//    			  break;
//    		  if(destArr[j] < u_start){
//    			  for(k = 20; k >= j; k--){
//    				  destArr[k+1] = destArr[k];
//    			  }//end for
//    			  destArr[j] = u_start;
//    			  break;
//    		  }//end if
//    		  else if(destArr[j] == 0){
//    			  destArr[j] = u_start;
//    			  break;
//    		  }
//    	  }//end for
//    	  //dest floor
//    	  for(j = 0; j < 21; j++){
//    		  if(destArr[j]== 0){
//    			  //destCnt--;?
//    			  j2 = j+1;
//    			  break;
//    		  }
//    	  }//end for
//    	  //dest floor
//    	  for(j = j2; j < 21; j++ ){
//    		  if(destArr[j] == u_dest)
//    			  //destCnt-- ?
//    			  break;
//    		  if(destArr[j] < u_dest){
//    			  for(k = 20; k >= j; k--){
//    				  destArr[k+1] = destArr[k];
//    			  }//end for
//    			  destArr[j] = u_dest;
//    			  break;
//    		  }//end if
//    		  else if(destArr[j] == 0){
//    			  destArr[j] = u_dest;
//    			  break;
//    		  }
//    	  }//end for
      }//end of case 3
      else if(cases == -3){
    	  //here j is j2
    	
    	  //start floor
    	  if(e_cFloor!=u_start){
	    	  for(j = 0; j < 21; j++){
	    		  if(destArr[j] < destArr[j+1] && destArr[j+1]!= 0 && destArr[j]!=0){
	    			  //destCnt--;?
	    			  j2 = j;
	    			  break;
	    		  }
	    	  }//end for
	    	  
	    	  //start floor(real)
	    	  for(j = j2; j < 21; j++ ){
	    		  if(destArr[j] == u_start)
	    			  //destCnt-- ?
	    			  break;
	    		  if(destArr[j] > u_start){
	    			  for(k = 21; k > j; k--){
	                      destArr[k] = destArr[k-1];
	                   }
	    			  destArr[k] = u_start;
	    			  break;
	    		  }//end if
	    		  else if(destArr[j] == 0){
	    			  destArr[j] = u_start;
	    			  break;
	    		  }
	    	  }//end for
    	  }
    	  //dest floor
    	  for(j = 0; j < 21; j++){
    		  if(destArr[j] > destArr[j+1] && destArr[j+1]!= 0 && destArr[j]!=0){
    			  //destCnt--;?
    			  j2 = j;
    			  break;
    		  }
    	  }//end for
    	  //dest floor
    	  for(j = j2; j < 21; j++ ){
    		  if(destArr[j] == u_dest)
    			  //destCnt-- ?
    			  break;
    		  if(destArr[j] > u_dest){
    			  for(k = 21; k > j; k--){
                      destArr[k] = destArr[k-1];
                   }
    			  destArr[k] = u_dest;
    			  break;
    		  }//end if
    		  else if(destArr[j] == 0){
    			  destArr[j] = u_dest;
    			  break;
    		  }
    	  }//end for
//    	  for(j = 0; j < 21; j++){
//    		  if(destArr[j]== 0){
//    			  //destCnt--;?
//    			  j2 = j+1;
//    			  break;
//    		  }
//    	  }//end for
//    	  
//    	  //start floor(real)
//    	  for(j = j2; j < 21; j++ ){
//    		  if(destArr[j] == u_start)
//    			  //destCnt-- ?
//    			  break;
//    		  if(destArr[j] > u_start){
//    			  for(k = 20; k >= j; k--){
//    				  destArr[k+1] = destArr[k];
//    			  }//end for
//    			  destArr[j] = u_start;
//    			  break;
//    		  }//end if
//    		  else if(destArr[j] == 0){
//    			  destArr[j] = u_start;
//    			  break;
//    		  }
//    	  }//end for
//    	  //dest floor
//    	  for(j = 0; j < 21; j++){
//    		  if(destArr[j]== 0){
//    			  //destCnt--;?
//    			  j2 = j+1;
//    			  break;
//    		  }
//    	  }//end for
//    	  //dest floor
//    	  for(j = j2; j < 21; j++ ){
//    		  if(destArr[j] == u_dest)
//    			  //destCnt-- ?
//    			  break;
//    		  if(destArr[j] > u_dest){
//    			  for(k = 20; k >= j; k--){
//    				  destArr[k+1] = destArr[k];
//    			  }//end for
//    			  destArr[j] = u_dest;
//    			  break;
//    		  }//end if
//    		  else if(destArr[j] == 0){
//    			  destArr[j] = u_dest;
//    			  break;
//    		  }
//    	  }//end for
      }
//      System.out.println("in dest algo >>> dest 0 : " + u_start + " dest 1 : " + u_dest);
      eleList.get(i).setDestArray(destArr, destCnt);
   }//end of dest algorithm
   
   //select elevator algorithm method
   private int select_elev(int n, int c, int d){// n is nth user, c is current floor and d is destination floor
//	 em.out.println("/////////select algorithm//////////");
	   //select elev algorithm
      int i, j;
      //ArrayList<Integer> T_W_T = new ArrayList<Integer>(3);//size 3(3 elevators)
      int[][] WT = new int[3][500];//size 45 ????????? what is this? temp comparison

      int temp;//temp integer to store complex expression
      int[] e_destArr;//destArr of each elevator
      //T_W_T.remove(T_W_T);//format previous TWT
      Person p;
      S_Elevator e;
      Person userN = T_personList.get(n);
//      System.out.println("initial total waiting time : "+total_waiting_time);
      //initialize T_W_T to current total waiting time of three elevators
      //T_W_T.remove(T_W_T);//remove self - empty
      total_waiting_time=0;
      for(i=0;i<T_personList.size();i++){
    	  total_waiting_time+=T_personList.get(i).waiting_time;
      }
      for(i = 0; i < 3; i++){//T_W_T Array is total waiting time of each elevator
         TWT[i] = total_waiting_time;
//         System.out.println("TWT "+i+" "+TWT[i]);
         for (j = 0; j < T_personList.size(); j++){
         	 WT[i][j] = T_personList.get(j).getWaiting_time();// IS IT RIGHT????????????????
//         	 System.out.println("WT["+i+"]["+j+"]"+WT[i][j]);
          }
      }
      
      for(i = 0; i < 3; i++){
         e = eleList.get(i);//i = 0,1,2 th elevator
        // int tmp_TWT, tmp_wt;//tmp_TWT stores integer totalTWT and tmp_wt stores integer waiting time of a user
         //T_W_T may be zero(stop means no users)

         //first_dest stores the first destination of ith elevator
         
         e_destArr = e.getDestArray();
         //tmp_TWT =TWT[i];
//         if(i==0)
//        	 tmp_personList = A_personList;
//         else if(i==1)
//        	 tmp_personList = B_personList;
//         else if(i==2)
//        	 tmp_personList = C_personList;
         if(e.getDirection() == 0){   //case1:
            WT[i][n] = Math.abs(e.getCurrentFloor() - c)*elev_moving + Math.abs(c - d) * elev_moving + (elev_openclose*2);
            //userN.setWaiting_time(tmp_wt);
            e.setCases(0);//algorithm2
         }
         else if(e.getDirection() == 1){//case2: go up
            if(e.getCurrentFloor() <= c && c < d){//case2-1 : C < c < d < D DEST class must be made
               //tmp_TWT += e.getPesrons()*(elev_openclose*2) + tmp_wt;
               for(j = 0; j < T_personList.size(); j++){//not all 45, but only in persons in the list
                  p = T_personList.get(j);
                  if(p.getCurrentElev() == i && j != n){
                     if(p.getStartFloor() < c && p.getDestFloor() > c && p.getStartFloor() < d && p.getDestFloor() > d && p.getStartFloor() < p.getDestFloor()){
                    	 WT[i][j] += elev_openclose*2;
                     }
                     else if((p.getStartFloor() == c && p.getStartFloor() < d && p.getDestFloor() > d || p.getStartFloor() < c && p.getDestFloor() > c) && p.getStartFloor() < p.getDestFloor()){   
                    	 WT[i][j] += elev_openclose;
                       }
                  }//end if
               }//end for
               WT[i][n] = Math.abs(e.getCurrentFloor() - d)*elev_moving + elev_openclose*2;
               
               
               for(j = 0; j < 21; j++){
            	   if(e_destArr[j] < d && e_destArr[j]!=0){
                       WT[i][n] += elev_openclose;
            	   }//end if
               }//end for
               e.setCases(1);
            }//end if
            else if(e.getCurrentFloor() > c && c < d ){
               //case2-2: c < C < d = D
            	WT[i][n] += Math.abs(e.getCurrentFloor() - e_destArr[0]*elev_moving + elev_openclose);
            	
               p = T_personList.get(T_personList.size()-1); // nth person
               for(j = 0; j < 21; j++){
                  if(e_destArr[j] != 0 && e_destArr[j+1] != 0){
                     temp = Math.abs(e_destArr[j] - e_destArr[j+1])*elev_moving + elev_openclose;
                     userN.setWaiting_time(userN.getWaiting_time() + temp);
                  }
                  else if(e_destArr[j] != 0 && e_destArr[j+1] == 0){
                     temp = Math.abs(e_destArr[j] - c)*elev_moving + elev_openclose;
                     userN.setWaiting_time(userN.getWaiting_time() + temp);
                  }
               }//end for
               WT[i][n] += Math.abs(c - d)*elev_moving + elev_openclose;
               e.setCases(2);
               WT[i][n] = 9999;
            }
            else if(c > d){
               WT[i][n] += Math.abs(e.getCurrentFloor() - c) + Math.abs(c - d)*elev_moving +  elev_openclose*2;
               
               for(j = 0; j < 21; j++){
            	   if(e_destArr[j] < e_destArr[j+1] && e_destArr[j] != 0 && e_destArr[j+1] != 0)
            		   WT[i][j] += elev_openclose;
               }//end for
               for(j = 0; j < T_personList.size(); j++){
            	   p = T_personList.get(j);
            	   if(p.getCurrentElev() == i && j != n){
            		   if(p.getStartFloor() < c && p.getDestFloor() > c && p.getStartFloor() < d && p.getDestFloor() > d && p.getStartFloor() < p.getDestFloor())
            			   WT[i][j] += elev_openclose*2;
            		   else if((p.getStartFloor() == c && p.getStartFloor() < d && p.getDestFloor() > d || p.getStartFloor() < c && p.getDestFloor() > c) && p.getStartFloor() <p.getDestFloor())
            			   WT[i][j] += elev_openclose;
            	   }//end if
               }//end for
               e.setCases(3);
            }//end if 
         }//end direction == 1
         
         else if(e.getDirection() == -1){
            if(e.getCurrentFloor() >= c && c > d){
               for(j = 0; j < T_personList.size(); j++){
                  p = T_personList.get(j);
                  if(p.getCurrentElev() == i && j != n){
                     if(p.getStartFloor() > c && p.getDestFloor() < c && p.getStartFloor() > d && p.getDestFloor() < d){
                    	 WT[i][j] += elev_openclose*2;
                     }
                     else if(p.getStartFloor() == c && p.getStartFloor() > d && p.getDestFloor() < d || p.getStartFloor() > c && p.getDestFloor() < c){
                    	 WT[i][j] += elev_openclose;
                     }
                  }//endif
               }//end for
               WT[i][n] = Math.abs(e.getCurrentFloor() - d)*elev_moving + elev_openclose*2;
               
               for(j = 0; j < 21; j++){
            	   if(e_destArr[j] != 0){
            		   if(e_destArr[j] > d){
            			   WT[i][n] += elev_openclose;
            		   }
            	   }
               }//endfor
               e.setCases(-1);
            }//end if
            else if(e.getCurrentFloor() < c && c > d){
               WT[i][n] += Math.abs(e.getCurrentFloor() - e_destArr[0])*elev_moving + elev_openclose;
               
               for(j = 0; j < 21; j++){
                  if(e_destArr[j] != 0 && e_destArr[j+1] != 0){
                     temp = Math.abs(e_destArr[j] - e_destArr[j+1])*elev_moving + elev_openclose;
                     userN.setWaiting_time(userN.getWaiting_time() + temp);
                  }
                  else if(e_destArr[j] != 0 && e_destArr[j+1] == 0){
                     temp = Math.abs(e_destArr[j] - c)*elev_moving + elev_openclose;
                     userN.setWaiting_time(userN.getWaiting_time() + temp);
                  }
               }//end for
               WT[i][n] += Math.abs(c - d)*elev_moving + elev_openclose;
               e.setCases(-2);
               WT[i][n] = 9999;
            }//end if
            else if(c < d){
                WT[i][n] += Math.abs(e.getCurrentFloor() - c) + Math.abs(c - d)*elev_moving + elev_openclose*2;
                
                for(j = 0; j < 21; j++){
                	if(e_destArr[j] > e_destArr[j+1] && e_destArr[j] != 0 && e_destArr[j+1] != 0){
                		WT[i][n] += elev_openclose;
                	}
                }
                
                for(j = 0; j < T_personList.size(); j++){
                	p = T_personList.get(j);
                	if(p.getCurrentElev() == i && j != n){
                		if(p.getStartFloor() > c && p.getDestFloor() < c && p.getStartFloor() > d && p.getDestFloor() < d &&p.getStartFloor() > p.getDestFloor()){
                			WT[i][j] += elev_openclose*2;
                		}
                		else if((p.getStartFloor() == c && p.getStartFloor() > d && p.getDestFloor() < d || p.getStartFloor() > c && p.getDestFloor() < c ) &&p.getStartFloor() > p.getDestFloor()){
                			WT[i][j] += elev_openclose;
                		}//end if
                	}//end if
                }//end for
                e.setCases(-3);
            }//end if
         }//end direction == -1
//         for (j = 0; j < T_personList.size(); j++){
//         	 WT[i][j] = T_personList.get(j).getWaiting_time();// IS IT RIGHT????????????????
////         	 System.out.println("updated WT["+i+"]["+j+"]"+WT[i][j]);
//          }
      }//end for
      
      
      
      //before choose elevator set e_cnt array
      int[] e_cnt = new int[3];
      int tmp_twt = 0;
      
      //e_cnt is an array of each count of each elevator 
      for(i = 0; i < 3; i++){
         for(j = 0; j < T_personList.size(); j++){
            tmp_twt += WT[i][j];
         }
         TWT[i] += tmp_twt;//update TWT
         tmp_twt = 0;
         e_cnt[i] = eleList.get(i).e_cnt;
      }//end for
      
//      WT_D += ("\nA : " + TWT[0]);
//      WT_D += ("\nB : " + TWT[1]);
//      WT_D += ("\nC : " + TWT[2]);
//      WT_D += ("\nTotal : " + total_waiting_time);
//      System.out.println("A : "+TWT[0]);
//      System.out.println("B : "+TWT[1]);
//      System.out.println("C : "+TWT[2]);
//      System.out.println();
//      System.out.println("T : "+total_waiting_time);
//      //update T_W_T
//      T_W_T.set(i, tmp_twt);
//      tmp_twt = 0;
//      e_cnt[i] = eleList.get(i).getPesrons();
//
//      first_dest = eleList.get(i).getDestArray()[0];      
//      if (e_cnt[i] == 0){//what is e_cnt
//    	  eleList.get(i).setDirection(0);
//      }
//      else if(e_cnt[i] > 0){
//    	  if(eleList.get(i).getCurrentFloor() < first_dest){
//    		  eleList.get(i).setDirection(1);
//    	  }
//    	  else if(eleList.get(i).getCurrentFloor() > first_dest){
//    		  eleList.get(i).setDirection(-1);
//    	  }
//      }
//      for(j = 0; j < 3; j++){
//    	  System.out.println("update TWT : "+TWT[j]);
//      }
      int[] T = new int[3];
      T[0] = eleList.get(0).e_cnt+T_personList.get(n).weight;
      T[1] = eleList.get(1).e_cnt+T_personList.get(n).weight;
      T[2] = eleList.get(2).e_cnt+T_personList.get(n).weight;
      //take elevator A
      if( ( TWT[0] <= TWT[1] && TWT[0] <= TWT[2] ||  T[1] > 900 && TWT[0] <= TWT[2]  ||  T[2] > 900 && TWT[0] <= TWT[1] ||  T[1] > 900 && T[2] > 900) && T[0] < 900 ){
         total_waiting_time = TWT[0];
         T_personList.get(n).setCurrentElev(0);//decide to take A elevator( = 0)
         //personList.get(n).setColor(Color.RED); //GUI section
         dest_algorithm(n, 0);
         S_Elevator a = eleList.get(0);
         //a.setPersons(a.getPesrons() + 1); //e_cnt(0) += 1;//increment logical count of elevator A
         a.e_cnt+=T_personList.get(n).weight;
         for(i = 0; i < T_personList.size(); i++){//set person's waiting time
        	 p = T_personList.get(i);
        	 p.setWaiting_time(WT[0][i]);
         }
         A_personList.add(T_personList.get(n));
//         System.out.println("choose A");
         return 0;
      }
      //take elevator B
      if( (TWT[0] > TWT[1] && TWT[2] > TWT[1] || T[0] > 900 && TWT[1] <= TWT[2] || T[2] > 900 && TWT[0] > TWT[1] ||  T[0] > 900 && T[2] > 900) && T[1] < 900 ){
         total_waiting_time = TWT[1];
         T_personList.get(n).setCurrentElev(1);//decide to take A elevator( = 0)
         //personList.get(n).setColor(Color.BLUE); //GUI section
         dest_algorithm(n, 1);//?
         S_Elevator b = eleList.get(1);
         //b.setPersons(b.getPesrons() + 1); //e_cnt(1) += 1;//increment logical count of elevator B
         b.e_cnt+=T_personList.get(n).weight;
         for(i = 0; i < T_personList.size(); i++){//set person's waiting time
        	 p = T_personList.get(i);
        	 p.setWaiting_time(WT[1][i]);
         }
         B_personList.add(T_personList.get(n));
//         System.out.println("choose B");
         return 1;
         
      }
      //take elevator C
      if( ((TWT[2] <TWT[0]) && (TWT[1] > TWT[2]) || ( T[0] > 900 && TWT[2] < TWT[1] ) || ( T[1] > 900 && TWT[2] < TWT[1]) || ( T[0] > 900 && T[1] > 900)) && T[2] < 900 ){
         total_waiting_time = TWT[2];
         T_personList.get(n).setCurrentElev(2);//decide to take A elevator( = 0)
         //personList.get(n).setColor(Color.GREEN); //GUI section
         dest_algorithm(n, 2);//?
         S_Elevator cc = eleList.get(2);
         //cc.setPersons(cc.getPesrons() + 1); //e_cnt(0) += 1;//increment logical count of elevator C
         cc.e_cnt+=T_personList.get(n).weight;
         for(i = 0; i < T_personList.size(); i++){//set person's waiting time
        	 p = T_personList.get(i);
        	 p.setWaiting_time(WT[2][i]);
         }
         C_personList.add(T_personList.get(n));
//         System.out.println("choose C");
         return 2;
      }
      
     
      return -1; // returns the number(or char) of Elevator, which the user will get on
   }
   
   //distribute elevators
//   void distribute(){
//   
////      int[] tmpArr = eleList.get(0).getDestArray();
////      tmpArr[0] = 1;
////      eleList.get(0).setDestArray(tmpArr, 1);
////      
////      tmpArr = eleList.get(1).getDestArray();
////      tmpArr[0] = 6;
////      eleList.get(1).setDestArray(tmpArr, 1);
////      
////      tmpArr = eleList.get(2).getDestArray();
////      tmpArr[0] = 12;
////      eleList.get(2).setDestArray(tmpArr, 1);
//      eleList.get(0).dest[0] = 1;
//      eleList.get(1).dest[0] = 6;
//      eleList.get(2).dest[0] = 12;
//   }
   
   
   //initialize floor, elevator
   void initialize(){
   
      
      /*empty personList*/
     // personList.clear();
      
      
//      /*create floor and initialize each floor info*/
//      for(int i = 0; i < 12; i++){
//         Floor f = new Floor(i+1);
//         f.setFloor(i+1);//set this floor number(1~12)
//         f.setFcnt(0);//set number of users awaiting on this floor to 0
//         
//         //[j][k] is 2 dimensional array for each floor
//         for(int j = 0; j < 3; j++){//j is elev(0~2 : A B C)
//            for(int k = 0; k < 15; k++){//kth user awaits in order on this floor
//               f.setWU(j,k,0);//set sequence of waiting user on the floor
//               f.setOut(j, k, 0);//set sequence of getting off user on the floor
//            }
//         }
//         floorList.add(f);
//      }
      
      /*create s_elevator and initialize some attributes*/
      for(int i = 0; i < 3; i++){
         S_Elevator e = new S_Elevator();//create one elevator
         //confusing e_cnt vs persons
         e.setEcnt(0);//set e_cnt, which indicates the number of users allocated on this elevator
         e.setPersons(0);//set the number of people on the elevator to zero
         e.elev_index = i;

//WHAT IS THESE
//          Person(i).Text = "0"   //person(i)�뒗 �깋源붾퀎�궗�엺 
//          State(i).Text = "STOP"   //?
//         Direction.Text = ""//?         
         
         int [] destArr = new int[22];//create temp array
         destArr = e.getDestArray(); //get destination ArrayList(elevetor.class)
         
         //set all destinations to 0
         for(int i2 = 0; i2 < 22; i2++){
            destArr[i2] = 0;
         }
         
         //set users orderly taken to 0
         int[] e_take = e.getTakeArr();//elevator.class
         for(int i2 = 0; i2 < 15; i2++){
            e_take[i2] = 0;
         }
         
         e.setTakeArr(e_take);
         eleList.add(e);//add on the elevator list
      }
//WHAT ARE THESE      
//      p = 1;
//      message.text = '-'
//      current floor.text = 1;
      Controller.total_waiting_time = 0;
      
      for(int i = 0; i < 3; i++){
    	  TWT[i] = 0;
      }
      this.choose = 0;
      
      
//end of initialization
      
      eleList.get(0).y=447;
      eleList.get(1).y=247;
      eleList.get(2).y=7;
//      /* distribution algorithm*/
//      for(int i = 0; i < 3; i++){
//    	  eleList.get(i).distribute();
//      }
   }

}