package ru.emil.parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class MyPattern {
    private String tag;
    private String[] patterns;

    public boolean isContains(String s) {
        boolean flag = true;

        for (String pattern : patterns) {
            if (!s.toLowerCase().contains(pattern.toLowerCase())) {
                flag = false;
                break;
            }
        }

        return flag;
    }

    public MyPattern(String tag, String[] patterns) {
        this.tag = tag;
        this.patterns = patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyPattern myPattern = (MyPattern) o;
        return Arrays.equals(patterns, myPattern.patterns);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(patterns);
    }
}
