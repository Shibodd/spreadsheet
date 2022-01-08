package graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/** A generic graph node. */
public class GraphNode<K, V> {	
	List<GraphNode<K, V>> children;
	List<GraphNode<K, V>> parents;
	
	K id;
	
	public V data;
	
	public GraphNode(K id, V data) {
		this.id = id;
		children = new LinkedList<GraphNode<K, V>>();
		parents = new LinkedList<GraphNode<K, V>>();
		this.data = data;
	}
	
	public K getId() { return id; }
	public List<GraphNode<K, V>> getChildren() { return Collections.unmodifiableList(children); }
	public List<GraphNode<K, V>> getParents() { return Collections.unmodifiableList(parents); }
	
	public void addChild(GraphNode<K, V> child) {
		if (children.contains(child))
			return;
		
		children.add(child);
		child.parents.add(this);
	}
	public void removeChild(GraphNode<K, V> child) {
		children.remove(child);
		child.parents.remove(this);
	}
	
	public void addParent(GraphNode<K, V> parent) {
		if (parents.contains(parent))
			return;
		
		parents.add(parent);
		parent.children.add(this);
	}
	public void removeParent(GraphNode<K, V> parent) {
		parents.remove(parent);
		parent.children.remove(this);
	}
	
	public void removeAllParents() {
		while (parents.size() > 0)
			parents.remove(0).children.remove(this);
	}
	
	public void removeAllChildren() {
		while (children.size() > 0)
			children.remove(0).parents.remove(this);
	}
	
	
	/** Performs a topological sort on the graph with an iterative DFS and returns the nodes in topological order. 
	 * @throws GraphCycleException A cycle was detected in the graph, therefore a topological order cannot be determined.
	 */
	public Stack<GraphNode<K, V>> topologicalSort() throws GraphCycleException {
		Stack<GraphNode<K, V>> ans = new Stack<GraphNode<K, V>>();
		
		topologicalSort(ans, new HashMap<K, DFSInfo<K, V>>());
		
		return ans;
	}
	
	/** Performs a topological sort on the graph with an iterative DFS using parameter nodesInfo to store the DFS data, and pushes the nodes on the stack passed as parameter. 
	 * @throws GraphCycleException A cycle was detected in the graph, therefore a topological order cannot be determined.
	 */
	public void topologicalSort(Stack<GraphNode<K, V>> ans, HashMap<K, DFSInfo<K, V>> nodesInfo) throws GraphCycleException {
		Stack<GraphNode<K, V>> stack = new Stack<GraphNode<K, V>>();
		stack.push(this);
		nodesInfo.put(id, new DFSInfo<K, V>(children.iterator(), 0));
		
		for (int t = 0; !stack.isEmpty(); ++t) {
			GraphNode<K, V> node = stack.peek();
			
			DFSInfo<K, V> info = nodesInfo.get(node.id);
			
			// Get the next child of node for which filter tests true
			DFSInfo<K, V> childInfo = null;
			GraphNode<K, V> child = null;
			while (info.childrenIterator != null && info.childrenIterator.hasNext()) {
				GraphNode<K, V> cur = info.childrenIterator.next();

				childInfo = nodesInfo.get(cur.id);
				
				if (childInfo == null) {
					// we haven't yet visited this node.
					nodesInfo.put(cur.id, new DFSInfo<K, V>(cur.children.iterator(), t + 1));
					child = cur;
					break;
				}
				else if (childInfo.end == -1) // a child of this node is active in the DFS, therefore there must be a cycle.
					throw new GraphCycleException("Cannot perform a Topological Sort when there are cycles in the graph.");
				
				// else, we already finished a DFS on this node, so check the next one;
			}

			
			// If there are no children of node left
			if (child == null) {
				info.childrenIterator = null; // release the iterator for the GC to collect
				info.end = t;
				stack.pop(); // remove this node
				ans.push(node); // push it to the result
			} else {
				stack.push(child); // child is the next node to be evaluated.
			}
		}
	}
}
