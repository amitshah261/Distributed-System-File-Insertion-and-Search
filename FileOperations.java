package Project1;

import java.io.File;
import java.io.IOException;

/**
 * This class is used to perform file operations i.e Send (copy) / Search file.
 * It is responsible to pass the request to the server and receive the reply from the server. 
 * 
 * @author Amit Shah
 *
 */
public class FileOperations {

	static FileOperations fo = null;
	
	/*
	 * Making the constructor private to make sure no new Objects can be created by other classes.
	 * This is done to observe Singleton Pattern.
	 */
	private FileOperations(){} // To ensure singleton pattern is implemented
	
	/**
	 * Creating a Singleton Object that will be returned every time this method is invoked
	 * @return Singleton Object of FileOperations class
	 */
	static FileOperations getFileOperationsObject(){
		if (fo == null ){
			fo = new FileOperations();
		}
		return fo;
	} 
	
	/**
	 * This method is used to copy a file from a source to the destination directory in the Server
	 * This method sends the copy request to the server which needs to copy the file.
	 * @param fileName	 		- The name of the file which needs to be copied to the host. 
	 * @param sourcePath 		- The source location of the file.
	 * @param destinationPath 	- The destination directory where the file needs to be copied.
	 * @param hostname	 		- The hostname of the server where file needs to be copied.
	 * @param serverPort 		- The port at which the server is listening for incoming connections.
	 * @return boolean	 		- Whether the file was successfully copied or not.
	 */
	boolean copyFileToHost(String fileName, String sourcePath, String destinationPath, String hostname, int serverPort){
		File file = new File(sourcePath + fileName); // Importing the file.
		FileSender client = new FileSender (hostname, serverPort); // Create a new connection to the server.
		return client.sendFile(file, destinationPath + fileName);
	}
	
	/**
	 * This method is used to find the file at a specified server and returns if the file is found or not. 
	 * @param fileName 	 - The name of the file which needs to be searched.
	 * @param serverId	 - The id assigned to the server.
	 * @param hostname	 - The hostname of the server.
	 * @param serverPort - The port at which the server is listening for incoming connections.
	 * @param level		 - The current level at which search is being made.
	 * @return boolean   - Boolean message whether the file is found or not.
	 * @throws IOException 
	 */
	public String findFile(String fileName, int serverId, String hostname, int serverPort, int level) throws IOException {
		FileSender client = new FileSender (hostname, serverPort); // Create a new connection to the server.
		return client.searchFile(fileName, serverId, level);
	}
}
