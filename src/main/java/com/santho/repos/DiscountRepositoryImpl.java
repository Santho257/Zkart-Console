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
    private DiscountRepositoryImpl(){
        discountFile = new File("public/db/zdiscount_db.protobuf");
        authService = AuthenticationService.getInstance();
        if(!discountFile.exists()){
            try(FileOutputStream discountOS = new FileOutputStream(discountFile)){
            }
            catch (IOException ex){
                throw new IllegalStateException("Error from our side! Please try again later");
            }
        }
    }

    public static DiscountRepositoryImpl getInstance(){
        if (instance == null) {
            instance = new DiscountRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void addDiscount(ZDiscount.Discount discount){
        try (FileInputStream discountIS = new FileInputStream(discountFile)){
            ZDiscount.AllDiscounts allDiscounts = ZDiscount.AllDiscounts.newBuilder()
                    .mergeFrom(discountIS).addDiscounts(discount).build();
            try (FileOutputStream discountOS = new FileOutputStream(discountFile)){
                allDiscounts.writeTo(discountOS);
            }
        }
        catch (IOException ex){
            throw new IllegalStateException("Error storing coupon! Sorry for the inconvenience");
        }
    }

    @Override
    public List<ZDiscount.Discount> getByUserId(String email){
        try (FileInputStream discountIS = new FileInputStream(discountFile)){
            return ZDiscount.AllDiscounts.parseFrom(discountIS).getDiscountsList()
                    .stream()
                    .filter(dis -> dis.getBelongsTo().equalsIgnoreCase(email) && !dis.getUsed())
                    .collect(Collectors.toList());
        }
        catch (IOException ex){
            throw new IllegalStateException("Error while fetching coupon! Please try again later!");
        }
    }

    @Override
    public void useCode(String code){
        ZDiscount.Discount discount;
        int remIndex;
        try (FileInputStream discountIS = new FileInputStream(discountFile)) {
            List<ZDiscount.Discount> allDiscounts = ZDiscount.AllDiscounts.parseFrom(discountIS).getDiscountsList();
            discount = allDiscounts
                    .stream()
                    .filter(dis -> dis.getCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Code not found"));
            remIndex = allDiscounts.indexOf(discount);
        }
        catch (IOException ex){
            throw new IllegalStateException("Error fetching coupon! Please try again later!");
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
        catch (IOException ex){
            throw new IllegalStateException("Error using coupon! Please try again later!");
        }
    }

    @Override
    public boolean isValid(String code, int orderCount){
        try (FileInputStream discountIS = new FileInputStream(discountFile)){
            List<ZDiscount.Discount> allDiscounts = ZDiscount.AllDiscounts.parseFrom(discountIS).getDiscountsList();
            ZDiscount.Discount discount = allDiscounts
                    .stream()
                    .filter(dis -> dis.getCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Code not found"));
            if(!discount.getBelongsTo().equals(authService.getLoggedIn()))
                throw new IllegalStateException("You are not allowed to use this token");
            if(discount.getUsed())
                throw new IllegalStateException("Already used this token");
            if(orderCount - 3 > discount.getOrderNumber())
                throw new IllegalStateException("Token Already Expired");
            return true;
        }
        catch (IOException ex){
            throw new IllegalStateException("Error validating coupon! Please try again later!");
        }
    }
}
