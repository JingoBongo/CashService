package HttpClient;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.MainRunner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class for making http requests
 */
public class HttpUtility {
    //  setting HttpClient that will make requests
    private final HttpClient client = HttpClient.newHttpClient();

    public static void syncCreation(String id, String name) {
        //wrapper for broadcast to same service name in a new thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("task", "syncName");
                jsonObject.put("id", id);
                jsonObject.put("name", name);

                String payload = jsonObject.toString();
                try {
                    //TODO
                    //uncomment when uniting with gate
//                    new HttpUtility().sendServiceCallToServicesWithCommand(MainRunner.gateUrl, payload,MainRunner.serviceName);
                } finally {
                }
                //catch (IOException e) {
//                    System.out.println(MainRunner.getCurTime()+" failed to request broadcast sync for "+id+", "+name);
//                    e.printStackTrace();
//                }


            }
        });
        thread.start();
    }

    public static void syncUpdate(String id, String role, String location) {
        //wrapper for broadcast to same service name in a new thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("task", "syncInfo");
//                jsonObject.put("id", id);
//                jsonObject.put("salary", salary);
//                jsonObject.put("daysOff", daysOff);
                String payload = jsonObject.toString();
                try {
                    //TODO
                    //uncomment when will work with gate
//                    new HttpUtility().sendServiceCallToServicesWithCommand(MainRunner.gateUrl, payload,MainRunner.serviceName);
                } finally {
                } // remove finally and bring back the catch
                // catch (IOException e) {
//                    System.out.println(MainRunner.getCurTime()+" failed to request broadcast sync for "+id+" fields");
//                    e.printStackTrace();
//                }


            }
        });
        thread.start();
    }

    public static boolean checkIfEmployeesServiceHasSuchName(String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("task", "getEmployeeRole");
        jsonObject.put("name", name);
        String payload = jsonObject.toString();
        String response = "";
        try {
            response = new HttpUtility().sendServiceCallToServicesWithCommand(MainRunner.gateUrl, payload, "createEmployee");
        } catch (IOException e) {
            System.out.println(MainRunner.getCurTime() + " failed to request presence of " + name);
            e.printStackTrace();
        }
        if (response != null && !response.equals("")) {
            ObjectMapper objectMapperForGetEmpl = new ObjectMapper();
            ObjectNode car = null;
            try {
                car = objectMapperForGetEmpl.readValue(response, ObjectNode.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (car.get("status").asText().equals("ok") || car.get("reason").asText().contains("has no role")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String checkIfEmployeesServiceHasNameWithRole(String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("task", "getEmployeeRole");
        jsonObject.put("name", name);
        String payload = jsonObject.toString();
        String response = "";
        try {
            response = new HttpUtility().sendServiceCallToServicesWithCommand(MainRunner.gateUrl, payload, "createEmployee");
        } catch (IOException e) {
            System.out.println(MainRunner.getCurTime() + " failed to request presence of " + name);
            e.printStackTrace();
        }
        if (response != null && !response.equals("")) {
            ObjectMapper objectMapperForGetEmpl = new ObjectMapper();
            ObjectNode car = null;
            try {
                car = objectMapperForGetEmpl.readValue(response, ObjectNode.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (car.get("status").asText().equals("ok")) {
                return car.get("role").asText();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * send simple GET request without header specifications and additional info
     *
     * @param destinationPage page for requesting data from
     * @return data obtained as result of GET request to the page
     * @throws IOException          error in I/O streams of application
     * @throws InterruptedException error in threading
     */
    public String sendGet(String destinationPage) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(destinationPage)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    /**
     * send request to establish connection with gateway
     *
     * @param destinationPage address of request
     * @param jsonRequest     json request
     * @throws IOException i/o error
     */
    public void sendHandshakeJsonPost(String destinationPage, String jsonRequest) throws IOException {

        //  append json content type property
        StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);

        //  initialize client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //  make POST request
        HttpPost request = new HttpPost(destinationPage);

        //  append headers to request
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Service-Call", "true");
        request.setEntity(entity);

        //  wait for response and handle it as String
        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textResponse = handler.handleResponse(response);
        System.out.println(MainRunner.getCurTime() + textResponse);
    }

    /**
     * send broadcast to all services available for gateway
     *
     * @param destinationPage where to send
     * @param jsonRequest     what to send
     * @throws IOException
     */
    public void sendServiceCall(String destinationPage, String jsonRequest) throws IOException {
        //  append json content type property
        StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);

        //  initialize client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //  make POST request
        HttpPost request = new HttpPost(destinationPage);

        //  append headers to request
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Service-Call", "broadcast:all");
        request.setEntity(entity);

        //  wait for response and handle it as String
        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textResponse = handler.handleResponse(response);
        System.out.println(MainRunner.getCurTime() + textResponse);
    }

    /**
     * send broadcast to services that are attached to the required command
     *
     * @param destinationPage where to send
     * @param jsonRequest     what to send
     * @param commandRequired command services of which are required
     * @return
     * @throws IOException
     */
    public String sendServiceCallToServicesWithCommand(String destinationPage, String jsonRequest, String commandRequired) throws IOException {
        //  append json content type property
        StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);

        //  initialize client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //  make POST request
        HttpPost request = new HttpPost(destinationPage);

        //  append headers to request
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Service-Call", "broadcast:" + commandRequired);
        request.setEntity(entity);

        //  wait for response and handle it as String
        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textResponse = handler.handleResponse(response);
        //TODO implement action for resp
        System.out.println(MainRunner.getCurTime() + textResponse);
        return textResponse;
    }

    /**
     * send json POST request that will initialize process on service
     *
     * @param destinationPage where to send request
     * @param functionName    json identifier of method
     * @param firstArgument   value appended to identifier
     * @throws IOException i/o error
     */
    public String sendJsonPost(String destinationPage, String functionName, String firstArgument) throws IOException {
        //  generate payload for request based on parameters
        String payload = "{\"functionName\":\"" + functionName + "\",\"amount\":" + firstArgument + "}";

        //  append json content type property
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        // initialize client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //  make PUT request
        HttpPost request = new HttpPost(destinationPage);

        //  append headers to request
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(entity);

        //  wait for response and handle it as String
        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textResponse = handler.handleResponse(response);
        System.out.println(MainRunner.getCurTime() + textResponse);


        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.readValue(textResponse, ObjectNode.class);

        return node.get("id").asText();
    }

    /**
     * send json PUT request that will continue work with process
     *
     * @param destinationPage where is directed request
     * @param id              id of request to continue
     * @param nameOfArgument  key of argument
     * @param argumentValue   value of argument
     * @throws IOException i/o exception
     */
    public String sendJsonPut(String destinationPage, String id, String nameOfArgument, String argumentValue) throws IOException {
        //  generate payload for request based on parameters
        String payload = "{\"id\":" + id + ",\"" + nameOfArgument + "\":\"" + argumentValue + "\"}";

        //  append content type propety to payload
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        //  initialize client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //  generate PUT request
        HttpPut request = new HttpPut(destinationPage);

        //  append headers to request
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(entity);

        //  wait for response and handle it as String
        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textResponse = handler.handleResponse(response);
        System.out.println(MainRunner.getCurTime() + textResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.readValue(textResponse, ObjectNode.class);

        return node.get("id").asText();
    }

    /**
     * send GET request that will finish work of process and get result
     *
     * @param destinationPageWithId where is directed request and ID of process to close and get
     * @throws IOException i/o error
     */
    public void sendJsonGet(String destinationPageWithId) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(destinationPageWithId);

        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        System.out.println(MainRunner.getCurTime() + handler.handleResponse(response));
    }

    //my old'e good'y methods
    public static void recursivePostRegisterInGate() throws UnsupportedEncodingException {
        try {
            String serviceUrl = "http://" + MainRunner.serviceHost + ":" + MainRunner.servicePort + "/" + MainRunner.serviceName;
            String gateUrl = "http://" + MainRunner.gateIp + ":" + MainRunner.gatePort + "/";


            org.apache.http.client.HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(gateUrl);

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("functionName", MainRunner.serviceName);
            jsonRequest.put("address", serviceUrl);

            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("Service-Call", "true");
            StringEntity entity = new StringEntity(jsonRequest.toString(), ContentType.APPLICATION_JSON);
            httppost.setEntity(entity);

//Execute and get the response.
            org.apache.http.HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != (HttpStatus.SC_OK)) {
                recursivePostRegisterInGate();
            } else {
                System.out.println(MainRunner.getCurTime() + "Registered at gateway.");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}