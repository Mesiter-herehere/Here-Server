package com.work.here.domain.controller;

import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.service.SelfIntroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/self-intros")
public class SelfIntroController {
    @Autowired
    private SelfIntroService selfIntroService;

    @GetMapping
    public List<SelfIntro> getAllSelfIntros() {
        return selfIntroService.getAllSelfIntros();
    }

    @PostMapping
    public SelfIntro createSelfIntro(@RequestBody SelfIntro selfIntro) {
        return selfIntroService.createSelfIntro(selfIntro);
    }

    @PutMapping("/{id}")
    public SelfIntro updateSelfIntro(@PathVariable Long id, @RequestBody SelfIntro selfIntro) {
        return selfIntroService.updateSelfIntro(id, selfIntro);
    }

    @DeleteMapping("/{id}")
    public void deleteSelfIntro(@PathVariable Long id) {
        selfIntroService.deleteSelfIntro(id);
    }
}