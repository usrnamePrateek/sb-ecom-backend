package com.ecommerce.ecom.service;

import com.ecommerce.ecom.dto.ProductDTO;
import com.ecommerce.ecom.dto.ProductResponse;
import com.ecommerce.ecom.entity.Cart;
import com.ecommerce.ecom.entity.Category;
import com.ecommerce.ecom.entity.Product;
import com.ecommerce.ecom.exceptions.model.ApiException;
import com.ecommerce.ecom.repositories.CartRepository;
import com.ecommerce.ecom.repositories.CategoryRepository;
import com.ecommerce.ecom.repositories.ProductRepository;
import io.micrometer.common.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final CartRepository cartRepository;
    private final String baseImageUrl;

    ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                   ModelMapper modelMapper, FileService fileService, CartRepository cartRepository,
                   @Value("${base.image.url}") String baseImageUrl) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
        this.cartRepository = cartRepository;
        this.baseImageUrl = baseImageUrl;
    }

    public ProductDTO addProduct(Long categoryId, ProductDTO productDetails) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ApiException("Category not found", HttpStatus.BAD_REQUEST));

        if (0 != productRepository.findProductByCategoryIdAndProductName(categoryId, productDetails.getProductName())) {
            throw new ApiException("Product with that name already exists in the category", HttpStatus.BAD_REQUEST);
        }

        Product product = modelMapper.map(productDetails, Product.class);
        product.setCategory(category);
        product.setImage("product.png");
        product.setSpecialPrice(product.getPrice() * (1 - product.getDiscount() * 0.01));
        productRepository.save(product);

        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,
                                          String category, String keyword) {
        Sort sortDetails = "asc".equals(sortOrder) ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortDetails);

        Specification<Product> spec = Specification.where(null);

        if(!StringUtils.isEmpty(category)) {
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("category").get(
                            "categoryName")), category.toLowerCase())));
        }

        if(!StringUtils.isEmpty(keyword)) {
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),
                            "%" + keyword.toLowerCase() + "%")));
        }

        Page<Product> productsPages = productRepository.findAll(spec, pageDetails);
        List<Product> products = productsPages.getContent();

        List<ProductDTO> productDTOS = products.stream().map(product -> {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            productDTO.setImage(constructImageUrl(product.getImage()));
            return productDTO;
        }).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPages.getNumber());
        productResponse.setPageSize(productsPages.getSize());
        productResponse.setTotalPages(productsPages.getTotalPages());
        productResponse.setTotalElements(productsPages.getTotalElements());
        productResponse.setIsLastPage(productsPages.isLast());
        return productResponse;
    }

    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ApiException("Category not found", HttpStatus.BAD_REQUEST));

        Sort sortDetails = "asc".equals(sortOrder) ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortDetails);

        Page<Product> productsPages = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productsPages.getContent();

        List<ProductDTO> productDTOS = products.stream().map(product ->
                modelMapper.map(product, ProductDTO.class)).toList();


        ProductResponse productResponse  = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPages.getNumber());
        productResponse.setPageSize(productsPages.getSize());
        productResponse.setTotalPages(productsPages.getTotalPages());
        productResponse.setTotalElements(productsPages.getTotalElements());
        productResponse.setIsLastPage(productsPages.isLast());
        return productResponse;
    }

    public ProductResponse getAllProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortDetails = "asc".equals(sortOrder) ? Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortDetails);

        Page<Product> productsPages = productRepository.findByProductNameLikeIgnoreCase("%".concat(keyword).concat("%"),
                pageDetails);

        List<Product> products = productsPages.getContent();

        List<ProductDTO> productDTOS = products.stream().map(product ->
                modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse  = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPages.getNumber());
        productResponse.setPageSize(productsPages.getSize());
        productResponse.setTotalPages(productsPages.getTotalPages());
        productResponse.setTotalElements(productsPages.getTotalElements());
        productResponse.setIsLastPage(productsPages.isLast());
        return productResponse;
    }

    public ProductDTO updateProduct(Long productId, ProductDTO productDetails) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ApiException("product not found", HttpStatus.BAD_REQUEST));

        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setDiscount(productDetails.getDiscount());
        product.setQuantity(productDetails.getQuantity());
        product.setSpecialPrice(productDetails.getPrice() * (1 - productDetails.getDiscount() * 0.01));

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ApiException("product not found", HttpStatus.BAD_REQUEST));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);

    }

    public ProductDTO updateImage(Long productId, MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ApiException("product not found", HttpStatus.BAD_REQUEST));

        String imageFileLoc = fileService.uploadFile(imageFile);
        product.setImage(imageFileLoc);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    private String constructImageUrl(String imageName){
        return baseImageUrl.concat(imageName);
    }
}
