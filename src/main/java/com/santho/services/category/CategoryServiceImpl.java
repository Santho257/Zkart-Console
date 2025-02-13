package com.santho.services.category;

import com.santho.helpers.DesignHelper;
import com.santho.helpers.InputHelper;
import com.santho.proto.ZCategory;
import com.santho.repos.CategoryRepository;
import com.santho.repos.CategoryRepositoryImpl;

import java.io.IOException;
import java.util.List;

public class CategoryServiceImpl implements CategoryService{
    private static CategoryServiceImpl instance;
    private final CategoryRepository categoryRepository;
    private CategoryServiceImpl(){
        categoryRepository = CategoryRepositoryImpl.getInstance();
    }
    public static CategoryServiceImpl getInstance(){
        if (instance == null) {
            instance = new CategoryServiceImpl();
        }
        return instance;
    }

    @Override
    public void addCategory(){
        String name = InputHelper.getInput("Enter Category name: ");
        addCategory(name);
    }

    @Override
    public void addCategory(String category){
        if(alreadyExists(category)) throw new IllegalArgumentException(category + " Already exists!!");
        categoryRepository.addCategory(ZCategory.Category.newBuilder().setName(category.toUpperCase()).build());
    }

    @Override
    public boolean alreadyExists(String name){
        List<ZCategory.Category> allCategories = categoryRepository.getAll();
        for(ZCategory.Category cat : allCategories){
            if(cat.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void displayAll(){
        List<ZCategory.Category> allCategories = categoryRepository.getAll();
        System.out.println(DesignHelper.printDesign(75, '-', "All Categories"));
        System.out.println(DesignHelper.printDesign(75));
        for(ZCategory.Category cat : allCategories){
            System.out.println(cat.getName());
        }
        System.out.println(DesignHelper.printDesign(75));
    }

    @Override
    public void removeCategory(){
        displayAll();
        String name = InputHelper.getInput("Enter Category Name: ");
        ZCategory.Category deletedCat = categoryRepository.getByName(name);
        categoryRepository.removeCategory(deletedCat);
    }
}
