package com.divinespark.repository;

import com.divinespark.entity.Blog;
import com.divinespark.entity.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Public blogs (blog listing page)
    List<Blog> findByStatusOrderByCreatedAtDesc(BlogStatus status);

    // Blog detail page (SEO friendly)
    Optional<Blog> findBySlugAndStatus(String slug, BlogStatus status);

    // Admin checks
    boolean existsBySlug(String slug);
}
