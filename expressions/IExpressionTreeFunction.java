package expressions;

import java.util.List;

public interface IExpressionTreeFunction {
	public Object apply(List<ExpressionTreeNode> parameters) throws ExpressionTreeTypeException, InvalidExpressionTreeException;
}
