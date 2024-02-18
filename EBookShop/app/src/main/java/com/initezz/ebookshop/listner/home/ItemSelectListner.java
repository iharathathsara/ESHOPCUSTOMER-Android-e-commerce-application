package com.initezz.ebookshop.listner.home;

import com.initezz.ebookshop.model.Item;

public interface ItemSelectListner {
    void singleItemView(Item item);
    void addToCart(Item item);
}
