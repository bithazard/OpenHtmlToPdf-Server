package bithazard.server.openhtmltopdfserver.controller;

import bithazard.server.openhtmltopdfserver.document.HtmlDocumentStrategy;
import bithazard.server.openhtmltopdfserver.document.HtmlDocumentStrategyFactory;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/pdf")
@Slf4j
class PdfController {
    private final String basePath;
    private final String baseUrl;
    private final String pdfProducer;
    private final HtmlDocumentStrategyFactory htmlDocumentStrategyFactory;
    private final Provider<PdfRendererBuilder> pdfRendererBuilderProvider;

    public PdfController(@Qualifier("basePath") File basePath, @Value("${pdf.producer:openhtmltopdf.com}") String pdfProducer,
            HtmlDocumentStrategyFactory htmlDocumentStrategyFactory, Provider<PdfRendererBuilder> pdfRendererBuilderProvider) {
        this.basePath = basePath.toString() + File.separator;
        String baseUriString;
        try {
            baseUriString = basePath.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            log.warn("Could not determine base directory. External files in templates or HTML files might not work.");
            baseUriString = "";
        }
        this.baseUrl = baseUriString;
        this.pdfProducer = pdfProducer;
        this.htmlDocumentStrategyFactory = htmlDocumentStrategyFactory;
        this.pdfRendererBuilderProvider = pdfRendererBuilderProvider;
    }

    @PostMapping(path = "/{filepath}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void generatePdf(@PathVariable String filepath, @RequestBody String templateParams, HttpServletResponse httpResponse) throws IOException {
        String fileExtension = getFileExtension(filepath);

        HtmlDocumentStrategy htmlDocumentStrategy = htmlDocumentStrategyFactory.getForExtension(fileExtension);
        Document document = htmlDocumentStrategy.createDocument(basePath + filepath, templateParams);

        String subPath = getSubPath(filepath);
        buildPdfAndWriteToStream(httpResponse.getOutputStream(), document, subPath);
    }

    private String getFileExtension(String filepath) {
        int indexOfLastDot = filepath.lastIndexOf(".");
        if (indexOfLastDot == -1) {
            return "";
        }
        return filepath.substring(indexOfLastDot + 1);
    }

    private String getSubPath(String filepath) {
        int indexOfLastSlash = filepath.lastIndexOf("/");
        if (indexOfLastSlash == -1) {
            return "";
        }
        return filepath.substring(0, indexOfLastSlash);
    }

    private void buildPdfAndWriteToStream(OutputStream outputStream, Document document, String subPath) throws IOException {
        PdfRendererBuilder pdfRendererBuilder = pdfRendererBuilderProvider.get();
        pdfRendererBuilder.withW3cDocument(document, baseUrl + subPath);
        pdfRendererBuilder.withProducer(pdfProducer);
        pdfRendererBuilder.toStream(outputStream);
        pdfRendererBuilder.run();
    }
}
