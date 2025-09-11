package damnosol.triggeriq.erp.infra.parser;

import damnosol.triggeriq.erp.core.application.port.out.PdfParserPort;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;

@Component
public class PdfParserAdapter implements PdfParserPort {

    private static final Logger log = LoggerFactory.getLogger(PdfParserAdapter.class);

    /**
     * Parse a PDF file and log extracted text.
     *
     * @param fileName File name relative to storage path
     */
    public void parsePdf(String fileName) {
        try {
            File file = Paths.get("./data/uploads", fileName).toFile();
            log.info("Parsing PDF file: {}", file.getAbsolutePath());

            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                log.info("Extracted PDF text:\n{}", text);
            }

        } catch (Exception e) {
            log.error("Failed to parse PDF file: {}", fileName, e);
        }
    }
}