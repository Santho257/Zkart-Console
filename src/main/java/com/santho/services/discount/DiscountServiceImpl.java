package com.santho.services.discount;

import com.santho.proto.ZDiscount;
import com.santho.repos.DiscountRepository;
import com.santho.repos.DiscountRepositoryImpl;
import com.santho.services.AuthenticationService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class DiscountServiceImpl implements DiscountService {
    private static DiscountServiceImpl instance;
    private final DiscountRepository discountRepository;
    private final AuthenticationService authService;

    private DiscountServiceImpl() throws IOException {
        discountRepository = DiscountRepositoryImpl.getInstance();
        authService = AuthenticationService.getInstance();
    }

    public static DiscountServiceImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new DiscountServiceImpl();
        }
        return instance;
    }

    @Override
    public String generateCoupon(int orderNumber) throws IOException {
        UUID coupon = UUID.randomUUID();
        String code = coupon.toString().substring(0, 6).toUpperCase();
        String logged = authService.getLoggedIn();
        discountRepository.addDiscount(ZDiscount.Discount.newBuilder()
                .setCode(code)
                .setBelongsTo(logged)
                .setOrderNumber(orderNumber)
                .setUsed(false)
                .build());
        return code;
    }

    @Override
    public void showUserCoupon(String email, int orderNumber) throws IOException {
        List<ZDiscount.Discount> userDiscount = discountRepository.getByUserId(email);
        System.out.println("Code   Validity");
        for (ZDiscount.Discount discount : userDiscount) {
            int validity = 4 - (orderNumber - discount.getOrderNumber());
            if (validity > 0)
                System.out.println(discount.getCode() + " " + validity);
        }
    }

    @Override
    public boolean hasCoupon(String email) throws IOException {
        return !discountRepository.getByUserId(email).isEmpty();
    }

    @Override
    public boolean isValid(String code, int count) throws IOException {
        return discountRepository.isValid(code, count);
    }

    @Override
    public void useDiscount(String code) throws IOException{
        discountRepository.useCode(code);
    }
}
