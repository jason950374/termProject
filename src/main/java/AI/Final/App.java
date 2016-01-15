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
//			String ss = "Singer is a person";
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
					temp = reader.searchWhere(term[1]);
					if(temp==null)
						temp = reader.searchAnything(term[1]);
					if(temp!=null)
						System.out.println(temp.get(0).getConcept());
					else
						System.out.println("I don't know.");
					break;
				case "when":
					temp = reader.searchWhen(term[1]);
					if(temp==null)
						temp = reader.searchWhen2(term[1]);
					if(temp==null)
						temp = reader.searchAnything(term[1]);
					if(temp!=null)
						System.out.println(temp.get(0).getConcept());
					else
						System.out.println("I don't know.");
//					for(Concept cc:temp){
//						System.out.print(cc.getConcept()+" "+cc.getScore());
////						while(cc.getParent()!=null){
////							cc = cc.getParent();
////							System.out.print(" "+cc.getConcept()+" "+cc.getScore());
////						}
//						System.out.println();
//					}
	            	
					break;
				case "why":
					temp = reader.searchWhy(term[1]);
					if(temp==null || temp.isEmpty())
						temp = reader.searchAnything(term[1]);
					if(temp!=null && !temp.isEmpty())
						System.out.println(temp.get(0).getConcept());
					else
						System.out.println("I don't know.");
//					for(Concept cc:temp){
//						System.out.println(cc.getConcept()+" "+cc.getScore());
////						while(cc.getParent()!=null){
////							cc = cc.getParent();
////							System.out.print(" "+cc.getConcept()+" "+cc.getScore());
////						}
//					}
					break;
				case "what":
					List<Concept> singleWord = new LinkedList<Concept>();
					boolean print = false;
					temp = reader.searchWhat(term[1]);
					if(temp==null)
						temp = reader.searchAnything(term[1]);
					if(temp==null)
						System.out.println("I don't know.");
					else{
						for(Concept cc:temp){
							if(cc.getConcept().contains("_")){
								System.out.println(cc.getConcept());
								print = true;
								break;
							}
							else{	
								singleWord.add(cc);
							}
							while(cc.getParent()!=null){
								cc = cc.getParent();
								System.out.print(" "+cc.getConcept()+" "+cc.getScore());
							}
						}
		            	if(!print&&!singleWord.isEmpty())
		            		System.out.println(singleWord.get(0));
					}
					break;
				case "who":
					temp = reader.searchWho(term[1]);
					if(temp==null)
						temp = reader.searchAnything(term[1]);
					if(temp!=null)
						System.out.println(temp.get(0).getConcept());
					else
						System.out.println("I don't know.");
//					for(Concept cc:temp){
//						System.out.println(cc.getConcept()+" "+cc.getScore());
////						while(cc.getParent()!=null){
////							cc = cc.getParent();
////							System.out.print(" "+cc.getConcept()+" "+cc.getScore());
////						}
//					}
					break;
				case "how":
					temp = reader.searchHow(term[1]);
					if(temp==null)
						temp = reader.searchAnything(term[1]);
					if(temp!=null)
						System.out.println(temp.get(0).getConcept());
					else
						System.out.println("I don't know.");
//					for(Concept cc:temp){
//						System.out.print(cc.getConcept()+" "+cc.getScore());
////						while(cc.getParent()!=null){
////							cc = cc.getParent();
////							System.out.print(" "+cc.getConcept()+" "+cc.getScore());
////						}
//						System.out.println();
//					}
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
