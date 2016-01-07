package AI.Final;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

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
        InputStream modelIn = null;
        String[] sent = null;
		String[] tags = null;
        try {
//        	modelIn = new FileInputStream("en-pos-maxent.bin");
//			POSModel posModel = new POSModel(modelIn);
//			POSTaggerME tagger = new POSTaggerME(posModel);
//			String ss = "Chicken";
//	    	sent =	WhitespaceTokenizer.INSTANCE.tokenize(ss);
//			tags = tagger.tag(sent);
//			for(String s:tags){
//				System.out.printf(s+" ");	
//			}
        	BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        	while((keyword = (String)buf.readLine()) != null){
        		term = keyword.split(" ");
        		switch (term[0].toLowerCase()) {
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
				case "what":
					temp = reader.searchWhat(term[1]);
					List<Concept> singleWord = new LinkedList<Concept>();
					boolean print = false;
					for(Concept cc:temp){
//						if(cc.getConcept().contains("_")){
							System.out.println(cc.getConcept()+" "+cc.getScore());
//							print = true;
//							break;
//						}
//						else{	
//							singleWord.add(cc);
//						}
//						while(cc.getParent()!=null){
//							cc = cc.getParent();
//							System.out.print(" "+cc.getConcept()+" "+cc.getScore());
//						}
					}
	            	if(!print&&!singleWord.isEmpty())
	            		System.out.println(singleWord.get(0));
					break;
				case "who":
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
				case "can":
					YesNo = reader.searchCapableOf(term[1], term[2]);
					if(YesNo)
						System.out.println("Yes");
					else
						System.out.println("No");
					break;
				case "will":
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
				case "do":
					YesNo = reader.searchIsA(term[1], term[2]);
					if(YesNo)
						System.out.println("Yes");
					else
						System.out.println("No");
					break;
				case "does":
					YesNo = reader.searchIsA(term[1], term[2]);
					if(YesNo)
						System.out.println("Yes");
					else
						System.out.println("No");
					break;
				default:
					System.out.println("Input error");
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
