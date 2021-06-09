import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Zhanr {

    private Document document;

    public Zhanr(String href){
        connect(href);
    }

    private void connect(String href) {

        try{
            document = Jsoup.connect(href).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getZhanr(String zhanr){
        if (zhanr.equals("anime")){
            try {
                document = Jsoup.connect("https://www.kinopoisk.ru/lists/navigator/anime/?quick_filters=high_rated&limit=20&tab=best").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (zhanr.equals("biography")){
            try {
                document = Jsoup.connect("https://www.kinopoisk.ru/lists/navigator/biography/?quick_filters=high_rated&limit=20&tab=best").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Elements name = document.getElementsByClass("selection-film-item-meta__meta-additional-item");
        ArrayList<String> zhanrs = new ArrayList<>();
        for (int i = 0; i < name.size(); i++){
            if (i < 10){
                zhanrs.add(name.get(i).attr("href"));
            }
        }

        Set<String> set = new HashSet<>();
        set.addAll(zhanrs);
        zhanrs.clear();
        zhanrs.addAll(set);

        String[] strZhanr = new String[zhanrs.size()];

        for (int i = 0; i < zhanrs.size(); i++){
            strZhanr[i] = zhanrs.get(i);

        }
        return strZhanr;
    }

}
