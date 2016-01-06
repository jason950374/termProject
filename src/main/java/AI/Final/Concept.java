package AI.Final;

public class Concept {
	private String concept;
	private int depth;
	private Concept parent;
	private double score;
	public Concept(String c,int d) {
		this.concept = c;
		this.depth = d;
		this.parent = null;
		this.score = 0;
	}
	public Concept(String c,double s) {
		this.concept = c;
		this.depth = 0;
		this.parent = null;
		this.score = s;
	}
	public Concept(String c,Concept con,int d) {
		this.concept = c;
		this.depth = d;
		this.parent = con;
		this.score = 0;
	}
	public Concept(String c,Concept con,double s) {
		this.concept = c;
		this.depth = 0;
		this.parent = con;
		this.score = s;
	}
	public String getConcept(){
		return this.concept;
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
