package com.santho.repos;

import com.santho.proto.ZCategory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepository{
    private static CategoryRepositoryImpl instance;
    private final File categoryFile;
    private CategoryRepositoryImpl(){
        this.categoryFile = new File("public/db/zcategories_db.protobuf");
    }
    public static CategoryRepositoryImpl getInstance(){
        if (instance == null) {
            instance = new CategoryRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void addCategory(ZCategory.Category newCategory){
        try(FileInputStream categoryIS = new FileInputStream(categoryFile)){
            ZCategory.AllCategories allCategories = ZCategory.AllCategories
                    .newBuilder().mergeFrom(categoryIS)
                    .addCategories(newCategory).build();
            try(FileOutputStream categoryOS = new FileOutputStream(categoryFile)){
                allCategories.writeTo(categoryOS);
            }
        }
        catch (IOException ex){
            throw new IllegalStateException("Error adding category! Please try again later");
        }
    }

    @Override
    public ZCategory.Category getByName(String name){
        try(FileInputStream cateIS = new FileInputStream(categoryFile)){
            return ZCategory.AllCategories.parseFrom(cateIS).getCategoriesList()
                    .stream().filter(cat -> cat.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Category with " + name + " not found!"));
        }
        catch (IOException ex){
            throw new IllegalStateException("Error fetching category! Please try again later");
        }
    }

    @Override
    public List<ZCategory.Category> getAll(){
        try(FileInputStream cateIS = new FileInputStream(categoryFile)){
            return ZCategory.AllCategories.parseFrom(cateIS).getCategoriesList();
        }
        catch (IOException ex){
            throw new IllegalStateException("Error fetching category! Please try again later");
        }
    }

    @Override
    public void removeCategory(ZCategory.Category category){
        try(FileInputStream categoryIS = new FileInputStream(categoryFile)){
            ZCategory.AllCategories allCategories = ZCategory.AllCategories.parseFrom(categoryIS);
            List<ZCategory.Category> catList = allCategories.getCategoriesList();
            int remIndex = catList.indexOf(category);
            ZCategory.AllCategories updated = allCategories.toBuilder().removeCategories(remIndex).build();
            try (FileOutputStream categoryOS = new FileOutputStream(categoryFile)){
                updated.writeTo(categoryOS);
            }
        }
        catch (IOException ex){
            throw new IllegalStateException("Error removing category! Please try again later");
        }
    }
}
