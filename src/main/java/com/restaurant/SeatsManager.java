package com.restaurant;

import com.restaurant.model.CustomerGroup;
import com.restaurant.model.Restaurant;
import com.restaurant.model.SeatManagerException;
import com.restaurant.model.Table;

/**
 * Size = 16 (object overhead) + 8 + 4 + 4 (padding) = 32 bytes
 * Annoted the performance in O notation.
 * N = number of Tables (restaurant.getTables().size())
 * E = number of Customers on queue (restaurant.getQueue().size())
 */

public class SeatsManager {
    //Size = 8
    private final Restaurant restaurant;

    private final AvailableSeatsCounter seatCounter;

    public SeatsManager(Restaurant restaurant, AvailableSeatsCounter seatCounter)
    {
        this.restaurant = restaurant;
        this.seatCounter=seatCounter;
    }

    /* Group arrives and wants to be seated. */
    public Table enter(CustomerGroup group){
        exceptionOnNull(group);

        Table table = tryToAlocateTableToCustomer(group);

        group.setTable(table);
        restaurant.addCustomer(group);
        return table;
    }

    //log(N)
    private Table tryToAlocateTableToCustomer(CustomerGroup group){
        Table table = seatCounter.tryAsignTableToGroupOfSize(group.getSize());
        if(table == null)
            enqueuCustomer(group);
        return table;
    }
    private void enqueuCustomer(CustomerGroup group){
        restaurant.getQueue().enqueue(group);
    }

    /* Whether seated or not, the group leaves the restaurant. */
    //O(E)
    public void leave(CustomerGroup group){
        exceptionOnNull(group);
        if(!restaurant.customerExists(group)){
            throw  new SeatManagerException("The customer group=" + group + " is not a customer from this restaurant");
        }
        Table table=group.getTable();
        if(isCustomerSeated(group)){
            seatCounter.releaseSeatsForTable(table,group.getSize());
            //After the customer leaves, allocate a new one
            //O(E)
            CustomerGroup waitingGroup = restaurant.getQueue().dequeGroupOfSize(group.getSize());
            allocateTableForWaitingCustomer(waitingGroup, table);
        }else{
            removeGroupFromQueue(group);
        }

        group.setTable(null);
        restaurant.removeCustomer(group);

    }

    private void allocateTableForWaitingCustomer(CustomerGroup group, Table table){
        if(group != null){
            seatCounter.bookSeatsForTable(table,group.getSize());
            group.setTable(table);
        }

    }

    private void removeGroupFromQueue(CustomerGroup group){
        restaurant.getQueue().dequeueGroup(group);
        group.setTable(null);

    }


    /* Return the table at which the group is seated, or null if
        they are not seated (whether they're waiting or already left). */
    //O(1)
    public Table locate(CustomerGroup group){
        exceptionOnNull(group);
        if(!restaurant.customerExists(group)){
            //customer is not seated in any table
            return null;
        }
        return group.getTable();
    }

    public boolean isCustomerSeated(CustomerGroup group){
        return (locate(group) != null);
    }


    private void exceptionOnNull(CustomerGroup group){
        if(group == null) throw  new SeatManagerException("Customer cannot be null");

    }
}
