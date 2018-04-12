package marketplace;

import java.io.IOException;
import java.util.List;
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
        return "additem";
    }

    @PostMapping("/additem")
    public String addItemPost(@Valid Item item, BindingResult bindingResult,
                              @RequestParam("files") MultipartFile[] files, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("keywords", new Item());
            return "additem";
        }
        if (files[0].isEmpty() || files.length > 3) {
            model.addAttribute("keywords", new Item());
            model.addAttribute("fileError", "1 to 3 images are required");
            return "additem";
        }

        Item newItem = itemService.saveItem(item);
        for (MultipartFile file : files) {
            imageService.saveImage(newItem, file);
            storageService.saveFile(newItem, file);
        }
        return "redirect:/myaccount";
    }

    @GetMapping("/edititem/{itemId}")
    public String editItem(@PathVariable(value="itemId") int itemId, Model model) {
        Item item = itemService.findItem(itemId);
        List<Image> images = imageService.findItemImages(itemId);
        int imageCount = images.size();

        model.addAttribute("keywords", new Item());
        model.addAttribute("item", item);
        model.addAttribute("images", images);
        model.addAttribute("imageCount", imageCount);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        return "edititem";
    }

    @PostMapping("/edititem/{itemId}")
    public String editItemPost(@Valid Item item, BindingResult bindingResult, @PathVariable(value="itemId") int itemId,
                               @RequestParam("files") MultipartFile[] files, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            List<Image> images = imageService.findItemImages(itemId);
            int imageCount = images.size();

            model.addAttribute("keywords", new Item());
            model.addAttribute("images", images);
            model.addAttribute("imageCount", imageCount);
            model.addAttribute("awsUrl", System.getenv("AWS_URL"));
            return "edititem";
        }
        if (!files[0].isEmpty()) {
            int imageCount = imageService.countItemImages(itemId);
            imageCount += files.length;

            if (imageCount > 3) {
                return "redirect:/edititem/" + itemId;
            }
        }

        item.setId(itemId);
        itemService.updateItem(item);

        for (MultipartFile file : files) {
            imageService.saveImage(item, file);
            storageService.saveFile(item, file);
        }
        return "redirect:/myaccount";
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

    @GetMapping("/removeitem/{itemId}")
    public String removeItem(@PathVariable(value="itemId") int itemId) {
        itemService.deleteItem(itemId);
        return "redirect:/myaccount";
    }

    @GetMapping("/results")
    public String results(@ModelAttribute Item item, Model model) {
        List<Item> items = itemService.findItems(item);
        model.addAttribute("items", items);
        return "results";
    }

    @PostMapping("/signout")
    public String signout() {
        return "redirect:/index";
    }
}