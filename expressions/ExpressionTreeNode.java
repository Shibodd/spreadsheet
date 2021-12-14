package expressions;


import java.util.LinkedList;
import java.util.List;

public abstract class ExpressionTreeNode {
	protected List<ExpressionTreeNode> children;
	
	public ExpressionTreeNode() {
		children = new LinkedList<ExpressionTreeNode>();
	}
	
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException { 
		children.add(node);
	}
	public void removeChild(ExpressionTreeNode node) {
		children.remove(node);
	}

	public abstract double evaluate() throws InvalidExpressionTreeException;
}
