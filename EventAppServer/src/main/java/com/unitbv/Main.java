package com.unitbv;

import com.unitbv.utilitypackage.CustomerUtilitiesServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        ExecutorService exSrv = Executors.newCachedThreadPool();

        try {
            CustomerUtilitiesServer server = new CustomerUtilitiesServer(9001);
            exSrv.submit(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
