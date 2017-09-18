package com.testlesson.ivan.datagramsocektexample.thread;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by ivan on 18/09/17.
 */

public class ReceiveThread extends Thread {

    public ReceiveThread(String stringIP,
                         Handler handler){
        this. stringIP = stringIP;
        this.handler = handler;

    }

    /**Флаг для разрыва бесконечного цикла. Грамотно было бы заменить это поле
     * на  isInterrupted*/
    boolean lockFlag = false;
    DatagramSocket socket;
    /**ip-адрес подается в формате строки(Пример: 192.168.0.12)*/
    String stringIP;
    /**Необязательный элемент. Используется для возврата ответов из потока*/
    Handler handler;

    @Override
    public void run() {
        super.run();

        try {
            //Настраиваем сокет подобно тому что было в "отсылающем" потоке
            //Но добавляем таймаут в 5 секунд
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(stringIP, 1025));
            socket.setSoTimeout(5000);
            //Выставляем флаг работы потока
            lockFlag = true;

            DatagramPacket receivePacket;

            while (lockFlag) {
                try {

                    //Настраиваем и вызываем метод принятия данных
                    byte[] buffer = new byte[2048];
                    receivePacket = new DatagramPacket(buffer, buffer.length);
                    //принимаем данные
                    socket.receive(receivePacket);

                    //Если что-то есть, нам сообщат
                    if (receivePacket.getData()[0] != -1) {
                        handler.sendMessage(
                                Message.obtain(
                                        handler,
                                        0,
                                        new Container(
                                                receivePacket.getData(),
                                                receivePacket.getLength()
                                                )));

                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    handleError(e);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    handleError(e);
                } catch (SocketException e) {
                    e.printStackTrace();
                    handleError(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    handleError(e);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            handleError(e);
        }
    }

    /**Метод для обработки ошибок, необязательный. В нем мы просто возвращаем владельцу
     * handler полученный нами exception*/
    protected void handleError(Exception exception){
        handler.sendMessage(Message.obtain(handler, 0,
                exception));
    }

    /**Метод можно вызвать из вне для остановки работы потока.*/
    public void end() {
        lockFlag = false;
        socket.close();
    }
}
