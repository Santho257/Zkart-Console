package com.santho.services.discount;

import java.io.IOException;

public interface DiscountService {
    String generateCoupon(int orderNumber) throws IOException;

    void showUserCoupon(String email, int orderNumber) throws IOException;

    boolean hasCoupon(String email) throws IOException;

    boolean isValid(String code, int count) throws IOException;

    void useDiscount(String code) throws IOException;
}
