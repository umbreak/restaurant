package com.restaurant;


import com.restaurant.model.SeatManagerException;
import com.restaurant.model.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

//Size  16 + 4 + 4 (padding) +32 + N * 40 * NUM_TABLES ~ O(NUM_TABLES)
public class SimpleAvailableSeatsCounter implements AvailableSeatsCounter{
    //32 + N * 40 * NUM_TABLES
    //N = size of array. In this implementation is 6 -->constant
    private final TreeMap<String,Table> availableSeats[];
    //4
    private final int maxTableSize;

    public SimpleAvailableSeatsCounter(List<Table> tables, int maxTableSize) {
        this.maxTableSize=maxTableSize;
        this.availableSeats=initTablesTree(tables,maxTableSize);
    }

    private TreeMap<String,Table>[] initTablesTree(List<Table> tables, int maxTableSize){
        TreeMap<String,Table>[] availableSeats=initializeAllTrees(maxTableSize);
        for (Table table : tables) {
            availableSeats[table.getAvailableSeats()].put(table.getId(),table);
        }
        return availableSeats;
    }

    private TreeMap<String,Table>[] initializeAllTrees(int maxTableSize){
        TreeMap<String,Table>[] availableSeats=new TreeMap[maxTableSize+1];
        for (int i = 0; i <= maxTableSize; i++) {
            availableSeats[i] = new TreeMap<String,Table>();
        }
        return availableSeats;
    }

    @Override
    //3 log(N) + O(availableSeats.length) ~ log(N)
    public Table tryAsignTableToGroupOfSize(int size) {
        throwExceptionIfOutOfBoundariesCounting0(size);

        Integer positionInArray=firstPossiblePositionForAvailableSeats(size);
        if(positionInArray == null) return null;

        //log(N)
        Table table=availableSeats[positionInArray].firstEntry().getValue();
        bookSeatsForTable(table,size);
        return table;
    }

    //O(availableSeats.length) --> constant time
    private Integer firstPossiblePositionForAvailableSeats(int size){
        for (int i = size; i < availableSeats.length; i++) {
            if(!availableSeats[i].isEmpty()) return i;
        }
        return null;
    }

    //Same performance as moveTableToAvailableSeatsArray
    @Override
    public void bookSeatsForTable(Table table, int size) {
        throwExceptionIfOutOfBoundariesCounting0(size);
        int availableSeatsBeforeRemoval = table.getAvailableSeats();
        table.removeAvailableSeats(size);
        int availableSeatsAfterRemoval=table.getAvailableSeats();
        moveTableToAvailableSeatsArray(
                table,
                availableSeatsBeforeRemoval,
                availableSeatsAfterRemoval);
    }

    @Override
    public List<Table> getAllTablesWithAvaialbleSize(int size) {
        throwExceptionIfOutOfBoundaries(size);
        return new ArrayList<>(availableSeats[size].values());
    }

    //Same performance as moveTableToAvailableSeatsArray
    @Override
    public void releaseSeatsForTable(Table table, int size) {
        throwExceptionIfOutOfBoundariesCounting0(size);
        int availableSeatsBeforeAddition = table.getAvailableSeats();
        table.addAvailableSeats(size);
        int availableSeatsAfterAddition=table.getAvailableSeats();
        moveTableToAvailableSeatsArray(
                table,
                availableSeatsBeforeAddition,
                availableSeatsAfterAddition);
    }

    //2 log(N) ~ log(N)
    private void moveTableToAvailableSeatsArray(Table table,int oldArrayIndex, int newArrayIndex){
        throwExceptionIfOutOfBoundaries(oldArrayIndex);
        throwExceptionIfOutOfBoundaries(newArrayIndex);
        String key=table.getId();
        //log(N)
        availableSeats[oldArrayIndex].remove(key);
        //log(N)
        availableSeats[newArrayIndex].put(key, table);

    }

    private void throwExceptionIfOutOfBoundaries(int size){
        if(size < 0 || size > maxTableSize)
            throw new SeatManagerException("Size ("+size+") of the group is out of the expected boundaries" );
    }

    private void throwExceptionIfOutOfBoundariesCounting0(int size){
        if(size < 1 || size > maxTableSize)
            throw new SeatManagerException("Size ("+size+") of the group is out of the expected boundaries" );
    }



}
