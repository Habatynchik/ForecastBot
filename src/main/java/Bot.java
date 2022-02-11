import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {
    final private String NAME = "RobocodeJava_bot";
    final private String API_TOKEN = "5027234989:AAGDq8iLCmmLh-RrHlN7af4Ib-qYxUR2cYE";
    final private String HELLO = "Привет, это WeatherBot \n с помошью команды /get {Город} ты сможешь узнать прогноз погоды";

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        String text = msg.getText();
        Weather weather = new Weather();

        if (text.equals("/start")) {
            sendMessage(msg, HELLO);
        } else if (text.contains("/get")) {
            String city = text.replaceAll("/get", "").trim();

            try {
                weather.writeFile(weather.createConnection(weather.createUrl(city)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sendMessage(msg, "Прогноз погоды");
                sendMessage(msg, weather.parseJSON());

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message msg, String str) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());
        sendMessage.setText(str);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
