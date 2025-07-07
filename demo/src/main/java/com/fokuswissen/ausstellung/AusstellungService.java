package com.fokuswissen.ausstellung;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class AusstellungService {

    private final AusstellungRepository ausstellungRepository;

    public AusstellungService(AusstellungRepository ausstellungRepository) {
        this.ausstellungRepository = ausstellungRepository;
    }

    public List<Ausstellung> getAllAusstellungen() {
        return ausstellungRepository.findAll();
    }

    public Optional<Ausstellung> getAusstellungById(String id) {
        return ausstellungRepository.findById(id);
    }

    public Ausstellung createAusstellung(Ausstellung ausstellung) {
        return ausstellungRepository.save(ausstellung);
    }

    public Ausstellung updateAusstellung(String id, Ausstellung updatedAusstellung) {
        return ausstellungRepository.findById(id).map(ausstellung -> {
            ausstellung.setTitel(updatedAusstellung.getTitel());
            ausstellung.setBeschreibung(updatedAusstellung.getBeschreibung());
            ausstellung.setLogo(updatedAusstellung.getLogo());
            ausstellung.setExponatIds(updatedAusstellung.getExponatIds());
            return ausstellungRepository.save(ausstellung);
        }).orElseGet(() -> {
            updatedAusstellung.setId(id);
            return ausstellungRepository.save(updatedAusstellung);
        });
    }

    public void deleteAusstellung(String id) {
        ausstellungRepository.deleteById(id);
    }

    // Optional: Exponat hinzufügen
    public Ausstellung addExponatToAusstellung(String ausstellungId, String exponatId) {
        return ausstellungRepository.findById(ausstellungId).map(ausstellung -> {
            ausstellung.addExponat(exponatId);
            return ausstellungRepository.save(ausstellung);
        }).orElseThrow(() -> new RuntimeException("Ausstellung nicht gefunden"));
    }

    // Optional: Exponat entfernen
    public Ausstellung removeExponatFromAusstellung(String ausstellungId, String exponatId) {
        return ausstellungRepository.findById(ausstellungId).map(ausstellung -> {
            ausstellung.removeExponat(exponatId);
            return ausstellungRepository.save(ausstellung);
        }).orElseThrow(() -> new RuntimeException("Ausstellung nicht gefunden"));
    }
}
