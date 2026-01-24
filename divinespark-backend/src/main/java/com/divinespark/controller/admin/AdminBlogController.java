package com.divinespark.controller.admin;


import com.divinespark.dto.BlogCreateRequest;
import com.divinespark.entity.Blog;
import com.divinespark.service.BlogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/blogs")
public class AdminBlogController {

    private final BlogService blogService;

    public AdminBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void createBlog(@RequestBody BlogCreateRequest request) {
        blogService.createBlog(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public void updateBlog(@PathVariable Long id, @RequestBody BlogCreateRequest request) {
        blogService.updateBlog(id, request);
    }

    @PatchMapping("/{id}/publish")
    public void publishBlog(@PathVariable Long id) {
        blogService.publishBlog(id);
    }

    @DeleteMapping("/{id}")
    public void deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
    }
}
