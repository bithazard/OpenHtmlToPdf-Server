package bithazard.server.openhtmltopdfserver.document;

import org.w3c.dom.Document;

import java.io.IOException;

public interface HtmlDocumentStrategy {
    Document createDocument(String filepath, String templateParams) throws IOException;
}
