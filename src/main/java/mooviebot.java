import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class mooviebot extends TelegramLongPollingBot {


    private static ArrayList<String> new_country;
    private final String botUserName = "VkKusvowBot";
    private final String botToken = "1439301783:AAHuMngYATbvrc4OywKv7nMtRQj2n5kmViA";



    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {   // как бот будет реагировать на новое сообщение

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                try {
                    execute(sendMessage(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else if (update.hasCallbackQuery()) {

            String calls = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (calls.equals("yep")){

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setMessageId((int) messageId);
                editMessageText.setText("continue");

                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (calls.equals("nope")){
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setMessageId((int) messageId);
                editMessageText.setText("watch");

                try {
                    execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    public SendMessage sendMessage(Update update) {


        int stage250 = -1;
        if (update.getMessage().getText().equals("/start")) {
            return start(update.getMessage().getChatId());
        } else if(update.getMessage().getText().equals("Поиск"))
        {
            SendMessage send=new SendMessage();
            send.setChatId(String.valueOf(update.getMessage().getChatId()));
            send.setText("Просто отправьте боту сообщение с названием интересующего фильма!");
            return send;
        } else if(update.getMessage().getText().equals("Список фильмов"))
        {
            return ListFilm(update.getMessage().getChatId());
        }else
        {

        }

        /* ВЛОЖЕННЫЕ КЛАВИАТУРЫ
        Пока я нашёл только один вариант - делать не вложенные if .
        Т.к он просто туда не заходит из-за того, что передаваемое сообщение не изменяется внутри функции getMessage()
         */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("К сожалению нам не удалось распознать вашу команду");
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        return sendMessage;
    }

    private InlineKeyboardMarkup setInline(String msg) //функция для прикрепления мини-опросика к фильму(да/нет/следующий)
    {
        if (msg.equals("Top-film")) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();  //создаем переменные, куда мы будем записывать наши кнопки к сообщению
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            inlineKeyboardButton.setText("Да");
            inlineKeyboardButton.setCallbackData("Да");
            inlineKeyboardButton1.setText("Нет, еще что-нибудь");
            inlineKeyboardButton1.setCallbackData("Нет, еще что-нибудь");
            ;
            keyboardButtonsRow1.add(inlineKeyboardButton);
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            rowList.add(keyboardButtonsRow1);
            inlineKeyboardMarkup.setKeyboard(rowList);
            return inlineKeyboardMarkup;
        }
        return null;
    }

    private SendMessage start(long chatId) //основная менюшка для бота(под комманду /start)
    {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true); //показ клав только определенным пользователям
        replyKeyboardMarkup.setOneTimeKeyboard(false);//скрыть клавиатуру после использования
        replyKeyboardMarkup.setResizeKeyboard(true); //высота клавиатуры


        keyboardRow1.clear();
        keyboardRow2.clear();
        keyboard.clear();
        keyboardRow1.add("Поиск");
        keyboardRow1.add("Список фильмов");
        keyboardRow2.add("По жанрам");
        keyboardRow2.add("По интересам");
        keyboardRow3.add("Инструкция");
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);
        keyboard.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboard);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setText("Добро пожаловать в наш фильмобот! Выберите, что вы хотите сделать из списка ниже");
        return sendMessage;


    }



    private SendMessage Zhanr(long chatId)
    {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();  //создаем переменные, куда мы будем записывать наши кнопки к сообщению
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();

        InlineKeyboardButton inlineKeyboardButton44 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton55 = new InlineKeyboardButton();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();

        List<InlineKeyboardButton> keyboardButtonsRow55 = new ArrayList<>();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowListss = new ArrayList<>();

        inlineKeyboardButton.setText("Комедии");
        inlineKeyboardButton.setCallbackData("Комедия");
        inlineKeyboardButton1.setText("Драммы");
        inlineKeyboardButton1.setCallbackData("dramma");
        inlineKeyboardButton2.setText("Боевики");
        inlineKeyboardButton2.setCallbackData("action");
        inlineKeyboardButton3.setText("Мелодраммы");
        inlineKeyboardButton3.setCallbackData("romance");
        inlineKeyboardButton4.setText("Мультфильмы");
        inlineKeyboardButton4.setCallbackData("animation");

        inlineKeyboardButton44.setText("test1");
        //inlineKeyboardButton44.setCallbackData(FilmsTop.getTop(""));
        //inlineKeyboardButton44.setCallbackData(FilmsTop.getTop("/lists/top250/?quick_filters=films&tab=all"));

        System.out.println("text");

        inlineKeyboardButton55.setText("test2");
        //Elements country = Jsoup.parse("https://www.kinopoisk.ru/film/1129900/").getElementsByClass("/lists/navigator/country-1/?quick_filters=films");
        //inlineKeyboardButton55.setCallbackData(String.valueOf(country));
        //inlineKeyboardButton55.setCallbackData(FilmsTop.connect("/lists/navigator/anime/?quick_filters=high_rated&limit=20&tab=best"));
        //inlineKeyboardButton55.setCallbackData(getZhanrss("anime/?quick_filters=high_rated&limit=20&tab=best"));
        //keyboardButtonsRow55.add(inlineKeyboardButton44);
        keyboardButtonsRow55.add(inlineKeyboardButton55);


        keyboardButtonsRow1.add(inlineKeyboardButton);
        keyboardButtonsRow2.add(inlineKeyboardButton1);
        keyboardButtonsRow3.add(inlineKeyboardButton2);
        keyboardButtonsRow4.add(inlineKeyboardButton3);
        keyboardButtonsRow5.add(inlineKeyboardButton4);


        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);

        rowListss.add(keyboardButtonsRow55);




        inlineKeyboardMarkup.setKeyboard(rowList);
        inlineKeyboardMarkup.setKeyboard(rowListss);
        //inlineKeyboardMarkup.setKeyboard(rowListss);

        System.out.println("text2");
        SendMessage send = new SendMessage();
        send.setText("Добро пожаловать в наш фильмобот! Выберите, какие фильмы вы хотите увидеть");
        send.setReplyMarkup(inlineKeyboardMarkup);
        send.setChatId(String.valueOf(chatId));
        return send;
    }
    private SendMessage ListFilm(long chatId)
    {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();  //создаем переменные, куда мы будем записывать наши кнопки к сообщению
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton11 = new InlineKeyboardButton();



        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow6 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow7 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow8 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow9 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow10 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow11 = new ArrayList<>();


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowListss = new ArrayList<>();

        inlineKeyboardButton1.setText("Комедии");
        inlineKeyboardButton1.setCallbackData("Комедия");
        inlineKeyboardButton2.setText("Драммы");
        inlineKeyboardButton2.setCallbackData("dramma");
        inlineKeyboardButton3.setText("Боевики");
        inlineKeyboardButton3.setCallbackData("action");
        inlineKeyboardButton4.setText("Мелодраммы");
        inlineKeyboardButton4.setCallbackData("romance");
        inlineKeyboardButton5.setText("Мультфильмы");
        inlineKeyboardButton5.setCallbackData("animation");



        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText("Добро пожаловать в наш фильмобот! Выберите, что вы хотите сделать из списка ниже, фильмы уже отсортированы по алфавиту!");
        return sendMessage;


    }

    private InlineKeyboardMarkup editMarkup()
    {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();  //создаем переменные, куда мы будем записывать наши кнопки к сообщению

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton11 = new InlineKeyboardButton();

        inlineKeyboardButton11.setText("След.");
        inlineKeyboardButton11.setCallbackData("1");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow6 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow7 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow8 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow9 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow10 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow11 = new ArrayList<>();


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowListss = new ArrayList<>();
        return null;
    }
}



