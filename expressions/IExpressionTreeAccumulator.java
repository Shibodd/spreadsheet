package expressions;

public interface IExpressionTreeAccumulator {
	public Object accumulate(Object accumulator, Object value) throws InvalidExpressionTreeException;
}
