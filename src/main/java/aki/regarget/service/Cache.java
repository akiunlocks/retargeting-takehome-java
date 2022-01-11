package aki.regarget.service;


import aki.regarget.model.Product;
import aki.regarget.model.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Cache {
    Map<String, User> users = new HashMap<>();
    Map<String, List<Product>> productsByCategory = new LinkedHashMap<>();
    Map<String, Product> productsById = new HashMap<>();
    Map<String, List<Product>> promotions_by_category = new HashMap<>();
    Map<String, Product> promotions_by_product_id = new LinkedHashMap<> ();

    public void addProduct(Product product) {
        productsById.put(product.getId(), product);
        String category = product.getCategory();
        productsByCategory.computeIfAbsent(category, x -> new LinkedList<Product>()).add(product);
    }

    public void addPromotion(Product promotion) {
        promotions_by_product_id.put(promotion.getId(), promotion);
        promotions_by_category.computeIfAbsent(promotion.getCategory(), x -> new LinkedList<Product>()).add(promotion);
    }
}
