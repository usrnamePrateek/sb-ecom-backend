package com.ecommerce.ecom.service;import com.ecommerce.ecom.dto.CategoryDTO;import com.ecommerce.ecom.dto.CategoryResponse;import com.ecommerce.ecom.entity.Category;import com.ecommerce.ecom.repositories.CategoryRepository;import org.modelmapper.ModelMapper;import org.springframework.data.domain.Page;import org.springframework.data.domain.PageRequest;import org.springframework.data.domain.Pageable;import org.springframework.data.domain.Sort;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.web.server.ResponseStatusException;import java.util.List;import java.util.Optional;@Servicepublic class CategoryService {    private final CategoryRepository categoryRepository;    private final ModelMapper modelMapper;    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {        this.categoryRepository = categoryRepository;        this.modelMapper = new ModelMapper();    }    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {        Sort sortDetails = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortDetails);        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);        List<Category> categories = categoryPage.getContent();        List<CategoryDTO> categoriesDTO = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();        CategoryResponse categoryResponse = new CategoryResponse();        categoryResponse.setContent(categoriesDTO);        categoryResponse.setPageNumber(categoryPage.getNumber());        categoryResponse.setPageSize(categoryPage.getSize());        categoryResponse.setTotalElements(categoryPage.getTotalElements());        categoryResponse.setTotalPages(categoryPage.getTotalPages());        categoryResponse.setIsLastPage(categoryPage.isLast());        return categoryResponse;    }    public void addCategory(CategoryDTO category) {        Optional<Category> foundCategory = categoryRepository.findByCategoryName(category.getCategoryName());        if (foundCategory.isPresent()) {            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");        }        Category categoryEntity = modelMapper.map(category, Category.class);        categoryRepository.save(categoryEntity);    }    public void deleteCategory(Long categoryId) {        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category does not exist"));        categoryRepository.delete(category);    }    public void editCategory(CategoryDTO category, long categoryId) {        Category foundCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category does not exist"));        foundCategory.setCategoryName(category.getCategoryName());        categoryRepository.save(foundCategory);    }}