package com.ragchat.controller;

import com.ragchat.model.Contract;
import com.ragchat.service.SessionService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contract.SessionRes create(@Valid @RequestBody Contract.CreateSessionReq req){
        return Contract.SessionRes.of(sessionService.createSession(req.userId(), req.title()));
    }

    @GetMapping
    public Page<Contract.SessionRes> list(@RequestParam String userId,
                                          @RequestParam(required = false) Boolean favorite,
                                          @ParameterObject Pageable pageable) {
        return sessionService.list(userId, favorite, pageable).map(Contract.SessionRes::of);
    }

    @PatchMapping("/{id}/rename")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rename(@PathVariable UUID id, @RequestParam String title) {
        sessionService.rename(id, title);
    }

    @PatchMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void favorite(@PathVariable UUID id, @RequestParam Boolean favorite) {
        if (favorite == null) throw new IllegalArgumentException("favorite required");
        sessionService.favorite(id, favorite);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        sessionService.delete(id);
    }
}
