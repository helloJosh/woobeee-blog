package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.TagCreateRequest;
import com.woobeee.blog.post.dto.TagUpdateRequest;
import com.woobeee.blog.post.entity.Tag;
import com.woobeee.blog.post.exception.TagDoesNotExistException;
import com.woobeee.blog.post.repository.TagRepository;
import com.woobeee.blog.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 태그 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(TagCreateRequest tagCreateRequest) {
        List<String> tagNames = tagCreateRequest.tags();
        for (String tagName : tagNames) {
            if (!tagRepository.existsTagByName(tagName)) {
                tagRepository.save(new Tag(tagName));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String tagName) {
        Tag tag = tagRepository
                .findTagByName(tagName)
                .orElseThrow(()->new TagDoesNotExistException(tagName + ": 태그가 존재하지 않습니다."));

        tagRepository.delete(tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(TagUpdateRequest tagUpdateRequest) {

        Tag tag = tagRepository
                .findTagByName(tagUpdateRequest.oldTagName())
                .orElseThrow(()->new TagDoesNotExistException(tagUpdateRequest.oldTagName() + ": 태그 이름이 존재하지 않습니다."));

        tag.setName(tagUpdateRequest.newTagName());

        tagRepository.save(tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag read(String tagName) {
        return tagRepository
                .findTagByName(tagName)
                .orElseThrow(()->new TagDoesNotExistException(tagName + ": 태그가 존재하지 않습니다."));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> readAll() {
        return tagRepository.findAll();
    }
}


