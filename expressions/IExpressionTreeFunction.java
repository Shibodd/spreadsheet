package expressions;

public interface IExpressionTreeFunction {
	public double apply(double[] parameters) throws InvalidExpressionTreeException;
}
