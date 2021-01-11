package bithazard.server.openhtmltopdfserver.document;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HtmlDocumentStrategyFactory {
    private final ApplicationContext context;
    private final Map<String, ObjectProvider<? extends HtmlDocumentStrategy>> extensionStrategyImplementationMap = new HashMap<>();

    public HtmlDocumentStrategy getForExtension(String extension) {
        ObjectProvider<? extends HtmlDocumentStrategy> htmlDocumentStrategy = extensionStrategyImplementationMap.get(extension.toLowerCase(Locale.ENGLISH));
        if (htmlDocumentStrategy == null) {
            throw new UnknownExtensionException("Extension '" + extension + "' cannot be handled.");
        }
        return htmlDocumentStrategy.getObject();
    }

    void registerStrategy(String extension, Class<? extends HtmlDocumentStrategy> strategyImplementation) {
        ObjectProvider<? extends HtmlDocumentStrategy> strategyImplementationProvider = context.getBeanProvider(strategyImplementation);
        extensionStrategyImplementationMap.put(extension.toLowerCase(Locale.ENGLISH), strategyImplementationProvider);
    }
}
