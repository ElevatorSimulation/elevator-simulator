

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;


public class Floor extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int f_cnt; //number of users who awaits on this floor
	private int floor;
	private Person p;
	private S_Elevator E_tmp;
	public ArrayList<Person> A_INpersonList = new ArrayList<Person>();
	public ArrayList<Person> B_INpersonList = new ArrayList<Person>();
	public ArrayList<Person> C_INpersonList = new ArrayList<Person>();
	public ArrayList<Integer> A_INuserX = new ArrayList<Integer>();
	public ArrayList<Integer> B_INuserX = new ArrayList<Integer>();
	public ArrayList<Integer> C_INuserX = new ArrayList<Integer>();
	public ArrayList<Person> OUTpersonList = new ArrayList<Person>();
	public ArrayList<Integer> OUTuserX = new ArrayList<Integer>();
	
	Timer tm1 = new Timer();
	TimerTask tsk1 = new TimerTask(){
		public void run(){
			
			for(int i=0;i<A_INpersonList.size();i++){
				if(A_INpersonList.get(i)!=null && A_INuserX.get(i)<79){
					if(i>=1 && A_INuserX.get(i-1)-17>A_INuserX.get(i))
						A_INuserX.set(i, A_INuserX.get(i)+1);
					else if(i==0)
						A_INuserX.set(i, A_INuserX.get(i)+1);
				}
			}
			for(int i=0;i<B_INpersonList.size();i++){
				if(B_INpersonList.get(i)!=null && B_INuserX.get(i)<79){
					if(i>=1 && B_INuserX.get(i-1)-17>B_INuserX.get(i))
						B_INuserX.set(i, B_INuserX.get(i)+1);
					else if(i==0)
						B_INuserX.set(i, B_INuserX.get(i)+1);
				}
			}
			for(int i=0;i<C_INpersonList.size();i++){
				if(C_INpersonList.get(i)!=null && C_INuserX.get(i)<79){
					if(i>=1 && C_INuserX.get(i-1)-17>C_INuserX.get(i))
						C_INuserX.set(i, C_INuserX.get(i)+1);
					else if(i==0)
						C_INuserX.set(i, C_INuserX.get(i)+1);
				}
			}
			for(int i=0;i<OUTpersonList.size();i++){
				if(OUTpersonList.get(i)!=null && OUTuserX.get(i)>-16){
					if(i>=1 && OUTuserX.get(i-1)+17<OUTuserX.get(i))
						OUTuserX.set(i, OUTuserX.get(i)-1);
					else if(i==0)
						OUTuserX.set(i, OUTuserX.get(i)-1);
				}
			}
			
			repaint();
		}
	};
	

	public Floor(int floor){
		this.f_cnt=0;
		this.floor = floor; //set the floor number when this floor class is created
		Timer tm = new Timer();
		TimerTask tsk = new TimerTask(){
			@SuppressWarnings("deprecation")
			public void run(){
				if(!OUTpersonList.isEmpty() && OUTpersonList.get(OUTpersonList.size()-1).finish==true){
//				System.out.println("HHH");
				OUTpersonList.get(OUTpersonList.size()-1).finish=false;
				
				add(OUTpersonList.get(OUTpersonList.size()-1));
				OUTpersonList.get(OUTpersonList.size()-1).show();
            }
			}
		};
		tm1.schedule(tsk1, 0, 20);
		tm.schedule(tsk, 0, 5);
	}
	
	@SuppressWarnings("deprecation")
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, 100, 30);
		g.setColor(Color.BLACK);
		g.setFont(new Font("±¼¸²",Font.PLAIN,11));
		g.drawString(Integer.toString(floor), 22, 9);
		for(int i = 0;i<A_INpersonList.size();i++){
			if(!A_INpersonList.isEmpty()){
				A_INpersonList.get(i).setBounds(A_INuserX.get(i),13,16,16);
				if(A_INuserX.get(i)==79){
					A_INpersonList.get(i).arrive = true;
					E_tmp = Controller.eleList.get(A_INpersonList.get(0).current_elev);
					if(A_INpersonList.get(i).start_floor == E_tmp.current_floor && E_tmp.OPEN==true && E_tmp.door == 2){
						A_INpersonList.get(i).hide();
						Controller.eleList.get(A_INpersonList.get(0).current_elev).personList.add(A_INpersonList.get(0));
						A_INuserX.remove(0);
						A_INpersonList.remove(0);
						//Controller.eleList.get(0).time=0;
					}
				}
			}
		}
		
		for(int i = 0;i<B_INpersonList.size();i++){
			if(!B_INpersonList.isEmpty()){
				B_INpersonList.get(i).setBounds(B_INuserX.get(i),13,16,16);
				if(B_INuserX.get(i)==79){
					B_INpersonList.get(i).arrive = true;
					E_tmp = Controller.eleList.get(B_INpersonList.get(0).current_elev);
					if(B_INpersonList.get(i).start_floor == E_tmp.current_floor && E_tmp.OPEN==true && E_tmp.door == 2){
						B_INpersonList.get(i).hide();
						Controller.eleList.get(B_INpersonList.get(0).current_elev).personList.add(B_INpersonList.get(0));
						B_INuserX.remove(0);
						B_INpersonList.remove(0);
						//Controller.eleList.get(1).time=0;
					}
				}
			}
		}
		
		for(int i = 0;i<C_INpersonList.size();i++){
			if(!C_INpersonList.isEmpty()){
				C_INpersonList.get(i).setBounds(C_INuserX.get(i),13,16,16);
				if(C_INuserX.get(i)==79){
					C_INpersonList.get(i).arrive = true;
					E_tmp = Controller.eleList.get(C_INpersonList.get(0).current_elev);
					if(C_INpersonList.get(i).start_floor == E_tmp.current_floor && E_tmp.OPEN==true && E_tmp.door == 2){
						C_INpersonList.get(i).hide();
						Controller.eleList.get(C_INpersonList.get(0).current_elev).personList.add(C_INpersonList.get(0));
						C_INuserX.remove(0);
						C_INpersonList.remove(0);
						//Controller.eleList.get(2).time=0;
					}
				}
			}
		}
		
		for(int i = 0;i<OUTpersonList.size();i++){
			if(!OUTpersonList.isEmpty() && !OUTuserX.isEmpty()){
				OUTpersonList.get(i).setBounds(OUTuserX.get(i),13,16,16);
				if(OUTuserX.get(i)==-16){
					OUTpersonList.get(i).hide();
					OUTpersonList.remove(0);
					OUTuserX.remove(0);
				}
			}
		}
		
	}
	
	
	
	public void setNewUser(Person P){
		this.p = P;
		this.p.setBounds(-16,13,16,16);
		this.add(p);
		if(p.current_elev==0){
			A_INpersonList.add(p);
			A_INuserX.add(-16);
		}
		else if(p.current_elev==1){
			B_INpersonList.add(p);
			B_INuserX.add(-16);
		}
		else if(p.current_elev==2){
			C_INpersonList.add(p);
			C_INuserX.add(-16);
		}
		this.f_cnt++;
	}
	
	public void setFloor(int f){
		this.floor = f;
	}
	
	public int getFloor(){
		return this.floor;
	}
	
	public void setFcnt(int fcnt){
		this.f_cnt = fcnt;
	}
	
	public int getFcnt(){
		return this.f_cnt;
	}

		
}