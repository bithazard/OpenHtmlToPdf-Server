package bithazard.server.openhtmltopdfserver.template;

import java.io.IOException;

public interface HtmlContentStrategy {
    String createHtml(String filepath, String templateParams) throws IOException;
}
