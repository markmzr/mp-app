package marketplace;

import java.util.List;

import marketplace.models.Item;
import marketplace.models.User;
import marketplace.services.ItemService;
import marketplace.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class WebController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/search";
    }

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("keywords", new Item());
        return "search";
    }

    @GetMapping("/results")
    public String results(Model model, @ModelAttribute Item item) {
        List<Item> items = itemService.findItems(item);
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
        return "register";
    }

    @PostMapping("/register")
    public String checkRegistration(@Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userService.userExists(user)) {
            bindingResult.rejectValue("username", "error.username", "An account with that username already exists");
            return "register";
        }
        else {
            userService.saveUser(user);
            return "registersuccess";
        }
    }

    @PostMapping("/signout")
    public String signout() {
        return "redirect:/search";
    }
}
