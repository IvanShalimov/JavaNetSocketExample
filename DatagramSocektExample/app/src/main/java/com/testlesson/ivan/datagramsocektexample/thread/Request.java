package com.testlesson.ivan.datagramsocektexample.thread;

import java.net.InetAddress;

/**
 * Created by ivan on 18/09/17.
 */


/**Класс данных, не содержит в себе никакой логики. Просто получает в конструктор
 * необходимые для исполнения данные.*/
public class Request {

    public byte[] getData() {
        return data;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }

    /**Данные в виде массива байтов*/
    private byte[] data;
    /**Порт адресата - куда вы собираете отправить*/
    private int port;
    /**Адрес куда вы собираетесь отправить в необходимом для сокетов формате*/
    InetAddress address;

    public Request(byte[] data, int port, InetAddress address){
        this.data = data;
        this.port = port;
        this.address = address;
    }
}
