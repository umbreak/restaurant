package com.restaurant.model;

import com.restaurant.CustomersQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 16(object overhead) + 24 + 4 + 4(overhead) + 8*N + 32*M*CAPACITY + 8. Since M >> N ~ O(32*M*CAPACITY)
 */
public class Restaurant {
    //32 * M * CAPACITY
    private final Map<String,CustomerGroup> customers;

    //8
    private final CustomersQueue queue;

    //4
    public final static int MAX_GROUP_SIZE=6;

    public Restaurant(List<Table> tables, CustomersQueue queue) {
        this.queue=queue;
        customers=new HashMap<>();
    }

    public CustomersQueue getQueue() {
        return queue;
    }

    public void addCustomer(CustomerGroup group){
        customers.put(group.getId(),group);
    }
    public boolean customerExists(CustomerGroup group){
        return customers.containsKey(group.getId());

    }
    public void removeCustomer(CustomerGroup group){
        customers.remove(group.getId());
    }
    public long numCustomers(){
        return customers.size();
    }
    public List<CustomerGroup> getCustomers(){
        return new ArrayList<>(customers.values());
    }
}
