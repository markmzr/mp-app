package marketplace.services;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import marketplace.models.Image;
import marketplace.models.Item;
import marketplace.models.QItem;
import marketplace.repositories.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("itemService")
public class ItemService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageService imageService;

    public Item getItem(int id) {
        return itemRepository.findById(id);
    }

    public List<Item> getItems(String keywords, String category, String condition, String sortBy) {
        QItem item = QItem.item;
        BooleanExpression keywordsEx = item.name.contains(keywords);
        if (category.equals("all")) {
            category = "";
        }
        BooleanExpression categoryEx = item.category.contains(category);

        if (condition.equals("all") || condition.equals("any")) {
            condition = "";
        }
        BooleanExpression conditionEx = item.itemCondition.contains(condition);

        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        List<Item> items = query.select(item)
                .from(item)
                .where(keywordsEx, categoryEx, conditionEx)
                .fetch();

        if (sortBy.equals("priceasc")) {
            items.sort((i1, i2) -> Integer.parseInt(i1.getPrice()) - Integer.parseInt(i2.getPrice()));
        } else if (sortBy.equals("pricedesc")) {
            items.sort((i1, i2) -> Integer.parseInt(i2.getPrice()) - Integer.parseInt(i1.getPrice()));
        }
        return items;
    }

    public LinkedHashMap<String, Item> getCategoryItems(String category) {
        QItem item = QItem.item;
        BooleanExpression categoryEx = item.category.contains(category);
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);

        List<Item> items = query.select(item)
                .from(item)
                .where(categoryEx)
                .fetch();
        Collections.shuffle(items);
        items = items.subList(0, 6);

        LinkedHashMap<String, Item> itemsMap = new LinkedHashMap<>();
        for (Item it : items) {
            Image image = imageService.getFirstItemImage(it);
            String imageName = image.getName();
            itemsMap.put(imageName, it);
        }
        return itemsMap;
    }

    public List<Item> getUserItems() {
        String username = getUsername();
        return itemRepository.findByUsernameOrderByDateDesc(username);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item saveItem(Item item) {
        String username = getUsername();
        item.setUsername(username);
        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        item.setDate(date);
        return itemRepository.save(item);
    }

    public void updateItem(Item updatedItem) {
        Item item = itemRepository.findById(updatedItem.getId());
        updatedItem.setUsername(item.getUsername());
        updatedItem.setDate(item.getDate());
        itemRepository.save(updatedItem);
    }

    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
