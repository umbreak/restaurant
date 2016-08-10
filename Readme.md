#System that manages seating for a Restaurant.  

The restaurant has round tables. Tables come in different sizes that can accommodate 2, 3, 4, 5 or 6 people. People arrive at our restaurant in groups of 6 or less. People in the same group want to be seated at the same table.    
You can seat a group at any table that has enough empty seats for them. If it's not possible to accommodate them, they're willing to wait.                
                                                                               
Once they're seated, they can stay as long as they want and you cannot ask them to move to another table (i.e. you cannot move them to make space for another group). In terms of fairness of seating order: seat groups in the order they arrive, but seat opportunistically. For example: a group of 6 is waiting for a table and there are 4 empty seats at a table for 6; if a group of 2 arrives you may put them at the table for 6 but only if you have nowhere else to put them. This may mean that the group of 6 waits a long time, possibly until they become frustrated and leave.                       


##About performance.
O notation in some places of the code to denote performance.
As a nomenclature:
* N = number of tables
* E = number of customers on the queue

Performance of the most relevant methods:
SeatsManager.enter --> log(N)
SeatManager.leave  --> O(E)
SeatManager.locate --> O(1)

##CustomersQueue implementation
There is an interface (CustomersQueue) because, if the method dequeGroupOfSize wants to be improved, we can provide another implementation.

##AvailableSeatsCounter implementation
There is an interface (AvailableSeatsCounter) because, if another data structure and algorithm wants to be implemented, it can be modified.
 SimpleAvailableSeatsCounter (the implementation) uses an array of TreeMaps. Every element of the treeMap it is a Table. Every position of the array tells us how many free available seats there are.
 For example, the elements on array[0] are all the lists which have 0 available seats.
