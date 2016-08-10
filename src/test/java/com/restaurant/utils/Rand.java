package com.restaurant.utils;

import java.util.Random;

/**
 * Created by dmontero on 15/05/16.
 */
public class Rand {
    private final static Random random=new Random();

    public static int randomRange(int minimum, int maximum){
       return minimum + random.nextInt((maximum - minimum) + 1);
    }

    public static int random(int maximum){
        return random.nextInt(maximum+1);
    }
}
