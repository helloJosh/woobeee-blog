package com.woobeee.blog.post.controller;

import com.woobeee.blog.api.Response;
import com.woobeee.blog.post.dto.response.PostReadResponse;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.Tag;
import com.woobeee.blog.post.service.PostService;
import com.woobeee.blog.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class TagController {
    private final TagService tagService;

    @GetMapping("/tags")
    public Response<List<String>> readTags(){
        List<Tag> tags = tagService.readAll();
        List<String> responses = new ArrayList<>();

        for (Tag tag : tags) {
            responses.add(tag.getName());
        }

        return Response.success(responses);
    }

    @DeleteMapping("/tags/{tagName}")
    public Response<Void> deleteTag(
            @PathVariable String tagName
    ) {
        tagService.delete(tagName);

        return Response.success();
    }
}
