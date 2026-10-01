package br.com.alura.runnercircleapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path diretorioUploads;
    private final String urlBase;

    public WebConfig(@Value("${app.uploads.dir:uploads}") String diretorio,
                     @Value("${app.uploads.url-base:/uploads}") String urlBase) {
        this.diretorioUploads = Paths.get(diretorio).toAbsolutePath().normalize();
        this.urlBase = urlBase;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(urlBase + "/**")
                .addResourceLocations(diretorioUploads.toUri().toString());
    }
}