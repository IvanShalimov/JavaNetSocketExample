package com.testlesson.ivan.datagramsocektexample.thread;

/**
 * Created by ivan on 18/09/17.
 Просто объек с данными, вовсе не обязательн оего использовать. Но для обработки ответа
 в нем есть необходимый минимум.
 */
public class Container {

    private byte[] data;
    private int size;

    public Container(byte[] array, int num){
        this.data = array;
        this.size = num;
    }
}
