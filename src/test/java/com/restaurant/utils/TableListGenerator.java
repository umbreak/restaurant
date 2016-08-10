package com.restaurant.utils;

import com.restaurant.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmontero on 15/05/16.
 */
public class TableListGenerator {

    public static class Builder {
        private final Map<Integer,Integer> amountOfTables;

        public Builder() {
            amountOfTables = new HashMap<>();
        }

        public Builder tableOfSize(int table, int amount){
            amountOfTables.put(table,amount);
            return this;
        }
        
        public  ArrayList<Table> build(){
            ArrayList<Table> tables=new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : amountOfTables.entrySet()) {
                Integer amount = entry.getValue();
                Integer tableSize = entry.getKey();
                for (int i = 0; i < amount; i++) {
                    Table table=new Table(tableSize);
                    tables.add(table);
                }
            }
            return tables;
        }

    }

}
