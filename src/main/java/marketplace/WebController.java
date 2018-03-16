package marketplace;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/search";
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("search", new Search());
        return "search";
    }

    @GetMapping("/results")
    public String searchSubmit(Model model, @ModelAttribute Search search) {
        Sql sql = new Sql();
        List<Item> items = sql.readItems(search.getKeywords());
        model.addAttribute("items", items);
        return "results";
    }
}
