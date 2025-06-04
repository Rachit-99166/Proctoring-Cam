package com.example.webcamopencv.Entity;

import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Id;  
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;  
import lombok.Data; 
import lombok.NoArgsConstructor; 

@Entity
@Data  
@NoArgsConstructor
@AllArgsConstructor  
public class ImageEntity { 

    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] imageData;

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
