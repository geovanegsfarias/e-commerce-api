package com.github.geovanegsfarias.cart;
import com.github.geovanegsfarias.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    CartItem toCartItem(CreateCartItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", source = "cart")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "quantity", source = "quantity")
    CartItem toCartItem(Cart cart, Product product, int quantity);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);
}