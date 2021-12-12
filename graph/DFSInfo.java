package graph;

import java.util.Iterator;

public class DFSInfo {
	public Iterator<GraphNode> childrenIterator;
	public int start;
	public int end;
	
	public DFSInfo(Iterator<GraphNode> childrenIterator, int start) {
		this.childrenIterator = childrenIterator;
		this.start = start;
		this.end = -1;
	}
}
