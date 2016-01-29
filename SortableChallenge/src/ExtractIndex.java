import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

/*This class extracts the models and product names from the products.txt file.
 The information is put into two maps. The first map "maker_productnames" groups the different products
 according to the manufacturer. For instance "[Sony: Sony_CyberShot_DSC_W310,Sony_TX10...etc].
 The second map "maker_model"stores the models under a given manufacturer i.e. "[Sony: DSC_W310, DSC_Hx100v,..etc]
*/


public class ExtractIndex 
{
	private Map<String, List<String>> maker_productnames = new HashMap<>();
	private Map<String,List<String>> maker_model= new HashMap<>();
	
	//constructor calls the indexListings method on instantiation
	public ExtractIndex()
	{
		indexListings();
	}
	
	
	public Map<String, List<String>> getMakerPdctNames()
	{
		return maker_productnames;
	}
	
	public Map<String, List<String>> getMakerModel()
	{
		return maker_model;
	}
	
	//this method extracts the needed information and adds to the two maps
	private void indexListings()
	{ 
		Scanner input;
		File file = new File ("src/products.txt");
		
		if (file.exists())
			{
				try
				 {
					input = new Scanner(file);
					while(input.hasNextLine())
					  {
						JSONObject jsonobj = new JSONObject(input.nextLine());
						String name = jsonobj.getString("product_name");
						String mk = jsonobj.getString("manufacturer");
						String maker = mk.toLowerCase();
						String model = jsonobj.getString("model");
						addMakerModel(maker, model);
						addMakerPdctNames(maker, name);
					  }// end while
					input.close();
				 }//end try
				
				catch (JSONException | FileNotFoundException e)
				{
					System.out.println("JSON Exception has occured");
					e.printStackTrace();
				}
				 
			}//end if
							
				
         }//end method indexListings
	
	//method adds the retrieved manufacturer and model to the appropriate map 
	private void addMakerModel(String maker, String model)
	{
		//check if map already contains this manufacturer and add the new model
		if(maker_model.containsKey(maker))
		 {
			List<String> model_set = maker_model.get(maker);
			model_set.add(model);
			maker_model.put(maker, model_set);
		 }
		
		//create entry for the new manufacturer
		else
		  {
			List<String> model_set = new ArrayList<>();
		    model_set.add(model);
			maker_model.put(maker, model_set);
		  }
	}//end method
	
	//add the retrieved maker and model to the appropriate map
	private void addMakerPdctNames(String maker, String name)
	{
		//check if map already contains this manufacturer 
		if(maker_productnames.containsKey(maker))
		  {
			 List<String> pctnames_set = maker_productnames.get(maker);
			 pctnames_set.add(name);
			 maker_productnames.put(maker, pctnames_set);
		   }
		
		//create new entry if not
		else
		   {
			 List<String> pctnames_set = new ArrayList<>();
			 pctnames_set.add(name);
			 maker_productnames.put(maker, pctnames_set);
			}
	}//end method
					
}//end class


