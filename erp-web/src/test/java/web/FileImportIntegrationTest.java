package web;

import damnosol.triggeriq.erp.adapters.web.controller.FileImportController;
import damnosol.triggeriq.erp.core.application.port.in.FileImportUseCase;
import damnosol.triggeriq.erp.infra.parser.ExcelParserAdapter;
import damnosol.triggeriq.erp.infra.parser.PdfParserAdapter;
import damnosol.triggeriq.erp.infra.storage.FileSystemStorageAdapter;
import damnosol.triggeriq.erp.web.TriggerIqErpApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileImportController.class)
@ContextConfiguration(classes = TriggerIqErpApplication.class)
class FileImportIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(FileImportIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileImportUseCase fileImportUseCase;

    @MockBean
    private FileSystemStorageAdapter storageAdapter;

    @MockBean
    private ExcelParserAdapter excelParser;

    @MockBean
    private PdfParserAdapter pdfParser;

    private MockMultipartFile excelFile;
    private MockMultipartFile pdfFile;

    @BeforeEach
    void setup() throws IOException {
        log.info("Generating test Excel and PDF files for integration test...");

        Path excelPath = TestFileGenerator.generateExcelFile("test_inventory");
        excelFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                Files.readAllBytes(excelPath)
        );
        log.info("Excel test file created at: {}", excelPath);

        Path pdfPath = TestFileGenerator.generatePdfFile("test_inventory");
        pdfFile = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                Files.readAllBytes(pdfPath)
        );
        log.info("PDF test file created at: {}", pdfPath);
    }

    @Test
    void testUploadExcelFile() throws Exception {
        log.info("Testing upload of Excel file...");
        when(storageAdapter.saveFile(anyString(), any(byte[].class)))
                .thenReturn(java.nio.file.Paths.get("/tmp/test.xlsx"));
        when(fileImportUseCase.importFile(any(), any()))
                .thenReturn("Excel file imported successfully");

        mockMvc.perform(multipart("/api/import/upload").file(excelFile))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Excel file imported successfully")));

        verify(fileImportUseCase, times(1)).importFile(any(), any());

        log.info("Excel file upload test passed.");
    }

    @Test
    void testUploadPdfFile() throws Exception {
        log.info("Testing upload of PDF file...");
        when(storageAdapter.saveFile(anyString(), any(byte[].class)))
                .thenReturn(java.nio.file.Paths.get("/tmp/test.pdf"));
        when(fileImportUseCase.importFile(any(), any()))
                .thenReturn("PDF file imported successfully");

        mockMvc.perform(multipart("/api/import/upload").file(pdfFile))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PDF file imported successfully")));

        verify(fileImportUseCase, times(1)).importFile(any(), any());

        log.info("PDF file upload test passed.");
    }

    @Test
    void testUploadUnsupportedFile() throws Exception {
        log.info("Testing upload of unsupported file type...");
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Just text".getBytes()
        );

        when(fileImportUseCase.importFile(any(), any()))
                .thenThrow(new IllegalArgumentException("Unsupported file type"));

        mockMvc.perform(multipart("/api/import/upload").file(txtFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Unsupported file type")));

        log.info("Unsupported file type upload test passed.");
    }
}