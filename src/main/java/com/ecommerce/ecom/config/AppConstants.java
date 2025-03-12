package com.ecommerce.ecom.config;

public class AppConstants {
    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "10";
    public static final String SORT_ORDER = "asc";
    public static final String SORT_CATEGORIES_BY = "categoryId";
    public static final String IMAGES_PATH = "images/";
    public static final String SORT_PRODUCTS_BY = "productId";

    public static enum AppRole{
        ROLE_USER,
        ROLE_SELLER,
        ROLE_ADMIN
    }
}
