import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Door extends JPanel{//Door class
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int index;
	public int door;
	
	public Door(int a){
		this.setBackground(Color.LIGHT_GRAY);
		index = a;
		repaint();
	}
	public void paintComponent(Graphics g){
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(1, 6, 50, 90);
		g.setColor(Color.DARK_GRAY);
		if(door==48 && index==1)
			g.drawRect(47, 6, 1, 90);
		if(door==48 && index==2)
			g.drawRect(0, 6, 1, 90);
		if(door>=24){
			if(door==48){
				if(index==1)
					g.drawRect(door-24, 7, 22-1, 22-1);
				else if(index==2)
					g.drawRect(3, 7, 22-1, 22-1);
			}
			else{
				if(index==1)
					g.drawRect(door-24, 7, 22-1, 22-1);
				else if(index==2)
					g.drawRect(2, 7, 22-1, 22-1);
			}
				
		}
		else{
			if(index==1)
				g.drawRect(0, 7, door-3, 22-1);
			else if(index==2)
				g.drawRect(2, 7, door-3, 22-1);
		}

	}
}