package marketplace;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import marketplace.models.Item;
import marketplace.services.ImageService;
import marketplace.services.ItemService;
import marketplace.services.StorageService;

@RestController
@RequestMapping("/items")
public class ItemRestController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private StorageService storageService;

    @GetMapping("/")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable int id) {
        return itemService.getItem(id);
    }

    @PostMapping()
    public Item createItem(@RequestBody Item item, Principal principal) {
        item.setUsername(principal.getName());
        itemService.saveItem(item);
        return item;
    }

    @PutMapping("/{id}")
    public void updateItem(@RequestBody Item item, @PathVariable int id,
                           Principal principal) {
        String itemUsername = itemService.getItem(id).getUsername();
        if (principal.getName().equals(itemUsername)) {
            itemService.updateItem(item);
        }
    }

    @DeleteMapping("/{id}")
    public void removeItem(@PathVariable int id, Principal principal) {
        String itemUsername = itemService.getItem(id).getUsername();
        if (principal.getName().equals(itemUsername)) {
            itemService.deleteItem(id);
            storageService.deleteItemFiles(id);
            imageService.deleteItemImages(id);
        }
    }
}
