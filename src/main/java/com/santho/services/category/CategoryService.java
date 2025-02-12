package com.santho.services.category;

import java.io.IOException;

public interface CategoryService {
    void addCategory() throws IOException;

    boolean alreadyExists(String name) throws IOException;

    void displayAll() throws IOException;

    void removeCategory() throws IOException;

    void addCategory(String category) throws IOException;
}
