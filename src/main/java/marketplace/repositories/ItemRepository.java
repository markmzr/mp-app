package marketplace.repositories;

import java.util.List;

import marketplace.models.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("itemRepository")
public interface ItemRepository extends JpaRepository<Item, Integer>, QuerydslPredicateExecutor {

    void deleteById(int id);
    Item findById(int id);
    List<Item> findByUsernameOrderByDateDesc(String username);
    Item save(Item item);
}
