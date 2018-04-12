package marketplace.repositories;

import java.util.List;

import marketplace.models.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("imageRepository")
public interface ImageRepository extends JpaRepository<Image, Integer> {
    int countByItemId(int itemId);
    void deleteByName(String name);
    List<Image> findByItemId(int itemId);
    Image save(Image image);
}
