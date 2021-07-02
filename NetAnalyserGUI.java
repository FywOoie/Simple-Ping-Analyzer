import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;

/**
 *		Title			     : NetAnalyserGUI.java
 *		Description  : A net analyser to know the information of ping between two websites
 *
 *		@author		Yibo Lian
 *		@version 	3.0
 */

public class NetAnalyserGUI extends JFrame implements ItemListener, MouseListener{
	//Create a list of components on GUI
	JLabel text1, text2, text3, text5, text6, text7, text8;
	TextArea text4;  JTextField inputText;
	Choice list;  JButton processButton;
	Container content;  //Initialize the Container
	
	int binSize, small = 0, large = 0, middle = 0;  //Initialize the bin size, the number of X in each row
	static final int defaultRows = 8;	
	//Initilize the number of rows when the application is set to run defaultly with selection = 1
	String selection = "1";  //The default choice of selection of the probes is set to 1
	String outputMessage = "", max = "", min = "";  //The default outputMessage is set to empty
	String[] rttTime;  //A String array which contains all the rtt time
	
	public NetAnalyserGUI(String numOfProbes){
		/**
		 *		This is a constructor for class NetAnalyser
		 *		@param		numOfProbes		A String that provides the max number of probes
		 */
		this.setTitle("NetAnalyser V3.0");  //Set the title of the Java application
		content = this.getContentPane();  //Initializing a container
		content.setLayout(null);  //Set the Null Layout
		
		this.leftHand(numOfProbes);  //Execute the left-hand side of the frame
		
		//The code to the middle and the right-hand side of the frame
		text4 = new TextArea("Your output will appear here..");  content.add(text4);
		text4.setBounds(500,5,220,20);
		text5 = new JLabel("Histogram");  content.add(text5);
		text5.setBounds(800,50,220,20);
		//The following are reserved places for each row in the histogram 
		text6 = new JLabel("");  content.add(text6);
		text6.setBounds(800,100,20,20);
		text7 = new JLabel("");  content.add(text7);
		text7.setBounds(800,150,20,20);
		text8 = new JLabel("");  content.add(text8);
		text8.setBounds(800,200,20,20);
	}

	public void leftHand(String numOfProbes){
		/**
		 *		This is a metod for left-hand side of the GUI to run
		 *		@param		numOfProbes		A String that provides the max number of probes
		 *		@return		void
		 */
		text1 = new JLabel("Enter Test URL & no. of probes and click on Process");  content.add(text1);
		//Create a JLabel which says the first line of the Java application
		text1.setBounds(10,5,300,50);  //Set the location of the JLabel
		text2 = new JLabel("Test URL");  content.add(text2);
		//Create a JLabel which says the second line of the Java application
		text2.setBounds(20,60,60,50);  //Set the location of the JLabel
		inputText = new JTextField(20);  content.add(inputText);
		//Create a JTextField which get a URL from the user
		inputText.setBounds(85,70,300,30);  //Set the location and size of the JTextField
		
		text3 = new JLabel("No. of probes ");  content.add(text3);
		//Create a JLabel which says the third line of the Java application
		text3.setBounds(90,110,80,50);  //Set the location of the JLabel
		
		processButton = new JButton("Process");  content.add(processButton);  //Create a JButton for users to push
		processButton.setBounds(140,160,80,40);  //Set the location of the JButton
		processButton.addMouseListener(this); //Handle the MouseEvent
		
		list = new Choice();content.add(list);  //Create a Choice list to select a probe
		for(int i = 1; i <= Integer.parseInt(numOfProbes); i++){list.add(""+i);}
		//Add choices from 1 to number of probes to the Choice list
		list.addItemListener(this);  //Handle the event
		list.setBounds(180,125,80,20);  //Set the location of the Choice list
	}
	
	public void itemStateChanged(ItemEvent e){
		/**
		 *		This is method for implementation of abstract method itemStateChanged() in interface
		 *		ItemListener. The function of this method is to save the user choice from Test URL.
		 *		@param		e		an ItemEvent instance from class ItemEvent. Not used in the method.
		 *		@return		void
		 */
		selection = list.getSelectedItem();  //Save the choice from the user
	}
	
