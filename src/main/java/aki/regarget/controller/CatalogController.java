package aki.regarget.controller;

import aki.regarget.service.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CatalogController {

    @Autowired
    Cache cache;

    @GetMapping({"/storecatalog"})
    public String getUserListing(Model model) {
        model.addAttribute("grillpath", "weber-grill.jpeg");
        model.addAttribute("sportspath", "bike.jpeg");
        return "store-catalog";
    }

    @GetMapping({"/storecatalog/{category}"})
    public String getProductsInCategory(Model model, @PathVariable String category) {
        model.addAttribute("products", cache.getProducts(category));

        return "catalog";
    }
}
