package com.td.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NormalCharConvertor {
	Locale locale = new Locale("en", "US");
	ResourceBundle rb = ResourceBundle.getBundle("westernChar", locale);
	
	public String charConvertor(String string) {
		String replacedString = string;
		EntityConvertor ec = new EntityConvertor();
		replacedString = ec.htmlEncoder(string);
		
		System.out.println("replaced string->"+replacedString);
		Pattern pattern = Pattern.compile("&(.*?);");
		
		Matcher matcher = pattern.matcher(replacedString);
		while(matcher.find()) {
			String data = matcher.group(0);
			System.out.println("data--->"+data);
		}
			
		return replacedString;
	}
	
	public String normalCharConversion(String oldString) {
		String newString;
		char[] charArray = oldString.toCharArray();
		for(int i = 0; i< charArray.length; i++) {
			if((int) charArray[i] > 128){
				 String hexaString = String.format("&#x%04X", (int)charArray[i]);
				 String replacedString = getValueFromPropertiesFile(hexaString);
				 if(replacedString != null) {
					 charArray[i] = replacedString.charAt(0);
				 }
			}
		}
		
		newString = new String(charArray);
		return newString;
				
	}
	
	public String getValueFromPropertiesFile(String key) {
		String value = null;
		try {
			value = rb.getString(key);
		}
		catch (Exception e){
			System.err.println("not find value for key->"+key);
		}
		return value;
	}
	public static void main(String[] args) {
//		char spl = 'ÿ';
//		char spl1 = 'é';
//		char spl2 = 'í';
//		char spl3 = 'á';
//		String value = "Elsevier España";
//		String value = "Neurología elemental";
//		String value = "Plum and Posner's Diagnosis of Stupor and Coma";
//		String value = "B978-84-9022-597-4.00003-6";
//		String value = "Examen clínico de los nervios craneales oculomotores (III,\n"
//					+"IV, VI)";
//		String value = "Cacho Gutiérrez";
//		String value = "Técnicas clínicas para el examen físico neurológico. I. Organización general, nervios craneales y nervios raquídeos periféricos";
		
//		for(int i =161; i< 255; i++) {
//			System.out.println(i+"->"+(char)i+"->"+String.format("&#x%04x", (int) (char)i));
//			System.out.println(String.format("&#x%04X", (int) (char)i)+"="+(char)i);
			
//		}
		
		System.out.println((int) '⁎'+"unicode :" + String.format("&#x%04x", (int) '⁎'));
//		System.out.println(new  NormalCharConvertor().normalCharConversion(value));
	}
}
