package com.restaurant;

import com.restaurant.model.CustomerGroup;

public interface CustomersQueue {

    boolean isEmpty();
    long size();
    CustomerGroup dequeGroupOfSize(int size);
    void enqueue(CustomerGroup customerGroup);
    void dequeueGroup(CustomerGroup customerGroup);

}
