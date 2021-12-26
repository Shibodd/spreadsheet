package graph;

import java.util.Iterator;

public class DFSInfo<K, V> {
	public Iterator<GraphNode<K, V>> childrenIterator;
	public int start;
	public int end;
	
	public DFSInfo(Iterator<GraphNode<K, V>> childrenIterator, int start) {
		this.childrenIterator = childrenIterator;
		this.start = start;
		this.end = -1;
	}
}
