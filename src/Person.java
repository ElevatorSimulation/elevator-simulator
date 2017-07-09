

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;


public class Person extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//from algorithm
	public int start_floor;	//from start floor
	public int dest_floor;		//to dest floor
	public int waiting_time;	//expected waiting time of elevator
	public int current_elev;	//allocated elevator to user
	public int weight;
	public boolean taked;
	public boolean finish = false;
	public boolean arrive = false;
	//when clicked, initialize all to 0
	
	
	String lName;
	
	public Person(){
		
	}
	public Person(String s){
		
	}
	public Person(int start, int dest){
		this.start_floor = start;
		this.dest_floor = dest;
		this.waiting_time = 0;
		this.current_elev = 0;
		this.weight = (int)(Math.random()*10)+55; // change!!
		this.taked = false;
	}
	public void paintComponent(Graphics g){ // Draw Registration form
		super.paintComponent(g);
		Graphics2D G = (Graphics2D)g;
		G.setColor(Color.BLACK);
		G.drawRect(0, 0, 15, 15);
		if(current_elev==0){
			G.setColor(Color.RED);
			G.fillRect(0, 0, 16, 16);
		}
		else if(current_elev==1){
			G.setColor(Color.BLUE);
			G.fillRect(0, 0, 16, 16);
		}
		else if(current_elev==2){
			G.setColor(Color.GREEN);
			G.fillRect(0, 0, 16, 16);
		}
		
		G.setColor(Color.BLACK);
		G.drawRect(0, 0, 15, 15);
		
		G.setColor(Color.WHITE);
//		if(current_elev==0)
//			G.setColor(Color.RED);
//		else if(current_elev==1)
//			G.setColor(Color.BLUE);
//		else if(current_elev==2)
//			G.setColor(Color.GREEN);
		
		G.setFont(new Font("±¼¸²",Font.PLAIN,11));
		if(dest_floor < 10)
			G.drawString(Integer.toString(dest_floor), 5, 12);
		else if(dest_floor >= 10)
			G.drawString(Integer.toString(dest_floor), 2, 12);
	}
	
	
	//get method
	public int getWaiting_time(){
		return waiting_time;
	}
	//set method
	public void setWaiting_time(int wt){
		this.waiting_time = wt;
	}
	
	//get method
	public int getCurrentElev(){
		return this.current_elev;
	}

	//set method
	public void setCurrentElev(int ce){
		this.current_elev = ce;
	}
	
	//get method
	public boolean getTaked(){
		return this.taked;
	}

	//set method
	public void setTaked(boolean t){
		this.taked = t;
	}
	
	//get method
	public int getStartFloor(){
		return this.start_floor;
	}

	//set method
	public void setStartFloor(int start){
		this.start_floor = start;
	}
	//get method
	public int getDestFloor(){
		return this.dest_floor;
	}

	//set method
	public void setDestFloor(int dest){
		this.dest_floor = dest;
	}
	
}
