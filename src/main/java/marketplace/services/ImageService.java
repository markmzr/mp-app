package marketplace.services;

import java.util.List;

import marketplace.models.Image;
import marketplace.models.Item;
import marketplace.repositories.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service("imageService")
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public int countItemImages(int itemId) {
        return imageRepository.countByItemId(itemId);
    }

    public Image getFirstItemImage(Item item) {
        return imageRepository.findFirstByItemId(item.getId());
    }

    public List<Image> getItemImages(int itemId) {
        return imageRepository.findByItemId(itemId);
    }

    public void saveImage(Item item, MultipartFile multipartFile) {
        int itemId = item.getId();
        String name = itemId + "/" + multipartFile.getOriginalFilename();
        Image image = new Image(itemId, name);
        imageRepository.save(image);
    }

    @Transactional
    public void deleteImage(int itemId, String imageName) {
        String name = itemId + "/" + imageName;
        imageRepository.deleteByName(name);
    }

    @Transactional
    public void deleteItemImages(int itemId) {
        imageRepository.deleteByItemId(itemId);
    }
}
