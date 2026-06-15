package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CartItemMapper.class)
public interface CartMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "cartItems", ignore = true)
    Cart toCart(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", source = "cartItems", qualifiedByName = "sumTotalPrice")
    CartResponse toCartResponse(Cart cart);

    @Named("sumTotalPrice")
    default BigDecimal sumTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }
}
