package Project1;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class is used to send a request for copy / search to the server and it also receives 
 * the reply from the server.
 * @author Amit Shah
 *
 */
public class FileSender{

	String hostname = null;
	int port;
	Socket clientSocket = null;
	
	/**
	 * The constructor is used to create a connection to the designated hostname and port.
	 * @param hostname
	 * @param port
	 */
	FileSender(String hostname,int port){
		this.hostname = hostname;
		this.port = port;	
		establishConnection();
	}
	
	/**
	 * This method establishes the connection to the port and the hostname specified.
	 */
	public void establishConnection(){

		try {

			clientSocket = new Socket(hostname, port);
		
		} 
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		} 
	}

	/**
	 * This function sends a request to copy a file at a given path. 
	 * @param file - The file which needs to be copied.
	 * @param filePath - The path at which the file needs to be copied.
	 * @return
	 */
	boolean sendFile(File file, String filePath){
		try{

			DataOutputStream DO = new DataOutputStream (clientSocket.getOutputStream());

			// Send a copy request to the socket.
			DO.writeUTF("copy");

			// Sending the path where file needs to be copied with the file name
			DO.writeUTF(filePath);

			// Send the actual File
			byte[] mybytearray = new byte[(int) file.length()];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(mybytearray, 0, mybytearray.length);
			OutputStream os = clientSocket.getOutputStream();
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();
			bis.close();
			clientSocket.close();
			return true; // File was transmitted successfully.
		}
		catch(Exception e){
			e.printStackTrace();
			return false; //File transmission failed.
		}
	}
	
	/**
	 * This function sends the request to search the file at a specified path 
	 * given the server id and level.
	 * @param fileName - The name of the file which needs to be searched.
	 * @param serverId - The id of the current server.
	 * @param level - The current level in the virtual tree where the search is being made.
	 * @return String - The path which was traversed to find the file and whether the file was found or not.
	 * @throws IOException 
	 */
	public String searchFile(String fileName, int serverId, int level) throws IOException {

		try{
			
			DataOutputStream DO = new DataOutputStream (clientSocket.getOutputStream());

			// Send a search request to the socket.
			DO.writeUTF("search");

			// Sending the path where file needs to be searched
			DO.writeUTF( fileName + "-" + serverId + "-" + level );
			
			DataInputStream DI = new DataInputStream(clientSocket.getInputStream());
			
			String searchResults = DI.readUTF();
			
			if(!searchResults.contains("-true")) // File not Found
				searchResults += "-false";
			
			return searchResults;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
				clientSocket.close();
		}
		return "FileNotFound";
	}


}
