package com.santho.repos;

import com.santho.proto.ZCategory;

import java.io.IOException;
import java.util.List;

public interface CategoryRepository {
    void addCategory(ZCategory.Category newCategory);

    ZCategory.Category getByName(String name);

    List<ZCategory.Category> getAll();

    void removeCategory(ZCategory.Category category);
}
