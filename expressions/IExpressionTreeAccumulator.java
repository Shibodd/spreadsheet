package expressions;

public interface IExpressionTreeAccumulator {
	public Object accumulate(Object accumulator, ExpressionTreeNode node) throws ExpressionTreeTypeException, InvalidExpressionTreeException;
}
