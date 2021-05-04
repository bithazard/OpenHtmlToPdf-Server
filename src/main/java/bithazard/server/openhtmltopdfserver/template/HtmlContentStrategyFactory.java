package bithazard.server.openhtmltopdfserver.template;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HtmlContentStrategyFactory {
    private final ApplicationContext context;
    private final Map<String, ObjectProvider<? extends HtmlContentStrategy>> extensionStrategyImplementationMap = new HashMap<>();

    public HtmlContentStrategy getForExtension(String extension) {
        ObjectProvider<? extends HtmlContentStrategy> htmlContentStrategy = extensionStrategyImplementationMap.get(extension.toLowerCase(Locale.ENGLISH));
        if (htmlContentStrategy == null) {
            throw new UnknownExtensionException("Extension '" + extension + "' cannot be handled.");
        }
        return htmlContentStrategy.getObject();
    }

    void registerStrategy(String extension, Class<? extends HtmlContentStrategy> strategyImplementation) {
        ObjectProvider<? extends HtmlContentStrategy> strategyImplementationProvider = context.getBeanProvider(strategyImplementation);
        extensionStrategyImplementationMap.put(extension.toLowerCase(Locale.ENGLISH), strategyImplementationProvider);
    }
}