	//Implement the abstract method from interface MouseListener
	public void mouseClicked(MouseEvent e){
		/**
		 *		This is a metod for implementation of abstract method mouseClicked() in interface
		 *		MouseListener. The function is to excute the code after clicking "Process" button
		 *		@param		e			a varible of instance MouseEvent. Not used in the method.
		 *		@return		void
		 */
		
		//Check if the URL is valid
		if(!this.checkURL(inputText.getText())){
			JOptionPane.showMessageDialog(null, "invalid URL! Once you click the button the program will terminate automatically", "Error", JOptionPane.ERROR_MESSAGE);
			//Open an interface which will tell the URL is wrong and it will exit automatically.
			System.exit(0);  //Exit
		}
		try{
			text4.setText("");  //Clear the JTextArea of last use
			content.remove(text6);  content.remove(text7);  content.remove(text8);
			int selectionNum = Integer.parseInt(selection);  //Transform selection from number of probes to interger
			
			//Provided approach to calling the ping from within Java
			Process p = Runtime.getRuntime().exec("cmd /c ping -n "+ selectionNum +" " + inputText.getText());
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			//Read the information in outputMessage
			String line = reader.readLine();
			while(line != null){
				outputMessage += (line+'\n');
				line = reader.readLine();
			}
			reader.close();  //close the BufferedReader
		}
		//Catch IOException
		catch (IOException event){
			event.printStackTrace();
			System.exit(0);
			//Provide the IO Exception information
		}
		//Catch InterruptedException
		catch (InterruptedException event){
			event.printStackTrace();
			System.exit(0);
			//Provide the Interrupted Exception information
		}
		finally{ }  
		
		//First we need to check if we can reach the URL
		if(outputMessage.indexOf("Ping request could not find host") == 0){
			JOptionPane.showMessageDialog(null, "Ping output resulted an error!", "Error", JOptionPane.ERROR_MESSAGE);
			//Open an interface which will tell getting the ping has failed and it will exit automatically.
			System.exit(0);  //Exit
		}
		text4.setText(outputMessage); //Show the users the outputMessage
		text4.setBounds(400,5,400,220);
		this.outputMessageHandler(outputMessage);  //Extract useful information from the outputMessage
		this.histogram();  //Create the histogram
		outputMessage = "";  //Set outputMessage to empty after a process
		small = 0; middle = 0; large = 0;  //Reset the number of each section in the histogram
	}
	
	public void outputMessageHandler(String outputMessage){
		/**
		 *		This is a metod to Extract useful information from the outputMessage String
		 *		@param		outputMessage		the instance varible in the class NetAnalyser and
		 *															contains the raw output from the ping command 
		 *		@return		void
		 */
		max = outputMessage.substring(outputMessage.indexOf("Maximum = ") + 10, outputMessage.indexOf("ms, Average"));  //Extract the value of max ping as a String
		min = outputMessage.substring(outputMessage.indexOf("Minimum = ") + 10, outputMessage.indexOf("ms, Maximum"));  //Extract the value of min ping as a String
		binSize = (Integer.parseInt(max) - Integer.parseInt(min))/3;  //Determin the bin size
		
		String[] infor = outputMessage.split("\n");  //Split the outputMessage based on \n
		rttTime = new String[infor.length - defaultRows+1];  
		//Create a new String list which contains only the  ping message 
		//i.e. lines like "Reply from 99.86.116.102: bytes=32 time=26ms rtt=243"
		
		//Extract the ping values from each line with RTT time
		for(int i=2;i<=(2+(infor.length - defaultRows));i++){
			int index1= infor[i].indexOf("time="), index2 = infor[i].indexOf("ms TTL");
			//Locate the position of words besides rtt time
			if((index1 >= 0) && (index2 >= 0)){
				rttTime[i-2] = infor[i].substring(index1 + 5, index2);  //Get the rtt time from the line
			}
			else{
				rttTime[i-2] = "-1";  //If a time-out or other errors appears will result the current index of line to -1
			}
		}
		
		//Count the number of RTT values in different interval divided by the condition that if the binSize is 0
		if(binSize == 0){
			for(int j = 0; j < rttTime.length; j++){
				if(rttTime[j] != "-1"){  //Filter those information with a time-out or other errors out
					if(rttTime[j].equals(min)){small++;}
					else if(rttTime[j].equals(max)){large++;}
					else{middle++;}
				}
			}
		}
		else{
			for(int j = 0; j < rttTime.length; j++){
				int temp = Integer.parseInt(rttTime[j]);  //Transform the String to int
				if(rttTime[j] != "-1"){  //Filter those information with a time-out or other errors
					if(temp < (Integer.parseInt(min)+binSize)){small++;}
					else if(temp >= (Integer.parseInt(max)-binSize)){large++;}
					else{middle++;}
				}
			}
		}
	}
	
