package com.example.webcamopencv.Controller;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private final VideoCapture camera = new VideoCapture(0); // Keep the camera open

    // Triggered every 20 seconds to capture an image
    @Scheduled(fixedRate = 20000)
    public void captureImagePeriodically() {
        if (isCapturing) {
            Mat capturedImage = captureFromWebcam();
            byte[] imageData = matToByteArray(capturedImage);

            if (imageData.length > 0) {
                imageService.saveImage(imageData);  // Save the image to MySQL
            }
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        // Get the latest image from the database
        byte[] latestImage = imageService.getLatestImage();

        // Convert byte[] to Base64 for displaying in HTML
        String base64Image = Base64.getEncoder().encodeToString(latestImage);

        // Add the latest image to the model
        model.addAttribute("imageData", base64Image);  // Pass the base64 image to Thymeleaf
        return "index";  // Return the view
    }

    // @PostMapping("/deleteAllImages")
    // @ResponseBody
    // public String deleteAllImages() {
    //     imageService.deleteAllImages();  // Call the service method to delete images
    //     return "redirect:/results";  // Redirect to the results page after deletion
    // }
    @PostMapping("/deleteAllImages")
    public String deleteAllImages(RedirectAttributes redirectAttributes) {
        // Call your service to delete all images from the database
        imageService.deleteAllImages();

        // Add a success message (optional) to show after redirection
        redirectAttributes.addFlashAttribute("message", "All images have been deleted successfully!");

        // Redirect to the index (or homepage) after deletion
        return "redirect:/";
    }

    @RequestMapping("/")
    public String index() {
        // Return the view for the index page (e.g., "index.html")
        return "index";
    }

    @PostMapping("/capture")
    public String captureImage(Model model) {
        isCapturing = true;  // Start capturing images
        return "redirect:/";
    }

    @PostMapping("/stop")
    public String stopCapture(Model model) {
        isCapturing = false;  // Stop capturing images
        camera.release();  // Release the webcam to stop capturing
        return "redirect:/";
    }

    @RequestMapping("/results")
    public String resultsPage(Model model) {
        // Get all images from the database
        List<ImageEntity> images = imageService.getAllImages();

        // Convert byte[] to Base64 strings for displaying
        List<String> base64Images = images.stream()
                .map(imageEntity -> Base64.getEncoder().encodeToString(imageEntity.getImageData()))
                .collect(Collectors.toList());

        // Add the list of images to the model to be displayed on the results page
        model.addAttribute("imageDataList", base64Images);
        return "results";  // Return the results page
    }

    @RequestMapping("/api/images")
    public ResponseEntity<List<String>> getAllImages() {
        // Get all images from the DB (in byte[] format)
        List<ImageEntity> images = imageService.getAllImages();

        // Convert byte[] to Base64 strings for API response
        List<String> base64Images = images.stream()
                .map(imageEntity -> Base64.getEncoder().encodeToString(imageEntity.getImageData()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(base64Images);  // Return the list of base64 images as JSON
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

        return frame; // Return the captured frame (image)
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

        return matOfByte.toArray();  // Return the encoded byte array
    }
}
