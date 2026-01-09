package com.example.item.controller;

import com.example.item.model.Item;
import com.example.item.service.ItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(validationErrors(bindingResult));
        }
        try {
            Item created = itemService.create(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable @NotBlank String itemId) {
        return itemService.getById(itemId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(error("Item not found")));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable @NotBlank String itemId,
                                        @Valid @RequestBody Item item,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(validationErrors(bindingResult));
        }
        // Ensure path itemId and body itemId align if provided
        if (item.getItemId() != null && !itemId.equals(item.getItemId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error("itemId in path and body must match"));
        }
        return itemService.update(itemId, item)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(error("Item not found")));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable @NotBlank String itemId) {
        boolean deleted = itemService.delete(itemId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Item not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, String> validationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : bindingResult.getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return errors;
    }

    private Map<String, String> error(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
