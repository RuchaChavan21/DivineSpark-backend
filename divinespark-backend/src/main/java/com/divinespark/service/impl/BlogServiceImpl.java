package com.divinespark.service.impl;

import com.divinespark.dto.*;
import com.divinespark.entity.Blog;
import com.divinespark.entity.enums.BlogStatus;
import com.divinespark.repository.BlogRepository;
import com.divinespark.service.BlogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public void createBlog(BlogCreateRequest request) {

        String slug = generateSlug(request.getTitle());

        if (blogRepository.existsBySlug(slug)) {
            throw new RuntimeException("Blog with same title already exists");
        }

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setSlug(slug);
        blog.setExcerpt(request.getExcerpt());
        blog.setContent(request.getContent());
        blog.setAuthorName(request.getAuthorName());
        blog.setAuthorRole(request.getAuthorRole());
        blog.setStatus(BlogStatus.DRAFT);
        // Removed setCreatedAt and setUpdatedAt as they are handled automatically by your Entity

        blogRepository.save(blog);
    }

    @Override
    public void updateBlog(Long id, BlogCreateRequest request) {

        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setTitle(request.getTitle());
        blog.setExcerpt(request.getExcerpt());
        blog.setContent(request.getContent());
        blog.setAuthorName(request.getAuthorName());
        blog.setAuthorRole(request.getAuthorRole());
        blog.setUpdatedAt(OffsetDateTime.now());
    }

    @Override
    public void publishBlog(Long id) {

        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setStatus(BlogStatus.PUBLISHED);
        blog.setUpdatedAt(OffsetDateTime.now());
    }

    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public List<BlogListResponse> getPublishedBlogs() {

        List<Blog> blogs = blogRepository
                .findByStatusOrderByCreatedAtDesc(BlogStatus.PUBLISHED);

        List<BlogListResponse> response = new ArrayList<>();

        for (Blog b : blogs) {
            BlogListResponse dto = new BlogListResponse();
            dto.setId(b.getId());
            dto.setTitle(b.getTitle());
            dto.setSlug(b.getSlug());
            dto.setExcerpt(b.getExcerpt());
            dto.setAuthorName(b.getAuthorName());
            dto.setAuthorRole(b.getAuthorRole());
            dto.setCreatedAt(b.getCreatedAt());

            // ✅ Set published to true for this list
            dto.setIsPublished(true);

            response.add(dto);
        }

        return response;
    }

    @Override
    public BlogDetailResponse getBlogBySlug(String slug) {

        Blog blog = blogRepository
                .findBySlugAndStatus(slug, BlogStatus.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        BlogDetailResponse res = new BlogDetailResponse();
        res.setTitle(blog.getTitle());
        res.setContent(blog.getContent());
        res.setAuthorName(blog.getAuthorName());
        res.setAuthorRole(blog.getAuthorRole());
        res.setCreatedAt(blog.getCreatedAt());

        // ✅ Set published status
        res.setIsPublished(true);

        return res;
    }

    // ---------- SLUG GENERATOR ----------
    private String generateSlug(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\w\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase(Locale.ENGLISH);
    }

    @Override
    public List<BlogListResponse> getAllBlogsForAdmin() {

        List<Blog> blogs = blogRepository.findAllByOrderByCreatedAtDesc();
        List<BlogListResponse> response = new ArrayList<>();

        for (Blog b : blogs) {
            BlogListResponse dto = new BlogListResponse();
            dto.setId(b.getId());
            dto.setTitle(b.getTitle());
            dto.setSlug(b.getSlug());
            dto.setExcerpt(b.getExcerpt());
            dto.setAuthorName(b.getAuthorName());
            dto.setAuthorRole(b.getAuthorRole());
            dto.setCreatedAt(b.getCreatedAt());

            // ✅ CRITICAL FIX: Map the status to the boolean field
            // This ensures the frontend sees correct status!
            dto.setIsPublished(b.getStatus() == BlogStatus.PUBLISHED);

            response.add(dto);
        }

        return response;
    }


    @Override
    public BlogDetailResponse getBlogByIdForAdmin(Long id) {

        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        BlogDetailResponse res = new BlogDetailResponse();
        res.setTitle(blog.getTitle());
        res.setContent(blog.getContent());
        res.setAuthorName(blog.getAuthorName());
        res.setAuthorRole(blog.getAuthorRole());
        res.setCreatedAt(blog.getCreatedAt());

        // ✅ CRITICAL FIX: Map the status here as well
        res.setIsPublished(blog.getStatus() == BlogStatus.PUBLISHED);

        return res;
    }

}