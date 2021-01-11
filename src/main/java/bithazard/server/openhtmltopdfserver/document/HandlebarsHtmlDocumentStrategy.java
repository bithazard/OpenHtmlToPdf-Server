package bithazard.server.openhtmltopdfserver.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
class HandlebarsHtmlDocumentStrategy implements HtmlDocumentStrategy {
    private final HtmlDocumentStrategyFactory htmlDocumentStrategyFactory;
    private final Handlebars handlebars;
    private final ObjectMapper objectMapper;
    private final W3CDom w3CDom;

    @PostConstruct
    private void postConstruct() {
        htmlDocumentStrategyFactory.registerStrategy("hbs", this.getClass());
    }

    @Override
    public Document createDocument(String filepath, String templateParams) throws IOException {
        Context context = getContextFromJson(templateParams);
        Template template;
        try {
            template = handlebars.compile(filepath);
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("The template " + ex.getMessage() + " was not found.");
        }
        String html = template.apply(context);
        return getW3cDocumentFromHtml(html);
    }

    private Context getContextFromJson(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
        return Context.newBuilder(jsonNode).resolver(JsonNodeValueResolver.INSTANCE).build();
    }

    private Document getW3cDocumentFromHtml(String html) {
        org.jsoup.nodes.Document jSoupDocument = Jsoup.parse(html);
        return w3CDom.fromJsoup(jSoupDocument);
    }
}
