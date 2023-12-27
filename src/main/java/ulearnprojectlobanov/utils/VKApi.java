package ulearnprojectlobanov.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ulearnprojectlobanov.entities.StudentVKProfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static ulearnprojectlobanov.database.Connector.GetProperties;


public class VKApi {
    public final HashMap<String, StudentVKProfile> infoMap = new HashMap<>();

    public VKApi() throws URISyntaxException, IOException {
        Parse();
        System.out.println("Данные из Вконтакте получены");
    }

    public void Parse() throws URISyntaxException, IOException {
        String resp_data = getData();
        JsonObject json = new Gson().fromJson(resp_data, JsonObject.class);
        JsonObject response = json.getAsJsonObject("response");
        JsonArray items = response.getAsJsonArray("items");

        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();

            StudentVKProfile info = new StudentVKProfile();

            String name = item.get("first_name").getAsString() + " " + item.get("last_name").getAsString();

            if (item.get("bdate") != null)
                info.setBirthDate(item.get("bdate").getAsString());

            if (item.getAsJsonObject("city") != null)
                info.setCity(item.getAsJsonObject("city").get("title").getAsString());

            if (item.getAsJsonObject("country") != null)
                info.setCountry(item.getAsJsonObject("country").get("title").getAsString());

            if (item.get("sex") != null)
                info.setGender(item.get("sex").getAsInt() == 1 ? "Female" : "Male");

            if (item.get("university_name") != null)
                info.setUniversity_name(item.get("university_name").getAsString());

            infoMap.put(name, info);
        }
    }

    private String getData() throws IOException, URISyntaxException {
        Properties properties = GetProperties();
        String urlRaw = String.format(
                "https://api.vk.com/method/groups.getMembers?group_id=%s&fields=%s&access_token=%s&v=5.131",
                properties.getProperty("GROUP_ID"),
                properties.getProperty("FIELDS"),
                properties.getProperty("VK_TOKEN")
        );
        Scanner scanner = new Scanner((InputStream) new URI(urlRaw).toURL().getContent());
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()) builder.append(scanner.nextLine());
        return builder.toString();
    }
}
