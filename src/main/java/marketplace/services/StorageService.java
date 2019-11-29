package marketplace.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import marketplace.models.Image;
import marketplace.models.Item;
import marketplace.repositories.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("StorageService")
public class StorageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AmazonS3 s3;

    public void saveFile(Item item, MultipartFile multipartFile) throws IOException {
        String filename = item.getId() + "/" + multipartFile.getOriginalFilename();
        File file = new File(multipartFile.getOriginalFilename());
        file.createNewFile();

        FileOutputStream stream = new FileOutputStream(file);
        stream.write(multipartFile.getBytes());
        stream.close();

        try {
            String bucket = System.getenv("BUCKET_NAME");
            s3.putObject(bucket, filename, file);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
        file.delete();
    }

    public void deleteFile(int itemId, String imageName) {
        String filename = itemId + "/" + imageName;
        s3.deleteObject(System.getenv("BUCKET_NAME"), filename);
    }

    public void deleteItemFiles(int itemId) {
        List<Image> images = imageRepository.findByItemId(itemId);
        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(System.getenv("BUCKET_NAME"));
        List<KeyVersion> keys = new ArrayList<>();

        for (Image image: images) {
            keys.add(new KeyVersion(image.getName()));
        }
        deleteRequest.setKeys(keys);
        s3.deleteObjects(deleteRequest);
    }
}
