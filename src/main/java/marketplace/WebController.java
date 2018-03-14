package marketplace;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    /*
    @GetMapping("/hello")
    public String hello(@RequestParam(name="name", required=false, defaultValue="Heroku") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }
    */

    /*
    @GetMapping("/index")
    public String searchForm(Model model) {
        model.addAttribute("search", new Search());
        return "index";
    }

    @PostMapping("/search")
    public String searchSubmit(@ModelAttribute Search search) {
        return "result";
    }
    */

    @GetMapping("/")
    public String index() {
        return "redirect:/search";
    }


    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("search", new Search());
        return "search";
    }

    @PostMapping("/search")
    public String searchSubmit(@ModelAttribute Search search) {
        return "result";
    }

}
