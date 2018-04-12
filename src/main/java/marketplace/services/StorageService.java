package marketplace.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import marketplace.models.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("StorageService")
public class StorageService {
    @Autowired
    private AmazonS3 s3;

    public void saveFile(Item item, MultipartFile multipartFile) throws IOException {
        String filename = item.getId() + "/" + multipartFile.getOriginalFilename();
        File file = new File(multipartFile.getOriginalFilename());
        file.createNewFile();
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(multipartFile.getBytes());
        stream.close();

        s3.putObject(new PutObjectRequest(System.getenv("BUCKET_NAME"), filename, file));
        file.delete();
    }

    public void deleteFile(int itemId, String imageName) {
        String filename = itemId + "/" + imageName;
        s3.deleteObject(System.getenv("BUCKET_NAME"), filename);
    }
}
