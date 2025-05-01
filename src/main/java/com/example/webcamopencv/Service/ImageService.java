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
        imageEntity.setImageData(imageData);
        imageRepository.save(imageEntity);
    }

    public List<ImageEntity> getAllImages() {
        return imageRepository.findAll();
    }

    public byte[] getLatestImage() {
        ImageEntity latestImage = imageRepository.findTopByOrderByIdDesc();
        if (latestImage == null) {
            return new byte[0];
        }
        return latestImage.getImageData();
    }

    public void deleteAllImages() {
        imageRepository.deleteAll();
    }

}
