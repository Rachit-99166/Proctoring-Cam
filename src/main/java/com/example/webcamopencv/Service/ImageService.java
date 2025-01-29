package com.example.webcamopencv.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webcamopencv.Entity.ImageEntity;
import com.example.webcamopencv.Repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public void saveImage(byte[] imageData) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageData(imageData);  // Set image data
        imageRepository.save(imageEntity);  // Save to DB
    }

    public List<ImageEntity> getAllImages() {
        return imageRepository.findAll();  // Retrieve all images from the database
    }

    // public byte[] getLatestImage() {
    //     return imageRepository.findTopByOrderByIdDesc().getImageData();  // Retrieve the latest image from the database
    // }
    public byte[] getLatestImage() {
        ImageEntity latestImage = imageRepository.findTopByOrderByIdDesc();
        if (latestImage == null) {
            // Return an empty byte array or a default image if no image exists
            return new byte[0];
        }
        return latestImage.getImageData();  // Return the image data
    }

    public void deleteAllImages() {
        imageRepository.deleteAll();  // Deletes all entries in the Image table
    }

}
