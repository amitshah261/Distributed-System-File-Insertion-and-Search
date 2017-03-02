package Project1;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class receives search and copy requests and performs the necessary actions at the server side.
 * This script is executed at the server side and the server listens for incoming requests at a port. 
 *  
 * @author Amit Shah
 *
 */
public class FileReceiver extends Thread{

	static final String serverDefaultPath = "Project1/Servers/Server";
	static final int minHitsToReplicate = 5;
	static final int maxNumberOfLevels = 2;
	static int port = 8400;
	
	Hasher hash = null;
	Map<Integer, String> DNS; 
	Random rand;
	Map<String, Integer> popularity;
	
	/**
	 * Create an Object of the FileReceiver class and initialize the global variables.
	 * 
	 * @param port - The port where the server needs to be listening.
	 * @throws IOException
	 */
	FileReceiver (int port) throws IOException{
		FileReceiver.port = port;
		this.rand = new Random();
		this.popularity = new HashMap<String, Integer>();
		this.DNS = DomainNameServer.getDNS(); // Gets Singleton object of the DNS class.
		this.hash = Hasher.getHasherObject(); // Gets Singleton object of the Hasher class.
	}
	
	/**
	 * The main method that drives the entire execution. 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException{
		FileReceiver server = new FileReceiver(port);
		server.start();
	}

	/**
	 * The run method that starts a server at the specific port.
	 */
	public void run(){
		
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			
			boolean done = false;

			while(!done){	
				System.out.println("Listening on port: " + port);
				
				// Wait for client request.
				clientSocket = serverSocket.accept();
				
				// Open an input stream to send data to client
				DataInputStream DI = new DataInputStream(clientSocket.getInputStream());
				
				// Receive the request type (copy / search)
				String requestType = DI.readUTF();
				
				switch(requestType){
				
					// If it is a copy File Request
					case "copy": {
						String filePath = DI.readUTF();
						copyFile(clientSocket, filePath);
						break;
					}
					// If it is a search request
					case "search": {
						// The file details are separated by a hyphen
						String fileDetails[] = DI.readUTF().split("-");
						
						// First part is the File name
						String fileName = fileDetails[0];
						
						//Second part is the server id.
						int serverId = Integer.parseInt(fileDetails[1]);
						
						// Third part is the current level of search.
						int level = Integer.parseInt(fileDetails[2]);
						
						fileSearch(clientSocket, fileName, serverId, level);
						
						break;
					}
					default: {
						System.err.println("Invalid Request Type");
						break;
					}
				}
			}
			
			serverSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 
		finally{
			try {
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is used to receive a file via the specified socket and copy it in the specific directory.
	 * @param clientSocket - The socket through which the file needs to be received.
	 * @param filePath - The path where the file needs to be saved.
	 */
	private void copyFile(Socket clientSocket, String filePath) {
		
		try{
			byte[] mybytearray = new byte[1024];
			InputStream is = clientSocket.getInputStream();
		    FileOutputStream fos = new FileOutputStream(filePath);
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
		    bos.write(mybytearray, 0, bytesRead);
		    System.out.println("File Received at " + filePath);
		    bos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This function searches the file given the filename in the server directory.
	 * @param clientSocket - The socket connection through which data will be received or sent.
	 * @param fileName - The name of the file which needs to be searched.
	 * @param serverId - The id of the current server where the file is being searched.
	 * @param level - The level of the virtual tree where the server exists.
	 */
	private void fileSearch(Socket clientSocket, String fileName, int serverId, int level) {
		try{
			
			String filePath = serverDefaultPath;
			
			// Get the server's directory.
			filePath += serverId + "/" + fileName + "/";
			
			// Retrieve the file.
			File lookupFile = new File(filePath);
			
			// Get the output stream to send a message through the socket.
			DataOutputStream DO = new DataOutputStream (clientSocket.getOutputStream());
			
			// Check if file was found.
			if(lookupFile.exists() && !lookupFile.isDirectory()) { 
				
				System.out.println("File Found at Server " + DNS.get(serverId));
				
				// This function returns whether the file needs to be replicated at the child. 
				boolean needToReplicate = updatePopularityCount(lookupFile.getName());
				
				if(needToReplicate){ 
					
					// File Not at Leaf.
					if(level < maxNumberOfLevels){
						
						//Replicate at the next level
						if(replicateFileToChild(lookupFile, level + 1)){ 
							System.out.println("File Replicated to child.");
						}
						else{ 
							System.err.println("File Replication Failed.");
						}
					}
					else
						System.out.println("File already at leaf.");
				}
				// Send the path where the file was found along with the message that file was found. 
			    DO.writeUTF( DNS.get(serverId) + "-" + true);
			}
			else{
				// Send request to search the file in the parent and append the current server name to it. 
				DO.writeUTF( DNS.get(serverId) + " => " + findFileInParent(fileName, level));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This function updates the number of times a request is received for a specific file.
	 * @param filename - The file name for which the count needs to be updated.
	 * @return
	 */
	private boolean updatePopularityCount(String filename) {
		int popularityCount = 1;
		boolean needToReplicate = false;
		if(popularity.containsKey(filename)){
			popularityCount += popularity.get(filename);
			System.out.println("File: " + filename + " popularityCount: " + popularityCount);
			if(popularityCount == minHitsToReplicate){
				needToReplicate = true;
				popularityCount = 0;
			}
			popularity.put(filename, popularityCount);
		}
		else{
			popularity.put(filename, popularityCount);
		}
		return needToReplicate;
	}
	
	/**
	 * This function is used to replicate a given file to child.
	 * @param lookupFile - The file which needs to be replicated.
	 * @param level - The level at which the file needs to be replicated.
	 * @return boolean - whether the file is successfully replicated or not.
	 */
	private boolean replicateFileToChild(File lookupFile, int level) {
		
		
		int randomIndex = getRandomIndex(level);
		String fileName = lookupFile.getName();
		
		int hostId = hash.getHashcode(fileName, level, randomIndex);
		String hostname = DNS.get(hostId);
		
		System.out.println("Replicating file to -> " + hostname);
		String filePath = serverDefaultPath;
		filePath += hostId + "/" + fileName + "/";
		
		
		FileSender client = new FileSender (hostname, port);
		
		return client.sendFile(lookupFile, filePath);
		
		
	}
	
	/**
	 * This function passes the search request to the parent.
	 * @param fileName - The name of the file which needs to be found.
	 * @param level - The current level of search.
	 * @return String - The path that was traversed to reach the file.
	 * @throws IOException 
	 */
	private String findFileInParent(String fileName, int level) throws IOException {
		
		if(--level >= 0){ // Go to previous level
			int randomIndex = getRandomIndex(level);
			int serverId = hash.getHashcode(fileName, level, randomIndex);
			String hostname = DNS.get(serverId);
			System.out.println("In file Receiver class -> connecting to " + (hostname));
			FileSender client = new FileSender (hostname, port);
			return client.searchFile(fileName, serverId, level);
		}
		return "";
	}
	
	/**
	 * This function generates a random index based on the level.
	 * @param level - The level at which random index needs to be found.
	 * @return
	 */
	private int getRandomIndex(int level){
		int randomIndex = (level == 0) ? 0 : rand.nextInt(level * 2);
		return randomIndex;	
	}

}


