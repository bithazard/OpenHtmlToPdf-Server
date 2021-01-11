package bithazard.server.openhtmltopdfserver.document;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
class DirectHtmlDocumentStrategy implements HtmlDocumentStrategy {
    private final HtmlDocumentStrategyFactory htmlDocumentStrategyFactory;
    private final W3CDom w3CDom;

    @PostConstruct
    private void postConstruct() {
        htmlDocumentStrategyFactory.registerStrategy("html", this.getClass());
    }

    @Override
    public Document createDocument(String filepath, String templateParams) throws IOException {
        org.jsoup.nodes.Document jSoupDocument = Jsoup.parse(new File(filepath), "UTF-8");
        return w3CDom.fromJsoup(jSoupDocument);
    }
}
