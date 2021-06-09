import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class DataBase {
    private static Document document;
    public static void connect(int i) {
        try {
            document = Jsoup.connect("https://www.kinopoisk.ru/lists/top250/?sort=title&page=" + i + "&tab=all").get();
        } catch (IOException e) {
            e.printStackTrace();
        }}

    public static String StartBase() throws SQLException {

        int conn=1;
        while(true){
            connect(conn);
            Elements proverka = document.select(".error-message__title");           //проверка на конец строки и выход из цикла
            if (proverka.text().equals("Ничего не найдено")) {
                System.out.println("Конец сайта");
                break;
            }

            if (document.hasText()) {                                                          //если документ получил значение-парсим
                Elements name = document.select("p[class=selection-film-item-meta__name]"); //название
                Elements rating = document.select("span[class=rating__value rating__value_positive]"); //рейтинг
                Elements position = document.select("span[class=film-item-rating-position__position]"); //место, которое занимает фильм в подборке
                Elements original_name = document.select("p[class=selection-film-item-meta__original-name]"); //оригинальное название
                Elements poster = document.select("img[class=selection-film-item-poster__image]");// для получения ссылки на фильм
                Elements country_zhanr = document.select("span[class=selection-film-item-meta__meta-additional-item]");

                //данные для подключения к БД
                final String user = "sbuhteoxvvetdi";
                final String password = "ff8d2eda15c967e777c29c5d5ff9dd76c690851ce0ad055c6fc4c771a049d11f";
                final String url = "jdbc:postgresql://ec2-54-74-60-70.eu-west-1.compute.amazonaws.com:5432/films";

                ArrayList<String> country = new ArrayList<>();
                ArrayList<String> zhanr = new ArrayList<>();
                int i = 0;

                while (true) {                                              //так как на странице параметр жанр и страна записываются в одну
                    if (!country_zhanr.isEmpty()) {                         //строку, то тут мы разбиваем полученные значения на отдельные списки
                        country.add(country_zhanr.get(0).text());
                        zhanr.add(country_zhanr.get(1).text());
                        country_zhanr.remove(1);
                        country_zhanr.remove(0);
                        i++;
                    } else break;
                }

                i = 0;
                final Connection connection = DriverManager.getConnection(url, user, password);      //подключаемся к базе данных
                try (Statement statement = connection.createStatement()) {
                    for (Element element : name) {
                        String check = "select * from films where name='"+ name.get(i).text()+"';";//sql запрос к БД
                        ResultSet b = statement.executeQuery(check);                               // тут нам надо проверить, что заданного
                        if (!b.next()) {                                                   //фильма еще нет в списке
                            String query = "INSERT INTO films VALUES(DEFAULT,'"                    //вставка в таблицу films информациии о фильмах
                                    + name.get(i).text() + "', '" +
                                    country.get(i) + "', '" + zhanr.get(i) + "', '" + rating.get(i).text() + "', '" + poster.get(i).attr("src") + "', '" + original_name.get(i).text().replace("'", "''") + "');";
                            statement.executeUpdate(query);
                            ResultSet result = statement.executeQuery("SELECT * FROM films ORDER BY Name ;");
                            System.out.println(result);


                            System.out.println(i);
                            }
                            i++;
                        }
                    } finally{
                        connection.close();                 //обязательно закрываем подключение к БД
                    }
                }
            else{
                    System.out.println("Проблема с подключением");
                }

            conn++;
            try {
                sleep((long) Math.random()*(10000-5000)+5000);   //надо чтобы запросы хоть как-то походили на человеческие и не блокались каптчей
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static ArrayList<kinopoisk> getName() throws SQLException //возвращает список всех фильмов, отсортированы по алфавиту
    {
       final String user = "sbuhteoxvvetdi";
                final String password = "ff8d2eda15c967e777c29c5d5ff9dd76c690851ce0ad055c6fc4c771a049d11f";
                final String url = "jdbc:postgresql://ec2-54-74-60-70.eu-west-1.compute.amazonaws.com:5432/films";
        ArrayList<kinopoisk> returned = new ArrayList<kinopoisk>();

        final Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        try (ResultSet result = statement.executeQuery("SELECT * FROM films ORDER BY name")) {
            while(result.next())
            {
                kinopoisk num1 = new kinopoisk();
                num1.setTitle(result.getString("name"));
                num1.setCountry(result.getString("country"));
                num1.setRated(result.getString("rating"));
                num1.setZhanr(result.getString("zhanr"));
                num1.setPoster(result.getString("url_picture"));
                num1.setYear(result.getString("date"));
                num1.setId(result.getInt("Id"));
                returned.add(num1);

            }

    }
        return returned;
    }

    public static ArrayList<kinopoisk> search(String search)throws SQLException//получает поисковой запрос и ищет по названию(вхождения)
    //возвращает список всех найденных фильмов, удовлетворяющих поиску
    {
        final String user = "postgres";
        final String password = "";
        final String url = "https://addons-sso.heroku.com/apps/5f493c1e-e309-4ec5-80fb-92cc6a69cdb1/addons/58740c68-8945-4f77-9e5e-b1c22660d342";
        ArrayList<kinopoisk> returned = new ArrayList<kinopoisk>();

        final Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        try (ResultSet result = statement.executeQuery("SELECT * FROM films ORDER BY name")) {
            while(result.next())
            {
                if(result.getString("name").contains(search)) {
                    kinopoisk num1 = new kinopoisk();
                    num1.setTitle(result.getString("name"));
                    num1.setCountry(result.getString("country"));
                    num1.setRated(result.getString("rating"));
                    num1.setZhanr(result.getString("zhanr"));
                    num1.setPoster(result.getString("url_picture"));
                    num1.setYear(result.getString("date"));
                    num1.setId(result.getInt("Id"));
                    returned.add(num1);
                }

            }
        }
        return returned;
    }
    public static ArrayList<kinopoisk> getZhanr(String search)throws SQLException  //получает жанр, какой надо найти и возвращает по жанрам фильм(русский, маленькие)
    {
        final String user = "postgres";
        final String password = "";
        final String url = "https://addons-sso.heroku.com/apps/5f493c1e-e309-4ec5-80fb-92cc6a69cdb1/addons/58740c68-8945-4f77-9e5e-b1c22660d342";
        ArrayList<kinopoisk> returned = new ArrayList<kinopoisk>();

        final Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        try (ResultSet result = statement.executeQuery("SELECT * FROM films ORDER BY name")) {
            while(result.next())
            {
                if(result.getString("zhanr").contains(search)) {
                    kinopoisk num1 = new kinopoisk();
                    num1.setTitle(result.getString("name"));
                    num1.setCountry(result.getString("country"));
                    num1.setRated(result.getString("rating"));
                    num1.setZhanr(result.getString("zhanr"));
                    num1.setPoster(result.getString("url_picture"));
                    num1.setYear(result.getString("date"));
                    num1.setId(result.getInt("Id"));
                    returned.add(num1);
                }

            }
        }
        return returned;
    }

}


