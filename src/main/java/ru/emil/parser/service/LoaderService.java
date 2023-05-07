package ru.emil.parser.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class LoaderService {

    @PostConstruct
    public void start() throws IOException {
        LocalDate start = LocalDate.of(2023,4,30);
        LocalDate localDate = start.minusDays(1);
        Set<LocalDate> dates = new HashSet<>();
        do {
            dates.add(localDate);
            localDate = localDate.minusDays(1);
        } while (localDate.isAfter(start.minusDays(1)));

        dates.forEach(date -> {
            try {
                loadPage(date);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public void loadPage(LocalDate date) throws IOException {
        String month = String.valueOf(date.getMonth().getValue());
        if (month.length() < 2) month = "0" + month;
        String day = String.valueOf(date.getDayOfMonth());
        if (day.length() < 2) day = "0" + day;
        String year = String.valueOf(date.getYear());

        String dateStr = ""+year + month + day;

        int page = 1;

        do {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(1, TimeUnit.MINUTES);
            client.setReadTimeout(1, TimeUnit.MINUTES);
            client.setWriteTimeout(1, TimeUnit.MINUTES);
            Request request = new Request.Builder()
                    .get()
                    .url(new URL("https://ru.hentai-cosplays.com/archive/date/"+dateStr+"/page/" + page++))
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
                    .build();
            Response response = client.newCall(request).execute();
            Set<String> matches = new HashSet<>();

            String regex2 = "<img id=.+loading=\"lazy\"></a>";
            Matcher m = Pattern.compile("(?=(" + regex2 + "))").matcher(response.body().string());
            while(m.find()) {
                matches.add(m.group(1));
            }
            if (matches.size() == 0) {
                log.info("закончил " + dateStr);
                break;
            }
            matches.stream().parallel().forEach(urlNew -> {

                String url = urlNew.split("\"")[5];
                String photoSet = urlNew.split("\"")[7]
                        .replace("\\", "_")
                        .replace("|", "_")
                        .replace("/", "_")
                        .replace("*", "_")
                        .replace("?", "_")
                        .replace("\"", "_")
                        .replace("<", "_")
                        .replace(">", "_")
                        .replace(":", "_");
                try {
                    //String groupName = url.split("/")[6];
                    //String bebeNum = url.split("/")[5];
                    //"C:\\models\\" + bebeNum + "\\" + dateStr + "\\" + groupName + "\\" + UUID.randomUUID().toString() +".jpg"
                    downloadImage(url.replace("p=700", "p=2000"), "C:\\models\\" +dateStr+ "\\" + photoSet +"\\"+ UUID.randomUUID().toString() +".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } while (true);
    }


    public static void downloadImage(String imageUrl, String savePath) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(1, TimeUnit.MINUTES);
            client.setReadTimeout(1, TimeUnit.MINUTES);
            client.setWriteTimeout(1, TimeUnit.MINUTES);
            Request request = new Request.Builder()
                    .url(new URL(imageUrl))
                    .get()
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
                    .build();
            Response response = client.newCall(request).execute();

            // Открываем поток для чтения данных по URL-адресу
            try (InputStream inputStream = response.body().byteStream()) {
                // Создаем Path-объект из указанного пути сохранения
                Path saveFilePath = Paths.get(savePath);

                // Создание папки, если ее нет
                Path saveDirectoryPath = saveFilePath.getParent();
                if (!Files.exists(saveDirectoryPath)) {
                    Files.createDirectories(saveDirectoryPath);
                }

                // Сохраняем данные из потока в файл по указанному пути
                Files.copy(inputStream, saveFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
