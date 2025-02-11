package com.santho.repos;

import com.santho.proto.ZCategory;
import com.santho.proto.ZProduct;

import java.io.IOException;
import java.util.List;

public interface CategoryRepository {
    void addCategory(ZCategory.Category newCategory) throws IOException;

    ZCategory.Category getByName(String name) throws IOException;

    List<ZCategory.Category> getAll() throws IOException;

    void removeCategory(ZCategory.Category category) throws IOException;
}
