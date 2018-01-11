package com.test.actiance.teams.response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.*;
public class TestExecutor {
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        Body body = new Body();
        RootMessage rmsg = new RootMessage();
        body.setContentType(2);
        body.setContent("hello brother");
        rmsg.setBody(body);
        //msg.setRoomId("Y2lzY29zcGFyazovL3VzL1JPT00vODUwN2EyMzAtMzVkZS0xMWU1LTg0NGYtNzM3M2U5MzhkMDU1");
        Gson gson = new Gson();
        String json = gson.toJson(rmsg);
        
        System.out.println(json);
       String urlString = "https://graph.microsoft.com/beta/groups/2408354a-4388-420d-92d5-11197e9af310/channels/8afcbe75-9ca3-43a7-ad1c-68823c10681f/chatThreads";
       executePost(urlString,json);
        
    }
    public static void executePost(String urlString,String json)
    {
        
        try {
//String urlString = "https://api.ciscospark.com/v1/messages";
System.out.println(urlString);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Authorization","eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkhIQnlLVS0wRHFBcU1aaDZaRlBkMlZXYU90ZyIsImtpZCI6IkhIQnlLVS0wRHFBcU1aaDZaRlBkMlZXYU90ZyJ9.eyJhdWQiOiJodHRwczovL2dyYXBoLm1pY3Jvc29mdC5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9lYjgzNGE4Ni0zZjE1LTQ4YmQtODgzNi01MjJkNWU4YmU3MTcvIiwiaWF0IjoxNTA3NjM3MjkwLCJuYmYiOjE1MDc2MzcyOTAsImV4cCI6MTUwNzY0MTE5MCwiYWlvIjoiWTJWZ1lPQTQxRGhOVFBCRTZCbmpOejhuS3A0OUJ3QT0iLCJhcHBfZGlzcGxheW5hbWUiOiJBdXRvbWF0aW9uX0FwcCIsImFwcGlkIjoiMTcyNTcyNWQtZmVkMi00MWQ1LTg2NDItNTEzYzNlY2FhODFlIiwiYXBwaWRhY3IiOiIxIiwiaWRwIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvZWI4MzRhODYtM2YxNS00OGJkLTg4MzYtNTIyZDVlOGJlNzE3LyIsIm9pZCI6ImNjM2Q1ZmQzLTVkZDctNDUzMy04ODJiLTMwM2I1YzQyYzEyYSIsInJvbGVzIjpbIlBlb3BsZS5SZWFkLkFsbCIsIkdyb3VwLlJlYWQuQWxsIiwiU2l0ZXMuUmVhZC5BbGwiLCJEaXJlY3RvcnkuUmVhZC5BbGwiLCJVc2VyLlJlYWQuQWxsIiwiRmlsZXMuUmVhZC5BbGwiLCJNYWlsLlJlYWQiLCJNZW1iZXIuUmVhZC5IaWRkZW4iXSwic3ViIjoiY2MzZDVmZDMtNWRkNy00NTMzLTg4MmItMzAzYjVjNDJjMTJhIiwidGlkIjoiZWI4MzRhODYtM2YxNS00OGJkLTg4MzYtNTIyZDVlOGJlNzE3IiwidXRpIjoiRk5yLXUtMXFIRWFKY0o1QXpSRUhBQSIsInZlciI6IjEuMCJ9.wwb97LCch99TJIUoYi0C9z0tBsrek2Y7P1qyeZAKuIR1rtRmHP5f8-cxR8-ARBGqIB5oUHUOjHReNbIVg_G3kg94h24GA5RAkz6xmgcNwa_wp64bGQcVKgRscyW4wolo13IkCdNW9bvQIxAXUHPgaULNpbu3DVl9AboY9rq5vgCZ6Xx0OogHj2T_9Ul8H82U5I_Xj4cTvtME4Xo21MJQkrdGjlXIsHUQ-b9X-HHxXQr8XRus1195kv00zuqPLyvpF18ETpiaRQM_AFWrG7p3dpmKGckAD-h1GNI1offJ5lnn_j-KG8Bwzr4_FWow-eOCtjmijEqCbsO3WRTJHZUvUA");
            String input = json;
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                    + conn.getResponseCode());
//            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
          } catch (MalformedURLException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
         }
    }
}
 
