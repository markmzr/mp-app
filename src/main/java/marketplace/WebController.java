package marketplace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/search";
    }

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("search", new Search());
        return "search";
    }

    @GetMapping("/results")
    public String results(Model model, @ModelAttribute Search search) {
        Sql sql = new Sql();
        List<Item> items = sql.readItems(search.getKeywords());
        model.addAttribute("items", items);
        return "results";
    }

    @GetMapping("/myaccount")
    public String myAccount() {
        return "myaccount";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        List<Error> errors = new ArrayList<>();
        model.addAttribute("errors", errors);
        return "register";
    }

    @PostMapping("/register")
    public String checkRegistration(Model model, @ModelAttribute User user) {
        if (!user.validUsername() || !user.validPassword()) {
            List<Error> errors = new ArrayList<>();

            if (!user.validUsername()) {
                errors.add(new Error("Username must be between 2 and 30 characters"));
            }

            if (!user.validPassword()) {
                errors.add(new Error("Password must be between 2 and 30 characters"));
            }

            model.addAttribute("errors", errors);
            return "register";
        }

        Sql sql = new Sql();
        if (sql.userExists(user)) {
            List<Error> errors = new ArrayList<>();
            Error error = new Error("Username already exists");
            errors.add(error);
            model.addAttribute("errors", errors);
            return "register";
        }
        else {
            sql.createUser(user);
            return "registersuccess";
        }
    }

    @PostMapping("/signout")
    public String signout() {
        return "redirect:/search";
    }
}
