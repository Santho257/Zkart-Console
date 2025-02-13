package com.santho.repos;

import com.santho.proto.ZDiscount;

import java.io.IOException;
import java.util.List;

public interface DiscountRepository {
    void addDiscount(ZDiscount.Discount discount);

    List<ZDiscount.Discount> getByUserId(String email);

    void useCode(String code);

    boolean isValid(String code, int orderCount);
}
