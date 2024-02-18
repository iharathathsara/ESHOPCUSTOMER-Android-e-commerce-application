package com.initezz.ebookshop.listner.cart;

import com.initezz.ebookshop.model.Item;

public interface CartProductSelectListener {
void singleItemView(Item item);
void increaseQty(Item item);
void decreaseQty(Item item);
void removeFromCart(Item item);
}
