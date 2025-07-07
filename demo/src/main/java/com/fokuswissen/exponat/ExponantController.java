package com.fokuswissen.exponat;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/exponate")
public class ExponantController
{
    private final ExponatService exponatService;

    public ExponantController(ExponatService exponatService)
    {
        this.exponatService = exponatService;
    }

    @GetMapping
    public List<Exponat> getAllExponate()
    {
        return exponatService.getAllExpo();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exponat> getExponatById(@PathVariable String id)
    {
        return exponatService.getExponatById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Exponat> createExponat(@RequestBody Exponat exponat)
    {
        Exponat created = exponatService.createExponat(exponat);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exponat> updateExponat(@PathVariable String id, @RequestBody Exponat exponat)
    {
        Exponat updated = exponatService.updateExponat(id, exponat);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExponat(@PathVariable String id)
    {
        exponatService.deleteExponat(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadMedia(
            @PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "mediaType", required = false) String mediaType)
    {
        try
        {
            if(file == null || file.isEmpty())
            {
                return ResponseEntity.badRequest().body("Keine Datei zum Hochladen angegeben.");
            }
            if(mediaType == null || mediaType.trim().isEmpty())
            {
                mediaType = "bilder";
            }
            String savedPath = exponatService.saveFile(id, file, mediaType);
            return ResponseEntity.ok("Datei gespeichert unter: " + savedPath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler: " + e.getMessage());
        }
    }
}
