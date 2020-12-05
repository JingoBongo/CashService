package mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.*;
import main.MainRunner;

import java.util.Set;

public class MongoHandler {


    public static boolean checkCollectionExists(DB database){
        Set<String> collectionNames = database.getCollectionNames();
        if(collectionNames.contains(MainRunner.dbCollectionName)){
            System.out.println(MainRunner.getCurTime()+"Collection "+MainRunner.dbCollectionName+" exists.");
            return true;
        } else {
            System.out.println(MainRunner.getCurTime()+"Collection "+MainRunner.dbCollectionName+" doesn't exist. Executing fail over");
            //TODO: failover behavior
            return false;
        }
    }

    public static boolean checkIfExistsByName(String name){
        MongoClient mongoClient = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
        DB database = mongoClient.getDB(MainRunner.dbName);
        System.out.println(MainRunner.getCurTime()+"Connected to "+MainRunner.dbName+" database");
        checkCollectionExists(database);
        DBCollection collection = database.getCollection(MainRunner.dbCollectionName);
        System.out.println(MainRunner.getCurTime()+"Connected to "+MainRunner.dbCollectionName+" collection");
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", name);
        DBCursor cursor = collection.find(searchQuery);
        boolean found = false;
        while (cursor.hasNext()) {
            found = true;
            cursor.next();
        }
        System.out.println(MainRunner.getCurTime()+"Employee "+name+" was found:"+found);
        return found;
    }
    public static boolean checkIfExistsById(String id){
        MongoClient mongoClient = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
        DB database = mongoClient.getDB(MainRunner.dbName);
        System.out.println(MainRunner.getCurTime()+"Connected to "+MainRunner.dbName+" database");
        checkCollectionExists(database);
        DBCollection collection = database.getCollection(MainRunner.dbCollectionName);
        System.out.println(MainRunner.getCurTime()+"Connected to "+MainRunner.dbCollectionName+" collection");
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("id", id);
        DBCursor cursor = collection.find(searchQuery);
        boolean found = false;
        while (cursor.hasNext()) {
            found = true;
            cursor.next();
        }
        System.out.println(MainRunner.getCurTime()+"Employee "+id+" was found:"+found);
        return found;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String res1 = MongoHandler.findRoleByName("name");
        String res2 = MongoHandler.findRoleByName("Baka Dono");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode car = objectMapper.readValue(res2, ObjectNode.class);
        System.out.println(car.get("role").asText().replaceAll("\"", ""));

        System.out.println();
//        searchByName("John");
//        boolean b = checkDbIsAvailable(MainRunner.previousPort);
//        System.out.println(b);
//        MongoClient mongoClient = new MongoClient("localhost", 27017);
//        DB database = mongoClient.getDB("first-db");
//        mongoClient.getDatabaseNames().forEach(System.out::println);
//        database.createCollection("customers", null);
//        database.getCollectionNames().forEach(System.out::println);
//
////insert
//        DBCollection collection = database.getCollection("customers");
//        BasicDBObject document = new BasicDBObject();
//        document.put("name", "Shubham");
//        document.put("company", "Baeldung");
//        collection.insert(document);
//
//
////update
//        BasicDBObject query = new BasicDBObject();
//        query.put("name", "Shubham");
//
//        BasicDBObject newDocument = new BasicDBObject();
//        newDocument.put("name", "John");
//
//        BasicDBObject updateObject = new BasicDBObject();
//        updateObject.put("$set", newDocument);
//
//        collection.update(query, updateObject);
//
////read
//        BasicDBObject searchQuery = new BasicDBObject();
//        searchQuery.put("name", "John");
//        DBCursor cursor = collection.find(searchQuery);
//
//        while (cursor.hasNext()) {
//            System.out.println("read"+cursor.next());
//        }
//
////delete
//        BasicDBObject deleteQuery = new BasicDBObject();
//        deleteQuery.put("name", "John");
//
//        collection.remove(deleteQuery);
    }

    public static void shutdown(){
//        db = connect("localhost:27017/admin");
        MongoClient mongo = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
//        mongo.getDatabase("test").
//        db.shutdownServer();
//        quit();

        
    }

    public static boolean checkDbIsAvailable(Integer previousPort) {
        MongoClient mongo = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
        try {
            mongo.getAddress();
            return true;
        } catch (Exception e){
            System.out.println(MainRunner.getCurTime()+"Mongo is down");
            mongo.close();
            return false;
        }
    }

    public static void insertIntoMongo(String id, String name) {
        MongoClient mongoClient = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        DB database = mongoClient.getDB(MainRunner.dbName);
        checkCollectionExists(database);
        DBCollection collection = database.getCollection(MainRunner.dbCollectionName);
        BasicDBObject document = new BasicDBObject();
        document.put("id", id);
        document.put("name", name);
        collection.insert(document);
        //should be synced out of this method

    }

    public static void updateInMongo(String id, String salary, String daysOff) {
        MongoClient mongoClient = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        DB database = mongoClient.getDB(MainRunner.dbName);
        checkCollectionExists(database);
        DBCollection collection = database.getCollection(MainRunner.dbCollectionName);

        BasicDBObject query = new BasicDBObject();
        query.put("id", id);

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("salary", salary);
        newDocument.put("daysOff", daysOff);

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        collection.update(query, updateObject);
        System.out.println(MainRunner.getCurTime()+MainRunner.getRequestsMap().get(id).name+" was updated in mongo with salary and daysOff");
    }


    public static String findRoleByName(String name) {
        MongoClient mongoClient = new MongoClient(MainRunner.dbIp, MainRunner.dbPort);
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        DB database = mongoClient.getDB(MainRunner.dbName);
        checkCollectionExists(database);
        DBCollection collection = database.getCollection(MainRunner.dbCollectionName);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", name);
        DBCursor cursor = collection.find(searchQuery);
        String result = "";
        while (cursor.hasNext()) {
//            System.out.println("read"+cursor.next());
            result += cursor.next();
        }

        return result;
    }
}
