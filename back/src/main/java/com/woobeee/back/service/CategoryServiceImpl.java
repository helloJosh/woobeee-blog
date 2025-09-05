package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostCategoryRequest;
import com.woobeee.back.dto.response.GetCategoryResponse;
import com.woobeee.back.entity.Category;
import com.woobeee.back.entity.Post;
import com.woobeee.back.repository.CategoryRepository;
import com.woobeee.back.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    @Override
    public void saveCategory(PostCategoryRequest request, Long parentId) {
        categoryRepository.save(
                new Category (
                        request.getNameKo(),
                        request.getNameEn(),
                        parentId)
        );

    }

    @Override
    public void deleteCategory(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(categoryId);

        while (!stack.isEmpty()) {
            Long id = stack.pop();
            ids.add(id);
            for (Category child : categoryRepository.findAllByParentId(id)) {
                stack.push(child.getId());
            }
        }

        postRepository.deleteAllByCategoryIdIn(ids);
        categoryRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<GetCategoryResponse> getCategoryList(String locale) {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) return List.of();

        List<Long> ids = categories.stream()
                .map(Category::getId)
                .toList();

        Map<Long, Integer> countMap = postRepository.countGroupByCategoryId(ids).stream()
                .collect(Collectors.toMap(
                        PostRepository.CategoryCount::getCategoryId,
                        c -> (int) c.getCnt()
                ));

        boolean isKo = locale != null && locale.toLowerCase().startsWith("ko");
        Function<Category, String> nameSelector = c -> isKo
                ? defaultIfBlank(c.getNameKo(), c.getNameEn())
                : defaultIfBlank(c.getNameEn(), c.getNameKo());

        Map<Long, GetCategoryResponse> dtoMap = new HashMap<>(ids.size() * 2);
        for (Category c : categories) {
            dtoMap.put(
                    c.getId(),
                    new GetCategoryResponse(
                            c.getId(),
                            nameSelector.apply(c),
                            countMap.getOrDefault(c.getId(), 0),
                            new ArrayList<>()
                    )
            );
        }

        List<GetCategoryResponse> roots = new ArrayList<>();
        for (Category c : categories) {
            GetCategoryResponse me = dtoMap.get(c.getId());
            Long parentId = c.getParentId();
            if (parentId == null) {
                roots.add(me);
            } else {
                GetCategoryResponse parent = dtoMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(me);
                }
            }
        }

        return roots;
    }
}
