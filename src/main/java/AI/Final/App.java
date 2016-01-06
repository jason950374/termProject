package AI.Final;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        JsonReader reader = new JsonReader();
        reader.init();
        String keyword;
        String[] term;
        Concept c;
        Boolean YesNo;
        List<Concept> temp;
        try {
        	BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        	while((keyword = (String)buf.readLine()) != null){
        		term = keyword.toLowerCase().split(" ");
        		switch (term[0]) {
				case "where":
					c = reader.searchWhere(term[1]);
	            	System.out.println(c.getConcept());
					break;
				case "when":
					temp = reader.searchWhen(term[1]);
					for(Concept cc:temp){
						System.out.print(cc.getConcept()+" "+cc.getScore());
						while(cc.getParent()!=null){
							cc = cc.getParent();
							System.out.print(" "+cc.getConcept()+" "+cc.getScore());
						}			
						System.out.println();
					}
	            	
					break;
				case "why":
					
					break;
				case "how":
					break;
				case "capableof":
					YesNo = reader.searchCapableOf(term[1], term[2]);
					if(YesNo)
						System.out.println("Yes");
					else
						System.out.println("No");
					break;
				case "isa":
					YesNo = reader.searchIsA(term[1], term[2]);
					if(YesNo)
						System.out.println("Yes");
					else
						System.out.println("No");
					break;
				default:
					break;
				}
        		
        	}
//        	for(Concept c :location){
//        		System.out.println(c.getConcept()+" "+c.getScore());
//        	}
        	
//        	reader.read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
