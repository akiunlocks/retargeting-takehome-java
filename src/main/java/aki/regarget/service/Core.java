package aki.regarget.service;

import aki.regarget.model.Catalog;
import aki.regarget.model.Product;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.util.UriBuilder;

import javax.servlet.http.HttpServletResponse;

@Service
public class Core {

    Gson jsonParser = new GsonBuilder().create();


    @Autowired
    Cache cache;

    public void loadStoreCatalog() throws IOException {
        String catalogFile = "/Users/alexbelyansky/eyeview/akiworkspace/retargeting-takehome-java/assets/store-catalog.json";
        try (FileReader fr = new FileReader(catalogFile)) {
            Catalog catalog = loadStoreCatalog(new FileReader(catalogFile));
            for (Product product: catalog.getProducts()) {
                cache.addProduct(product);
            }
        }
    }

    public void loadPromotions() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(
                URI.create("https://aki-public.s3-us-west-2.amazonaws.com/take-home/promotions/promoted_products.csv")
        ).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpServletResponse.SC_OK) {
            throw new IOException("failed fetching promotions : status " + response.statusCode());
        }
        List<Product> promotions = new CsvToBeanBuilder(new StringReader(response.body())).withType(Product.class).build().parse();
        for (Product promotion: promotions) {
            promotion.setSrc(promotion.getUrl());
            cache.addPromotion(promotion);
        }
    }

    public Catalog loadStoreCatalog(Reader input) {
        return jsonParser.fromJson(input, Catalog.class);
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
