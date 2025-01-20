import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends JFrame implements ActionListener,MouseListener
{
	JToggleButton[][] board;
	JPanel boardPanel;
	boolean firstClick;
	//*****************
	ImageIcon mineIconn, flag;
	ImageIcon[] numbers;

	//*****************************
	int numMines;
	int emptyBoxCount;
	Font font=new Font("Arial",Font.PLAIN,9);
	boolean gameOver=false;
	public Minesweeper()
	{
		mineIconn = new ImageIcon("Minesweeper Images/mine0.png");
		mineIconn= new ImageIcon(mineIconn.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH));

		flag = new ImageIcon("Minesweeper Images/flag.png");
		flag= new ImageIcon(flag.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH));

		numbers=new ImageIcon[8];
		for(int i = 0; i < 8; i++){

			numbers[i] = new ImageIcon("Minesweeper Images/"+(i+1)+".png");
			numbers[i] = new ImageIcon(numbers[i].getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH));
		}


		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		}catch(Exception e){

		}
		//default rgb that dentler put: 255,128,130, mycolor1: 80,150,200, my col2: 60,120,255, my col3: 244,187,68, my colo4: 243,229,171
		UIManager.put("ToggleButton.select",new Color(243,229,171)); //changes color of clicked box

		createBoard(9,9);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}


	public void createBoard(int row,int col)
	{
		if(boardPanel!=null)
			this.remove(boardPanel);

		board=new JToggleButton[row][col];
		firstClick=true;
		numMines=10;
		//+++++++++++++++++++++++++++++
		emptyBoxCount=row*col-numMines;
		//+++++++++++++++++++++++++++++
		boardPanel=new JPanel();
		boardPanel.setLayout(new GridLayout(row,col));

		//make a bunch of JToggleButtons and store them in your array!
		for(int r=0;r<row;r++)
		{
			for(int c=0;c<col;c++)
			{
				board[r][c]=new JToggleButton();
				board[r][c].putClientProperty("row",r);
				board[r][c].putClientProperty("col",c);
				board[r][c].putClientProperty("state",0);
				board[r][c].setMargin(new Insets(0,0,0,0));
				//++++++++++++++
				board[r][c].setFocusable(false);
				//++++++++++++++
				board[r][c].addMouseListener(this);
				boardPanel.add(board[r][c]);
			}
		}
		this.add(boardPanel,BorderLayout.CENTER);

		this.setSize(col*40,row*40);
		this.revalidate();
	}



	public void actionPerformed(ActionEvent e)
	{

	}
	public void setMinesAndCounts(int row,int col)
	{
		//you know what to do....
		int count=0;
		while(count<numMines)
		{
			int randR=(int)(Math.random()*board.length);
			int randC=(int)(Math.random()*board[0].length);
			int state=(int)(board[randR][randC].getClientProperty("state"));
			if(state!=10 && Math.abs(randR-row)>1 && Math.abs(randC-col)>1)
			{
				board[randR][randC].putClientProperty("state",10);
				for(int r=randR-1;r<=randR+1;r++)
				{
					for(int c=randC-1;c<=randC+1;c++)
					{
						try
						{
							state=(int)(board[r][c].getClientProperty("state"));
							if(state!=10)
							{
								board[r][c].putClientProperty("state",state+1);

							}
						}
						catch(ArrayIndexOutOfBoundsException e){}
					}
				}


				count++;
			}
		}
		/*for(int r=0;r<board.length;r++)
		{
			for(int c=0;c<board[0].length;c++)
			{
				String st=""+board[r][c].getClientProperty("state");
				board[r][c].setText(st);
			}
		}
*/

	}

	public void mouseReleased(MouseEvent e)
	{
		int row=(int)(((JToggleButton)e.getComponent()).getClientProperty("row"));
		int col=(int)(((JToggleButton)e.getComponent()).getClientProperty("col"));
		if(!gameOver)
		{
			if(e.getButton()==MouseEvent.BUTTON1 && board[row][col].isEnabled())
			{
				if(firstClick)
				{
					setMinesAndCounts(row,col);
					firstClick=false;
				}
				int state=(int)(board[row][col].getClientProperty("state"));
				if(state==10)
				{
					gameOver=false;
					JOptionPane.showMessageDialog(null,"You are a loser!");
					//show all the mines!
					showMines();
				}
				else
				{
					//magic happens
					board[row][col].setSelected(true);
					emptyBoxCount--;
					expand(row,col);
					checkWin();

				}
			}
		}

	}
	public void showMines(){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length;j++){

				board[i][j].setEnabled(false);
				int state=(int)(board[i][j].getClientProperty("state"));
				if(state==10)
				{
					board[i][j].setIcon(mineIconn);
					board[i][j].setDisabledIcon(mineIconn);
				}
			}
		}
	}
	public void checkWin()
	{
		if(emptyBoxCount==0)
		{
			JOptionPane.showMessageDialog(null,"You are a winner!");
		}
	}

	public void expand(int row,int col)
	{
		if(!board[row][col].isSelected())
		{
			board[row][col].setSelected(true);
			emptyBoxCount--;
		}

		int state=(int)(board[row][col].getClientProperty("state"));
		if(state!=0)
		{
			//board[row][col].setText(""+state);
			//set the image for the button
			board[row][col].setIcon(numbers[state-1]);
			board[row][col].setDisabledIcon(numbers[state-1]); // reduces the color of numbers going grayscale
		}
		else
		{
			for(int r=row-1;r<=row+1;r++)
			{
				for(int c=col-1;c<=col+1;c++)
				{
					try
					{
						if(!board[r][c].isSelected())
							expand(r,c);
					}catch(ArrayIndexOutOfBoundsException e){}
				}
			}


		}


	}

	public void mousePressed(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}



	public static void main(String[] args)
	{
		Minesweeper app=new Minesweeper();
	}
}