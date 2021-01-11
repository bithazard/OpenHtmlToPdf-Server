package bithazard.server.openhtmltopdfserver;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.util.XRLog;
import org.jsoup.helper.W3CDom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.File;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener
    void onApplicationEvent(ContextRefreshedEvent event) {
        XRLog.setLoggerImpl(new Slf4jLogger());
    }

    @Bean
    Handlebars handlebars() {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
        handlebars.with(new FileTemplateLoader(new File("").getAbsolutePath(), ""));
        handlebars.with(new HighConcurrencyTemplateCache());
        return handlebars;
    }

    @Bean
    W3CDom w3CDom() {
        return new W3CDom();
    }

    @Bean
    @Lazy
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PdfRendererBuilder pdfRendererBuilder() {
        return new PdfRendererBuilder();
    }

    @Bean(name = "basePath")
    File basePath(@Value("${root.dir:documents}") String rootDir) {
        return new File(rootDir);
    }
}
