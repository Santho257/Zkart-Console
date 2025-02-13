package com.santho.services.category;

import java.io.IOException;

public interface CategoryService {
    void addCategory();

    boolean alreadyExists(String name);

    void displayAll();

    void removeCategory();

    void addCategory(String category);
}
