package Project1;

/**
 * This class is used to calculate the Hashvalue of a given input, it returns the index of the server
 * which needs to be accessed for a given filename, level and index.
 * 
 * @author Amit Shah
 *
 */
public class Hasher {

	static Hasher hash = null;
	
	// Making the constructor private so no new objects can be created.
	private Hasher(){}
	
	/**
	 * Creates (if required) and returns a Singleton object of this class.
	 * @return Singleton object of Hasher.
	 */
	static Hasher getHasherObject(){
		if(hash == null){
			hash = new Hasher();
		}
		return hash;
	}
	
	/**
	 * The function calculates the hash value (server index) given the filename, level and index
	 * @param fileName - The file name for which the hash value needs to be calculated.
	 * @param level - The level of the virtual tree which needs to be reached (ranging between 0 to n) 
	 * @param index - The index of the node at a particular level (ranging between 0 to level^2)
	 * @return integer value - server index.
	 */
	int getHashcode(String fileName, int level, int index){
		int hashCode = (fileName).hashCode() % 7;
		int indexOfServer = hashCode + ((int)Math.pow(2, level) - 1) + index ;
		if( (indexOfServer) >= 7){
			return indexOfServer - 7 ;  
		}
		return indexOfServer;		
	}
} 
