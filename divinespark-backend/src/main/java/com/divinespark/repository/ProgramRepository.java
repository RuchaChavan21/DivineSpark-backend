package com.divinespark.repository;

import com.divinespark.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findByCategoryAndActiveTrueOrderByCreatedAtDesc(String category);

    List<Program> findAllByOrderByCreatedAtDesc();
}
