package marketplace.repositories;

import java.util.List;

import marketplace.models.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("itemRepository")
public interface ItemRepository extends JpaRepository<Item, Integer> {
    void deleteById(int id);
    Item findById(int id);
    List<Item> findByNameContaining(String name);
    List<Item> findByNameContainingOrderByPriceAsc(String name);
    List<Item> findByNameContainingOrderByPriceDesc(String name);
    List<Item> findByUsername(String username);
    Item save(Item item);
}
