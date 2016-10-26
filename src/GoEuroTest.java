/*
 * Name: Aaron May
 * Date: 26.10.16
 * Version: 1.0
 * 
 * This program queries the GoEURO API with a country name as a command line argument and 
 * a CSV file is created containing the _id, name, type, latitude and longitude for any country who's name 
 * contains the user input String.
 * The CSV file is stored in the location where this program was ran from.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONException;

public class GoEuroTest {

	public static void main(String[] args){
		if (validateInput(args) == 0) { //validate that no errors are contained in args input
			JSONArray json = readJSONUrl("http://api.goeuro.com/api/v2/position/suggest/en/" + args[0]); //connect to API and store JSON data using args parameter
			
			if (json == null) { //check that read to JSON API was successful
				System.exit(2); //an error occurred with the call to the API and program should not go any further
			}
			
			if (json.length() == 0) { //check that the JSON read returned data
				System.out.println("No JSON data was returned using the input: " + args[0]); 
			}
			else { //JSON data successfully returned
				System.out.println("JSON data successfully captured using the input: " + args[0]); //output success message with reiterating user input
				int fileWriteResult = writeJSONToFile(json); //write JSON data to file and store outcome result
				if (fileWriteResult == 0) { //test if write successful
					System.out.println("CSV file succesfully written to."); //output success message to user
					System.out.println("CSV generated at location: " + System.getProperty("user.dir") + "/JSON.csv"); //output location of CSV file and name
				}
				else {
					System.out.println("Error occured writing to file");
				}
			}
		}
		else {
			System.exit(1);//an error occurred with the command line input and program should not go any further
		}

	}

	/*
	 * Takes as input a url which is expecting JSON to be returned.
	 * JSON data then stored in a JSONArray and returned.
	 * 
	 * @param url string to be used to connect to and capture JSON from
	 * @return JSONArray is the read was successful, null if read was unsuccessful
	 */
	private static JSONArray readJSONUrl(String url) {

		InputStream inputStream; //used to connect to URL
		try {
			inputStream = new URL(url).openStream(); //creates connection to URL
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))); //create reader for returned data
			StringBuilder stringBuilder = new StringBuilder(); //used to store data return from URL
			int nextInput = -1; 
			while ((nextInput = bufferedReader.read()) != -1) { //captures and stores all data from URL
				stringBuilder.append((char) nextInput);
			}    
			inputStream.close(); //all data is captured so no longer needed
			JSONArray jsonFromUrl = new JSONArray(stringBuilder.toString()); //create JSONArray structure from the read data
			return jsonFromUrl;
		} catch (IOException | JSONException e) {
			System.out.println(e.toString() + " occured while attempting to read JSON URL");
			return null; //error occurred while reading JSON
		}
	}

	/*
	 * Takes as parameter a JSONArray which then data is stored into a CSV file at the user current location.
	 * The data stored into the CSV file is _id, name, type, latitude and longitude.
	 * The CSV format uses Windows-1252 to allow for special characters to be displayed.
	 * If the file already exists, it is overwritten with the new execution.
	 * 
	 * @param jsonArray an array of JSON data which will be attempted to be written to a CSV file 
	 * @return 0 if the write to file was successful, 1 if the write was unsuccessful
	 */
	private static int writeJSONToFile(JSONArray jsonArray) {

		PrintWriter writer;
		try {
			writer = new PrintWriter(System.getProperty("user.dir") + "/JSON.csv", "Windows-1252"); //create JSON.csv file at users current location 
			writer.println("_id,name,type,latitude,longitude"); //store the headings for the CSV
			for (int i = 0; i < jsonArray.length(); i++) { //use all data from entire JSONArray
				writer.println(jsonArray.getJSONObject(i).get("_id") + "," + //write specific data to file
						jsonArray.getJSONObject(i).get("name") + "," +
						jsonArray.getJSONObject(i).get("type") + "," +
						jsonArray.getJSONObject(i).getJSONObject("geo_position").get("latitude") + "," +
						jsonArray.getJSONObject(i).getJSONObject("geo_position").get("longitude"));
			}
			writer.close(); //printwriter no longer needed and can be closed
			return 0; //write finished successfully 
		} catch (FileNotFoundException | UnsupportedEncodingException | JSONException e) {
			System.out.println(e.toString() + " occured while writing to CSV file"); //output exception message
			return 1; //error occurred while writing to file
		}
	}

	/*
	 * Takes as parameter a String array, which is than verified that only contains one segment of data and outputs any errors that may occur.
	 * Input validated in separate method to allow error messaging to be expanded with ease.
	 * 
	 * @param args argument entered in from user and is then validated
	 * @return 0 if the arg is validated successful, 1 if the arg is unsuccessful at being validated
	 */
	private static int validateInput(String[] args) {
		if (args.length == 1) {//check that only one arg has been entered		
			return 0;//input was validated successfully
		}
		else {//output instructions to ensure command line argument is in the correct format
			System.out.println("Incorrect argument format. Requires 1 argument ");
			System.out.println("Command line format: java -jar GoEuroTest.jar \"CITY_NAME\"");
			return 1;//return error code
		}
	}
}


/*
Please implement your solution as a stand alone application which can be started from the command line, 
i.e. send us a fat jar file with all dependencies. 
You can use Java 8 and open source libraries that you think help you to fulfill this task. 
Also send us the source code to your solution. We use GitHub, so if you put your source code 
into a GitHub repository, it will make our life easier. 
We will evaluate your source code as well as the functionality of the program: 
Does it run, how does it handle errors, how well-engineered is the architecture etc. Thank you!
 */