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

    public List<Item> findItems(Item item) {
        List<Item> items = itemRepository.findByNameContaining(item.getName());
        return items;
    }
}
