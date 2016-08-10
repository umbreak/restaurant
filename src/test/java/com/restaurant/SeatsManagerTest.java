package com.restaurant;


import com.restaurant.model.CustomerGroup;
import com.restaurant.model.Restaurant;
import com.restaurant.model.Table;
import com.restaurant.utils.Rand;
import com.restaurant.utils.TableListGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class SeatsManagerTest {

    private SeatsManager manager;
    private Restaurant restaurant;

    private final static int amountOf6Tables = 10;
    private final static int amountOf5Tables = 10;
    private final static int amountOf4Tables = 10;
    private final static int amountOf3Tables = 10;
    private final static int amountOf2Tables = 10;

    private final static int numberOfCustomers = 500;

    private AvailableSeatsCounter seatsCounter;

    @Before
    public void init() {
        ArrayList<Table> tables = new TableListGenerator.Builder()
                .tableOfSize(2, amountOf2Tables).tableOfSize(3, amountOf3Tables).tableOfSize(4, amountOf4Tables).tableOfSize(5, amountOf5Tables).tableOfSize(6, amountOf6Tables).build();
        CustomersQueue basicQueue = new CustomerSimpleQueue();
        seatsCounter = new SimpleAvailableSeatsCounter(tables,6);
        restaurant = new Restaurant(tables, basicQueue);
        manager = new SeatsManager(restaurant, seatsCounter);
    }

    @After
    public void finish() {
        checksCorrectCountingForAvailableTable();
    }

    /**
     * Leave all the customers form the restaurant.
     */
    @Test
    public void leaveAllCustomers() {
        customersEnter();
        leaveCustomer(restaurant.numCustomers(), CustomerType.Both);
        Assert.assertTrue(restaurant.getQueue().isEmpty());
        Assert.assertTrue(restaurant.numCustomers() == 0);

    }

    /**
     * Leave all the customers which are on the queue form the restaurant.
     */
    @Test
    public void leaveQueueCustomers() {
        customersEnter();
        System.out.println(restaurant.getQueue().size());

        long oldNumCustomers = restaurant.getCustomers().stream().filter(group -> manager.isCustomerSeated(group)).count();
        leaveCustomer(restaurant.getQueue().size(), CustomerType.Waiting);
        Assert.assertTrue(restaurant.getQueue().isEmpty());
        long newNumCustomers = restaurant.getCustomers().stream().filter(group -> manager.isCustomerSeated(group)).count();
        Assert.assertEquals(oldNumCustomers, newNumCustomers);
    }

    /**
     * Leave all the customers which are seated from the restaurant.
     */
    @Test
    public void leaveSeatedCustomers() {
        customersEnter();
        long oldQueueSize = restaurant.getQueue().size();
        long oldNumCustomersNotSeated = restaurant.getCustomers().stream().filter(group -> !manager.isCustomerSeated(group)).count();
        leaveCustomer(oldNumCustomersNotSeated, CustomerType.InTable);
        Assert.assertTrue(restaurant.getQueue().size() < oldQueueSize);
        long numCustomersSeated = restaurant.getCustomers().stream().filter(group -> !manager.isCustomerSeated(group)).count();
        Assert.assertTrue(oldNumCustomersNotSeated > numCustomersSeated);
        System.out.println("customers not seated before=" + oldNumCustomersNotSeated + " now=" + numCustomersSeated + " ( difference=" + (oldNumCustomersNotSeated - numCustomersSeated) + ")");

    }

    private void leaveCustomer(long quantity, CustomerType customerType) {
        long lastQueueSize = restaurant.getQueue().size();
        CustomerGroup customerGroup = getRandomCustomer(customerType);
        if (customerGroup != null && quantity > 0) {
            boolean wasSeated = manager.isCustomerSeated(customerGroup);
            Table table = customerGroup.getTable();
            Integer oldAvailability = (table == null) ? null : table.getAvailableSeats();

            manager.leave(customerGroup);
            System.out.println("Group of " + customerGroup.getSize() + " left the restaurant");
            Assert.assertFalse(restaurant.customerExists(customerGroup));
            if (wasSeated) {
                checksOnCustomerLeaveFromTable(customerGroup, table, oldAvailability, lastQueueSize);
            } else {
                checksOnCustomerLeaveFromQueue(customerGroup, lastQueueSize);
            }
            leaveCustomer(--quantity, customerType);
        }

    }

    private boolean isCustomerFromTheRequiredType(CustomerType type, CustomerGroup group) {
        switch (type) {
            case Both:
                return true;
            case InTable:
                return manager.isCustomerSeated(group);
            case Waiting:
                return !manager.isCustomerSeated(group);
        }
        return false;
    }

    private CustomerGroup getRandomCustomer(final CustomerType customerType) {
        Collection<CustomerGroup> values = restaurant.getCustomers();
        List<CustomerGroup> listCustomer = new ArrayList<>(values);
        Collections.shuffle(listCustomer);
        if (listCustomer.isEmpty()) return null;
        Optional<CustomerGroup> any = listCustomer.stream().filter(group -> isCustomerFromTheRequiredType(customerType, group)).findAny();
        if (any.isPresent()) return any.get();
        return null;
    }


    private long checksOnCustomerLeaveFromQueue(CustomerGroup group, long lastQueueSize) {
        Assert.assertEquals(restaurant.getQueue().size(), --lastQueueSize);
        return lastQueueSize;
    }

    private long checksOnCustomerLeaveFromTable(CustomerGroup group, Table table, int oldTableAvailability, long lastQueueSize) {
        Assert.assertFalse(manager.isCustomerSeated(group));
        //That means somebody else is using his table after the customer leaves
        if (oldTableAvailability == table.getAvailableSeats()) {
            Assert.assertEquals(--lastQueueSize, restaurant.getQueue().size());
            CustomerGroup customerUsingTable = findCustomerUsingTable(table);
            System.out.println("Group of " + customerUsingTable.getSize() + " on the waiting queue enters the restaurant");
            Assert.assertNotNull(customerUsingTable);
        } else {
            Assert.assertEquals(oldTableAvailability + group.getSize(), table.getAvailableSeats());
        }
        return lastQueueSize;
    }

    private CustomerGroup findCustomerUsingTable(Table table) {
        for (CustomerGroup customerGroup : restaurant.getCustomers()) {
            if (Objects.equals(table, customerGroup.getTable()))
                return customerGroup;
        }
        return null;
    }

    /**
     * Enter $numberOfCustomers to the restaurant
     */
    @Test
    public void customersEnter() {
        long lastQueueSize = restaurant.getQueue().size();
        for (int i = 0; i < numberOfCustomers; i++) {
            int groupSize = Rand.randomRange(1, 6);
            CustomerGroup group = new CustomerGroup(groupSize);
            manager.enter(group);
            System.out.println("Group of " + group.getSize() + " trying to enter the restaurant");
            //Check that always the tables list are sorted
            if (manager.isCustomerSeated(group)) {
                checksOnCustomerEnterAndItIsSeated(group);
            } else {
                lastQueueSize = checksOnCustomerEnterAndItIsSetToWait(lastQueueSize, group);
            }

        }
    }

    private void checksOnCustomerEnterAndItIsSeated(CustomerGroup group) {
        Assert.assertNotNull(group.getTable());
        System.out.println("Group of " + group.getSize() + " is seated");
    }

    private long checksOnCustomerEnterAndItIsSetToWait(long lastQueueSize, CustomerGroup group) {
        Assert.assertEquals(++lastQueueSize, restaurant.getQueue().size());
        Assert.assertFalse(thereIsFreeTableBruteForce(group));
        System.out.println("Group of " + group.getSize() + " could not be seated in any table. No table avaiable");
        return lastQueueSize;
    }


    private void checksCorrectCountingForAvailableTable() {
        Map<String, Integer> ocupiedSeatsOnTablesFromCustomers = getOcupiedSeatsOnTablesFromCustomers();
        for (Table table : getAllTables()) {
            Integer ocupiedSeatsOnCustomerCount = ocupiedSeatsOnTablesFromCustomers.get(table.getId());
            if (ocupiedSeatsOnCustomerCount == null) {
                ocupiedSeatsOnCustomerCount = 0;
            }
            int availableSeats = table.getAvailableSeats();
            Assert.assertEquals((table.getTotalSeats() - ocupiedSeatsOnCustomerCount), availableSeats);
        }
    }

    private List<Table> getAllTables(){
        List<Table> tables=new ArrayList<>();
        for (int i = 0; i <=6 ; i++) {
            tables.addAll(seatsCounter.getAllTablesWithAvaialbleSize(i));
        }
        return tables;
    }

    private Map<String, Integer> getOcupiedSeatsOnTablesFromCustomers() {
        Map<String, Integer> ocupiedSeatsFromCustomer = new HashMap<>();
        for (CustomerGroup customer : restaurant.getCustomers()) {
            if (manager.isCustomerSeated(customer)) {
                Integer ocupiedSeats = customer.getSize();
                String tableID = customer.getTable().getId();
                if (ocupiedSeatsFromCustomer.containsKey(tableID)) {
                    ocupiedSeats += ocupiedSeatsFromCustomer.get(tableID);
                }
                ocupiedSeatsFromCustomer.put(tableID, ocupiedSeats);
            }
        }
        return ocupiedSeatsFromCustomer;
    }


    private boolean thereIsFreeTableBruteForce(CustomerGroup group) {
        for (Table table : getAllTables()) {
            if (table.getAvailableSeats() == group.getSize())
                return true;
        }
        return false;
    }

    private enum CustomerType {
        Waiting, InTable, Both
    }

}
