package AI.Final;

import java.util.Comparator;

public class ConceptCmp implements Comparator{
	public int compare(Object arg0, Object arg1) {
		Concept c1 = (Concept)arg0;
		Concept c2 = (Concept)arg1;
		if(c1.getScore()<c2.getScore())
			return 1;
		else
			return -1;
	}
}
