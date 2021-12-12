package graph;

public class GraphCycleException extends Exception {
	public GraphCycleException() { super(); }
	public GraphCycleException(String message) { super(message); }
	public GraphCycleException(Throwable cause) { super(cause); }
	public GraphCycleException(String message, Throwable cause) { super(message, cause); }
}
