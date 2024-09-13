package com.github.user.activity;

import com.google.gson.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.net.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

            Scanner input = new Scanner(System.in);
            String user;
            System.out.println("Inserting my GitHub username:");

            while (!(user = input.nextLine().trim()).equalsIgnoreCase("exit")) {

                getUserActivity(user);
            }

    }

    private static void getUserActivity(String user) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/users/"+user+"/events"))
                    .header("Authorization", "Bearer INSERT MY TOKEN")
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                getInfoFromResponse(response.body());

            } else {
                System.out.println("Error:" + response.statusCode());
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void getInfoFromResponse(String body) {

        JsonArray events = JsonParser.parseString(body).getAsJsonArray();

        for (JsonElement element: events) {

            String eventType = element.getAsJsonObject().get("type").getAsString();
            String msg = switch (eventType) {
                case "CreateEvent" ->
                        "CreateEvent ID: " + element.getAsJsonObject().get("id") + ", in repo: " + element.getAsJsonObject().get("repo").getAsJsonObject().get("name").getAsString() + ", created at: " + element.getAsJsonObject().get("created_at").getAsString();
                case "PushEvent" ->
                        "PushEvent ID: " + element.getAsJsonObject().get("id") + ", in repo: " + element.getAsJsonObject().get("repo").getAsJsonObject().get("name").getAsString() + ", with " + element.getAsJsonObject().get("payload").getAsJsonObject().get("commits").getAsJsonArray().size() + " commit(s)" + ", created at: " + element.getAsJsonObject().get("created_at").getAsString();
                default -> "Other Event: " + eventType.replace("Event", "");
            };

            System.out.println(msg);
        }



    }
}