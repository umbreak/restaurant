package com.restaurant;

import com.restaurant.model.SeatManagerException;
import com.restaurant.model.Table;
import com.restaurant.utils.TableListGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by dmontero on 08/05/16.
 */
public class AvailableSeatsCounterTest {

    private ArrayList<Table> tables;

    private final static int amountOf6Tables=15;
    private final static int amountOf5Tables=25;
    private final static int amountOf4Tables=10;
    private final static int amountOf3Tables=8;
    private final static int amountOf2Tables=30;

    private AvailableSeatsCounter seatsCounter;


    @Before
    public void init(){
        this.tables=new TableListGenerator.Builder()
                .tableOfSize(2,amountOf2Tables).tableOfSize(3,amountOf3Tables).tableOfSize(4,amountOf4Tables).tableOfSize(5,amountOf5Tables).tableOfSize(6,amountOf6Tables).build();
        seatsCounter = new SimpleAvailableSeatsCounter(tables,6);
    }
    @Test
    public void testInitAvaialability() {
        Integer[] sizesChecks=new Integer[]{0,0,amountOf2Tables,amountOf3Tables,amountOf4Tables,amountOf5Tables,amountOf6Tables};
        for (int i = 0; i < sizesChecks.length; i++) {
            int amount=sizesChecks[i];
            List<Table> tables=seatsCounter.getAllTablesWithAvaialbleSize(i);
            Assert.assertEquals(tables.size(),amount);
            checkAvailatyOfTablesEqualsTo(tables,i);
        }
    }

    @Test
    public void testTryAsignTables(){
        Integer[] sizesChecks=new Integer[]{amountOf2Tables,amountOf3Tables,amountOf4Tables,amountOf5Tables,amountOf6Tables};
        for (int i = 0; i < sizesChecks.length; i++) {
            int size=i+2;
            int amount=sizesChecks[i];
            int availabilityCount=seatsCounter.getAllTablesWithAvaialbleSize(size).size();
            for (int j = 0; j < amount; j++) {
                System.out.println("Trying to assing a table to a group of size=" + size);
                Table table=seatsCounter.tryAsignTableToGroupOfSize(size);
                Assert.assertNotNull(table);
                int newAvailability=seatsCounter.getAllTablesWithAvaialbleSize(size).size();
                Assert.assertEquals(newAvailability,--availabilityCount);
            }
            Assert.assertTrue(seatsCounter.getAllTablesWithAvaialbleSize(size).isEmpty());
        }
        Assert.assertEquals(seatsCounter.getAllTablesWithAvaialbleSize(0).size(),amountOf2Tables+amountOf3Tables+amountOf4Tables+amountOf5Tables+amountOf6Tables);

        //trying to add more customers than available on tables
        for (int i = 2; i < 7; i++) {
            Assert.assertNull(seatsCounter.tryAsignTableToGroupOfSize(i));

        }
    }

    @Test
    public void tryToReleaseTables(){
        testTryAsignTables();
        Integer[] sizesChecks=new Integer[]{amountOf2Tables,amountOf3Tables,amountOf4Tables,amountOf5Tables,amountOf6Tables};
        for (int i = 0; i < sizesChecks.length; i++) {
            int size=i+2;
            int amount=sizesChecks[i];
            int availabilityCount=seatsCounter.getAllTablesWithAvaialbleSize(size).size();
            for (int j = 0; j < amount; j++) {
                System.out.println("Group of size=" + size + " leaves");
                Table table=getOneTablesWithTotalSize(size);
                Assert.assertNotNull(table);
                seatsCounter.releaseSeatsForTable(table,size);
                int newAvailability=seatsCounter.getAllTablesWithAvaialbleSize(size).size();
                Assert.assertEquals(newAvailability,++availabilityCount);
            }
            Assert.assertEquals(seatsCounter.getAllTablesWithAvaialbleSize(size).size(),amount);
        }
        Assert.assertTrue(seatsCounter.getAllTablesWithAvaialbleSize(0).isEmpty());
    }

    @Test(expected = SeatManagerException.class)
    public void testToReleaseException(){
        Table table=getOneTablesWithTotalSize(5);
        seatsCounter.releaseSeatsForTable(table,6);
    }

    @Test(expected = SeatManagerException.class)
    public void testToReleaseException2(){
        testTryAsignTables();
        Table table=getOneTablesWithTotalSize(5);
        seatsCounter.releaseSeatsForTable(table,3);
        seatsCounter.releaseSeatsForTable(table,3);
    }

    @Test public void testAsignTablesFromOtherSizes(){
        //example with 5
        int availabilityCount5=seatsCounter.getAllTablesWithAvaialbleSize(5).size();
        int availabilityCount6=seatsCounter.getAllTablesWithAvaialbleSize(6).size();

        for (int i = 0; i < amountOf5Tables+amountOf6Tables; i++) {
            Table table=seatsCounter.tryAsignTableToGroupOfSize(5);
            Assert.assertNotNull(table);

            int newAvailability5=seatsCounter.getAllTablesWithAvaialbleSize(5).size();
            int newAvailability6=seatsCounter.getAllTablesWithAvaialbleSize(6).size();
            if(i < amountOf5Tables){
                Assert.assertEquals(newAvailability5,--availabilityCount5);
                Assert.assertEquals(newAvailability6,availabilityCount6);
            }else{
                Assert.assertEquals(newAvailability5,availabilityCount5);
                Assert.assertEquals(newAvailability6,--availabilityCount6);
            }
        }
        Assert.assertTrue(seatsCounter.getAllTablesWithAvaialbleSize(5).isEmpty());
        Assert.assertTrue(seatsCounter.getAllTablesWithAvaialbleSize(6).isEmpty());
        Assert.assertEquals(seatsCounter.getAllTablesWithAvaialbleSize(0).size(),amountOf5Tables);
        Assert.assertEquals(seatsCounter.getAllTablesWithAvaialbleSize(1).size(),amountOf6Tables);


    }

    private Table getOneTablesWithTotalSize(int size){
        for (int i = 0; i < 7; i++) {
            List<Table> allTablesWithAvaialbleSize = seatsCounter.getAllTablesWithAvaialbleSize(i);
            Collections.shuffle(allTablesWithAvaialbleSize);
            for (Table table : allTablesWithAvaialbleSize) {
                if(table.getTotalSeats() == size) return table;
            }
        }
        return null;
    }


    private void checkAvailatyOfTablesEqualsTo(List<Table> tables, int availability){
        for (Table table : tables) {
            Assert.assertEquals(table.getAvailableSeats(),availability);
        }
    }

}
