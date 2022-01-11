package expressions;

public interface IExpressionTreeAccumulator {
	/**@param accumulator The partial result of the accumulation.
	 * @param value The new value.
	 * @return The new partial result.
	 */
	public Object accumulate(Object accumulator, Object value);
}
