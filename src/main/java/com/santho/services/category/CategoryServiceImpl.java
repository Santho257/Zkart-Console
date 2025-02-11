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
    public void addCategory() throws IOException {
        String name = InputHelper.getInput("Enter Category name: ");
        if(alreadyExists(name)) throw new IllegalArgumentException(name + " Already exists!!");
        categoryRepository.addCategory(ZCategory.Category.newBuilder().setName(name.toUpperCase()).build());
    }

    @Override
    public boolean alreadyExists(String name) throws IOException {
        List<ZCategory.Category> allCategories = categoryRepository.getAll();
        for(ZCategory.Category cat : allCategories){
            if(cat.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void displayAll() throws IOException {
        List<ZCategory.Category> allCategories = categoryRepository.getAll();
        System.out.println(DesignHelper.printDesign(50, '-', "All Categories"));
        System.out.println(DesignHelper.printDesign(50));
        for(ZCategory.Category cat : allCategories){
            System.out.println(cat.getName());
        }
        System.out.println(DesignHelper.printDesign(50));
    }

    @Override
    public void removeCategory() throws IOException{
        displayAll();
        String name = InputHelper.getInput("Enter Category Name: ");
        ZCategory.Category deletedCat = categoryRepository.getByName(name);
        categoryRepository.removeCategory(deletedCat);
    }
}
