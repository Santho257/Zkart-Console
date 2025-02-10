package com.santho.repos;

import com.santho.proto.ZDiscount;

import java.io.IOException;
import java.util.List;

public interface DiscountRepository {
    void addDiscount(ZDiscount.Discount discount) throws IOException;

    List<ZDiscount.Discount> getByUserId(String email) throws IOException;

    void useCode(String code) throws IOException;

    boolean isValid(String code, int orderCount) throws IOException;
}
