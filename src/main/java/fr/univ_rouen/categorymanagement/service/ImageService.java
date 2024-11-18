package fr.univ_rouen.categorymanagement.service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.allowed-extensions}")
    private List<String> allowedExtensions;

    @Value("${app.upload.max-file-size}")
    private long maxFileSize;

    // Initialisation du répertoire de téléchargement
    @PostConstruct
    public void init() {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Directory created at: " + path.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }
    public String saveImage(MultipartFile image) throws IOException {
        // Valider l'image (vérifie l'extension et la taille du fichier)
        validateImage(image);
        String fileName = generateUniqueFileName(image);
        Path uploadPath = getUploadPath();
        Path filePath = uploadPath.resolve(fileName);

        // Vérification du chemin d'enregistrement
        System.out.println("Saving image to: " + filePath.toString());

        // Copier l'image
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Image saved successfully.");
        return getImageUrl(fileName);
    }

    private void validateImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Le fichier image est vide");
        }
        if (image.getSize() > maxFileSize) {
            throw new IllegalArgumentException("La taille du fichier dépasse la limite autorisée");
        }
        String fileExtension = getFileExtension(image.getOriginalFilename());
        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Type de fichier non autorisé");
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }

    private Path getUploadPath() throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        return uploadPath;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Resource loadImageAsResource(String fileName) {
        try {
            Path filePath = getUploadPath().resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException | FileNotFoundException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error while loading file " + fileName, ex);
        }
    }

    public String getImageUrl(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/categories/images/")
                .path(fileName)
                .toUriString();
    }
}