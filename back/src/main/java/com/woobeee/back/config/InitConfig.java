package com.woobeee.back.config;


import com.woobeee.back.entity.Category;
import com.woobeee.back.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitConfig {
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        if (false) {
            Category category = new Category("BACKEND", "BACKEND", null);
            category = categoryRepository.save(category);

            Category category1 = new Category("Spring Batch", "Spring Batch", category.getId());
            category1 = categoryRepository.save(category1);

            Category category2 = new Category("FRONTEND", "FRONTEND", null);
            category2 = categoryRepository.save(category2);

            Category category3 = new Category("NextJS", "NextJS", category2.getId());
            category3 = categoryRepository.save(category3);
        }

    }
}
