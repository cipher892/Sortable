import java.util.List;

//this class uses the gson library to convert strings into JSON format
public class ToJSON 
{
	String product_name;
    List<String> listings;
	
	public ToJSON(String product_name, List<String> listings)
	{
		this.product_name = product_name;
		this.listings = listings;
	}//end constructor

}//end class
