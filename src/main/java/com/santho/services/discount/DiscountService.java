package com.santho.services.discount;

import java.io.IOException;

public interface DiscountService {
    String generateCoupon(int orderNumber);

    void showUserCoupon(String email, int orderNumber);

    boolean hasCoupon(String email);

    boolean isValid(String code, int count);

    void useDiscount(String code);
}
