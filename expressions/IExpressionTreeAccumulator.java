package expressions;

public interface IExpressionTreeAccumulator {
	public double accumulate(double accumulator, double value) throws InvalidExpressionTreeException;
}
