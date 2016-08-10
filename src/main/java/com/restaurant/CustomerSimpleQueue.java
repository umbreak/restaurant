package com.restaurant;

import com.restaurant.model.CustomerGroup;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Size = 16+24+8N ~ O(8N) bytes
 */
public class CustomerSimpleQueue implements CustomersQueue {
    //Size = 24 + N*8
    private final List<CustomerGroup> queue;

    public CustomerSimpleQueue() {
        //It is a linkedList because we will need to remove elements in a concrete possition
        //For linkedList: Iterator.remove() is O(1)
        queue= new LinkedList<>();
    }

    //O(1)
    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    //O(1)
    @Override
    public long size() {
        return queue.size();
    }

    //0(E)
    @Override
    public CustomerGroup dequeGroupOfSize(int size) {
        if(isEmpty()) return null;
        CustomerGroup dequeuedCustomer=null;
        Iterator<CustomerGroup> iterator = queue.iterator();

        // Iterate in reverse.
        while(iterator.hasNext() && dequeuedCustomer == null) {
            CustomerGroup customerGroup = iterator.next();
            if(customerGroup.getSize() == size){
                dequeuedCustomer=customerGroup;
                //O(1)
                iterator.remove();
            }
        }
        return dequeuedCustomer;
    }

    //0(1)
    @Override
    public void enqueue(CustomerGroup customerGroup) {
        queue.add(customerGroup);
    }

    @Override
    public void dequeueGroup(CustomerGroup group) {
        queue.remove(group);
    }

}
