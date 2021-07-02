public class InvalidNumOfProbesException extends Exception{
	/**
	*		Title			    : InvalidNumOfProbesException.java
	*		Description  : A class that defines an exception when the number of probes is invalid
	*
	*		@author		Yibo Lian
	*		@version 	1.0
	*/
	public InvalidNumOfProbesException(){
		/**
		 *		This is the constructor for the class InvalidNumOfProbesException
		 */
		super("\nInvalid number of probes given. You should give a number between 10 and 20");
		//Print on the command that the given number of probes is invalid
	}
}