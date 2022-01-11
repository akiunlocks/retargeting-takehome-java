package aki.regarget.service;

import aki.regarget.model.Catalog;
import aki.regarget.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public Catalog loadStoreCatalog(Reader input) {
        return jsonParser.fromJson(input, Catalog.class);
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
