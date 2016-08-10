package com.restaurant;

import com.restaurant.model.CustomerGroup;
import com.restaurant.utils.Rand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class CustomerQueueTest {

    private CustomersQueue queue;
    private List<CustomerGroup> customersID;

    private final static int numberOfCustomers = 20;


    @Before
    public void init() {
        queue = new CustomerSimpleQueue();
        customersID = new ArrayList<>();
    }

    @Test
    public void enqueue() {
        for (int i = 0; i < numberOfCustomers; i++) {
            CustomerGroup customerGroup = new CustomerGroup(Rand.randomRange(1, 6));
            queue.enqueue(customerGroup);
            customersID.add(customerGroup);
            System.out.println("Enqueued group of size==" + customerGroup.getSize() + " customer=" + customerGroup);
        }
        Assert.assertEquals(queue.size(), numberOfCustomers);
    }

    @Test
    public void dequeue() {
        enqueue();
        long queueSize = queue.size();
        while (!queue.isEmpty()) {
            int size = Rand.randomRange(1, 6);
            CustomerGroup customerGroup = queue.dequeGroupOfSize(size);
            if (customerGroup != null) {
                Assert.assertEquals(queue.size(), --queueSize);
                Assert.assertTrue(checkIsFirstInOrderOfAdition(customerGroup));
                customersID.remove(customerGroup);
                System.out.println("Dequeying group of size=" + size + " customerGroup=" + customerGroup);
            } else {
                System.out.println("Customer for size=" + size + " not found");
            }
        }
    }

    private boolean checkIsFirstInOrderOfAdition(CustomerGroup group) {
        int positionOfAdition = customersID.indexOf(group);
        for (int i = 0; i < customersID.size(); i++) {
            CustomerGroup customerGroup = customersID.get(i);
            if (!customerGroup.getId().equals(group.getId())
                    && customerGroup.getSize() == group.getSize() && i < positionOfAdition) {
                System.out.println("customer=" + customerGroup + " was enqueued before customer=" + group);
                System.out.println("position of first in the list is=" + i + " position of second is " + positionOfAdition);
                return false;
            }
        }
        return true;
    }

}
