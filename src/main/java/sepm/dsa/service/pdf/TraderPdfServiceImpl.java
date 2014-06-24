package sepm.dsa.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import sepm.dsa.model.Location;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;
import sepm.dsa.service.TraderService;

import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class TraderPdfServiceImpl implements TraderPdfService {
	final private int cols = 4;

	private TraderService traderService;

	@Override
	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	@Override
	public void generatePdf(FileOutputStream fos) throws PdfGenerationException {
		try {
			// Initialize PDF document
			Document document = new Document(PageSize.A4, 36, 36, 76, 36);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			writer.setPageEvent(new TraderPdfPageEventHelper());
			document.open();

			String traderLocation = null;
			Font headingFont = getHeadingFont();

			List<Trader> traders = getSortedTraders();
			for (Trader trader: traders) {
				// Check for location changes
				if (traderLocation == null || ! traderLocation.equals(trader.getLocation().toString())) {
					if (traderLocation != null) {
						document.newPage();
					}

					traderLocation = trader.getLocation().getName();
					Paragraph locationGrouping = new Paragraph(traderLocation, headingFont);
					locationGrouping.setAlignment(Element.ALIGN_CENTER);
					locationGrouping.setSpacingAfter(30);
					document.add(locationGrouping);
				}

				PdfPTable table = new PdfPTable(cols);
				table.setSpacingAfter(20);
				table.getDefaultCell().setPadding(3);

				// Trader Cell
				StringBuilder traderText = new StringBuilder();
				traderText.append(trader.getName() + " ");
				traderText.append("(" + trader.getCategory().getName() + ")\n");
				traderText.append(trader.getLocation().getName() + "\n");
				if (trader.getComment().length() > 0) {
					traderText.append("Kommentar: " + trader.getComment() + "\n");
				}
				PdfPCell traderCell = new PdfPCell(table.getDefaultCell());
				traderCell.setPhrase(new Phrase(traderText.toString()));
				traderCell.setColspan(cols);

				table.addCell(traderCell);

				// Header Cells
				table.addCell(new Phrase("Vorrat", headingFont));
				table.addCell(new Phrase("Produkt", headingFont));
				table.addCell(new Phrase("Qualität", headingFont));
				table.addCell(new Phrase("Stückpreis", headingFont));

				// Offers
				ArrayList<Offer> sortedOffers = getSortedOffers(trader);
				for (Offer offer: sortedOffers) {
					table.addCell(offer.getAmount().toString());
					table.addCell(offer.getProduct().getName());
					table.addCell(offer.getQuality().getName());
					table.addCell(offer.getPricePerUnit().toString());
				}

				table.setKeepTogether(true);
				table.setHeaderRows(2);
				document.add(table);
			}

			if (traders.size() == 0) {
				document.add(new Paragraph("Keine Händler vorhanden."));
			}
			document.close();
		} catch (DocumentException e) {
			throw new PdfGenerationException("PDF error", e);
		}
	}

	private Font getHeadingFont() {
		Font headingFont = new Font();
		headingFont.setStyle(Font.BOLD);
		headingFont.setSize(14);
		return headingFont;
	}

	private List<Trader> getSortedTraders() {
		List<Trader> traders = traderService.getAll();
		traders.sort((o1, o2) -> {
			Location l1 = o1.getLocation();
			Location l2 = o2.getLocation();
			int regionCompare = l1.getRegion().getName().compareTo(l2.getRegion().getName());
			if (regionCompare != 0) return regionCompare;

			return l1.getName().compareTo(l2.getName());
		});
		return traders;
	}

	private ArrayList<Offer> getSortedOffers(Trader trader) {
		ArrayList<Offer> sortedOffers = new ArrayList<>(trader.getOffers());
		sortedOffers.sort((o1, o2) -> {
			int nameCompare = o1.getProduct().getName().compareTo(o2.getProduct().getName());
			if (nameCompare != 0) return nameCompare;

			return o1.getQuality().compareTo(o2.getQuality());
		});
		return sortedOffers;
	}

	/**
	 * Private Event class to add a header with page numbers
	 * Taken from http://itextpdf.com/examples/iia.php?id=104
	 */
	private class TraderPdfPageEventHelper extends PdfPageEventHelper {
		/** The header text. */
		String header;
		/** The template with the total number of pages. */
		PdfTemplate total;

		/**
		 * Allows us to change the content of the header.
		 * @param header The new header String
		 */
		public void setHeader(String header) {
			this.header = header;
		}

		/**
		 * Creates the PdfTemplate that will hold the total number of pages.
		 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
		 *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
		 */
		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(30, 20);
		}

		/**
		 * Adds a header to every page
		 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
		 *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
		 */
		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable table = new PdfPTable(3);
			try {
				table.setWidths(new int[]{24, 24, 4});
				table.setTotalWidth(527);
				table.setLockedWidth(true);
				table.getDefaultCell().setFixedHeight(20);
				table.getDefaultCell().setBorder(Rectangle.BOTTOM);
				table.addCell(header);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(String.format("Seite %d von", writer.getPageNumber()));
				PdfPCell cell = new PdfPCell(Image.getInstance(total));
				cell.setBorder(Rectangle.BOTTOM);
				table.addCell(cell);
				table.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
			}
			catch(DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}

		/**
		 * Fills out the total number of pages before the document is closed.
		 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(
		 *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
		 */
		public void onCloseDocument(PdfWriter writer, Document document) {
			Phrase text = new Phrase(String.valueOf(writer.getPageNumber() - 1));
			ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 2, 6, 0);
		}
	}
}
