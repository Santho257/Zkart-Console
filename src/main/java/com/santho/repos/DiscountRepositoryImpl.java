package com.santho.repos;

import com.santho.proto.ZDiscount;
import com.santho.services.AuthenticationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DiscountRepositoryImpl implements DiscountRepository{
    private static DiscountRepositoryImpl instance;
    private final File discountFile;
    private final AuthenticationService authService;
    private DiscountRepositoryImpl() throws IOException {
        discountFile = new File("public/db/zdiscount_db.protobuf");
        authService = AuthenticationService.getInstance();
        if(!discountFile.exists()){
            try(FileOutputStream discountOS = new FileOutputStream(discountFile)){

            }
        }
    }

    public static DiscountRepositoryImpl getInstance() throws IOException{
        if (instance == null) {
            instance = new DiscountRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void addDiscount(ZDiscount.Discount discount) throws IOException{
        try (FileInputStream discountIS = new FileInputStream(discountFile)){
            ZDiscount.AllDiscounts allDiscounts = ZDiscount.AllDiscounts.newBuilder()
                    .mergeFrom(discountIS).addDiscounts(discount).build();
            try (FileOutputStream discountOS = new FileOutputStream(discountFile)){
                allDiscounts.writeTo(discountOS);
            }
        }
    }

    @Override
    public List<ZDiscount.Discount> getByUserId(String email) throws IOException {
        try (FileInputStream discountIS = new FileInputStream(discountFile)){
            return ZDiscount.AllDiscounts.parseFrom(discountIS).getDiscountsList()
                    .stream()
                    .filter(dis -> dis.getBelongsTo().equalsIgnoreCase(email) && !dis.getUsed())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void useCode(String code) throws IOException {
        ZDiscount.Discount discount;
        int remIndex;
        try (FileInputStream discountIS = new FileInputStream(discountFile)) {
            List<ZDiscount.Discount> allDiscounts = ZDiscount.AllDiscounts.parseFrom(discountIS).getDiscountsList();
            discount = allDiscounts
                    .stream()
                    .filter(dis -> dis.getCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Code not found"));
            remIndex = allDiscounts.indexOf(discount);
        }
        try (FileInputStream discountIS = new FileInputStream(discountFile)) {
            ZDiscount.AllDiscounts discounts = ZDiscount.AllDiscounts.parseFrom(discountIS)
                    .toBuilder()
                    .setDiscounts(remIndex, discount.toBuilder().setUsed(true).build())
                    .build();
            try(FileOutputStream discountOS = new FileOutputStream(discountFile)){
                discounts.writeTo(discountOS);
            }
        }
    }

    @Override
    public boolean isValid(String code, int orderCount) throws IOException {
        try (FileInputStream discountIS = new FileInputStream(discountFile)){
            List<ZDiscount.Discount> allDiscounts = ZDiscount.AllDiscounts.parseFrom(discountIS).getDiscountsList();
            ZDiscount.Discount discount = allDiscounts
                    .stream()
                    .filter(dis -> dis.getCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Code not found"));
            System.out.println(discount);
            if(!discount.getBelongsTo().equals(authService.getLoggedIn()))
                throw new IllegalArgumentException("You are not allowed to use this token");
            if(discount.getUsed())
                throw new IllegalArgumentException("Already used this token");
            if(orderCount - 3 > discount.getOrderNumber())
                throw new IllegalArgumentException("Token Already Expired");
            return true;
        }
    }
}
