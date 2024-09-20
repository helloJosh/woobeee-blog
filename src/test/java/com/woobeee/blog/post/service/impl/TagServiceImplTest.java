package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.request.TagCreateRequest;
import com.woobeee.blog.post.dto.request.TagUpdateRequest;
import com.woobeee.blog.post.entity.Tag;
import com.woobeee.blog.post.exception.TagDoesNotExistException;
import com.woobeee.blog.post.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @DisplayName("태그 생성")
    @Test
    void createTags() {
        TagCreateRequest tagCreateRequest = TagCreateRequest.builder()
                .tags(List.of("Java", "Spring", "JPA"))
                .build();

        when(tagRepository.existsTagByName(anyString())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(any(Tag.class));

        tagService.create(tagCreateRequest);

        verify(tagRepository, times(3)).save(any(Tag.class)); // Save should be called for each tag
    }

    @DisplayName("태그 삭제")
    @Test
    void deleteTag() {
        String tagName = "Java";
        Tag tag = new Tag(tagName);
        when(tagRepository.findTagByName(tagName)).thenReturn(Optional.of(tag));

        tagService.delete(tagName);

        verify(tagRepository, times(1)).delete(tag);
    }

    @DisplayName("태그 삭제 - 존재하지 않는 경우")
    @Test
    void deleteTag_NotExist() {
        String tagName = "NonExistentTag";
        when(tagRepository.findTagByName(tagName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.delete(tagName))
                .isInstanceOf(TagDoesNotExistException.class)
                .hasMessageContaining(tagName + ": 태그가 존재하지 않습니다.");
    }

    @DisplayName("태그 업데이트")
    @Test
    void updateTag() {
        TagUpdateRequest request = TagUpdateRequest.builder()
                .oldTagName("OldTag")
                .newTagName("NewTag")
                .build();
        Tag tag = new Tag(request.oldTagName());

        when(tagRepository.findTagByName(request.oldTagName())).thenReturn(Optional.of(tag));

        tagService.update(request);

        assertThat(tag.getName()).isEqualTo("NewTag");
        verify(tagRepository, times(1)).save(tag);
    }

    @DisplayName("태그 업데이트 - 존재하지 않는 경우")
    @Test
    void updateTag_NotExist() {
        TagUpdateRequest request = TagUpdateRequest.builder()
                .oldTagName("NonExistentTag")
                .newTagName("NewTag")
                .build();
        when(tagRepository.findTagByName(request.oldTagName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.update(request))
                .isInstanceOf(TagDoesNotExistException.class)
                .hasMessageContaining(request.oldTagName() + ": 태그 이름이 존재하지 않습니다.");
    }

    @DisplayName("태그 조회 - 존재하지 않는 경우")
    @Test
    void readTag_NotExist() {
        String tagName = "NonExistentTag";
        when(tagRepository.findTagByName(tagName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.read(tagName))
                .isInstanceOf(TagDoesNotExistException.class)
                .hasMessageContaining(tagName + ": 태그가 존재하지 않습니다.");
    }

    @DisplayName("태그 전체 조회")
    @Test
    void readAllTags() {
        List<Tag> tags = List.of(new Tag("Java"), new Tag("Spring"), new Tag("JPA"));
        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.readAll();

        verify(tagRepository, times(1)).findAll();
        assertThat(result).hasSize(3);
    }
}