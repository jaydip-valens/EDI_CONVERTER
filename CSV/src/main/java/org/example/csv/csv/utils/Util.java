package org.example.csv.csv.utils;

import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class Util {
    public String createStringForSegment(String value,int size) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.append(value);
        if (value.length() < size) {
            stringBuilder.append(" ".repeat(size - value.length()));
        }
        return stringBuilder.toString();
    }
}
