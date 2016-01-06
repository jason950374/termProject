package AI.Final;
/**
 * Read concept from conceptnet5 by input the concept you want to search.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonReader {
	Set<String> locationSet;
	Set<String> timeSet;
	Set<String> prepositionsTimeSet;
	public JsonReader(){
		locationSet = new HashSet<String>();
		timeSet = new HashSet<String>();
		prepositionsTimeSet = new HashSet<String>();
	}
	
	private String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}
	public void init(){
		List<String> input;
		
		input = readFile("data\\location.txt");
		locationSet.addAll(input);
		input = readFile("data\\time.txt");
		timeSet.addAll(input);
		input = readFile("data\\prepositionTime.txt");
		prepositionsTimeSet.addAll(input);
	}
	
	private List<String> readFile(String filename)
	{
	  List<String> records = new ArrayList<String>();
	  try
	  {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	    	if(!line.substring(0,1).equals("/"))
	    		records.add(line);
	    }
	    reader.close();
	    return records;
	  }
	  catch (Exception e)
	  {
	    System.err.format("Exception occurred trying to read '%s'.", filename);
	    e.printStackTrace();
	    return null;
	  }
	}
	public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
  	}
	
	
	public void read() throws IOException, JSONException {
		BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
		ResultSet rs = null;
		String keyword = null;
		String str = null;
		String start = null;
		String rel = null;
		String end = null;
		double score = 0;
		String temp[];
		System.out.println("enter the keyword you want to search for:");
		while((keyword = (String)buf.readLine()) != null){
			JSONObject json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
		    int num = (Integer)json.get("numFound");
		    System.out.println(num);
		    for(int i=0; i<num; i++){
		    	rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
		    	if(rel.equals("atlocation")||rel.equals("locationofaction")){
		    		temp = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
			    	if(!temp[2].equals("en"))
			    		continue;
		    		start = temp[3];
		    		System.out.println("start:"+start);
		    		if(start.equals(keyword)){
		    			temp = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
		    			if(!temp[2].equals("en"))
				    		continue;
		    			end = temp[3];
		    			System.out.println("end:"+end);
		    		}
			    	
		    	}
		    	else if(rel.equals("usedfor")){
		    		temp = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
			    	if(!temp[2].equals("en"))
			    		continue;
		    		start = temp[3];
		    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
		    		System.out.println("UsedFor start:"+start);
		    		System.out.println("UsedFor score:"+score);
		    	}
		    }
		}
	}
	
	public Concept searchWhere(String keyword) throws IOException, JSONException{
		JSONObject json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
		Queue<Concept> q = new LinkedList<Concept>();
		Set<String> used = new HashSet<String>();
		List<Concept> location = new LinkedList<Concept>();
		int num = (Integer)json.get("numFound");
		Concept c = null;
		String rel = null;
		String start[];
		String end[];
		double score;
		for(int i=0;i<num;i++){
			c = null;
			rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
			start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
			if(rel.equals("atlocation")||rel.equals("locationofaction")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(start[3].equals(keyword)&&locationSet.contains(end[3])){
	    				c = new Concept(end[3],score*2.5);
	    			}
	    			else if(end[3].equals(keyword)&&locationSet.contains(start[3])){
	    				c = new Concept(start[3],score*2.5);
	    			}
	    			if(c!=null){
	    				for(Concept tmp:location){
		    				if(c.getConcept().equals(tmp.getConcept())){
		    					c = new Concept(c.getConcept(),c.getScore()+tmp.getScore());
		    					location.remove(tmp);
		    					break;
		    				}
		    			}
		    			location.add(c);
	    			}
	    		}
	    	}
	    	else if(rel.equals("usedfor")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(end[3].equals(keyword)&&locationSet.contains(start[3])){
	    				c = new Concept(start[3],score);
	    			}
	    			if(c!=null){
	    				for(Concept tmp:location){
		    				if(c.getConcept().equals(tmp.getConcept())){
		    					c = new Concept(c.getConcept(),c.getScore()+tmp.getScore());
		    					location.remove(tmp);
		    					break;
		    				}
		    			}
		    			location.add(c);
	    			}
	    		}
	    	}
	    	else if(rel.equals("relatedto")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(start[3].equals(keyword)){
	    				q.add(new Concept(end[3],1));
	    			}
	    			else{
	    				q.add(new Concept(start[3],1));
	    			}
	    		}
	    	}
		}
		if(!location.isEmpty()){
			try {
				Collections.sort(location, new ConceptCmp());
			} catch (Exception e) {
				// TODO: handle exception
			}
	        return location.get(0);
		}
		used.add(keyword);
		while(!q.isEmpty()){
			c = q.remove();
			keyword = c.getConcept();
			if(c.getDepth()>3||used.contains(keyword))
				continue;
			json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
			num = (Integer)json.get("numFound");
			for(int i=0;i<num;i++){
				c = null;
				rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
				start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
	    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
	    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
				if(rel.equals("atlocation")||rel.equals("locationofaction")){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword)&&locationSet.contains(end[3])){
		    				c = new Concept(end[3],score*2.5);
		    			}
		    			else if(end[3].equals(keyword)&&locationSet.contains(start[3])){
		    				c = new Concept(start[3],score*2.5);
		    			}
		    			if(c!=null){
		    				for(Concept tmp:location){
			    				if(c.getConcept().equals(tmp.getConcept())){
			    					c = new Concept(c.getConcept(),c.getScore()+tmp.getScore());
			    					location.remove(tmp);
			    					break;
			    				}
			    			}
			    			location.add(c);
		    			}
		    		}
		    	}
		    	else if(rel.equals("usedfor")){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(end[3].equals(keyword)&&locationSet.contains(start[3])){
		    				c = new Concept(start[3],score);
		    			}
		    			if(c!=null){
		    				for(Concept tmp:location){
			    				if(c.getConcept().equals(tmp.getConcept())){
			    					c = new Concept(c.getConcept(),c.getScore()+tmp.getScore());
			    					location.remove(tmp);
			    					break;
			    				}
			    			}
			    			location.add(c);
		    			}
		    		}
		    	}
		    	else if(rel.equals("relatedto")){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword)){
		    				q.add(new Concept(end[3],1));
		    			}
		    			else{
		    				q.add(new Concept(start[3],1));
		    			}
		    		}
		    	}
			}
			if(!location.isEmpty()){
				try {
					Collections.sort(location, new ConceptCmp());
				} catch (Exception e) {
					// TODO: handle exception
				}
		        return location.get(0);
			}
			used.add(keyword);
		}
		
		
		return null;
	}
	
	public List<Concept> searchWhen(String keyword) throws IOException, JSONException{
		JSONObject json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
		Queue<Concept> q = new LinkedList<Concept>();
		Set<String> used = new HashSet<String>();
		List<Concept> time = new LinkedList<Concept>();
		int num = (Integer)json.get("numFound");
		int cDepth = 0;
		Concept c;
		String rel = null;
		String start[];
		String end[];
		double score;
		for(int i=0;i<num;i++){
			c = null;
			rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
			start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
    		if(score < 1)
    			continue;
    			
			if(rel.equals("relatedto")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(start[3].equals(keyword)){
	    				if(timeSet.contains(end[3]))
	    					time.add(new Concept(end[3],score));
	    				else{
	    					String buffer[] = end[3].split("_");
	    					for(int j = 1; j < buffer.length; j++){
	    						if(prepositionsTimeSet.contains(buffer[j-1]) 
	    								&& timeSet.contains(buffer[j])){
	    	    					time.add(new Concept(buffer[j],score));
	    	    					break;
	    						}
	    					}
	    					q.add(new Concept(end[3],1));
	    				}
	    			}
	    			else{
	    				if(timeSet.contains(start[3]))
	    					time.add(new Concept(start[3],score));
	    				else{
	    					String buffer[] = start[3].split("_");
	    					for(int j = 1; j < buffer.length; j++){
	    						if(prepositionsTimeSet.contains(buffer[j-1]) 
	    								&& timeSet.contains(buffer[j])){
	    	    					time.add(new Concept(buffer[j],score));
	    	    					break;
	    						}
	    					}
	    					q.add(new Concept(start[3],1));
	    				}
	    			}
	    		}
	    	}
			
			else if(rel.equals("hassubevent") || rel.equals("hasprerequisite")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(start[3].equals(keyword)){
	    				if(timeSet.contains(end[3]))
	    					time.add(new Concept(end[3],score));
	    				else{
	    					String buffer[] = end[3].split("_");
	    					for(int j = 1; j < buffer.length; j++){
	    						if(prepositionsTimeSet.contains(buffer[j-1]) 
	    								&& timeSet.contains(buffer[j])){
	    	    					time.add(new Concept(buffer[j],score));
	    	    					break;
	    						}
	    					}
	    					q.add(new Concept(end[3],1));
	    				}
	    			}
	    		}
	    	}
		}
		if(!time.isEmpty()){
			try {
				Collections.sort(time, new ConceptCmp());
			} catch (Exception e) {
				// TODO: handle exception
			}
	        return time;
		}
		used.add(keyword);
		
		while(!q.isEmpty()){
			c = q.remove();
			keyword = c.getConcept();
			cDepth = c.getDepth();
			if(cDepth>3||used.contains(keyword))
				continue;
			json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
			num = (Integer)json.get("numFound");
			for(int i=0;i<num;i++){
//				c = null;
				rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
				start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
	    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
	    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
	    		if(score < 1)
	    			continue;
	    		if(rel.equals("relatedto")){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword)){
		    				if(timeSet.contains(end[3]))
		    					time.add(new Concept(end[3],c,score));
		    				else{
		    					String buffer[] = end[3].split("_");
		    					for(int j = 1; j < buffer.length; j++){
		    						if(prepositionsTimeSet.contains(buffer[j-1]) 
		    								&& timeSet.contains(buffer[j])){
		    	    					time.add(new Concept(buffer[j],score));
		    	    					break;
		    						}
		    					}
		    					q.add(new Concept(end[3],c,cDepth+1));
		    				}
		    			}
		    			else{
		    				if(timeSet.contains(start[3]))
		    					time.add(new Concept(start[3],c,score));
		    				else{
		    					String buffer[] = start[3].split("_");
		    					for(int j = 1; j < buffer.length; j++){
		    						if(prepositionsTimeSet.contains(buffer[j-1]) 
		    								&& timeSet.contains(buffer[j])){
		    	    					time.add(new Concept(buffer[j],score));
		    	    					break;
		    						}
		    					}
		    					q.add(new Concept(start[3],c,cDepth+1));
		    				}
		    			}
		    		}
		    	}
	    		
	    		if(rel.equals("hassubevent") || rel.equals("hasprerequisite")){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword)){
		    				if(timeSet.contains(end[3]))
		    					time.add(new Concept(end[3],c,score));
		    				else{
		    					String buffer[] = end[3].split("_");
		    					for(int j = 1; j < buffer.length; j++){
		    						if(prepositionsTimeSet.contains(buffer[j-1]) 
		    								&& timeSet.contains(buffer[j])){
		    	    					time.add(new Concept(buffer[j],score));
		    	    					break;
		    						}
		    					}
		    					q.add(new Concept(end[3],c,cDepth+1));
		    				}
		    			}
		    		}
		    	}
			}
			if(!time.isEmpty()){
				try {
					Collections.sort(time, new ConceptCmp());
				} catch (Exception e) {
					// TODO: handle exception
				}
		        return time;
			}
			used.add(keyword);
		}
		
		
		return null;
	}
	
	public boolean searchCapableOf(String keyword,String goal) throws IOException, JSONException{
		JSONObject json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
		Queue<Concept> q = new LinkedList<Concept>();
		Set<String> used = new HashSet<String>();
		int num = (Integer)json.get("numFound");
		int cDepth = 0;
		Concept c;
		String rel = null;
		String start[];
		String end[];
		String surfaceText;
		String buffer[];
		double score;
		for(int i=0;i<num;i++){
			c = null;
			rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
			start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
    		surfaceText = json.getJSONArray("edges").getJSONObject(i).get("surfaceText").toString();
    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
    		
			if(rel.equals("capableof")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			buffer = end[3].split("_");
	    			if(start[3].equals(keyword)){
	    				for(String s:buffer){
		    				if(goal.equals(s))
		    					return true;
		    			}
	    			}
	    		}
	    	}
			else if(rel.equals("notcapableof")){
				if(start[2].equals("en")&&end[2].equals("en")){
	    			buffer = end[3].split("_");
	    			if(start[3].equals(keyword)){
	    				for(String s:buffer){
		    				if(goal.equals(s))
		    					return false;
		    			}
	    			}
	    		}
	    	}
			else if(rel.equals("relatedto") && score >= 1){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(start[3].equals(keyword)){
	    				q.add(new Concept(end[3],c,1));
	    			}
	    			else{
	    				q.add(new Concept(start[3],c,1));
	    			}
	    		}
	    	}
			else if(rel.equals("isa") && score >= 1){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			if(start[3].equals(keyword) 
	    					&& !(surfaceText.contains("not ") || surfaceText.contains("n't ")) ){
	    				q.add(new Concept(end[3],c,1));
	    			}
	    		}
	    	}
		}
		used.add(keyword);
		
		while(!q.isEmpty()){
			c = q.remove();
			keyword = c.getConcept();
			cDepth = c.getDepth();
			if(cDepth > 1||used.contains(keyword))
				continue;
			json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
			num = (Integer)json.get("numFound");
			for(int i=0;i<num;i++){
//				c = null;
				rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
				start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
	    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
	    		surfaceText = json.getJSONArray("edges").getJSONObject(i).get("surfaceText").toString();
	    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
	    		if(rel.equals("capableof")){
	    			if(start[2].equals("en")&&end[2].equals("en")){
		    			buffer = end[3].split("_");
		    			if(start[3].equals(keyword)){
		    				for(String s:buffer){
			    				if(goal.equals(s)){
			    					return true;
			    				}
			    			}
		    			}
		    		}
		    	}
	    		else if(rel.equals("notcapableof")){
	    			if(start[2].equals("en")&&end[2].equals("en")){
		    			buffer = end[3].split("_");
		    			if(start[3].equals(keyword)){
		    				for(String s:buffer){
			    				if(goal.equals(s))
			    					return false;
			    			}
		    			}
		    		}
		    	}
				else if(rel.equals("relatedto") && score >= 1){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword)){
		    				q.add(new Concept(end[3],c,cDepth+1));
		    			}
		    			else{
		    				q.add(new Concept(start[3],c,cDepth+1));
		    			}
		    		}
		    	}
				else if(rel.equals("isa") && score >= 1){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword) 
		    					&& !(surfaceText.contains("not ") || surfaceText.contains("n't ")) ){
		    				q.add(new Concept(end[3],c, cDepth+1));
		    			}
		    		}
		    	}
			}
			used.add(keyword);
		}
		
		
		return false;
	}
	
	public boolean searchIsA(String keyword,String goal) throws IOException, JSONException{
		JSONObject json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
		Queue<Concept> q = new LinkedList<Concept>();
		Set<String> used = new HashSet<String>();
		int num = (Integer)json.get("numFound");
		int cDepth = 0;
		Concept c;
		String rel = null;
		String start[];
		String end[];
		String surfaceText;
		String buffer[];
		double score;
		for(int i=0;i<num;i++){
			c = null;
			rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
			start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
    		surfaceText = json.getJSONArray("edges").getJSONObject(i).get("surfaceText").toString();
    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
    		
			if(rel.equals("isa")){
	    		if(start[2].equals("en")&&end[2].equals("en")){
	    			buffer = end[3].split("_");
	    			boolean answer = false;
	    			if(start[3].equals(keyword)){
	    				if(goal.equals(end[3])){
	    					answer = true;
	    				}
	    				for(String s:buffer){
	    					if(surfaceText.contains("not ") || surfaceText.contains("n't ") 
	    							|| s.contains("not")){
	    						answer = false;
	    						break;
	    					}
		    				if(goal.equals(s)){
		    					System.out.println("s");
		    					answer = true;
	    					}
		    			}
	    				if(answer){
	    					return true;
	    				}
	    			}
	    		}
	    	}
			else if(rel.equals("notisa") || rel.equals("Antonym")){
				if(start[2].equals("en")&&end[2].equals("en")){
	    			buffer = end[3].split("_");
	    			if(start[3].equals(keyword)){
	    				for(String s:buffer){
		    				if(goal.equals(s))
		    					return false;
		    			}
	    			}
	    		}
	    	}
//			else if(rel.equals("relatedto") && score >= 1){
//	    		if(start[2].equals("en")&&end[2].equals("en")){
//	    			if(start[3].equals(keyword)){
//	    				q.add(new Concept(end[3],c,1));
//	    			}
//	    			else{
//	    				q.add(new Concept(start[3],c,1));
//	    			}
//	    		}
//	    	}
		}
		used.add(keyword);
		
		while(!q.isEmpty()){
			c = q.remove();
			keyword = c.getConcept();
			cDepth = c.getDepth();
			if(cDepth > 1||used.contains(keyword))
				continue;
			json = readJsonFromUrl("http://conceptnet5.media.mit.edu/data/5.4/c/en/"+keyword+"?limit=1000");
			num = (Integer)json.get("numFound");
			for(int i=0;i<num;i++){
//				c = null;
				rel = json.getJSONArray("edges").getJSONObject(i).get("rel").toString().substring(3).toLowerCase();
				start = json.getJSONArray("edges").getJSONObject(i).get("start").toString().split("/");
	    		end = json.getJSONArray("edges").getJSONObject(i).get("end").toString().split("/");
	    		surfaceText = json.getJSONArray("edges").getJSONObject(i).get("surfaceText").toString();
	    		score = (Double) json.getJSONArray("edges").getJSONObject(i).get("weight");
	    		
	    		if(rel.equals("isa")){
	    			if(start[2].equals("en")&&end[2].equals("en")){
	    				/**end term can be the combination of terms**/
		    			buffer = end[3].split("_");
		    			boolean answer = false;
		    			for(String s:buffer){
		    				if(surfaceText.contains("not ") || surfaceText.contains("n't ") 
		    						|| s.contains("not")){
		    					answer = false;
		    					break;
		    				}
			    			if(goal.equals(s)){
			    				answer = true;
		    				}
		    			}
	    				if(answer){
	    					return true;
	    				}
		    		}
		    	}
	    		else if(rel.equals("notisa") || rel.equals("Antonym")){
	    			if(start[2].equals("en")&&end[2].equals("en")){
		    			buffer = end[3].split("_");
		    			if(start[3].equals(keyword)){
		    				for(String s:buffer){
			    				if(goal.equals(s))
			    					return false;
			    			}
		    			}
		    		}
		    	}
//				else if(rel.equals("relatedto") && score >= 1){
//		    		if(start[2].equals("en")&&end[2].equals("en")){
//		    			if(start[3].equals(keyword)){
//		    				q.add(new Concept(end[3],c,cDepth+1));
//		    			}
//		    			else{
//		    				q.add(new Concept(start[3],c,cDepth+1));
//		    			}
//		    		}
//		    	}
				else if(rel.equals("isa") && score >= 1){
		    		if(start[2].equals("en")&&end[2].equals("en")){
		    			if(start[3].equals(keyword)){
		    				q.add(new Concept(end[3],c, cDepth+1));
		    			}
		    		}
		    	}
			}
			used.add(keyword);
		}
		
		
		return false;
	}

	
}

