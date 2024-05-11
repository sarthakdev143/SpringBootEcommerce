package com.example.web_app.rest;

import com.example.web_app.model.GrainDTO;
import com.example.web_app.service.GrainService;
import com.example.web_app.util.ReferencedException;
import com.example.web_app.util.ReferencedWarning;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/grains", produces = MediaType.APPLICATION_JSON_VALUE)
public class GrainResource {

    private final GrainService grainService;

    public GrainResource(final GrainService grainService) {
        this.grainService = grainService;
    }

    @GetMapping
    public ResponseEntity<List<GrainDTO>> getAllGrains() {
        return ResponseEntity.ok(grainService.findAll());
    }

    @GetMapping("/{grainId}")
    public ResponseEntity<GrainDTO> getGrain(
            @PathVariable(name = "grainId") final Integer grainId) {
        return ResponseEntity.ok(grainService.get(grainId));
    }

    @PostMapping
    public ResponseEntity<Integer> createGrain(@RequestBody @Valid final GrainDTO grainDTO) {
        final Integer createdGrainId = grainService.create(grainDTO);
        return new ResponseEntity<>(createdGrainId, HttpStatus.CREATED);
    }

    @PutMapping("/{grainId}")
    public ResponseEntity<Integer> updateGrain(
            @PathVariable(name = "grainId") final Integer grainId,
            @RequestBody @Valid final GrainDTO grainDTO) {
        grainService.update(grainId, grainDTO);
        return ResponseEntity.ok(grainId);
    }

    @DeleteMapping("/{grainId}")
    public ResponseEntity<Void> deleteGrain(@PathVariable(name = "grainId") final Integer grainId) {
        final ReferencedWarning referencedWarning = grainService.getReferencedWarning(grainId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        grainService.delete(grainId);
        return ResponseEntity.noContent().build();
    }

}
