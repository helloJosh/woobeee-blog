package com.woobeee.blog.post.service.impl;

import com.woobeee.blog.post.dto.request.CategoryRequest;
import com.woobeee.blog.post.dto.request.PostCreateRequest;
import com.woobeee.blog.post.dto.request.PostUpdateRequest;
import com.woobeee.blog.post.entity.*;
import com.woobeee.blog.post.entity.enums.Status;
import com.woobeee.blog.post.exception.*;
import com.woobeee.blog.post.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @DisplayName("게시글 생성 성공 테스트")
    @Test
    void testCreatePostSuccess() {
        CategoryRequest child = CategoryRequest.builder().id(1L).name("DB").children(null).build();

        List<CategoryRequest> categoryRequests = List.of(child);
        List<String> tags = List.of("Java", "Spring");
        PostCreateRequest request = PostCreateRequest.builder()
                .title("testTitle")
                .context("context")
                .categories(categoryRequests)
                .tags(tags).build();

        Category category = Category.builder().id(1L).name("DB").build();
        when(categoryRepository.findCategoryByName("DB")).thenReturn(Optional.of(category));

        Tag javaTag = new Tag("Java");
        when(tagRepository.existsTagByName("Java")).thenReturn(true);
        when(tagRepository.findTagByName("Java")).thenReturn(Optional.of(javaTag));

        Tag springTag = new Tag("Spring");
        when(tagRepository.existsTagByName("Spring")).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(springTag);

        postService.create(request);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @DisplayName("게시글 삭제 성공 테스트")
    @Test
    void testDeletePostSuccess() {
        Long postId = 1L;
        Post post = new Post("Title", "Context", 0L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.delete(postId);

        assertEquals(Status.NONACTIVE, post.getStatus());
        verify(postRepository, times(1)).save(post);
    }

    @DisplayName("게시글 찾기 오류 테스트")
    @Test
    void testDeletePostNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostDoesNotExistException.class, () -> postService.delete(postId));
    }

    @DisplayName("게시글 조회 성공 테스트")
    @Test
    void testReadPostSuccess() {
        Long postId = 1L;
        Post post = new Post("Title", "Content", 0L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.read(postId);

        assertNotNull(result);
        assertEquals(1L, result.getCount());
    }

    @DisplayName("게시글 조회 찾기 오류 테스트")
    @Test
    void testReadPostNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostDoesNotExistException.class, () -> postService.read(postId));
    }

    @DisplayName("게시글 수정 성공 테스트")
    @Test
    void testUpdatePostSuccess() {
        Long postId = 1L;
        Post post = new Post("Old Title", "Old Content", 0L);
        Category oldCategory = Category.builder().id(1L).name("OldCategory").build();
        post.addPostCategory(oldCategory);

        Category newCategory = Category.builder().id(2L).name("NewCategory").build();
        post.addPostCategory(newCategory);

        PostUpdateRequest request = PostUpdateRequest.builder()
                .postId(postId)
                .title("New Title")
                .context("New Content")
                .tags(List.of("NewTag"))
                .categories(List.of(CategoryRequest.builder().id(2L).name("NewCategory").build()))
                .build();


        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(categoryRepository.findCategoryByName("NewCategory")).thenReturn(Optional.of(newCategory));
        when(postCategoryRepository.findPostCategoryByCategoryAndPost(oldCategory, post)).thenReturn(Optional.of(new PostCategory(post, oldCategory)));

        Tag newTag = new Tag("NewTag");
        when(tagRepository.findTagByName("NewTag")).thenReturn(Optional.of(newTag));

        postService.update(request);

        assertEquals("New Title", post.getTitle());
        assertEquals("New Content", post.getContext());
        assertTrue(post.getPostCategories().stream().anyMatch(pc -> pc.getCategory().getName().equals("NewCategory")));
        verify(postRepository, times(1)).save(post);
    }

    @DisplayName("게시글 수정 실패 테스트")
    @Test
    void testUpdatePostNotFound() {
        Long postId = 1L;
        Post post = new Post("Old Title", "Old Content", 0L);
        Category oldCategory = Category.builder().id(1L).name("OldCategory").build();
        post.addPostCategory(oldCategory);

        Category newCategory = Category.builder().id(2L).name("NewCategory").build();
        post.addPostCategory(newCategory);

        PostUpdateRequest request = PostUpdateRequest.builder()
                .postId(postId)
                .title("New Title")
                .context("New Content")
                .tags(List.of("NewTag"))
                .categories(List.of(CategoryRequest.builder().id(2L).name("NewCategory").build()))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostDoesNotExistException.class, () -> postService.update(request));
    }

    @DisplayName("카테고리 게시글 조회 성공 테스트")
    @Test
    void testReadCategoryPostsSuccess() {
        Long categoryId = 1L;
        Category category = new Category("Tech");
        Post post = new Post("Title1", "Content1", 0L);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        PostCategory postCategory = new PostCategory(post, category);
        List<PostCategory> postCategories = Arrays.asList(postCategory);
        when(postCategoryRepository.findAllByCategory(category)).thenReturn(postCategories);

        List<Post> posts = Arrays.asList(post);
        when(postRepository.findAllByPostCategories(postCategories)).thenReturn(posts);

        List<Post> result = postService.readCategoryPosts(categoryId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Title1", result.get(0).getTitle());
    }

    @DisplayName("카테고리 게시글 조회 실패 테스트")
    @Test
    void testReadCategoryPostsCategoryNotFound() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryDoesNotExistException.class, () -> postService.readCategoryPosts(categoryId));
    }

    @DisplayName("게시글 전체 조회 테스트")
    @Test
    void testReadAllPosts() {
        List<Post> posts = Arrays.asList(new Post("Title1", "Content1", 0L), new Post("Title2", "Content2", 0L));
        when(postRepository.findAll()).thenReturn(posts);

        List<Post> result = postService.readAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Title2", result.get(1).getTitle());
    }

    @DisplayName("게시글 태그 조회 성공 테스트")
    @Test
    void testReadTagPostsSuccess() {
        String tagName = "Java";
        Tag tag = new Tag(tagName);
        Post post = new Post("Title1", "Content1", 0L);
        when(tagRepository.findTagByName(tagName)).thenReturn(Optional.of(tag));

        PostTag postTag = new PostTag(post, tag);
        List<PostTag> postTags = Arrays.asList(postTag);
        when(postTagRepository.findPostTagsByTag(tag)).thenReturn(postTags);

        List<Post> posts = Arrays.asList(post);
        when(postRepository.findAllByPostTags(postTags)).thenReturn(posts);

        List<Post> result = postService.readTagPosts(tagName);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Title1", result.get(0).getTitle());
    }

    @DisplayName("게시글 태그 조회 오류 테스트")
    @Test
    void testReadTagPostsTagNotFound() {
        String tagName = "NonExistentTag";
        when(tagRepository.findTagByName(tagName)).thenReturn(Optional.empty());

        assertThrows(TagDoesNotExistException.class, () -> postService.readTagPosts(tagName));
    }
}