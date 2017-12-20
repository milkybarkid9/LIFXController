package lifx.lifx_controller;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static lifx.lifx_controller.MainActivity.auth;
import static lifx.lifx_controller.MainActivity.BASE_URL;

// an object of light, group or scene
public class LightObj {
    private String selector;

    LightObj(String selector){
        String id = "null";

        // verify selector
        if (selector.equals("all")){
            this.selector = selector;
            System.out.println("Selector 'all' found");
        }else {
            HTTPResponse response;
            try {
                response = request("lights/all", "GET", new Pair<>("Authorization", "Bearer " + auth));
                try {
                    JSONArray lights = new JSONArray(response.getReponse());
                    for (int i = 0; i < lights.length(); i++) {
                        JSONObject item = lights.getJSONObject(i);
                        String label = item.getString("label");
                        System.out.println("label=" + label);
                        if (label.equals(selector)) {
                            id = item.getString("id");
                            System.out.println("id=" + id);
                        } else {
                            System.out.println("id not found");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    this.selector = null;
                }
                this.selector = id;
            } catch (IOException e) {
                e.printStackTrace();
                this.selector = null;
            }
        }
    }

    public String getSelector(){
        return selector;
    }

    public int toggle() {
        HTTPResponse response;
        try {
            response = request("lights/"+selector+"/toggle", "POST", new Pair<>("Authorization", "Bearer "+auth)); //TODO: change all to id

            if (response.getCode() == 200){
                System.out.println("LightObj: "+ selector +" toggling");
            }else {
                System.out.println("LightObj: "+ selector +" failed to toggle");
            }

            return response.getCode();

        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }

    public String setState(Integer power, String colour, Double brightness, Double duration) {
        String body = "{}";

        try {
            JSONObject json = new JSONObject();

            // power
            if ( power != null ) {
                if (power == 0)
                    json.put("power", "off");
                if (power == 1)
                    json.put("power", "on");
            }

            // colour
            if ( colour != null )
                json.put("color",colour);

            // brightness
            if ( brightness != null )
                json.put("brightness",brightness);

            // duration
            if ( duration != null )
                json.put("duration",duration);

            body = json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HTTPResponse response;
        try {
            response = request("lights/"+selector+"/state", "PUT", new Pair<>("Authorization", "Bearer "+auth), body); //TODO: change all to id
            JSONObject item = new JSONObject(response.getReponse());
            return item.getString("status");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return "offline";
    }

    private HTTPResponse request(String endpoint, String type, Pair header) throws IOException{
        String url = BASE_URL +endpoint;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(type);
        con.setRequestProperty(header.first.toString(), header.second.toString());

        int responseCode = con.getResponseCode();
        System.out.println("\nSending '"+type+"' request to URL: " + url);
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        return new HTTPResponse(responseCode, response.toString());
    }

    private HTTPResponse request(String endpoint, String type, Pair header, String body) throws IOException{
        String url = BASE_URL +endpoint;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(type);
        con.setRequestProperty(header.first.toString(), header.second.toString());
        con.setRequestProperty("Content-Type","application/json");

        byte[] outputInBytes = body.getBytes("UTF-8");
        OutputStream os = con.getOutputStream();
        os.write( outputInBytes );
        os.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending '"+type+"' request to URL: " + url);
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        return new HTTPResponse(responseCode, response.toString());
    }

}
