package spreadsheet;

public class CellEvaluationError {
	public final Exception exception;
	
	public CellEvaluationError(Exception ex) {
		this.exception = ex;
	}
	
	public String toString() {
		return "ERROR";
	}
}
