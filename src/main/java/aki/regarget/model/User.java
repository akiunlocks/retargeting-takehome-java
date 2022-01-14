package aki.regarget.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class User {
    private static final Integer FREQ_CAP_LIMIT = 2;
    public Set<String> categories = new HashSet<>();
    public Set<String> productsClicked = new HashSet<>();
    public Map<String, AtomicInteger> productsAdvertised = new HashMap<>();

    public Collection<Product> getNonFreqCappedProducts(Collection<Product> products) {
        return products.stream()
                .filter(
                        x -> productsAdvertised.getOrDefault(x.getId(), new AtomicInteger(0)).get() < FREQ_CAP_LIMIT
                )
                .collect(
                        Collectors.toList()
                );
    }

    public void promotionShown(Product product) {
        productsAdvertised.putIfAbsent(product.getId(), new AtomicInteger(0));
        productsAdvertised.get(product.getId()).incrementAndGet();
    }
}
