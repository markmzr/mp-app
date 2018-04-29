package marketplace;

import java.io.IOException;
import java.util.*;
import javax.validation.Valid;

import marketplace.models.Image;
import marketplace.models.Item;
import marketplace.models.User;
import marketplace.services.ImageService;
import marketplace.services.ItemService;
import marketplace.services.StorageService;
import marketplace.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class WebController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private StorageService storageService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("keywords", new Item());
        return "index";
    }

    @GetMapping("/additem")
    public String addItem(Model model) {
        model.addAttribute("keywords", new Item());
        model.addAttribute("item", new Item());
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "additem";
    }

    @PostMapping("/additem")
    public String addItemPost(@Valid Item item, BindingResult bindingResult,
                              @RequestParam("latitude") String latitude, @RequestParam("longitude") String longitude,
                              @RequestParam("files") MultipartFile[] files, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("keywords", new Item());
            model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
            return "additem";
        }
        if (files[0].isEmpty() || files.length > 3) {
            model.addAttribute("keywords", new Item());
            model.addAttribute("fileError", "1 to 3 images are required");
            model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
            return "additem";
        }

        item.setLatitude(latitude);
        item.setLongitude(longitude);
        System.out.println("lat: " + latitude + "long: " + longitude);
        Item newItem = itemService.saveItem(item);

        for (MultipartFile file : files) {
            imageService.saveImage(newItem, file);
            storageService.saveFile(newItem, file);
        }
        return "redirect:/myaccount";
    }

    @GetMapping("/edititem")
    public String editItem(@RequestParam(value="id") int itemId, Model model) {
        Item item = itemService.findItem(itemId);
        List<Image> images = imageService.findItemImages(itemId);
        int imageCount = images.size();

        model.addAttribute("keywords", new Item());
        model.addAttribute("item", item);
        model.addAttribute("images", images);
        model.addAttribute("imageCount", imageCount);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "edititem";
    }

    @PostMapping("/edititem")
    public String editItemPost(@Valid Item item, BindingResult bindingResult, @RequestParam("id") int itemId,
                               @RequestParam("latitude") String latitude, @RequestParam("longitude") String longitude,
                               @RequestParam("files") MultipartFile[] files, Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            List<Image> images = imageService.findItemImages(itemId);
            int imageCount = images.size();

            model.addAttribute("keywords", new Item());
            model.addAttribute("item", item);
            model.addAttribute("images", images);
            model.addAttribute("imageCount", imageCount);
            model.addAttribute("awsUrl", System.getenv("AWS_URL"));
            model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
            return "edititem";
        }
        if (files.length > 0 && !files[0].isEmpty()) {
            int imageCount = imageService.countItemImages(itemId);
            imageCount += files.length;

            if (imageCount > 3) {
                return "redirect:/edititem?id=" + itemId;
            }

            for (MultipartFile file : files) {
                imageService.saveImage(item, file);
                storageService.saveFile(item, file);
            }
        }

        item.setId(itemId);
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        itemService.updateItem(item);
        return "redirect:/myaccount";
    }

    @GetMapping("/item")
    public String item(@RequestParam("id") int itemId, Model model) {
        Item item = itemService.findItem(itemId);
        List<Image> images = imageService.findItemImages(itemId);

        model.addAttribute("keywords", new Item());
        model.addAttribute("item", item);
        model.addAttribute("images", images);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "item";
    }

    @GetMapping("/myaccount")
    public String myAccount(Model model) {
        List<Item> items = itemService.findUserItems();
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
    public String checkRegistration(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userService.userExists(user)) {
            bindingResult.rejectValue("username", "error.username", "An account with that username already exists");
            return "register";
        }

        userService.saveUser(user);
        model.addAttribute("registerSuccess", "Your account has been successfully created.");
        model.addAttribute("keywords", new Item());
        return "index";
    }

    @GetMapping("/removeimage/{itemId}/{imageName}")
    public String removeImage(@PathVariable(value="itemId") int itemId,
                              @PathVariable(value="imageName") String imageName) {
        int imageCount = imageService.countItemImages(itemId);
        if (imageCount == 1) {
            return "redirect:/edititem/" + itemId;
        }

        imageService.deleteImage(itemId, imageName);
        storageService.deleteFile(itemId, imageName);
        return "redirect:/edititem/" + itemId;
    }

    @GetMapping("/removeitem")
    public String removeItem(@RequestParam(value="id") int itemId) {
        itemService.deleteItem(itemId);
        storageService.deleteItemFiles(itemId);
        imageService.deleteItemImages(itemId);
        return "redirect:/myaccount";
    }

    @GetMapping("/results")
    public String results(@ModelAttribute Item item, @RequestParam(value="condition") String condition,
                          @RequestParam(value="sort") String sortBy, Model model) {
        List<Item> items = new ArrayList<Item>();
        switch(sortBy) {
            case "relevance" :
                items = itemService.findItems(item);
                break;
            case "priceasc" :
                items = itemService.findItemsSortByPriceAsc(item);
                break;
            case "pricedesc" :
                items = itemService.findItemsSortByPriceDesc(item);
                break;
        }

        if (condition.equals("new") || condition.equals("used")) {
            Iterator<Item> iterator = items.iterator();
            while (iterator.hasNext()) {
                Item it = iterator.next();

                if (!it.getItemCondition().equals(condition)) {
                    iterator.remove();
                }
            }
        }

        LinkedHashMap<String, Item> map = new LinkedHashMap<String, Item>();
        for (Item it : items) {
            Image image = imageService.findFirstItemImage(it);
            map.put(image.getName(), it);
        }

        model.addAttribute("keywords", new Item());
        model.addAttribute("resultCount", items.size());
        model.addAttribute("items", map);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "results";
    }

    @PostMapping("/signout")
    public String signout() {
        return "redirect:/index";
    }
}