package org.jared.dealstream;

import lombok.NonNull;
import lombok.Value;

public class DynamoPriceRepository {
    @Value
    public static class ProductPrice {
        @NonNull
        String sku;
        double price;
        @NonNull
        String retailer;
        String url;
    }

    public static void main(String... args) {
    }
}
