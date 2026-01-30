package com.divinespark.service;

import com.divinespark.dto.BlogCreateRequest;
import com.divinespark.dto.BlogDetailResponse;
import com.divinespark.dto.BlogListResponse;

import java.util.List;

public interface BlogService {

    void createBlog(BlogCreateRequest request);

    void updateBlog(Long id, BlogCreateRequest request);

    void publishBlog(Long id);

    void deleteBlog(Long id);

    List<BlogListResponse> getPublishedBlogs();

    BlogDetailResponse getBlogBySlug(String slug);

    List<BlogListResponse> getAllBlogsForAdmin();

    BlogDetailResponse getBlogByIdForAdmin(Long id);

}
