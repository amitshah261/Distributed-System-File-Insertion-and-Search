package Project1;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * This class is the main client file, it is the main driver class for the Distributed Hash Table.
 * The user is asked what type of operation he wants to perform.
 * 1. Insertion
 * 2. Search
 * The Server should be active and listening before the user requests a particular action.
 * Usage : java DHTClient
 * 
 * @author Amit Shah
 */

public class DHTClient {

	static final int serverPort = 8400;
	static final String clientDirectory = "Project1/Servers/Client/",
			serverDirectory = "Project1/Servers/Server";
	Map<Integer, String> DNS;
	FileOperations fo;
	Hasher hash = null;
			
	/**
	 * Initializing the global variables.
	 */
	DHTClient(){
		try{
			this.DNS = DomainNameServer.getDNS();
			this.fo = FileOperations.getFileOperationsObject();
			this.hash = Hasher.getHasherObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * The main method from where the program begins execution and the menu is displayed to the user.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException{
		DHTClient client = new DHTClient();
		client.displayMenu();
	}
	
	/**
	 * This method displays the menu to the user till the user decides to exit the program.
	 * @throws IOException 
	 */
	private void displayMenu() throws IOException {
		Scanner scan = new Scanner(System.in);
		boolean exitProgram = false;
		
		while(!exitProgram){
			System.out.println("\n##########################################################\n");
			System.out.println("What would you like to do? \n"
					+ "1. Insert File \n"
					+ "2. Search File \n"
					+ "3. Exit");

			String choice = scan.nextLine();
			
			switch(choice){
				case "1":
					System.out.println("\nEnter File Name: ");
					String fileName = scan.nextLine();
					if(insertFile(fileName))
						System.out.println("\nFile Inserted Successfully\n");
					else
						System.err.println("\nInsertion Error!\n");
					break;
				
				case "2":
					System.out.println("\nEnter File to search: ");
					fileName = scan.nextLine();
					
					if(searchFile(fileName))
						System.out.println("\nFound file!\n");
					else
						System.err.println("\nFile not Found, try Again!\n");
					break;
				
				case "3": 
					System.out.println("Thank you!");
					exitProgram = true;
					break;
				
				default:
					System.err.println("Invalid Choice, Please try again. \n");
					break;
			}
		}
		scan.close();
	}
	
	/**
	 * This function is used to search a file on the server, it computes the host where the file is
	 * to be searched first (which will be one of the leaf nodes randomly selected).
	 * @param fileName - The File which needs to be searched on the server.
	 * @return Boolean value - Whether the file is found or not.
	 * @throws IOException 
	 */
	private boolean searchFile(String fileName) throws IOException {
		
		//Begin search at leaf node
		int leafNodeLevel = 2;
		
		//Pick a random index of the child.
		Random rand = new Random();
		int randomIndex = rand.nextInt(leafNodeLevel * 2);
		

		int hostId = 		hash.getHashcode(fileName, leafNodeLevel, randomIndex);
		String hostname = 	DNS.get(hostId);

		// The search results are separated by a hyphen.
		String[] searchResults = fo.findFile(fileName, hostId, hostname, serverPort, leafNodeLevel).split("-"); 
		String searchPath = searchResults[0]; 
		
		boolean fileFound = Boolean.parseBoolean(searchResults[1]);
		
		System.out.println("\nThe file was searched at " + searchPath);
		
		return fileFound;
	}
	
	/**
	 * This function is used to insert a file on the server by copying it from the client directory.
	 * @param fileName - The name of the file which needs to be inserted.
	 * @return boolean value - Whether the file was successfully inserted or not.
	 */
	boolean insertFile(String fileName){
		
		// Identify the server where the insertion request needs to be directed
		final int rootLevel = 0;
		
		int hostId = hash.getHashcode(fileName, rootLevel, rootLevel); //Inserting at the root.
		String hostname = DNS.get(hostId); 

		String serverPath = serverDirectory; 
		
		// Navigate to the folder belonging to the specific server found by the hash function.
		serverPath += hostId  + "/"; // since the folders are named as Server0 i.e Server[hostId]
		
		try{
			return fo.copyFileToHost(fileName, clientDirectory, serverPath, hostname, serverPort);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
}
