package com.example.web_app.rest;

import com.example.web_app.model.MessageDTO;
import com.example.web_app.service.MessageService;
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
@RequestMapping(value = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageResource {

    private final MessageService messageService;

    public MessageResource(final MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.findAll());
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDTO> getMessage(
            @PathVariable(name = "messageId") final Integer messageId) {
        return ResponseEntity.ok(messageService.get(messageId));
    }

    @PostMapping
    public ResponseEntity<Integer> createMessage(@RequestBody @Valid final MessageDTO messageDTO) {
        final Integer createdMessageId = messageService.create(messageDTO);
        return new ResponseEntity<>(createdMessageId, HttpStatus.CREATED);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Integer> updateMessage(
            @PathVariable(name = "messageId") final Integer messageId,
            @RequestBody @Valid final MessageDTO messageDTO) {
        messageService.update(messageId, messageDTO);
        return ResponseEntity.ok(messageId);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable(name = "messageId") final Integer messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

}
