package com.example.webcamopencv.Repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;
 
import com.example.webcamopencv.Entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
 
    ImageEntity findTopByOrderByIdDesc(); 
}  
 
