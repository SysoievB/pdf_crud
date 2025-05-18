package com.pdf_crud;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfController {
    private final PdfService pdfService;

    @PostMapping("/{name}")
    public ResponseEntity<InputStreamResource> createPdf(@PathVariable String name, @RequestBody String text) {
        record Pair(
                String pdfName,
                InputStreamResource file) {
        }

        try {
            return Optional.of(new Pair(
                                    pdfService.createPdf(name, text),
                                    pdfService.getPdf(name)
                            )
                    )
                    .map(pair -> ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pair.pdfName())
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pair.file()))
                    .orElseThrow();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String name) {
        try {
            InputStreamResource resource = pdfService.getPdf(name);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deletePdf(@PathVariable String name) {
        try {
            pdfService.deletePdf(name);
            return ResponseEntity.ok("Deleted: " + name);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete failed");
        }
    }
}
