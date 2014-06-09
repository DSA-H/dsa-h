package sepm.dsa.service.pdf;

import sepm.dsa.service.TraderService;

import java.io.FileOutputStream;

public interface TraderPdfService {

	/**
	 * Generates a list of the traders' assortment natures in a PDF file
	 * @param fos the outputStream in which the resulting pdf is written
	 * @throws PdfGenerationException
	 */
	void generatePdf(FileOutputStream fos) throws PdfGenerationException;

	void setTraderService(TraderService traderService);
}
