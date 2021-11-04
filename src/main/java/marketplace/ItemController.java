package marketplace;

import java.io.IOException;
import java.util.*;
import javax.validation.Valid;

import marketplace.models.Image;
import marketplace.models.Item;
import marketplace.services.ImageService;
import marketplace.services.ItemService;
import marketplace.services.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ItemController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private StorageService storageService;

    @GetMapping("/additem")
    public String addItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "additem";
    }

    @PostMapping("/additem")
    public String addItemPost(@Valid Item item, BindingResult bindingResult,
                              @RequestParam("latitude") String latitude, @RequestParam("longitude") String longitude,
                              @RequestParam("files") MultipartFile[] files, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
            return "additem";
        }
        if (files[0].isEmpty() || files.length > 3) {
            model.addAttribute("imageError", "1 to 3 images are required");
            model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
            return "additem";
        }
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        Item savedItem = itemService.saveItem(item);

        for (MultipartFile file : files) {
            imageService.saveImage(savedItem, file);
            storageService.saveFile(savedItem, file);
        }
        return "redirect:/myaccount";
    }

    @GetMapping("/edititem")
    public String editItem(@RequestParam(value="id") int itemId, Model model) {
        Item item = itemService.getItem(itemId);
        String itemUsername = item.getUsername();
        String currentUser = getCurrentUser();

        if (!itemUsername.equals(currentUser)) {
            throw new AccessDeniedException("403");
        }
        List<Image> images = imageService.getItemImages(itemId);
        model.addAttribute("item", item);
        model.addAttribute("images", images);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "edititem";
    }

    @PostMapping("/edititem")
    public String editItemPost(@Valid Item item, BindingResult bindingResult, @RequestParam("id") int itemId,
                               @RequestParam("latitude") String latitude, @RequestParam("longitude") String longitude,
                               @RequestParam("files") MultipartFile[] files, Model model) throws Exception {
        String itemUsername = itemService.getItem(itemId).getUsername();
        String currentUser = getCurrentUser();

        if (!itemUsername.equals(currentUser)) {
            throw new AccessDeniedException("403");
        }
        if (bindingResult.hasErrors()) {
            List<Image> images = imageService.getItemImages(itemId);
            model.addAttribute("images", images);
            model.addAttribute("awsUrl", System.getenv("AWS_URL"));
            model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
            return "edititem";
        }
        if (files.length > 0 && !files[0].isEmpty()) {
            int imageCount = imageService.countItemImages(itemId);
            imageCount += files.length;

            if (imageCount > 3) {
                List<Image> images = imageService.getItemImages(itemId);
                int maxImages = 3 - imageService.countItemImages(itemId);

                model.addAttribute("images", images);
                model.addAttribute("imageError", "Only " + maxImages + " more image(s) may be uploaded");
                model.addAttribute("awsUrl", System.getenv("AWS_URL"));
                model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
                return "edititem";
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
        Item item = itemService.getItem(itemId);
        List<Image> images = imageService.getItemImages(itemId);
        model.addAttribute("item", item);
        model.addAttribute("images", images);
        model.addAttribute("awsUrl", System.getenv("AWS_URL"));
        model.addAttribute("apiKey", System.getenv("GOOGLE_API_KEY"));
        return "item";
    }

    @GetMapping("/removeimage/{itemId}/{imageName}")
    public String removeImage(@PathVariable(value="itemId") int itemId,
                              @PathVariable(value="imageName") String imageName, Model model) {
        Item item = itemService.getItem(itemId);
        String itemUsername = item.getUsername();
        String currentUser = getCurrentUser();
        int imageCount = imageService.countItemImages(itemId);

        if (!itemUsername.equals(currentUser) || imageCount == 1) {
            throw new AccessDeniedException("403");
        }
        imageService.deleteImage(itemId, imageName);
        storageService.deleteFile(itemId, imageName);
        return "redirect:/edititem?id=" + itemId;
    }

    @GetMapping("/removeitem")
    public String removeItem(@RequestParam(value="id") int itemId) {
        String itemUsername = itemService.getItem(itemId).getUsername();
        String currentUser = getCurrentUser();

        if (!itemUsername.equals(currentUser)) {
            throw new AccessDeniedException("403");
        }
        itemService.deleteItem(itemId);
        storageService.deleteItemFiles(itemId);
        imageService.deleteItemImages(itemId);
        return "redirect:/myaccount";
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
