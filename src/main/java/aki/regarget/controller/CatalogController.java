package aki.regarget.controller;

import aki.regarget.model.Product;
import aki.regarget.model.User;
import aki.regarget.service.Cache;
import aki.regarget.service.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CatalogController {

    static Set<String> SUPPOTED_CATEGORIES = Set.of("sports","grilling");

    @Autowired
    Cache cache;

    @Autowired
    Core core;

    @GetMapping({"/web/storecatalog"})
    public String getUserListing(Model model) {
        model.addAttribute("grillpath", "weber-grill.jpeg");
        model.addAttribute("sportspath", "bike.jpeg");
        return "store-catalog";
    }

    @GetMapping({"/web/storecatalog/{category}"})
    public String getProductsInCategory(Model model, @PathVariable String category, HttpServletResponse response, @CookieValue(name="akiuser", defaultValue = "") String akiuserid) {
        if (!SUPPOTED_CATEGORIES.contains(category)) {
            model.addAttribute("error_msg", "unsuppoted category " + category);
            return "error";
        }
        akiuserid = setCookieIfNeeded(response, akiuserid);
        cache.users.get(akiuserid).categories.add(category);

        model.addAttribute("products", cache.getProducts(category));

        return "catalog";
    }

    @GetMapping({"/web/productclicked/{productId}"})
    public String productClicked(@PathVariable String productId, HttpServletResponse response, @CookieValue(name="akiuser", defaultValue = "") String akiuserid) {
        akiuserid = setCookieIfNeeded(response, akiuserid);
        cache.users.get(akiuserid).productsClicked.add(productId);

        return "store-catalog";

    }

    @GetMapping({"/web/quoteoftheday"})
    public String quoteOfTheDay(@CookieValue(name="akiuser", defaultValue = "") String akiuserid, Model model) {
        Product product = cache.getProducts("default").get(0);

        if (!StringUtils.isEmpty(akiuserid)) {
            User akiUser = cache.users.get(akiuserid);
            if (!CollectionUtils.isEmpty(akiUser.productsClicked)) {
                HashSet<String> dbProductIdsClicked = new HashSet(akiUser.productsClicked);
                dbProductIdsClicked.retainAll(cache.promotions_by_product_id.keySet());
                List<Product> dbProductsClicked = dbProductIdsClicked
                        .stream()
                        .map(p -> cache.lookupProduct(p).get())
                        .collect(
                                Collectors.toList()
                        );
                Collection<Product> dbProductsClickedNotCapped = akiUser.getNonFreqCappedProducts(dbProductsClicked);

                if (!CollectionUtils.isEmpty(dbProductsClickedNotCapped)) {
                    product = dbProductsClickedNotCapped.iterator().next();
                    return showProductAd(model, product, akiUser);
                }
            }
            if (!CollectionUtils.isEmpty(akiUser.categories)) {
                HashSet<String> categoriesVisited = new HashSet<>(akiUser.categories);
                categoriesVisited.retainAll(cache.productsByCategory.keySet());
                if (!categoriesVisited.isEmpty()) {
                    String chosenCategory = categoriesVisited.iterator().next();
                    List<Product> products = cache.productsByCategory.get(chosenCategory);
                    Collection<Product> productsNotFreqCapped = akiUser.getNonFreqCappedProducts(products);
                    if (!productsNotFreqCapped.isEmpty()) {
                        product = productsNotFreqCapped.iterator().next();
                        return showProductAd(model, product, akiUser);
                    }
                }
            }
        }


        return showProductAd(model, product, null);
    }

    private String showProductAd(Model model, Product product, User akiUser) {
        model.addAttribute("product", product);
        if (akiUser != null) {
            akiUser.promotionShown(product);
        }
        return "adpage";
    }

    private String setCookieIfNeeded(
            HttpServletResponse response,
            @CookieValue(name = "akiuser", defaultValue = "") String akiuserid
    ) {
        if (StringUtils.isEmpty(akiuserid)) {
            akiuserid = core.generateUniqueId();
            cache.getOrAddUser(akiuserid);
            Cookie cookie = new Cookie("akiuser", akiuserid);
            cookie.setPath("/web");
            cookie.setMaxAge(300);
            response.addCookie(cookie);
        }

        return akiuserid;
    }

}
