package com.td.jdom;
		import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.td.mongodb.bean.City;
import com.td.mongodb.bean.Country;
import com.td.mongodb.dao.CountryDao;
import com.td.mongodb.dao.GeographicalDao;
		public class Parsecountry {
			
            Country cc=null;
		        String csvFile = "C:\\Users\\74461\\Desktop\\country.csv";
		        String cityfile="C:\\Users\\74461\\Desktop\\city_INI\\Country_wise_City";
		        String line = "";
		        String cvsSplitBy = ",";
		    	
				public Parsecountry(ClassPathXmlApplicationContext ctx){
		    	
					CountryDao cd=ctx.getBean(CountryDao.class);
		        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

		            while ((line = br.readLine()) != null) {
		            	if(!line.isEmpty()&&line!=null){
		            	// use comma as separator
		            	cc=new Country();
		                String[] country = line.split(cvsSplitBy);

		              
		               if(country[0]!=null&&country[1]!=null&&country[2]!=null)
		               {  cc.setCountryName(country[0]);
		                cc.setTwolettercountryCode(country[2]);
		                cc.setthreelettercountryCode(country[1]);
		                String countrycodeValidate=country[1];
		                System.out.println();
		                countrycodeValidate=countrycodeValidate+".txt";
		               process(cityfile,countrycodeValidate,cc);
		               System.out.println("Country [code="+country[0]  + country[1] + " , name=" + country[2] + "]");
		              if(country[0].equalsIgnoreCase("South Korea")){
		               System.out.println(country[0]);
		            	  cd.create(cc);
		              }
		               }

		            }
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }

		    	}  
				public void  process(String cityfile,String countrycodeValidate,Country cc){
					List<City>cities=null;
					File traverse=new File(cityfile);
					File[]file=traverse.listFiles();
					for(File code:file){ 
						cities=new ArrayList<City>();
	                        	String countrycode=code.getName();
	                        	if(countrycodeValidate.equalsIgnoreCase(countrycode)){
	                        		String linecity="";
	                            
	                            BufferedReader brcity;
								try {
									brcity = new BufferedReader(new FileReader(code));
								
	                            while((linecity=brcity.readLine())!=null){
	                            	linecity = linecity.replaceAll("\\t", "@");
	                            	System.out.println(linecity);
	                            	if(linecity.contains("@")){
	                            	String [] linevalue=linecity.split("@");
	                            	String city=linevalue[2];
	                            	if(city!=null&&!city.isEmpty()){
	                            	cities.add(new City(city));
	                            	System.out.println(city);
	                            	}
	                            }
	                            }
	                            cc.setCity(cities);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
	                            
	                            
	                            
	                            
	                        	}
					
				}

		}
		}

