package sepm.dsa.service.pdf;

public class PdfGenerationException extends RuntimeException {
	private static final long serialVersionUID = -3174601741649024144L;

	public PdfGenerationException(String message) {
		super(message);
	}

	public PdfGenerationException(String message, Throwable cause) {
		super(message, cause);
	}
}
