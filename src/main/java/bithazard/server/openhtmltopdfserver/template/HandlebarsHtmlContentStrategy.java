package bithazard.server.openhtmltopdfserver.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
class HandlebarsHtmlContentStrategy implements HtmlContentStrategy {
    private final HtmlContentStrategyFactory htmlContentStrategyFactory;
    private final Handlebars handlebars;
    private final ObjectMapper objectMapper;

    @PostConstruct
    private void postConstruct() {
        htmlContentStrategyFactory.registerStrategy("hbs", this.getClass());
    }

    @Override
    public String createHtml(String filepath, String templateParams) throws IOException {
        Template template;
        try {
            template = handlebars.compile(filepath);
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("The template " + ex.getMessage() + " was not found.");
        }
        Context context = getContextFromJson(templateParams);
        return template.apply(context);
    }

    private Context getContextFromJson(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
        return Context.newBuilder(jsonNode).resolver(JsonNodeValueResolver.INSTANCE).build();
    }
}
