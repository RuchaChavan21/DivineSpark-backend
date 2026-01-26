package com.divinespark.controller.admin;

import com.divinespark.dto.BlogCreateRequest;
import com.divinespark.dto.BlogDetailResponse;
import com.divinespark.dto.BlogListResponse;
import com.divinespark.service.BlogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/blogs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlogController {

    private final BlogService blogService;

    public AdminBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // 🔹 GET all blogs (admin view: includes drafts + published)
    @GetMapping
    public List<BlogListResponse> getAllBlogs() {
        return blogService.getAllBlogsForAdmin();
    }

    // 🔹 GET single blog by ID (admin edit view)
    @GetMapping("/{id}")
    public BlogDetailResponse getBlog(@PathVariable Long id) {
        return blogService.getBlogByIdForAdmin(id);
    }

    // 🔹 CREATE blog
    @PostMapping
    public void createBlog(@RequestBody BlogCreateRequest request) {
        blogService.createBlog(request);
    }

    // 🔹 UPDATE blog
    @PutMapping("/{id}")
    public void updateBlog(
            @PathVariable Long id,
            @RequestBody BlogCreateRequest request
    ) {
        blogService.updateBlog(id, request);
    }

    // 🔹 PUBLISH / UNPUBLISH blog
    @PatchMapping("/{id}/publish")
    public void publishBlog(@PathVariable Long id) {
        blogService.publishBlog(id);
    }

    // 🔹 DELETE blog
    @DeleteMapping("/{id}")
    public void deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
    }
}
