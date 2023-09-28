package com.bestshop.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    //Expose a directory on the file system - to be accessible by the clients
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";//This defines the name of the directory containing user photos.
        Path userPhotosDir = Paths.get("user-photos");//object representing the directory where user photos are stored.

        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + dirName + "/**")// any request for a URL that starts with /user-photos/ will be handled by this resource handler.
                .addResourceLocations("file:/" + userPhotosPath + "/");

        // Categories

        String categoryImagesDirName = "../category-images";
        Path categoryImagesDir = Paths.get(categoryImagesDirName);

        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/category-images/**")
                .addResourceLocations("file:/" + categoryImagesPath + "/");

        // Brands

        String brandLogosDirName = "../brand-logos";
        Path brandLogosDir = Paths.get(brandLogosDirName);

        String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/brand-logos/**")
                .addResourceLocations("file:/" + brandLogosPath + "/");

    }

}
