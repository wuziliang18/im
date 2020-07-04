package com.wuzl.im.server;

/**
 * 服务启动类
 * 
 * @author ziliang.wu
 *
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(10010);
        server.start();
    }
}
