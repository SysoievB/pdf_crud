package com.pdf_crud;

import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
public class PdfService {
    private static final String UPLOAD_DIR = "pdfs/";
    private static final String FILE_EXTENSION = ".pdf";

    public PdfService() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createPdf(String fileName, String text) {
        try (val document = new PDDocument()) {
            val page = new PDPage();
            document.addPage(page);

            try (val contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);

                Arrays.stream(text.split("\n"))
                        .forEach(line -> {
                            try {
                                contentStream.showText(line);
                                contentStream.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                contentStream.endText();
            }
            val fullName = fileName + FILE_EXTENSION;
            document.save(UPLOAD_DIR + fullName);
            document.close();

            return fullName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStreamResource getPdf(String fileName) throws IOException {
        Path path = Paths.get(UPLOAD_DIR + fileName + FILE_EXTENSION);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("PDF not found");
        }
        return new InputStreamResource(Files.newInputStream(path));
    }


    public void deletePdf(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(UPLOAD_DIR + fileName + FILE_EXTENSION));
    }
}
