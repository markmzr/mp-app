package marketplace.services;

import java.util.List;

import marketplace.models.Item;
import marketplace.repositories.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Item> findUserItems(String username) {
        List<Item> items = itemRepository.findByUsername(username);
        return items;
    }

    public void saveItem(Item item) {
        itemRepository.save(item);
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
}
