package com.example.webcamopencv.Controller;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors; 
 
import org.opencv.core.Mat; 
import org.opencv.core.MatOfByte; 
import org.opencv.imgcodecs.Imgcodecs;  
import org.opencv.videoio.VideoCapture; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.scheduling.annotation.Scheduled; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import com.example.webcamopencv.Entity.ImageEntity;
import com.example.webcamopencv.Service.ImageService; 

@Controller
public class WebcamController { 

    @Autowired
    private ImageService imageService;
    private boolean isCapturing = false;
    private final VideoCapture camera = new VideoCapture(0);

    @Scheduled(fixedRate = 20000)
    public void captureImagePeriodically() {
        if (isCapturing) {
            Mat capturedImage = captureFromWebcam();
            byte[] imageData = matToByteArray(capturedImage);
            if (imageData.length > 0) {
                imageService.saveImage(imageData);
            }
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        byte[] latestImage = imageService.getLatestImage();
        String base64Image = Base64.getEncoder().encodeToString(latestImage);
        model.addAttribute("imageData", base64Image); 
        return "index";
    }

    @PostMapping("/deleteAllImages")
    public String deleteAllImages(RedirectAttributes redirectAttributes) {
        imageService.deleteAllImages();
        redirectAttributes.addFlashAttribute("message", "All images have been deleted successfully!");
        return "redirect:/";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/capture")
    public String captureImage() { 
        isCapturing = true;
        return "redirect:/";
    }

    @PostMapping("/stop") 
    public String stopCapture() {
        isCapturing = false;
        camera.release();
        return "redirect:/";
    }

    @RequestMapping("/results")
    public String resultsPage(Model model) {
        List<ImageEntity> images = imageService.getAllImages(); 
        List<String> base64Images = images.stream()
                .map(imageEntity -> Base64.getEncoder().encodeToString(imageEntity.getImageData()))
                .collect(Collectors.toList());
        model.addAttribute("imageDataList", base64Images); 
        return "results";
    }

    private Mat captureFromWebcam() {
        if (!camera.isOpened()) {
            System.out.println("Error: Webcam is not accessible.");
            return new Mat();
        }
        Mat frame = new Mat();
        boolean success = camera.read(frame);
        if (!success || frame.empty()) {
            System.out.println("Error: Failed to capture image.");
            return new Mat();
        }
        return frame;
    }
 
    private byte[] matToByteArray(Mat mat) {
        if (mat.empty()) {
            System.out.println("Error: Mat is empty.");
            return new byte[0];
        }
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".png", mat, matOfByte);
        if (!success) {
            System.out.println("Error: Failed to encode image.");
            return new byte[0];
        }
        return matOfByte.toArray();
    }
}
