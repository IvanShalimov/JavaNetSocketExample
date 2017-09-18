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
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ivan on 18/09/17.
 */

public class RequestThread extends Thread {

    /**Очередь запросов состоящия из специальных классов.*/
    private Queue<Request> queue = new LinkedList<>();
    /**Флаг для разрыва бесконечного цикла. Грамотно было бы заменить это поле
     * на  isInterrupted*/
    private boolean flag = false;
    /**ip-адрес подается в формате строки(Пример: 192.168.0.12)*/
    String sourceIP;
    /**Необязательный элемент. Используется для возврата ответов из потока*/
    Handler handler;

    public RequestThread(String ip, Handler handler){
        sourceIP = ip;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        //Выставляем флаг работы в труе
        flag = true;

        while (flag){
            try {
                //Если в очереди есть запросы на исполнение
                if (!queue.isEmpty()) {

                    //Создаем экземпляр сокета именно в методе run(), иначе сокет будет
                    // открыт на потоке который владеет экземпляром потока
                    //Обязательно инициализируем null
                    DatagramSocket socket = new DatagramSocket(null);
                    //Выставляем этот флаг
                    socket.setReuseAddress(true);
                    //Цепляем сокет к своему ip-адрессу и нужному нам порту на нашей машине
                    socket.bind(new InetSocketAddress(sourceIP,1025));

                    //Извлекаем запрос из начала очереди
                    Request sendData = queue.remove();

                    //Формируем датаграмму
                    DatagramPacket packetSend = new DatagramPacket(
                            sendData.getData(),
                            sendData.getData().length,
                            sendData.getAddress(),
                            sendData.getPort());

                    //Отправляем датаграмму
                    socket.send(packetSend);

                    //Обязательно закрываем соединение
                    socket.close();

                }
            } catch (SocketTimeoutException e ) {
                e.printStackTrace();
                handleError(e);
            } catch (UnknownHostException e ) {
                e.printStackTrace();
                handleError(e);
            } catch (SocketException e ) {
                e.printStackTrace();
                handleError(e);
            } catch (IOException e ) {
                e.printStackTrace();
                handleError(e);
            }
        }
        // socket.close()
    }

    /**Метод для обработки ошибок, необязательный. В нем мы просто возвращаем владельцу
     * handler полученный нами exception*/
    protected void handleError(Exception exception){
        handler.sendMessage(Message.obtain(handler, 0, exception));
    }

    /**Вставляем запрос в очередь*/
    public void putRequest(Request request){
        queue.add(request);
    }

    /**Метод можно вызвать из вне для остановки работы потока.*/
    public void end(){
        flag = false;
    }
}
