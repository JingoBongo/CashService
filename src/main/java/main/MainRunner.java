package main;

import com.sun.net.httpserver.HttpServer;
import httpHandler.CreateMoneyRecordsHandler;
import routines.MongoSwitcherRoutine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainRunner {
    public static ConcurrentHashMap<String, SingleProcessValues> requestsMap = new ConcurrentHashMap<String, SingleProcessValues>();
    public static String servicePort;
    public static String serviceName;
    public static int poolSize;
    public static String gateIp;
    public static String gatePort;
    public static String dbType;
    public static String dbIp = "localhost";
    public static String dbName = "first-db";
    public static String dbCollectionName = "money";
    public static String dbOldCollectionName = "money";
    public static Integer dbPort = 27017;
    public static String serviceHost = "localhost";
    public static String gateUrl;
    public static Integer previousPort = 27017;
    public static Integer newPort;
    public static boolean dbIsSwitched = false;
    private static DateTimeFormatter dtf;
    private static LocalDateTime now;

    public static String getCurTime(){
        now = LocalDateTime.now();
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(now)+": ";
    }

    public static ConcurrentHashMap<String, SingleProcessValues> getRequestsMap() {
        return requestsMap;
    }

    public static void main(String[] args) {
        //update variables, where needed
        poolSize = 10;
        servicePort = "8081";
        serviceName = "createMoneyRecords";
        gatePort = "8003";
        dbType = "mongoDB";
        gateUrl = "http://" + MainRunner.gateIp + ":" + MainRunner.gatePort + "/";
        //TODO uncomment when uniting with db
        //register at gate
//        try {
//            recursivePostRegisterInGate();
//        } catch (UnsupportedEncodingException e) {
//            System.out.println("Registering in gate failed");
        // return; // we failed to register, therefore no sense in starting other things
//            e.printStackTrace();
//        }

        //set up timer and routine
        Timer timer = new Timer();
        timer.schedule(new MongoSwitcherRoutine(), 0, 3*60*1000);  // was 12 hours 12*60*60*1000, make it 3 minutes

        //start server w/ context handler
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(serviceHost, Integer.parseInt(servicePort)), 0);
        } catch (IOException e) {
            System.out.println(getCurTime()+"Failed to start server part: context handler");
            e.printStackTrace();
        }
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        assert server != null;
        server.createContext("/"+ MainRunner.serviceName, new CreateMoneyRecordsHandler());
        server.setExecutor(threadPoolExecutor);
        server.start();
        System.out.println(getCurTime()+" Server "+serviceName+" started on port "+ MainRunner.servicePort);
    }
}
