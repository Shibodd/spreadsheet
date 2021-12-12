package graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class GraphNode {
	int id;
	
	List<GraphNode> children;
	List<GraphNode> parents;
	
	public GraphNode(int id) {
		this.id = id;
		children = new LinkedList<GraphNode>();
		parents = new LinkedList<GraphNode>();
	}
	
	public int getId() { return id; }
	public List<GraphNode> getChildren() { return Collections.unmodifiableList(children); }
	public List<GraphNode> getParents() { return Collections.unmodifiableList(parents); }
	
	public void addChild(GraphNode child) {
		children.add(child);
		child.parents.add(this);
	}
	public void removeChild(GraphNode child) {
		children.remove(child);
		child.parents.remove(this);
	}
	
	public Stack<GraphNode> topologicalSort(Predicate<GraphNode> filter) throws GraphCycleException {
		HashMap<Integer, DFSInfo> nodesInfo = new HashMap<Integer, DFSInfo>();
		
		Stack<GraphNode> stack = new Stack<GraphNode>();
		stack.push(this);
		nodesInfo.put(id, new DFSInfo(children.iterator(), 0));
		
		Stack<GraphNode> ans = new Stack<GraphNode>();
		
		for (int t = 0; !stack.isEmpty(); ++t) {
			GraphNode node = stack.peek();
			
			DFSInfo info = nodesInfo.get(node.id);
			
			// Get the next child of node for which filter tests true
			DFSInfo childInfo = null;
			GraphNode child = null;
			while (info.childrenIterator != null && info.childrenIterator.hasNext()) {
				GraphNode cur = info.childrenIterator.next();

				childInfo = nodesInfo.get(cur.id);
				
				if (childInfo == null) {
					// we haven't yet visited this node.
					nodesInfo.put(cur.id, new DFSInfo(cur.children.iterator(), t + 1));
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
		
		return ans;
	}
}
