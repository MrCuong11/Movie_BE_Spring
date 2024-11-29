package com.movie.Movie_BE.Service;

import com.movie.Movie_BE.Model.Category;
import com.movie.Movie_BE.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories (){
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById (Long id){
        return  categoryRepository.findById(id);
    }

    public Category createCategory (Category category){
        return categoryRepository.save(category);
    }

    public void deleteCategory (Long id){
        categoryRepository.deleteById(id);
    }

    public Category updateCategory (Long id, Category categoryDetail){
        return categoryRepository.findById(id).map(category -> {
           if (categoryDetail.getName() != null){
               category.setName(categoryDetail.getName());
           }
           if (categoryDetail.getSlug() != null){
               category.setSlug(categoryDetail.getSlug());
           }

           return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with: "+id));
    }
}
