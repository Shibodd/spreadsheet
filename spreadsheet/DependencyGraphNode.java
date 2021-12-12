package spreadsheet;

import graph.GraphNode;

public class DependencyGraphNode extends GraphNode {
	IDependencyChangedListener dependencyChangedListener;
	
	public DependencyGraphNode(int id, IDependencyChangedListener dependencyChangedListener) {
		super(id);
		this.dependencyChangedListener = dependencyChangedListener;
	}
}
