package AI.Final;

import java.util.LinkedList;
import java.util.List;

public class Concept {
	private String concept;
	private List<String> rel;
	private int depth;
	private Concept parent;
	private double score;
	public Concept(String c,String r,int d) {
		this.concept = c;
		this.rel = new LinkedList<String>();
		this.rel.add(r);
		this.depth = d;
		this.parent = null;
		this.score = 0;
	}
	public Concept(String c,String r,double s) {
		this.concept = c;
		this.rel = new LinkedList<String>();
		this.rel.add(r);
		this.depth = 0;
		this.parent = null;
		this.score = s;
	}
	public Concept(String c,String r,Concept con,int d) {
		this.concept = c;
		this.rel = new LinkedList<String>();
		this.rel.add(r);
		this.depth = d;
		this.parent = con;
		this.score = 0;
	}
	public Concept(String c,String r,Concept con,double s) {
		this.concept = c;
		this.rel = new LinkedList<String>();
		this.rel.add(r);
		this.depth = 0;
		this.parent = con;
		this.score = s;
	}
	public void setScore(double s){
		this.score = s;
	}
	public void addRel(String r){
		this.rel.add(r);
	}
	public void addRel(List<String> r){
		this.rel.addAll(r);
	}
	public String getConcept(){
		return this.concept;
	}
	public List<String> getRel(){
		return this.rel;
	}
	public int getDepth(){
		return this.depth;
	}
	public double getScore(){
		return this.score;
	}
	public Concept getParent(){
		return this.parent;
	}
}
