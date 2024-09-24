package com.work.here.service;

import com.work.here.entity.SelfIntro;
import com.work.here.repository.SelfIntroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelfIntroService {
    @Autowired
    private SelfIntroRepository selfIntroRepository;

    public List<SelfIntro> getAllSelfIntros() {
        return selfIntroRepository.findAll();
    }

    public SelfIntro createSelfIntro(SelfIntro selfIntro) {
        return selfIntroRepository.save(selfIntro);
    }

    public SelfIntro updateSelfIntro(Long id, SelfIntro selfIntro) {
        selfIntro.setId(id);
        return selfIntroRepository.save(selfIntro);
    }

    public void deleteSelfIntro(Long id) {
        selfIntroRepository.deleteById(id);
    }
}