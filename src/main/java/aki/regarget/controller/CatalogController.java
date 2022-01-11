package aki.regarget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CatalogController {

    @GetMapping({"/storecatalog"})
    public String getUserListing(Model model) {
        model.addAttribute("grillpath", "weber-grill.jpeg");
        model.addAttribute("sportspath", "bike.jpeg");
        return "store-catalog";
    }

    @GetMapping({"/storecatalog/{category}"})
    public String getUserListing(Model model, @PathVariable String category) {

    }
}
