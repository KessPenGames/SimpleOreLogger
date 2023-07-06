package net.simple.orelogger.utils;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ColorText {


    public static String getColored(String string) {
        if(string == null) return null;
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> getColored(List<String> list) {
        return list.stream().map(ColorText::getColored).collect(Collectors.toList());
    }

    public static String[] getColored(String[] args) {
        return Arrays.stream(args).map(ColorText::getColored).toArray(String[]::new);
    }

    public static String getHEXColored(String string) {
        final Pattern sixCharHex = Pattern.compile("&#([0-9a-fA-F]{6})");
        Matcher matcher = sixCharHex.matcher(string);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            final StringBuilder replacement = new StringBuilder(14).append("&x");
            matcher.group(1).chars().mapToObj(c -> (char) c).forEach(c -> replacement.append('&').append(c));
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return getColored(sb.toString());
    }

    public static List<String> getHEXColored(List<String> string) {
        return string.stream().map(ColorText::getHEXColored).collect(Collectors.toList());
    }

    public static String[] getHEXColored(String[] args) {
        return Arrays.stream(args).map(ColorText::getHEXColored).toArray(String[]::new);
    }

    private ColorText() {}

}
