package marketplace;

import java.util.*;
import javax.validation.Valid;

import marketplace.models.Image;
import marketplace.models.Item;
import marketplace.models.User;
import marketplace.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        LinkedHashMap<String, Item> appliances = itemService.getCategoryItems("appliances");
        LinkedHashMap<String, Item> books = itemService.getCategoryItems("books");
        LinkedHashMap<String, Item> electronics = itemService.getCategoryItems("electronics");
        LinkedHashMap<String, Item> furniture = itemService.getCategoryItems("furniture");
        LinkedHashMap<String, Item> tools = itemService.getCategoryItems("tools");
        LinkedHashMap<String, Item> vehicles = itemService.getCategoryItems("vehicles");

        model.addAttribute("appliances", appliances);
        model.addAttribute("books", books);
        model.addAttribute("electronics", electronics);
        model.addAttribute("furniture", furniture);
        model.addAttribute("tools", tools);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        return "index";
    }

    @GetMapping("/myaccount")
    public String myAccount(Model model) {
        List<Item> items = itemService.getUserItems();
        model.addAttribute("items", items);
        return "myaccount";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userService.userExists(user)) {
            bindingResult.rejectValue("username", "error.username", "An account with that username already exists");
            return "register";
        }
        userService.saveUser(user);
        model.addAttribute("registered", "Your account has been created.");
        index(model);
        return "index";
    }

    @GetMapping("/results")
    public String results(@RequestParam(value = "keywords", defaultValue = "", required = false) String keywords,
                          @RequestParam(value = "category", defaultValue = "all", required = false) String category,
                          @RequestParam(value = "condition", defaultValue = "all", required = false) String condition,
                          @RequestParam(value = "sort", defaultValue = "relevance", required = false) String sortBy, Model model) {
        List<Item> items = itemService.getItems(keywords, category, condition, sortBy);
        LinkedHashMap<String, Item> itemsMap = new LinkedHashMap<>();

        for (Item item : items) {
            Image image = imageService.getFirstItemImage(item);
            String imageName = image.getName();
            itemsMap.put(imageName, item);
        }
        String results = items.size() == 1 ? " result for " : " results for ";
        category = category.equals("all") ? "All Categories" : StringUtils.capitalize(category);
        String message = keywords.isEmpty() ? category : "\"" + keywords + "\"";

        model.addAttribute("keywords", keywords);
        model.addAttribute("itemCount", items.size());
        model.addAttribute("results", results);
        model.addAttribute("message", message);
        model.addAttribute("items", itemsMap);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "results";
    }

    @PostMapping("/signout")
    public String signout() {
        return "redirect:/index";
    }
}
