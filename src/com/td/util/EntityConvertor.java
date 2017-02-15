package com.td.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class EntityConvertor {
	public static void main(String[] args) {
		EntityConvertor ec = new EntityConvertor();
//		ec.htmlEncoder("bibliografías");
		ec.htmlDecoder("Laboratory of Neurosciences, Graduate Program in Health Sciences, Health Sciences Unit, University of Southern Santa Catarina, Crici&#x00FA;ma, SC, <span class=\"country\">Brazil</span>");
//		ec.htmlEncoder(ec.htmlDecoder("CESEFOR, Forestry Services and Promotion Centre of Castilla y Le&oacute;n, Pol&iacute;gono Industrial &ldquo;Las Casas&rdquo;. Calle C, Parcela 4. 42005 Soria,"));
//		
//		ec.xmlExcape("<span class=\"xps_organization\">SFM-RI</span>, <span class=\"xps_state\">UVa</span>");
//		
//		ec.xmlUnExcape(ec.xmlExcape("<span class=\"xps_organization\">SFM-RI</span>, <span class=\"xps_state\">UVa</span>"));
	}
	
	public String htmlEncode(final String string) {
		  final StringBuffer stringBuffer = new StringBuffer();
		  for (int i = 0; i < string.length(); i++) {
		    final Character character = string.charAt(i);
		    if (CharUtils.isAscii(character)) {
		      // Encode common HTML equivalent characters
		      stringBuffer.append(StringEscapeUtils.escapeHtml4(character.toString()));
		    } else {
		      // Why isn't this done in escapeHtml4()?
		      stringBuffer.append(
		          String.format("&#x%x;",
		              Character.codePointAt(string, i)));
		    }
		  }
		  return stringBuffer.toString();
		}
	
	public String htmlEncoder(String str){
		String encoded = StringEscapeUtils.escapeHtml4(str);
		System.out.println("Html Encoded: "+ encoded);
		return encoded;
		
	}
	
	public String htmlDecoder(String str){
		String decoded = StringEscapeUtils.unescapeHtml4(str);
		System.out.println("Html Decoded: "+ decoded);
		return decoded;
	}
	
	public String xmlExcape(String xml){
		String decoded = StringEscapeUtils.escapeXml(xml);
		System.out.println("XML Escape: "+ decoded);
		return decoded;
	}
	
	public String xmlUnExcape(String xml){
		String decoded = StringEscapeUtils.unescapeXml(xml);
		System.out.println("XML Unescape: "+ decoded);
		return decoded;
	}
	

}
