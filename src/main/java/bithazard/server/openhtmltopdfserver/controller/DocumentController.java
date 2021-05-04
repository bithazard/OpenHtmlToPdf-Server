package bithazard.server.openhtmltopdfserver.controller;

import bithazard.server.openhtmltopdfserver.template.HtmlContentStrategy;
import bithazard.server.openhtmltopdfserver.template.HtmlContentStrategyFactory;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

@RestController
@Slf4j
class DocumentController {
    private final String basePath;
    private final String baseUrl;
    private final String pdfProducer;
    private final HtmlContentStrategyFactory htmlContentStrategyFactory;
    private final Provider<PdfRendererBuilder> pdfRendererBuilderProvider;
    private final W3CDom w3CDom;

    public DocumentController(@Qualifier("basePath") File basePath, @Value("${pdf.producer:openhtmltopdf.com}") String pdfProducer,
            HtmlContentStrategyFactory htmlContentStrategyFactory, Provider<PdfRendererBuilder> pdfRendererBuilderProvider, W3CDom w3CDom) {
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
        this.htmlContentStrategyFactory = htmlContentStrategyFactory;
        this.pdfRendererBuilderProvider = pdfRendererBuilderProvider;
        this.w3CDom = w3CDom;
    }

    @PostMapping(path = "/html/{*filepath}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> generateHtml(@PathVariable String filepath, @RequestBody String templateParams) throws IOException {
        String html = createHtml(filepath, templateParams);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @PostMapping(path = "/pdf/{*filepath}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void generatePdf(@PathVariable String filepath, @RequestBody String templateParams, HttpServletResponse httpResponse) throws IOException {
        String html = createHtml(filepath, templateParams);

        Document document = getW3cDocumentFromHtml(html);
        String subPath = getSubPath(filepath);
        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        buildPdfAndWriteToStream(httpResponse.getOutputStream(), document, subPath);
    }

    private String createHtml(String filepath, String templateParams) throws IOException {
        String fileExtension = getFileExtension(filepath);

        HtmlContentStrategy htmlContentStrategy = htmlContentStrategyFactory.getForExtension(fileExtension);
        return htmlContentStrategy.createHtml(basePath + filepath, templateParams);
    }

    private String getFileExtension(String filepath) {
        int indexOfLastDot = filepath.lastIndexOf(".");
        if (indexOfLastDot == -1) {
            return "";
        }
        return filepath.substring(indexOfLastDot + 1);
    }

    private Document getW3cDocumentFromHtml(String html) {
        org.jsoup.nodes.Document jSoupDocument = Jsoup.parse(html);
        return w3CDom.fromJsoup(jSoupDocument);
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
