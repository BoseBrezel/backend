package com.fokuswissen.ausstellung;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ausstellungen")
public class AusstellungController {

    private final AusstellungService ausstellungService;

    public AusstellungController(AusstellungService ausstellungService) {
        this.ausstellungService = ausstellungService;
    }

    @GetMapping
    public List<Ausstellung> getAllAusstellungen() {
        return ausstellungService.getAllAusstellungen();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ausstellung> getAusstellungById(@PathVariable String id) {
        Optional<Ausstellung> ausstellung = ausstellungService.getAusstellungById(id);
        return ausstellung.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ausstellung createAusstellung(@RequestBody Ausstellung ausstellung) {
        return ausstellungService.createAusstellung(ausstellung);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ausstellung> updateAusstellung(@PathVariable String id, @RequestBody Ausstellung updatedAusstellung) {
        Ausstellung ausstellung = ausstellungService.updateAusstellung(id, updatedAusstellung);
        return ResponseEntity.ok(ausstellung);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAusstellung(@PathVariable String id) {
        ausstellungService.deleteAusstellung(id);
        return ResponseEntity.noContent().build();
    }

    // Optional: Endpunkte für Exponate verwalten

    @PostMapping("/{ausstellungId}/exponate/{exponatId}")
    public ResponseEntity<Ausstellung> addExponat(@PathVariable String ausstellungId, @PathVariable String exponatId) {
        Ausstellung ausstellung = ausstellungService.addExponatToAusstellung(ausstellungId, exponatId);
        return ResponseEntity.ok(ausstellung);
    }

    @DeleteMapping("/{ausstellungId}/exponate/{exponatId}")
    public ResponseEntity<Ausstellung> removeExponat(@PathVariable String ausstellungId, @PathVariable String exponatId) {
        Ausstellung ausstellung = ausstellungService.removeExponatFromAusstellung(ausstellungId, exponatId);
        return ResponseEntity.ok(ausstellung);
    }
}
