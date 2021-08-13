package ru.mrartur4ik.bantools;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {
	public static char colorChar = "&".charAt(0);
	public static String color(String s){
		return ChatColor.translateAlternateColorCodes(colorChar, s);
	}
	public static String containsIgnoreCase(List<String> list, String s){
		String result = null;
		for(String s1 : list){
			if(s.equalsIgnoreCase(s1)){
				result = s1;
				break;
			}
		}
		return result;
	}
	public static List<String> stringSetToList(Set<String> set){
		return new ArrayList<String>(set);
	}
}
