import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

//        https://www.metaweather.com/api/
//        https://aqicn.org/api/

            String location;

            do {

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter location name:");

            location= scanner.nextLine().replace(" ", "%20")+"";

            if (getWoeid(location)==0 || location.length()<3)System.out.println("Couldn't find " + location+".");

            }while (getWoeid(location)==0 || location.length()<3) ;

            System.out.println("woeid: " + getWoeid(location));

            getWeather(getWoeid(location));

            System.out.println("air quality index: " + getAqi(location));

            getAirPollutionLevel(getAqi(location));

    }

    private static int getWoeid(String location) throws IOException {

        int woeid=0;

        URL url = new URL("https://www.metaweather.com/api/location/search/?query=" + location);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        conn.connect();

        //Getting the response code
        int responsecode1 = conn.getResponseCode();

        if (responsecode1 != 200) {
            throw new RuntimeException("HttpResponseCode1: " + responsecode1);
        } else {

            String inline = "";
            Scanner scanner2 = new Scanner(url.openStream());

            while (scanner2.hasNext()) {
                inline += scanner2.nextLine();
            }

            scanner2.close();

            org.json.JSONArray jsonArray = new org.json.JSONArray(inline);

            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                woeid = jsonObject1.optInt("woeid");
                break;
            }
        }
        return woeid;
    }

    private static void getWeather(int woeid){

        try {

            URL url2 = new URL("https://www.metaweather.com/api/location/"+woeid);

            HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();

            conn2.setRequestMethod("GET");

            conn2.connect();

            int responsecode2 = conn2.getResponseCode();

            if (responsecode2 != 200) {
                throw new RuntimeException("HttpResponseCode2: " + responsecode2);
            } else {

                String inline = "";
                Scanner scanner3 = new Scanner(url2.openStream());

                while (scanner3.hasNext()) {
                    inline += scanner3.nextLine();
                }

                scanner3.close();

                JSONParser parse = new JSONParser();

                JSONObject data_obj = (JSONObject) parse.parse(inline);

                JSONArray arr = (JSONArray) data_obj.get("consolidated_weather");

                LocalDate localDate = LocalDate.now();

                for (int i = 0; i < arr.size(); i++) {

                    JSONObject new_obj = (JSONObject) arr.get(i);

                    if (new_obj.get("applicable_date").equals(String.valueOf(localDate))) {
                        System.out.println("applicable date: " + localDate);
                        System.out.println("min temp: " + new_obj.get("min_temp"));
                        System.out.println("max temp: " + new_obj.get("max_temp"));
                        System.out.println("the temp: " + new_obj.get("the_temp"));
                        System.out.println("weather state: " + new_obj.get("weather_state_name"));
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getAqi(String location) throws IOException, ParseException {

             URL url3 = new URL("https://api.waqi.info/search/?keyword="+ location + "&token=9dc280c5e166755f12474a8b8310956468c64594");

            HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
            conn3.setRequestMethod("GET");
            conn3.connect();

            int responsecode = conn3.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url3.openStream());

                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                scanner.close();

                JSONParser parse = new JSONParser();

                JSONObject data_obj = (JSONObject) parse.parse(inline);

                JSONArray arr = (JSONArray) data_obj.get("data");

                JSONObject new_obj = (JSONObject) arr.get(0);

                String aqi = new_obj.get("aqi").toString();

                return Integer.valueOf(aqi);

            }
    }

    private static void getAirPollutionLevel(int aqi){

        System.out.print("air pollution level: ");
        if (aqi<=50){
            System.out.print("Good");
        }
        if (aqi>=51 && aqi<=100){
            System.out.print("Moderate");
        }
        if (aqi>=101 && aqi<=150){
            System.out.print("Unhealthy for Sensitive Groups");
        }
        if (aqi>=151){
            System.out.print("Unhealthy");
        }
    }
}