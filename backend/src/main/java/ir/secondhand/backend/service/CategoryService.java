package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.request.CategoryRequest;
import ir.secondhand.backend.dto.response.CategoryResponse;
import ir.secondhand.backend.entity.Category;
import ir.secondhand.backend.exception.DuplicateResourceException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * مدیریت دسته‌بندی‌های آگهی (شامل ساختار سلسله‌مراتبی اختیاری).
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("این دسته‌بندی قبلا ثبت شده است.");
        }
        Category category = new Category(request.getTitle(), request.getDescription());
        if (request.getParentId() != null) {
            category.setParent(findCategoryOrThrow(request.getParentId()));
        }
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        category.setTitle(request.getTitle());
        category.setDescription(request.getDescription());
        if (request.getParentId() != null && !request.getParentId().equals(id)) {
            category.setParent(findCategoryOrThrow(request.getParentId()));
        } else {
            category.setParent(null);
        }
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("این دسته‌بندی پیدا نشد."));
    }
}
