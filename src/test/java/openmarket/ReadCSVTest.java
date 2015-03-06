package openmarket;

import java.util.List;
import java.util.Map.Entry;

import junit.framework.TestCase;

import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;

public class ReadCSVTest extends TestCase{

	
	public void testRead() {
		
		List<Entry<String, Integer>> list = Tools.readGoogleProductCategories();
		String s = Tools.googleProductCategoriesToInserts(list);
		
		System.out.println(s);
		
		System.out.println(list.get(1));
		
	
				
		
	}
	
	public void testGetExternalIP() {
		System.out.println(DataSources.EXTERNAL_IP);
	}
}
