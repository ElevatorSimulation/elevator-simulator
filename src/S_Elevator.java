


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import javax.swing.Timer;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class S_Elevator extends JPanel implements  Runnable{
   
   
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
ArrayList<JButton> jbArr;   //button action connection
   public int elev_index;
   public int y = 447, velX = 1;   // y is an initial height of every elevator and velX is move speed
   public int door = 48;   //initial position of a door
   private int doorX = -1;   //door open and close direction(-1 or 1)
   private int door2 = 55;
   public int time= 0;
   private int distribute_timer = 0;
   private int i=0;
   //from algorithm
   public int current_floor; // current floor of this elevator
   public int direction;   //state of this elevator(1 :up, 0: stop, -1: down)
   private int persons;   //number of people on this elevator
   private int t_w_t; //total waiting time
   public int[] dest = new int[22];
      
   //from second algorithm
   public boolean UP;
   public boolean DOWN;
   public boolean OPEN;
   public boolean CLOSE;
   
   Door d1 = new Door(1);
   Door d2 = new Door(2);
   
   public ArrayList<Person> personList = new ArrayList<Person>();
   private int[] take = new int[15]; //users which is ordered by time getting on
   public int cases; //some cases
   public int e_cnt; //number of users who is allocated an elevator
   private JTextField capacity;
   private JTextField state;
   String display_floor = "";
   JLabel warning1 = new JLabel();
   JLabel warning2 = new JLabel();
   Timer tm = new Timer();
   Timer always = new Timer();
   TimerTask tsk = new TimerTask(){//always 
      @SuppressWarnings("deprecation")
	public void run(){
         //System.out.println("hi " + UP);
    	  
    	  
         if(UP == true && DOWN == false){
            if(y > 7){
               y--;
            }
            else
               UP = false;
         }
         if(DOWN == true && UP == false){
            if(y < 447)
               y++;
            else
               DOWN = false;
         }
         
         if(OPEN == true && door > 2){//arrive
            door = door + doorX;
            door2 = door2 - doorX;
            d1.door=door;
      	  	d2.door=door;
         }
         else if(OPEN == true && door == 2){
            time++;
            boolean tmp=false;
            if(elev_index==0)
            	tmp=Controller.floorList.get(current_floor-1).A_INpersonList.isEmpty();
            else if(elev_index==1)
            	tmp=Controller.floorList.get(current_floor-1).B_INpersonList.isEmpty();
            else if(elev_index==2)
            	tmp=Controller.floorList.get(current_floor-1).C_INpersonList.isEmpty();
            
            if(time>150 && tmp==true){
               OPEN = false;
               CLOSE = true;
               time = 0;
            }
            if(!personList.isEmpty() && personList.get(personList.size()-1).arrive==true){
               personList.get(personList.size()-1).arrive=false;
               personList.get(personList.size()-1).taked = true;
               add(personList.get(personList.size()-1));               
               personList.get(personList.size()-1).show();
               Controller.floorList.get(current_floor-1).f_cnt--;
               persons+= personList.get(personList.size()-1).weight;
            }
            if(personList.size()>0){
               i++;
               i=i%personList.size();
            }
               if(!personList.isEmpty() && personList.get(i).dest_floor==current_floor && personList.get(i).taked==true){
            	  personList.get(i).hide();
                  persons-=personList.get(i).weight;
                  e_cnt-=personList.get(i).weight;
                  personList.get(i).finish = true;
                  personList.get(i).taked=false;
                  personList.get(i).waiting_time=0;
                  Controller.floorList.get(current_floor-1).OUTpersonList.add(personList.get(i));
                  Controller.floorList.get(current_floor-1).OUTuserX.add(79);
                  personList.remove(i);
                  
               }  
         }
         
         //close door
         if(CLOSE == true && door < 48){
            door = door - doorX;
            door2 = door2 + doorX;
            d1.door=door;
      	  	d2.door=door;
//            d1.door=door;
//            d2.door=door;
//            d1.door2=door2;
//            d2.door2=door2;
         }
         else if(CLOSE == true && door == 48){
            CLOSE = false;
//            d1.CLOSE=false;
//            d2.CLOSE=false;
         }
         repaint();
      }
   };
   
   TimerTask alwaysTask = new TimerTask(){
      public void run(){
//         for(int i = 0; i<21; i++){
//            System.out.println("dest : "+dest[i]);
//         }
    	  //always get direction
//         
         int i;
         if(dest[0] == 0 && dest[1] != 0){//next dest still remains
            for(i = 0; i < 21; i++){
               dest[i] = dest[i+1];
            }
            dest[21] = 0;
         }
         
         if(current_floor < dest[0] && dest[0] != 0 && OPEN == false && CLOSE == false){
            UP = true;
         }
         else if(current_floor > dest[0] && dest[0] != 0 && OPEN == false && CLOSE == false){
            DOWN = true;
         }
         //if(elev_index==0) System.out.println(current_floor + "---"+dest[0] + "---" + dest[1]);
         if(current_floor == dest[0] && dest[0] != 0 && Controller.floorList.isEmpty()==false && Controller.floorList.get(current_floor-1).f_cnt==0 && e_cnt==0){//stop on the dest floor
        	 //door close
        	 
            UP = false;
           DOWN = false;
           dest[0] = 0;
         }
         else if(current_floor == dest[0] && dest[0] != 0){//stop on the dest floor
            
            OPEN = true;
//            d1.OPEN=true;
//            d2.OPEN=true;
             UP = false;
             DOWN = false;
             dest[0] = 0;
          }
         
         if (e_cnt== 0)
               direction = 0;
         else if (e_cnt > 0 && dest[0] != 0){
              if (current_floor < dest[0])
                     direction = 1;
              else if (current_floor > dest[0])
                     direction = -1;
         }
         
         
         if(Controller.floorList.get(current_floor-1).f_cnt == 0 && e_cnt ==0 && CLOSE == false && OPEN == false && direction==0){
        	 distribute_timer++;
             if(distribute_timer == 300){
          	  // System.out.println("distribute");
          	   distribute_timer = 0;
          	   distribute();
             }

         }
      }//end run
   };
   

   

   public S_Elevator(){
	  
      this.t_w_t = 0;
      this.persons = 0;
      this. direction = 0;
      this.current_floor = 1;
      this.door=48;
      setOpaque(false);   //this prevents GUI error
      Image warn1 = null;
      Image warn2 = null;
      try{
         File image = new File("donot1.jpg");
         warn1 = ImageIO.read(image);
         image = new File("donot2.jpg");
         warn2 = ImageIO.read(image);
      }
      catch(IOException e){
      }
      warning1 = new JLabel(new ImageIcon(warn1));
      warning2 = new JLabel(new ImageIcon(warn2));
      capacity = new JTextField();
      state = new JTextField(" ");
      capacity.setHorizontalAlignment((int) CENTER_ALIGNMENT);
      state.setHorizontalAlignment((int) CENTER_ALIGNMENT);
     
      this.add(state);
      this.add(capacity);
        this.add(warning1);
        this.add(warning2);
        
      tm.schedule(tsk, 0, 20);
      always.schedule(alwaysTask, 0, 20);
      //waiting.schedule(wait, 3000);
      //capacity.move
      this.add(d1);
		this.add(d2);
		d1.door=door;
  	  d2.door=door;
   }//default constructor   
   //constructor
   
   
   public void paintComponent(Graphics g){
      //super? 
      super.paintComponent(g);

    //draw elevator background
    		g.setColor(Color.LIGHT_GRAY);
    		g.fillRect(0, 0, 110, 629);
    		g.setColor(Color.DARK_GRAY);
    		g.drawRect(0, 0, 110-1, 629-1);
    		g.setColor(Color.WHITE);

    		g.setColor(Color.WHITE);
    		g.fillRect(5, 5, 100, 562);
    		g.setColor(Color.DARK_GRAY);
    		g.drawRect(5, 5, 100-1, 562-1);

    		g.drawRect(54, 5, 1, y+5);

    		//draw a box of an elevator   
    		for(int i = 0;i<personList.size();i++){
    			if(!personList.isEmpty() && i>=0 && i<=4)
    				personList.get(i).setBounds(11+18*(i),y+90,16,16);
    			else if(!personList.isEmpty() && i>=5 && i<=9)
    				personList.get(i).setBounds(11+18*(i-5),y+72,16,16);
    			else if(!personList.isEmpty() && i>=10 && i<=14)
    				personList.get(i).setBounds(11+18*(i-10),y+54,16,16);

    		}
    		//if this elevator is moving, on == 1 and update y values
    		if(OPEN == false && CLOSE == false){
    			d1.setBounds(7, y+12, 47, 100);
    			g.setColor(Color.LIGHT_GRAY);
    			//g.fillRect(7, y+12, 48, 100);
    			g.setColor(Color.DARK_GRAY);
    			g.drawRect(7, y+12, 48-1, 100-1);
    			d2.setBounds(55, y+12, 47, 100);

    			g.setColor(Color.LIGHT_GRAY);
    			//g.fillRect(55, y+12, 48, 100);
    			g.setColor(Color.DARK_GRAY);
    			g.drawRect(55, y+12, 48-1, 100-1);
    			if(door==48){
    				d1.setBounds(7, y+12, 48, 100);
    				d2.setBounds(54,y+12,48,100);
    			}
    			


    			g.setColor(Color.DARK_GRAY);
    			g.drawRect(31, y+19, 22-1, 22-1);
    			g.setColor(Color.DARK_GRAY);
    			g.drawRect(57, y+19, 22-1, 22-1);
    			warning1.setBounds(32, y+20, 20, 20);
    			warning2.setBounds(58, y+20, 20, 20);
    		}
    		else if(OPEN == true || CLOSE == true){//else this elevator stops, on == 0 and update x door values
    			d1.setBounds(7, y+12, door-1, 100);
    			g.setColor(Color.LIGHT_GRAY);
    			//g.fillRect(7, y+12, door, 100);
    			g.setColor(Color.DARK_GRAY);
    			g.drawRect(7, y+12, door-1, 100-1);
    			d2.setBounds(door2, y+12, door-1, 100);
    			
    			
    			g.setColor(Color.LIGHT_GRAY);
    			//g.fillRect(door2, y+12, door, 100);
    			g.setColor(Color.DARK_GRAY);
    			g.drawRect(door2, y+12, door-1, 100-1);
    			g.setColor(Color.DARK_GRAY);
    			if(door>=24){
    				g.drawRect(door-17, y+19, 22-1, 22-1);
    				warning1.setBounds(door-16, y+20, 20, 20);

    				g.drawRect(door2+2, y+19, 22-1, 22-1);
    				warning2.setBounds(door2+3, y+20, 20, 20);
    			}
    			else{
    				warning1.setBounds(8, y+20, door-4, 20);
    				g.drawRect(7, y+19, door-3, 22-1);

    				warning2.setBounds(door2+3, y+20, door-4, 20);
    				g.drawRect(door2+2, y+19, door-3, 22-1);
    			}
    		}
     
     
      
      g.setColor(Color.BLACK); // screen
      g.fillRect(7, y, 96, 18);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(7, y, 96-1, 18-1);
//      g.drawRect(7+1, y+1, 96-2, 18-2);
      
      g.setColor(Color.LIGHT_GRAY); // floor
      g.fillRect(7, y+108, 96, 10);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(7, y+108, 96-1, 10-1);
      
      //elevator floor number
      
      if(y == 447 ){
         current_floor = 1;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 407){
         current_floor = 2;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 367){
         current_floor = 3;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 327){
         current_floor = 4;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 287){
         current_floor = 5;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 247){
         current_floor = 6;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 207){
         current_floor = 7;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 167){
         current_floor = 8;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 127){
         current_floor = 9;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 87){
         current_floor = 10;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 47){
         current_floor = 11;
         display_floor = Integer.toString(current_floor);
      }
      else if(y == 7){
         current_floor = 12;
         display_floor = Integer.toString(current_floor);
      }
      
      if (UP == true && DOWN == false){
         g.setFont(new Font("굴림",Font.BOLD,14));
         g.setColor(Color.RED);
         g.drawString("▲", 82, y+15);
         
      }
      else if(DOWN == true && UP == false){
         g.setFont(new Font("굴림",Font.BOLD,14));
         g.setColor(Color.RED);
         g.drawString("▼", 82, y+13);
      }
      
      g.setColor(Color.RED);
      g.setFont(Controller.ttfReal);
      if(current_floor<10)
         g.drawString(display_floor, 51, y+15);//depending on this.currentfloor 
      else if(current_floor>=10)
         g.drawString(display_floor, 48, y+15);//depending on this.currentfloor 

      //draw the below of this elevator
      g.setColor(Color.WHITE); 
      g.fillRect(2, 573, 106, 54);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(2, 573, 106-1, 54-1);
      
      g.setColor(Color.BLACK);
      g.drawString("W : ", 5, 595);
      g.drawString("S : ", 5, 616);
      
      capacity.setFont(Controller.ttfReal);
      capacity.setText(Integer.toString(persons));
      capacity.setBounds(27, 578, 36, 22);
      
      
      g.setColor(Color.BLACK);
      g.drawString("/900", 65, 595);
      
      state.setFont(Controller.ttfReal);
      state.setText("DOWN");
      state.setBounds(27, 601, 77, 22);
      if(UP==true && DOWN==false)
         state.setText("UP");
      else if(UP==false && DOWN==true)
         state.setText("DOWN");
      else if(UP==false && DOWN==false)
         state.setText("STOP");
   }   
   public void run(){//one problem. after restarting, the elevator go back to the first floor and restarts

   }//run is executed once and actionPerformed every times

   
   public void distribute(){
	   if(this.elev_index == 0)
		   this.dest[0] = 1;
	   else
		   this.dest[0] = elev_index*6;
   }
   
   

   

   //get method
   public int getDirection(){
      return this.direction;
   }

   //set method
   public void setDirection(int direction){
      this.direction = direction;
   }


   //get method
   public int getT_W_T(){
      return this.t_w_t;
   }

   //set method
   public void setT_W_T(int twt){
      this.t_w_t = twt;
   }
   
   //get method
   public int getCurrentFloor(){
      return this.current_floor;
   }

   //set method
   public void setCurrentFloor(int c){
      this.current_floor = c;
   }

   //get method
   public int getPesrons(){
      return this.persons;
   }

   //set method
   public void setPersons(int persons){
      this.persons = persons;
   }

   //get method
   public int[] getDestArray(){
      return this.dest.clone();//array copy
   }

   //set method
   public void setDestArray(int[] destArr, int destCnt){
      this.dest = destArr.clone();//is this Array copy right? 
//      System.out.println("destCnt : "+destCnt);
      
      for(int i = 0; i<21; i++){
//         System.out.println("dest : "+dest[i]);
      }

   }
   
   //get method
   public int getEcnt(){
      return this.e_cnt;
   }

   //set method
   public void setEcnt(int ecnt){
      this.e_cnt = ecnt; 
   }
   
   //get method
   public int[] getTakeArr(){
      return this.take.clone();
   }

   //set method
   public void setTakeArr(int[] take){
      this.take = take.clone(); 
   }

   //get method
   public int getCases(){
      return this.cases;
   }

   //set method
   public void setCases(int cases){
      this.cases = cases; 
   }
   
   public void setUP(int cases){
      this.cases = cases; 
   }
      
}