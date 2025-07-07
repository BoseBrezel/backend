package com.fokuswissen.exponat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fokuswissen.vuforia.VuforiaClient;

import java.nio.file.Path;

@Service
public class ExponatService
{

    private final ExponatRepository exponatRepository;
    private final VuforiaClient vuforiaClient;

    public ExponatService(ExponatRepository exponatRepository, VuforiaClient vuforiaClient )
    {
        this.exponatRepository = exponatRepository;
        this.vuforiaClient = vuforiaClient;

    }

    public List<Exponat> getAllExpo()
    {
        return exponatRepository.findAll();
    }

    public Optional<Exponat> getExponatById(String id)
    {
        return exponatRepository.findById(id);
    }

    public Exponat createExponat(Exponat exponat)
    {
        if (exponat.getScanBild() != null)
        {
            vuforiaClient.uploadImageTarget(exponat.getTitel(), exponat.getScanBild());
        }
        return exponatRepository.save(exponat);
    }

    public Exponat updateExponat(String id, Exponat updatedExponat)
    {
        return exponatRepository.findById(id).map(exponat ->
        {
            exponat.setTitel(updatedExponat.getTitel());
            exponat.setScanBild(updatedExponat.getScanBild());
            exponat.setBeschreibung(updatedExponat.getBeschreibung());
            exponat.setAusstellungsIds(updatedExponat.getAusstellungsIds());
            exponat.setVideoUrl(updatedExponat.getVideoUrl());
            exponat.setBildUrls(updatedExponat.getBildUrls());
            exponat.setQuiz(updatedExponat.getQuiz());
        
            if (updatedExponat.getScanBild() != null && !updatedExponat.getScanBild().isEmpty())
            {
                vuforiaClient.uploadImageTarget(updatedExponat.getTitel(), updatedExponat.getScanBild());
            }
            return exponatRepository.save(exponat);
        }).orElseGet(() ->
        {
            updatedExponat.setId(id);
            return exponatRepository.save(updatedExponat);
        });
    }

    public void deleteExponat(String id)
    {
        exponatRepository.deleteById(id);
    }

    public String saveFile(String exponatId, MultipartFile file, String mediaType) throws Exception {
    if (file == null || file.isEmpty())
    {
        throw new IllegalArgumentException("Keine Datei übergeben.");
    }

    if (mediaType == null || mediaType.trim().isEmpty())
    {
        mediaType = "bilder";
    }

    String baseDir = "uploads/";

    String subDir;
    switch (mediaType.toLowerCase())
    {
        case "scanbild":
            subDir = "scanbild/";
            break;
        case "video":
            subDir = "videos/";
            break;
        case "bild":
        case "bilder":
        default:
            subDir = "bilder/";
            break;
    }

    String uploadDir = baseDir + subDir;
    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path filePath = Paths.get(uploadDir + filename);

    Files.createDirectories(filePath.getParent());

    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    Exponat exponat = exponatRepository.findById(exponatId)
            .orElseThrow(() -> new RuntimeException("Exponat nicht gefunden"));

    String fileUrl = "/media/" + subDir + filename;

    if("scanbild".equalsIgnoreCase(mediaType))
    {
        exponat.setScanBild(fileUrl);
    }
    else if("video".equalsIgnoreCase(mediaType))
    {
        exponat.setVideoUrl(fileUrl);
    }
    else
    {
        ArrayList<String> bildUrls = exponat.getBildUrls();
        if (bildUrls == null) bildUrls = new ArrayList<>();
        bildUrls.add(fileUrl);
        exponat.setBildUrls(bildUrls);
    }

    exponatRepository.save(exponat);

    return fileUrl;
}

}