	//Method for create a histogram
	public void histogram(){
		/**
		 *		This is a metod to create the histogram showed in the right-hand side of the GUI 
		 *		@return		void
		 */
		//Confirm the bounds of each row in the histogram
		String temp1 = String.valueOf(Integer.parseInt(min)+binSize);
		String temp2 = String.valueOf(Integer.parseInt(max)-binSize);
		
		/*Generate information of each line divided by the condition that if binSize is 0. 
		If binSize is 0, it will mean that min + binSize is still 0, so we will create another form of histogram
		where the first and the third section will be the value of min and max, rather than a interval*/
		String row1 = "", row2 = "", row3 = "";
		if(max.equals(min)){
			//If max equals to min, only a single interval will be printed
			row1 = "RTT=" + min;
			for (int i = 0; i < small ;i++){row1 += "        X";}
		}
		else if(binSize == 0){	
			row1 = "RTT=" + min;	
			for (int i = 0; i < small ;i++){row1 += "        X";}
			row2 = temp1 + "<RTT<" +  temp2;
			for (int j = 0; j < middle ;j++){row2 += "        X";}
			row3 = "RTT=" +  max;
			for (int k = 0; k < large ;k++){row3 += "       X";}
		}
		else{
			row1 = min + "<=RTT<" +  temp1;
			for (int i = 0; i < small ;i++){row1 += "        X";}
			row2 = temp1 + "<=RTT<" +  temp2;
			for (int j = 0; j < middle ;j++){row2 += "        X";}
			row3 = temp2 + "<=RTT<=" +  max;
			for (int k = 0; k < large ;k++){row3 += "       X";}
		}
		//Add the inofrmation to the GUI
		text6 = new JLabel(row1);  content.add(text6);
		text6.setBounds(800,100,600,20);
		text7 = new JLabel(row2);  content.add(text7);
		text7.setBounds(800,150,600,20);
		text8 = new JLabel(row3);  content.add(text8);
		text8.setBounds(800,200,600,20);
		
		this.writeToFile(temp1, temp2);  //Write the information to the file
	}
	
	//The 4 methods below are to implement the abstract method from interface MouseListener
	public void mouseReleased(MouseEvent e){	}
	public void mousePressed(MouseEvent e){	}
	public void mouseEntered(MouseEvent e){	}
	public void mouseExited(MouseEvent e){	}
	
	public void writeToFile(String s1, String s2){
		/**
		 *		This is a metod to write the histogram data to the file 
		 *		@param		s1		A String which is the upper bound of the first interval of the histogram
									s2		A String which is the upper bound of the second interval of the histogram
		 *		@return		void
		 */
		 
		 //Create a String which will be the name of the file
		String fileName = inputText.getText().replace('.', '-') + "-" +Year.now() + "-" + 
									LocalDate.now().getMonthValue() + "-"+ LocalDate.now().getDayOfMonth() 
									+"-" + LocalTime.now().getHour() + "-" + LocalTime.now().getMinute() 
									+ "-" + LocalTime.now().getSecond() + ".txt" ;
		//Create the String which will be the contents of the file when binSize = 0
		String contents = fileName + "\n\n" + "RTT(ms)  histogram\n" + min + "-" + s1 + ":" + small + "\n"+ s1 
										+ "-" + s2 + ":" + middle + "\n"+ s2 + "-" + max +":" + large;
		if(max.equals(min)){
			contents = fileName + "\n\n" + "RTT(ms)  histogram\n" + min + ":" + small;
		}
		else if(binSize == 0){
			contents = fileName + "\n\n" + "RTT(ms)  histogram\n" + min + ":" + small + "\n"+ s1 + "-" + s2 + ":" 
							+ middle + "\n"+ max +":" + large;
		}
		try{
			FileWriter fileWriter = new FileWriter(fileName);  //Create a new FileWriter
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);  //Create a new BufferedWriter
			bufferedWriter.write(contents);  //Write the contents to the file
			bufferedWriter.close();  //Close the BufferedWriter
			fileWriter.close();  //Close the FileWriter
		}
		catch(IOException event){
			//The IOException of writing will be shown.
			event.printStackTrace();
			System.exit(0);
		}
	}
	
	public boolean checkURL(String url){
		/**
		 *		This is a metod to check if the input URL is valid by checking if it is empty or doesn't include a '.'
		 *		@param		url		A String that get the input URL and it is used to check its validation
		 *		@return		boolean		If it returns false, it will mean that the url is invalid
		 */
		if(url.equals("")){	return false;}  //Check if it's empty
		else if(url.indexOf('.') == -1){	return false;}  //Check if it contains at least one dot
		else{ return true;	}  //If it satisfies both conditions above, it will return true.
	}
}