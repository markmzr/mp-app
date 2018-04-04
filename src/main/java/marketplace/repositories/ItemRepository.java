package marketplace.repositories;

import java.util.List;

import marketplace.models.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("itemRepository")
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByNameContaining(String name);
}
