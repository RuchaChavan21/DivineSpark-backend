package com.divinespark.service;

import com.divinespark.dto.ProgramRequest;
import com.divinespark.dto.ProgramResponse;
import com.divinespark.entity.Program;

import java.util.List;

public interface ProgramService {

    Program create(ProgramRequest request);

    Program update(Long id, ProgramRequest request);

    void delete(Long id);

    List<Program> getAllAdmin();

    List<ProgramResponse> getByCategory(String category);
}
