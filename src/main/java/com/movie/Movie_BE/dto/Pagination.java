package com.movie.Movie_BE.dto;

import org.springframework.data.domain.Page;

public class Pagination {
    private final long totalItems;
    private final int totalItemsPerPage;
    private final int currentPage;
    private final int totalPages;

    public Pagination(Page<?> page) {
        this.totalItems = page.getTotalElements();
        this.totalItemsPerPage = page.getSize();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalItemsPerPage() {
        return totalItemsPerPage;
    }

    public int getCurrentPage() {
        return currentPage + 1; // Để trả về trang 1 thay vì trang 0
    }

    public int getTotalPages() {
        return totalPages;
    }
}
