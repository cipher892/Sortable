import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONException;

import com.google.gson.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/*This class extracts information from the listings.txt file and uses text comparison
 to compare with information found in the two maps from the ExtractIndex class. It then uses 
 this information to make a decision regarding the product this specific listing falls under.
 To elaborate further, the manufacturer of a given listing is retrieved. Using the "maker_model" 
 map from the ExtractIndex class, the models under this manufacturer are obtained. The "title" 
 string for the listing is obtained and using string comparison, each string is compared with
 the list of models (under this specific manufacturer. This greatly reduces the number of searches from 743
 to less than 40 on the average). If a match is made, the product that corresponds to this listing 
 is retrieved using its map index (since models and product names are retrieved and stored at the 
 same time.Also this reduces the number of searches from 743 to less than 40) .This product name 
 and listing is then stored into a map as key and value respectively. The resulting map is then converted into JSON format. 
  */

public class MatchInsert 
{
	private static ExtractIndex extract = new ExtractIndex();
	
	//a map that contains a manufacturer and its models
	private static Map<String, List<String>> maker_model = extract.getMakerModel();
	
	//a map that contains a manufacturer and a list of the product names
	private static Map<String, List<String>> maker_products = extract.getMakerPdctNames();
	
	//map that stores the product names and its extracted listings 
	private static Map<String,List<String>> productListings = new HashMap<>();
	
	private static ToJSON result;
	private static Gson gson;
	
	public static void main(String[] args) 
	{
		Scanner input;
		File file  = new File("src/listings.txt");
		
		if(file.exists())
		{
			try
			{
				input = new Scanner(file);
				while (input.hasNextLine())
				{
					//extract information from the listings.txt file
					JSONObject jsonobj = new JSONObject(input.nextLine());
					String title = jsonobj.getString("title");
					String[] tokens = title.split(" ");
					String makers = jsonobj.getString("manufacturer");
					String[] tokens2 = makers.split(" ");
					String mk = tokens2[0];
					String maker = mk.toLowerCase();
					
					//call the matchListings method to find a match 
					matchListings(maker, title, tokens);
					
				}//end while
			}//end try
			
			catch(JSONException | FileNotFoundException err)
			{
				err.printStackTrace();
			}
		}//end if
		
		//store the result in a file after conversion to JSON
		displayResult(productListings);
	}//end method main
	
	//method that attempts to match a listing to a product
	private static void matchListings(String maker, String title, String[] tokens)
	{
	    //retrieve the list of models under this manufacturer
		if(maker_model.containsKey(maker))
		  {
			 List<String> models = maker_model.get(maker);
			 for (String model : models)
			   {
				  /*integer that counts the number of matches. if the model consists
				  of two strings, there should be two matches with two strings in the title
				  string*/
				  int occ = 0;
				  
				  /*this is an exception that handles when a match occurs but the number 
				   * of matches is less than the strings in the model.*/
				  boolean exc = false;
				  
				  String[] tok = model.split(" ");
				  if (tok.length > 0)
					{
					  for (String token : tokens)
					   {
						  for (int i=0; i<tok.length;++i)
						  	{
						  	  if (i==0 && tok[0].equalsIgnoreCase(token))
						  		 exc = true;
						  	 
						  	  if (tok[i].equalsIgnoreCase(token))
						  		 ++occ;
							 }
						 } //end token for 
					} //end if 
				
				  //if there is a match, search the product map and retrieve the product name 		  	
				 if(occ == tok.length|| exc == true)
				    {
					   //call the searchProduct method and retrieve the product name 
					   String product = searchProduct(models, model, maker);
					   
					   //add this listing to to the productListings map
					   addListing(product,title);
					   break;
				    }
			   }//end model for
		  }//end if
   }//end method matchListings

//method that adds listings to the productLisitngs map 
  private static void addListing(String product, String title)
  {
	 if(!product.equalsIgnoreCase(" "))
		{
		   //check if the product already exists in the map
		   if(productListings.containsKey(product))
			 {
				List<String> listings = productListings.get(product);
				listings.add(title);
				productListings.put(product, listings);
			  }
		   
		   //if not create a new entry and add listing
		   else
			  {
				List<String> listings = new ArrayList<>();
				listings.add(title);
				productListings.put(product, listings);
			  }
		  }//end product if
  }//end method addProductLisitngs
						  
	//method that searches the maker_product map and retrieves a product name		
	private static String searchProduct(List<String> mod, String model, String maker)
	{
		//get the product names array for this maker
		List<String> mkp = maker_products.get(maker);
		
		String product = " ";
		int x = -1;
		for(String m : mod)
		{
			x++;
			if(m.equalsIgnoreCase(model))
			{
				product = mkp.get(x);
				break;
			}
		}
		
		return product;
	}//end method searchProduct
	
	//method that converts the productLisings map into JSON and store in a file
	private static void displayResult(Map<String, List<String>> arr)
	{
		gson = new Gson();
		Set<String> keys = new HashSet<>(arr.keySet());
		Formatter output = null;
		try
		{
			 output = new Formatter("src/result.txt");
		}
		
		catch(FileNotFoundException fe)
		{
			fe.printStackTrace();
		}
		
		for(String key : keys)
		{
			List<String> list = arr.get(key);
		    result = new ToJSON(key, list);
		    
		    //use the gson library to convert to JSON
		    String result_json = gson.toJson(result);
		    
			output.format("%s\n", result_json);
		}
			
	}//end method displayResult


}//end class MatchInsert
