package Project1;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create a DNS with mappings of Server Id's against the Server Names. 
 * 
 * @author Amit Shah
 *
 */
public class DomainNameServer {
	
	// A Singleton Object of the DNS class.
	static DomainNameServer DNSObject = null ;
	
	/**
	 * Key value mapping of each server where Key is the Id and Value is the serverid.
	 */
	Map<Integer, String> DNS = new HashMap<>();
	
	// Each server is mapped with an ID that remains consistent throughout the application
	static final int[] serverIds = {0, 1, 2, 3, 4, 5, 6};
	// List of servers names that are a part of the architecture.
	static final String[] serverHostnames = {	"glados.cs.rit.edu", 	"queen.cs.rit.edu",	"doors.cs.rit.edu", 	
												"medusa.cs.rit.edu",	"queeg.cs.rit.edu", 	"reddwarf.cs.rit.edu",
												"rhea.cs.rit.edu"	
											};
	
	/**
	 * Returns the DNS HashMap that is used to map each server with a corresponding ID.
	 * Its a static object that will be accessed by other classes to get access to the HashMap. 
	 */
	static Map<Integer, String> getDNS(){
		return getDNSObject().DNS;
	}
	/**
	 * Returns a Singleton object of the DNS class.
	 * @return DNS class object.
	 */
	private static DomainNameServer getDNSObject(){
		if(DNSObject == null){
			DNSObject = new DomainNameServer();
		}
		return DNSObject; 
	}
	
	/**
	 * Initializing the Domain Name Server HashMap with values of server id against the server names.
	 */
	private DomainNameServer(){
		DNS = new HashMap<>();
		for(int index = 0; index < serverIds.length; index++ ){
			DNS.put(serverIds[index], serverHostnames[index]);
		}
	}
	
}
