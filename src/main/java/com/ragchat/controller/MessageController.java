package com.ragchat.controller;

import com.ragchat.model.ChatMessage;
import com.ragchat.model.Contract;
import com.ragchat.service.MessageService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Contract.MsgRes create(@PathVariable UUID id, @Valid @RequestBody Contract.CreateMsgReq req){
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(req.sender())
                .content(req.content())
                .context(req.context())
                .metadata(req.metadata())
                .build();
        return Contract.MsgRes.of(messageService.addMessage(id, chatMessage));
    }

    @GetMapping("/{id}")
    public Page<Contract.MsgRes> list(@PathVariable UUID id, @ParameterObject Pageable pageable){
        return messageService.history(id, pageable).map(Contract.MsgRes::of);
    }
}
