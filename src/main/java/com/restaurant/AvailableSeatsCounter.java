package com.restaurant;


import com.restaurant.model.Table;

import java.util.List;

public interface AvailableSeatsCounter {

    Table tryAsignTableToGroupOfSize(int size);
    void releaseSeatsForTable(Table table, int size);
    void bookSeatsForTable(Table table, int size);
    List<Table> getAllTablesWithAvaialbleSize(int size);
}
