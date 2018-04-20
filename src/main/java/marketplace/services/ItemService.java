package marketplace.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import marketplace.models.Item;
import marketplace.repositories.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("itemService")
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Item findItem(int id) {
        Item item = itemRepository.findById(id);
        return item;
    }

    public List<Item> findItems(Item item) {
        List<Item> items = itemRepository.findByNameContaining(item.getName());
        return items;
    }

    public List<Item> findItemsSortByPriceAsc(Item item) {
        List<Item> items = itemRepository.findByNameContainingOrderByPriceAsc(item.getName());
        return items;
    }

    public List<Item> findItemsSortByPriceDesc(Item item) {
        List<Item> items = itemRepository.findByNameContainingOrderByPriceDesc(item.getName());
        return items;
    }

    public List<Item> findUserItems() {
        String username = getUsername();
        List<Item> items = itemRepository.findByUsername(username);
        return items;
    }

    public Item saveItem(Item item) {
        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        item.setDate(date);
        String username = getUsername();
        item.setUsername(username);

        Item newItem = itemRepository.save(item);
        return newItem;
    }

    public void updateItem(Item updatedItem) {
        Item item = itemRepository.findById(updatedItem.getId());
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        itemRepository.save(item);
    }

    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    public String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }
}
