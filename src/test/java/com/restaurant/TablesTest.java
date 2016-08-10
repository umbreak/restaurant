package com.restaurant;

import com.restaurant.model.Table;
import com.restaurant.utils.TableListGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by dmontero on 08/05/16.
 */
public class TablesTest {

    private ArrayList<Table> tables;

    private final static int amountOf6Tables=10;
    private final static int amountOf5Tables=10;
    private final static int amountOf4Tables=10;
    private final static int amountOf3Tables=10;
    private final static int amountOf2Tables=10;


    @Before
    public void init(){
        this.tables=new TableListGenerator.Builder()
                .tableOfSize(2,amountOf2Tables).tableOfSize(3,amountOf3Tables).tableOfSize(4,amountOf4Tables).tableOfSize(5,amountOf5Tables).tableOfSize(6,amountOf6Tables).build();
    }
    @Test
    public void testTablesGenerator() {
        Assert.assertEquals(this.tables.size(),amountOf6Tables+amountOf5Tables+amountOf4Tables+amountOf3Tables+amountOf2Tables);
        Map<Integer,Integer> tablesWithSize=new HashMap<>();
        for (Table table : tables) {
            Assert.assertEquals(table.getAvailableSeats(),table.getTotalSeats());
            Integer amount = tablesWithSize.get(table.getAvailableSeats());
            amount = (amount == null) ? 1 : amount+1;
            tablesWithSize.put(table.getTotalSeats(),amount);
        }
        Assert.assertTrue(tablesWithSize.get(2).equals(amountOf2Tables));
        Assert.assertTrue(tablesWithSize.get(3).equals(amountOf3Tables));
        Assert.assertTrue(tablesWithSize.get(4).equals(amountOf4Tables));
        Assert.assertTrue(tablesWithSize.get(5).equals(amountOf5Tables));
        Assert.assertTrue(tablesWithSize.get(6).equals(amountOf6Tables));
    }

    @Test
    public void ascendingSortList(){
        Collections.shuffle(tables);
        Collections.sort(tables);
        int minTableAvailableSizeBeginning=2;
        int previousTableAvailableSize=minTableAvailableSizeBeginning;
        for (Table table : tables) {
            Assert.assertTrue(table.getAvailableSeats() >= previousTableAvailableSize);
            previousTableAvailableSize=table.getAvailableSeats();
        }
    }
}
