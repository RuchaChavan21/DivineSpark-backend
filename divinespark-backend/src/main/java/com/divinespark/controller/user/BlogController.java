package com.divinespark.controller.user;

import com.divinespark.dto.*;
import com.divinespark.service.BlogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public List<BlogListResponse> getBlogs() {
        return blogService.getPublishedBlogs();
    }

    @GetMapping("/{slug}")
    public BlogDetailResponse getBlog(@PathVariable String slug) {
        return blogService.getBlogBySlug(slug);
    }
}
