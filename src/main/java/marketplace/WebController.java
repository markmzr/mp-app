package marketplace;

import java.util.List;

import marketplace.models.Item;
import marketplace.models.User;
import marketplace.services.ItemService;
import marketplace.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class WebController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("keywords", new Item());
        return "index";
    }

    @GetMapping("/additem")
    public String addItem(Model model) {
        model.addAttribute("keywords", new Item());
        model.addAttribute("item", new Item());
        return "additem";
    }

    @PostMapping("/additem")
    public String addItemPost(@ModelAttribute Item item) {
        String username = getUsername();
        item.setUsername(username);
        itemService.saveItem(item);
        return "redirect:/myaccount";
    }

    @GetMapping("/edititem/{id}")
    public String editItem(@PathVariable(value="id") String id, Model model) {
        int idInt = Integer.parseInt(id);
        Item item = itemService.findItem(idInt);
        model.addAttribute("keywords", new Item());
        model.addAttribute("item", item);
        return "edititem";
    }

    @PostMapping("/edititem/{id}")
    public String editItemPost(@PathVariable(value="id") String id, @ModelAttribute Item item) {
        int idInt = Integer.parseInt(id);
        item.setId(idInt);
        itemService.updateItem(item);
        return "redirect:/myaccount";
    }

    @GetMapping("/myaccount")
    public String myAccount(Model model) {
        String username = getUsername();
        List<Item> items = itemService.findUserItems(username);
        model.addAttribute("keywords", new Item());
        model.addAttribute("items", items);
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

    @GetMapping("/results")
    public String results(Model model, @ModelAttribute Item item) {
        List<Item> items = itemService.findItems(item);
        model.addAttribute("items", items);
        return "results";
    }

    @GetMapping("/removeitem/{id}")
    public String removeItem(@PathVariable(value="id") String id) {
        int idInt = Integer.parseInt(id);
        itemService.deleteItem(idInt);
        return "redirect:/myaccount";
    }

    @PostMapping("/signout")
    public String signout() {
        return "redirect:/index";
    }

    public String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }
}
