import javax.swing.*;

/**
 *		Title			    : NetAnalyser.java
 *		Description  : The main program for the NetAnalyser application to run
 *
 *		@author		Yibo Lian
 *		@version 	2.0
 */
 
public class NetAnalyser{
	public static void main(String[] args) throws InvalidNumOfProbesException{
		/**
		 *		This is the main method for class NetAnalyser to run
		 *		@param		args[]		a String array which can access user input 
		 *											from cmd, the first parameter will be the 
		 *											max number of probes of each process
		 *		@return		void
		 *		@exception	This method could throw an InvalidNumOfProbesException
		 */
		if(Integer.parseInt(args[0]) > 20 || Integer.parseInt(args[0]) < 10){
			throw new InvalidNumOfProbesException();  //Throw an InvalidNumOfProbesException
		}
		else{
			JFrame netAnalyserGUI = new NetAnalyserGUI(args[0]);  //Create a new netAnalyser instance
			netAnalyserGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //Set the operation when closing the frame
			netAnalyserGUI.setSize(1500, 270);  //Set the size of the frame
			netAnalyserGUI.setVisible(true);  //Set the visibility of the frame to true
		}
	}
}