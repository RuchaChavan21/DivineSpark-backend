package com.divinespark.service.impl;

import com.divinespark.dto.ProgramRequest;
import com.divinespark.dto.ProgramResponse;
import com.divinespark.entity.Program;
import com.divinespark.repository.ProgramRepository;
import com.divinespark.service.ProgramService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository repository;

    public ProgramServiceImpl(ProgramRepository repository) {
        this.repository = repository;
    }

    @Override
    public Program create(ProgramRequest request) {
        Program program = new Program();
        mapRequest(program, request);
        return repository.save(program);
    }

    @Override
    public Program update(Long id, ProgramRequest request) {
        Program program = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        mapRequest(program, request);
        return repository.save(program);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Program> getAllAdmin() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getByCategory(String category) {
        return repository
                .findByCategoryAndActiveTrueOrderByCreatedAtDesc(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void mapRequest(Program program, ProgramRequest request) {
        program.setTitle(request.getTitle());
        program.setDescription(request.getDescription());
        program.setCategory(request.getCategory());
        program.setImageUrl(request.getImageUrl());
    }

    private ProgramResponse mapToResponse(Program program) {
        ProgramResponse res = new ProgramResponse();
        res.setId(program.getId());
        res.setTitle(program.getTitle());
        res.setDescription(program.getDescription());
        res.setImageUrl(program.getImageUrl());
        return res;
    }
}
