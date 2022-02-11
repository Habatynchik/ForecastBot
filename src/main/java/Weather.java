import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

public class Weather {

    final private String API_KEY = "9afa599e312051b0ab45fcd866141e75";
    final private String URL = "https://api.openweathermap.org/data/2.5/forecast?";

    String createUrl(String city) {

        StringBuilder stringBuilder = new StringBuilder(URL);
        Map<String, String> map = new HashMap<>();

        map.put("q", city);
        map.put("appid", API_KEY);
        map.put("units", "metric");
        map.put("lang", "ru");
        map.put("cnt", "20");

        map.forEach((k, v) -> stringBuilder.append(k + "=" + v + "&"));

        return stringBuilder.toString();
    }

    String createConnection(String url) throws IOException {
        URL obj = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");

        Scanner fileScanner = new Scanner(connection.getInputStream());

        StringBuilder response = new StringBuilder();

        while (fileScanner.hasNext()) {
            response.append(fileScanner.nextLine());
        }

        fileScanner.close();
        return response.toString();
    }


    void writeFile(String response) {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/data.json")) {
            fileWriter.write(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String parseJSON() throws IOException, ParseException {

        List<Forecast> list = new ArrayList<>();

        FileReader fileReader = new FileReader("src/main/resources/data.json");
        Scanner scanner = new Scanner(fileReader);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine()).append("\n");
        }
        fileReader.close();
        scanner.close();

        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(stringBuilder.toString());
        JSONArray weatherArray = (JSONArray) jsonObject.get("list");

        for (int i = 0; i < 20; i++) {
            JSONObject weatherData = (JSONObject) weatherArray.get(i);
            JSONArray weatherList = (JSONArray) weatherData.get("weather");
            JSONObject weather = (JSONObject) weatherList.get(0);
            String description = weather.get("description").toString();
            String icon = weather.get("icon").toString();

            JSONObject main = (JSONObject) weatherData.get("main");

            String temp = main.get("temp").toString();
            String date = weatherData.get("dt_txt").toString();

            list.add(new Forecast(date, temp, description));
        }

        return parserMessage(list);
    }

    String parserMessage(List<Forecast> list) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd ");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        String todayDate = today.format(formatter);
        String tomorrowDate = tomorrow.format(formatter);

        StringBuilder todayList = new StringBuilder("Today forecast\n");
        StringBuilder tomorrowList = new StringBuilder("\nTomorrow forecast\n");

        for (Forecast forecat : list) {
            if (forecat.getDate().contains(todayDate)) {
                todayList.append("\nTime: ").append(forecat.getDate().replaceAll(todayDate, ""));
                todayList.append("\nTemp: ").append(forecat.getTemp()).append("°C");
                todayList.append("\nDescription: ").append(forecat.getDescription()).append("\n");
            } else if (forecat.getDate().contains(tomorrowDate)){
                tomorrowList.append("\nTime: ").append(forecat.getDate().replaceAll(tomorrowDate, ""));
                tomorrowList.append("\nTemp: ").append(forecat.getTemp()).append("°C");
                tomorrowList.append("\nDescription: ").append(forecat.getDescription()).append("\n");
            }
        }



        return todayList.append(tomorrowList).toString();
    }
}

