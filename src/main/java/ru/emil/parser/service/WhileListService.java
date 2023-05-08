package ru.emil.parser.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.emil.parser.model.MyPattern;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class WhileListService {

    private Set<MyPattern> listPatterns = new HashSet<>();


    @PostConstruct
    void load() throws FileNotFoundException {
        Scanner sc = new Scanner(new FileInputStream("C:\\emil\\IdeaProjects\\parser\\src\\main\\resources\\whiteList1.txt"));
        while (sc.hasNext()) {
            String tag = sc.nextLine().trim().toLowerCase();
            String[] patterns = tag.split(" ");
            listPatterns.add(new MyPattern(tag, patterns));
        }
    }

    Optional<MyPattern> isInWhiteList(String photoset) {
        return listPatterns.stream().parallel().filter(myPattern -> myPattern.isContains(photoset)).findAny();
    }

    public Set<MyPattern> getListPatterns() {
        return listPatterns;
    }
}
