package com.td.jdom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.td.mongodb.bean.Address;
import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Artifact_Book;
import com.td.mongodb.bean.Author;
import com.td.mongodb.bean.DatasetZipEntry;
import com.td.mongodb.bean.DivisionName;
import com.td.mongodb.bean.GeographicalLocation;
import com.td.mongodb.bean.Organization;
import com.td.mongodb.dao.AuthorsDao;
import com.td.mongodb.dao.DatasetZipEntryDao;
import com.td.mongodb.dao.GeographicalDao;
import com.td.mongodb.dao.OrganizationDao;
import com.td.util.FileUtility;
import com.td.util.NormalCharConvertor;
import com.td.util.SevenZipExtract;

/**
 * @author 74414
 *
 */
@DisallowConcurrentExecution
public class XMLExtractionJob implements Job{
	public static Logger log = Logger.getLogger(XMLExtractionJob.class);
	public static Logger statusLogger = Logger.getLogger("reportsLogger");
	public String path = "C:\\Books\\Direnzobrazil\\";
//	private String file1 = path+"main.xml";
	
	public static int refCount=0;

    private static ClassPathXmlApplicationContext ctx= new ClassPathXmlApplicationContext("Spring-Mongo-Config.xml");
	Locale locale = new Locale("en", "US");
//    EntityToSymbolConverter symbolConvertor = new EntityToSymbolConverter();
	public NormalCharConvertor normalCharConvertor= new NormalCharConvertor(); ;
	
	public ResourceBundle xmlPathResourceBundle = ResourceBundle.getBundle("xmlPath", locale);
	public static String DTD_PATH;
	int count =0; 
	public XMLExtractionJob() {
		
	}
	
	/*
     * (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	     JobDataMap dataMap = arg0.getJobDetail().getJobDataMap();
	     DTD_PATH = dataMap.getString("DTD_PATH");

    	log.info("***********starting XMLDataEntry JOB -> " + new Date() + " dtd path is->"+DTD_PATH+ "**********");
    	jobExecutionofNewElsvJournalArticles();
		ctx.close();
	}
 
    /**
     * job execution for reading new journal articles retrieved from path "XML_FOR_REF_DATABASE" and delete them after successful insertion into database
     */
    public void jobExecutionofNewElsvJournalArticles() {
		// TODO Auto-generated method stub
    	String journalPath = xmlPathResourceBundle.getString("XML_FOR_REF_DATABASE");
    	journalPath = journalPath.trim();
    	
    	String successPath = xmlPathResourceBundle.getString("SUCCESS_XML");
    	
    	FileUtility fileUtil = new FileUtility();
    	
    	File JournalArticlePathfile = new File(journalPath);
    	if(JournalArticlePathfile.isDirectory()) {
    		File [] journalArticleList = JournalArticlePathfile.listFiles();       //get all journal's Article list
    		Arrays.sort(journalArticleList);
    		log.info("***TOTAL FILES ="+journalArticleList.length+"****");
    		for(File file : journalArticleList) {
    			try {
					log.info("parsing XML started-->"+file.getAbsolutePath());
//					long start = System.currentTimeMillis();
					parseFile(file.getAbsolutePath());
//					
//					/*** delete after successful insertion into db*****/
					log.debug("move success file after processing->"+fileUtil.moveFile(file.getAbsolutePath(), successPath));
//					
//					long end = System.currentTimeMillis();
//					System.out.println("time taken to process an article-->"+(end-start)/1000);
					Thread.sleep(500);
//					break;
				} catch (Exception e) {
					log.error(e);
				} 
    		}
    	}
	}

	/**
     * @deprecated
     * job execution, i.e., finding XML from specified path in xmlPath.properties file and call function for XML parsing. 
     * This basically uses path which involves zip extraction and then read folder hierarchy to get XML
     */
    public void jobExecution() {
		/*try {
			Runtime.getRuntime().exec("cmd /c start 80.bat");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

    	String journalPath = xmlPathResourceBundle.getString("journalPath");
    	journalPath = journalPath.trim();
    	String extractedZipFilePath = xmlPathResourceBundle.getString("extractedZipPath");      //where we have to extract the zip
    	extractedZipFilePath = extractedZipFilePath.trim();
    	log.debug(journalPath+"**"+extractedZipFilePath);
    	FileUtility fileUtil = new FileUtility();
    	
    	File[] listDateWiseFiles = fileUtil.listFiles(journalPath); //from journal path get the all the main date wise directory where all the zip files are contained
    	if(listDateWiseFiles != null) {     
    		
    		  if(listDateWiseFiles.length > 0) {
    			  for (int i = 0; i < listDateWiseFiles.length; i++) 
    			  {
    				  /*** check zip files directly inside date folder ****/
    				  File[] listFiles = fileUtil.listFiles(listDateWiseFiles[i].getAbsolutePath());
    				  if(listFiles != null ) {
    					  for(int k =0; k< listFiles.length ; k++) {
    						  extractXMLFromZip(listFiles[k], fileUtil, extractedZipFilePath);
    					  }
    				  }
    				  /****************************************************/
    				  
    				  /***** check zip files inside done folder *********/
					  String donePath = listDateWiseFiles[i].getAbsolutePath()+"/Done";
					  File[] listDoneFiles = fileUtil.listFiles(donePath);      
					  if(listDoneFiles != null) {
		    			  for (int j = 0; j < listDoneFiles.length; j++) {
		    				  extractXMLFromZip(listDoneFiles[j], fileUtil, extractedZipFilePath);
		    			  }
					  }
    			  }
    		  }
    	}
//    	ctx.close();
    }

    /**
     * job execution, i.e., finding XML for elsevier journals from specified path in xmlPath.properties file and call function for XML parsing.
     * we need to select last stage XML by ourself on the basis of last modified date of file.
     */
    public void jobExecutionOfElsevierJournals() {
    	String journalPath = xmlPathResourceBundle.getString("journalPath");
    	journalPath = journalPath.trim();
    	
    	File JournalPathfile = new File(journalPath);
    	if(JournalPathfile.isDirectory()) {
    		File [] journalList = JournalPathfile.listFiles();       //get all journal list
    		Arrays.sort(journalList);
    		
    		for(File journal : journalList) {
    			if(journal.isDirectory()) {
    				File [] journalWiseArticleList = journal.listFiles();
    				
    				for(File article : journalWiseArticleList) {
    					File artifactArchFile = new File(article.getAbsolutePath()+"\\Archive");
    					if(artifactArchFile.exists()) {
		    				File mainXML = getTaggedXML(artifactArchFile);
		    				if(mainXML != null) {
		    					try {
		    						log.info("main XML-->"+mainXML.getAbsolutePath());
		    						parseFile(mainXML.getAbsolutePath());
		    						
		    						Thread.sleep(1000);
		    					} catch (Exception e) {
		    						log.error(e);
		    					} 
		    				}
    					}
    				}
    			}
    		}
    	}
		
    }
    
    
    public File getTaggedXML(File file) {
		File mainXML = null;
		if(file.isDirectory()) {
			File [] listOfFiles = file.listFiles();
			Arrays.sort(listOfFiles, new Comparator<File>() {
			    public int compare(File f1, File f2) {
			    	try{
				    	int f1int = Integer.parseInt(f1.getName());
				    	int f2int = Integer.parseInt(f2.getName());
				    	return Integer.compare(f1int, f2int);
			    	}
			    	catch(NumberFormatException nfe) {
			    		return Long.compare(f1.lastModified(), f2.lastModified());
					}
			    }
			});
			for(int f=listOfFiles.length-1; f>0; f--) {
				if(listOfFiles[f].isDirectory()) {
					File lastStageXMLFile = new File(listOfFiles[f].getAbsolutePath()+"\\tx1.xml");
					if(lastStageXMLFile.exists()) {
						mainXML = lastStageXMLFile;
						break;
					}
				}
			}
			
		}
		return mainXML;
	}
    /**
     * @param which
     * @param startElem
     * @return founded element
     */
    public Element getElement(String which,Element startElem) {
	    List<Element> nodes = startElem.getChildren();
	    Iterator<Element> iter = nodes.iterator();

	    Element element = null;
	    if(startElem.getName().equals(which))
	    	element = startElem;
	    else {
			while (iter.hasNext()) {
				Element elem = iter.next();
				element =  getElement(which, elem);
				if(element != null) {
					break;
				}
			}
	    }
		return element;
	 }

    /**
     * checking in db if zip parsing already exist or not.If not then extract the zip and process it and update the status of the zip in db
     * @param zipfile
     * @param fileUtil
     * @param extractedZipFilePath
     */
    public void extractXMLFromZip(File zipfile, FileUtility fileUtil, String extractedZipFilePath) {
    	if(zipfile.getName().contains(".zip")) {
    		log.debug("*********processing of zip started-->"+zipfile.getAbsolutePath());
    		DatasetZipEntryDao dataSetMongoOp = ctx.getBean("datasetZipEntryDao", DatasetZipEntryDao.class);
    		DatasetZipEntry datasetObject = dataSetMongoOp.searchByName(zipfile.getName());
    		boolean isParsingNeeded = false;
    		if(datasetObject == null) { //it's a new entry
    			String zipName = zipfile.getName();
    			String path = zipfile.getAbsolutePath();
    			log.debug("zip -->"+zipName+" at path "+path.substring(0, path.indexOf(zipName)-1));
    			datasetObject = new DatasetZipEntry(zipName, path.substring(0, path.indexOf(zipName)-1), new Date(), null, "STARTED");
    			
    			dataSetMongoOp.create(datasetObject);
    			isParsingNeeded = true;
    		}
    		else {
    			if(!datasetObject.getStatus().equals("SUCCESS")) { //if zip entry already exist and there is some error in processing that zip, then we'll reset  the values
    				log.debug("zip entry reset-->"+zipfile.getName());
    				datasetObject.setStartTime(new Date());
    				datasetObject.setStatus("STARTED");
    				isParsingNeeded = true;
    			}
    		}
    		try {
    		if(isParsingNeeded && SevenZipExtract.extractZip(zipfile, new File(extractedZipFilePath), new String[]{".xml"})){
    			log.debug("zip extraction started of zip name->"+zipfile.getAbsolutePath());
    			
    			String datasetXMLPath = extractedZipFilePath+"/thom"+zipfile.getName().substring(0, zipfile.getName().indexOf(".zip"));
//			  log.debug(datasetXMLPath);
    			
    				File file = new File(datasetXMLPath+"/dataset.xml");
    				SAXBuilder builder = new SAXBuilder();
    				Document jdomDoc= builder.build(file);
    				Element rootElement = jdomDoc.getRootElement();
    				Element element = getElement("ml", rootElement);
    				if(element != null) {
    					Element pathElement = element.getChild("pathname", Namespace.getNamespace("http://www.elsevier.com/xml/schema/transport/journal-2013.1/s100"));
    					if(pathElement != null) {
    						String pathForXML = pathElement.getValue().trim();
    						log.debug("file path for parsing-->"+datasetXMLPath+"/"+pathForXML);
    						parseFile(datasetXMLPath+"/"+pathForXML);        // from here actual parsing of article will start
    					}
    				}
    				
    				dataSetMongoOp.updateByZipName(zipfile.getName(), "SUCCESS", new Date());        //if processed successfully
    			}
    			
    		}
    		catch (JDOMException | IOException e) {
				log.error(e);
				dataSetMongoOp.updateByZipName(zipfile.getName(), "FAIL", null);
			}
    		catch( Exception e) {
    			log.error("exception-->"+e);
    			dataSetMongoOp.updateByZipName(zipfile.getName(), "FAIL", null);
    		}
			finally {
				if(isParsingNeeded) {
					String datasetXMLPath = extractedZipFilePath+"/thom"+zipfile.getName().substring(0, zipfile.getName().indexOf(".zip"));
					File mainfile = new File(datasetXMLPath);
					try {
						log.debug("deleted->"+fileUtil.deleteRecursive(mainfile));
					} catch (FileNotFoundException e) {
						log.error(e);
					}
				}
			}
    	}
    }
    
     /**
     * @param filePath
     * @throws JDOMException
     * @throws IOException
     * @category  XML parsing and converting into equivalent object and insert into database
     */
    public void parseFile(String filePath) throws JDOMException, IOException {
     
    	/*if(count>0){
        	new Parsecountry(ctx);
        count++;
        }*/
    		String fileString = replaceFileByString(filePath);
	    	
	        // Use a SAX builder
    		SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
	        builder.setEntityResolver(new CustomEntityResolver(DTD_PATH));
	        Document jdomDoc = null;
	        if(fileString != null && !fileString.equals("")) {
	        	jdomDoc = builder.build(new StringReader(fileString));
	        }
	        else {
	        	jdomDoc = builder.build(filePath);
	        }
	        //get the root element
	        Element rootElement = jdomDoc.getRootElement();

	        log.debug("rootelement lang-->"+rootElement.getAttribute("lang", Namespace.XML_NAMESPACE).getValue());
	 
	        new ParsingUtil(rootElement, ctx, this);
	       
	        
	        /*if(rootElement.getName().equals("article") || rootElement.getName().equals("simple-article")){
	        	new ParsingUtil(rootElement, ctx, this);
	        }
	        else if(rootElement.getName().equals("book")) { //Kuldeep
	        	new BookParsing(rootElement, ctx, this); 
	        }*/
    }
    
    public void parseFile(InputStream inputStream) throws JDOMException, IOException {
    
        // Use a SAX builder
		SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
        //builder.setEntityResolver(new CustomEntityResolver(DTD_PATH));
        Document jdomDoc = null;
        if(inputStream != null) {
        	jdomDoc = builder.build(inputStream);
        	//get the root element
            Element rootElement = jdomDoc.getRootElement();
            log.debug("rootelement lang-->"+rootElement.getAttribute("lang", Namespace.XML_NAMESPACE).getValue());
             new ParsingUtil(rootElement, ctx, this);
            }
    }

	/**
     * @param oldFileName
     * @return converting file content into UTF-8 format and return a string
     */
    public String replaceFileByString(String oldFileName) {
    	BufferedReader br = null;
    	StringBuilder lineBuilder = null;
    	try {
    		 br = new BufferedReader(new InputStreamReader(new FileInputStream(oldFileName), "UTF-8"));
    		 String line;
    		 lineBuilder = new StringBuilder();
             while ((line = br.readLine()) != null) {
//            	 log.debug("line->"+line);
                if (line.contains("&#x204e;"))
                   line = line.replace("&#x204e;", "*");
                lineBuilder.append(line+"\n");
             }
    	}catch (Exception e) {
//        	log.error(e);
           return null;
        } finally {
           try {
              if(br != null)
                 br.close();
           } catch (IOException e) {
              //
           }
        }
		return lineBuilder.toString();
	}
	
    /**
	 * @param inputString
	 * @return remove whitespaces from input string and return modified string
	 */
	public String removeWhiteSpaces(String inputString) {
		inputString = inputString.replace("\n", " ");
		inputString = inputString.replace("\r", "");
		inputString = inputString.replace("\t"," ");
		inputString = inputString.replace("  ", "");
		return inputString;
		
    }
    
	
    /**
     * @deprecated
     * @param oldFileName
     * @param tmpFileName
     * @return File
     * Used for replacing special html entity &#x204e; and read, write new file in UTF-8 format
     */
    public File replace(String oldFileName, String tmpFileName) {
        BufferedReader br = null;
        OutputStreamWriter bw = null;
        try {
//           br = new BufferedReader(new FileReader(oldFileName));
          br = new BufferedReader(new InputStreamReader(new FileInputStream(oldFileName), "UTF-8"));
          bw = new OutputStreamWriter(new FileOutputStream(tmpFileName), "UTF-8");
//           bw = new BufferedWriter(new FileWriter(tmpFileName));
           String line;
           while ((line = br.readLine()) != null) {
//        	   log.debug("&#x204e;-->"+line.contains("&#x204e;"));
              if (line.contains("&#x204e;"))
                 line = line.replace("&#x204e;", "*");
              bw.write(line+"\n");
           }
        } catch (Exception e) {
//        	log.error(e);
           return null;
        } finally {
           try {
              if(br != null)
                 br.close();
           } catch (IOException e) {
              //
           }
           try {
              if(bw != null)
                 bw.close();
           } catch (IOException e) {
              //
           }
        }
        File newFile = new File(tmpFileName);
        return newFile;
     }
    
    /**
     * @param numString
     * @return parse String and handle the exception if string is not a number and return converted Integer
     */
    public Integer parseNumber(String numString) {
    	int num = 0;
    	 try{
			num = Integer.parseInt(numString);
		 }
		 catch(NumberFormatException ne) {
		 }
    	return num;
    }
    
    /**
     * @param artifactObj
     * @param headerElement
     * @return parsing header's author info and return the list of authors
     */
    
 @SuppressWarnings("unused")
public List<Author> authorParsing(Artifact artifactObj, Element headerElement) {
    	AuthorsDao ad=ctx.getBean("authorDao", AuthorsDao.class);
    	Author authInd=new Author();;
    	List<Author> authors = null;
		if(artifactObj.getAuthors() == null) {
			authors = new ArrayList<Author>();
		}
		else {
			authors = artifactObj.getAuthors();
		}
       	for(Element authorElement: headerElement.getChildren()) {
    			if(authorElement.getName().equals("author")) {
    				Author author = new Author();
    				String  fullName = "";
    				boolean correspAuthor = false;
	       			for(Element authorInfo: authorElement.getChildren()) {
	       				String nodeName = authorInfo.getName().trim();
	       				String nodeValue = normalCharConvertor.normalCharConversion(authorInfo.getValue().trim());
	       				log.debug(nodeName+"->"+nodeValue);
	       				if(nodeName.equals("cross-ref")) {
	       					List<Element> crossRefInfo = authorInfo.getChildren();
	    	    					for(Element supInfo: crossRefInfo) {
	    	    						String supValue = normalCharConvertor.normalCharConversion(supInfo.getValue().trim());
	    		    					if(supValue.equals("*")){
	    		    						correspAuthor = true;
	    		    						log.debug("this is correspondence author");
	    		    					}
	    		    					else {
	    		    						author.setSupValue(supValue);
	    		    					}
	    	    					}
	       				}
	       				else  {
	       					switch(nodeName) {
	       						case "given-name" : 
	       											author.setGiven_name(nodeValue);
	       											authInd.setGiven_name(nodeValue);
	       						                    fullName += " "+nodeValue;
	       						                    break;
	       						case "surname" : 	
	       											author.setFamily_name(nodeValue);
	       											authInd.setFamily_name(nodeValue);
	       											fullName += " "+nodeValue;
	       											break;
	       						case "e-address" : author.setEmail(nodeValue);
	       						                   authInd.setEmail(nodeValue);
	       										   break;
	       					}
	       				}
	       			}
	       			
	    			author.setCorrespAuthor(correspAuthor);
	    			authInd.setCorrespAuthor(correspAuthor);
	       			author.setFullName(fullName);
	       			authInd.setFullName(fullName);
	       			//author.setOrganization(new ArrayList<Organization>());
	       			boolean authorExist = false;
	       			for(Author auth : authors) {
	    				if(auth.getFamily_name().equalsIgnoreCase(author.getFamily_name()) && ((auth.getGiven_name() != null && author.getGiven_name() != null)? auth.getGiven_name().equalsIgnoreCase(author.getGiven_name()) : true)){ //added check in case if artifact object already exist in db
	    					/*if(auth.getOrganization() == null && author.getOrganization() != null) {
	    						auth.setOrganization(author.getOrganization());
	    					}*/
	    					if(auth.getDegrees() == null && author.getDegrees() != null) {
	    						auth.setDegrees(author.getDegrees());
	    						authInd.setDegrees(author.getDegrees());
	    					}
	    					if(auth.getPrefix() == null || auth.getPrefix().equals("")){
	    						auth.setPrefix(author.getPrefix());
	    						authInd.setPrefix(author.getPrefix());;
	    					}
	    					if(auth.getAlt_name() == null || auth.getAlt_name().equals("")){
	    						auth.setAlt_name(author.getAlt_name());
	    					}
	    					if(auth.getInitials() == null || auth.getInitials().equals("")){
	    						auth.setInitials(author.getInitials());
	    						authInd.setInitials(author.getInitials());
	    					}
	    					if(auth.getOrcid() == null || auth.getOrcid().equals("")){
	    						auth.setOrcid(author.getOrcid());
	    						authInd.setOrcid(author.getOrcid());
	    					}
	    					if(auth.getEmail() == null || auth.getEmail().equals("")){
	    						auth.setEmail(author.getEmail());
	    						authInd.setEmail(author.getEmail());
	    					}
	    					
	    					if(auth.getSuffix() == null || auth.getSuffix().equals("")){
	    						auth.setSuffix(author.getSuffix());
	    						authInd.setSuffix(author.getSuffix());
	    					}
	    					if(auth.getUrl() == null || auth.getUrl().equals("")){
	    						auth.setUrl(author.getUrl());
	    						auth.setUrl(author.getUrl());
	    					}
	    					if(auth.getSupValue() == null ) {
	    						auth.setSupValue(author.getSupValue());
	    						auth.setSupValue(author.getSupValue());
	    					}
	    					auth.setCorrespAuthor(author.getCorrespAuthor());
	    					authInd.setCorrespAuthor(author.getCorrespAuthor());
	    					authorExist = true;
	    					break;
	    				}
	    			}	
	       			
	       			if(authors.size() == 0 ||!authorExist) {  //add new author object if there is no author in list or author does not exist in the list
	       				authors.add(author);
	       				if(fullName!=null&&authInd.getFullName()!=null){
	       				ad.create(authInd);
	       				}
	       			}
    			}
    			else if(authorElement.getName().equals("affiliation")) {
    				String labelValue = "";
    				for(Element affInfo: authorElement.getChildren()) {
    					if(affInfo.getName().equals("label")) {
    						labelValue = affInfo.getValue().trim();
    					}
    					else if(affInfo.getName().equals("affiliation")) {
    						log.debug("labelvalue-->"+labelValue);
    						StringBuilder orgSB= new StringBuilder();
    						String orgAddr = "", orgState = "", orgPostalCode = "", orgCountry = "", orgCity = "";
    						for(Element element: affInfo.getChildren()) {
    							String nodeName = element.getName().trim();
    							String nodeValue = normalCharConvertor.normalCharConversion(removeWhiteSpaces(element.getValue().trim()));
    	    					if(nodeName.equals("organization"))
    	    						orgSB.append(nodeValue+",");
    	    					else{
    	    						log.debug(nodeName+"->"+nodeValue);
    	    						switch(nodeName) {
    	    							case "address-line" : orgAddr = nodeValue; break;
    	    							case "state" : orgState = nodeValue; break;
    	    							case "postal-code" : orgPostalCode = nodeValue; break;
    	    							case "country" : orgCountry = nodeValue; break;
    	    							case "city" :orgCity = nodeValue; break;
    	    						
    	    						}
    	    					}
       					}
    						String orgName = orgSB.toString();
    						
    						log.debug("organization-->"+orgName);
    						for(Author author : authors) {
    							/*
    							 * set each author object's organization value in two cases
    							 * 1. if cur rent affiliation has no labelvalue and author from authorlist has no supvalue and author correspondence value is false
    							 * 2. if there is some supvalue of author that is equal to current affiliation's label value
    							 * 3. there could be multiple sup value associated to a single author, so split it and check with all values
    							 */
    							String [] supValueArray = null;
    							if(author.getSupValue() != null) {
    								if(author.getSupValue().contains(",")) {
    									supValueArray = author.getSupValue().split(",");
    								}
    								else {
    									supValueArray = new String[]{author.getSupValue()};
    								}
    							}
    							boolean matchedSupFlag= false;
    							if(supValueArray != null) {
	    							for(String sup : supValueArray) {
	    								if(labelValue.equalsIgnoreCase(sup)) {   //if author's any supvalue is equal to current affiliation value then matchedSupFlag will be true
	    									matchedSupFlag = true;
	    								}
	    							}
    							}
//    							if((labelValue.equals("") && author.getSupValue() == null && !author.getCorrespAuthor() ) || (author.getSupValue() != null && labelValue.equals(author.getSupValue().trim())) ){  
								if((labelValue.equals("") && supValueArray == null && !author.getCorrespAuthor() ) || matchedSupFlag ){  
    								List<Organization> orgList = new ArrayList<Organization>();
    								Organization orgObj = null;
    								GeographicalLocation geolocCity=null;
    								GeographicalLocation geolocState=null;
    								GeographicalLocation geolocPincode=null;
    								GeographicalLocation geolocCountry=null;
    								GeographicalLocation geoloc=new GeographicalLocation();
									//log.debug("setting affiliation of author-->"+author.getGiven_name());
									OrganizationDao od = ctx.getBean("organizationDao", OrganizationDao.class);
									GeographicalDao gd=ctx.getBean("geographicalDao", GeographicalDao.class);
									orgObj = od.readByName(orgName);
									geolocCity=gd.readBycityName(orgCity);
									geolocCountry=gd.readBycountryName(orgCountry);
									geolocState=gd.readBystateName(orgState);
									geolocPincode=gd.readBypostalcode(orgPostalCode);
									if(geolocCity==null||geolocState==null||geolocCountry==null||geolocPincode==null){
										if(geolocCity==null){
											geoloc.setCity(orgCity);
										}
										if(geolocCountry==null){
											geoloc.setCountry(orgCountry);
										}
										if(geolocState==null){
											geoloc.setState(orgState);
										}
										
										if(geolocPincode==null){
											geoloc.setPostal_code(orgPostalCode);
										}
										gd.create(geoloc);
										
									}
									if(orgObj == null) {
										List<DivisionName> divisionList = new ArrayList<DivisionName>();
										orgObj = new Organization(orgName, divisionList, "", orgAddr, "", "");
										
    									od.create(orgObj);
    									
									}
									
									boolean orgExist = false;
    								for(Organization org: orgList) {   //if author already has organization list then compare organization
    									if(org.getName().equalsIgnoreCase(orgName)) {
    										orgExist = true;
    										break;
    									}
    								}
    								if(orgList.size() == 0 || !orgExist) {   
    									orgList.add(orgObj);
    									
    								}
    							}
    						}
    					}
    				}
    			}
    			else if(authorElement.getName().equals("correspondence")) {
    				String label = "", address_line ="", city = "", state = "", state_code = "",country = "", country_code = "", postal_code = "",tel="",fax = "";
    				
    				String corresValue = normalCharConvertor.normalCharConversion(removeWhiteSpaces(authorElement.getValue()));
    				log.debug("corrs->"+corresValue);
    				String [] corresArray;
    				if(corresValue.indexOf(";") > -1){
    					corresArray = corresValue.split(";");
    				}
    				else {
    					corresArray = new String[1];
    					corresArray[0] = corresValue;
    				}
    				for(String value : corresArray) {
    					if(value.indexOf("Tel.:") > -1) {
    						tel = value.substring(value.indexOf("Tel.:")+5);
    						log.debug("tel-->"+tel);
    					}
    					else if(value.toLowerCase().indexOf("fax:") > -1) {
    						fax = value.substring(value.toLowerCase().indexOf("fax.:")+5);
    						if(fax.contains("."))
    							fax = fax.substring(0,fax.lastIndexOf("."));
    						log.debug("fax-->"+fax);
    					}
    					else {
    						if(value.indexOf(":") > -1) {
    							value = value.substring(value.indexOf(":")+1);
    						}
    						if(value.indexOf(".") > -1) {
    							value = value.substring(0, value.lastIndexOf("."));
    						}
    						address_line = value;
    						log.debug("address line-->"+address_line);
    					}
    				}
    				Address address = new Address(label, address_line, city, state, state_code, country, country_code, postal_code, tel, fax);
    				for(Author author: authors) {
    					if(author.getCorrespAuthor()) 
    						if(author.getAddress() == null)    //added check in case if artifact object already exist in db
    							author.setAddress(address);
    					        
    				}
    			}
    		}
       	for(Author author : authors) {
       		author.setSupValue(null);
       	}
       
       	
       	return authors;
       }
    
    @SuppressWarnings("null")
	public List<Author> authorParsing(Artifact_Book artifactObj, Element headerElement) {
    	AuthorsDao ad=ctx.getBean("authorDao", AuthorsDao.class);
    	Author authInd=null;
    	List<Author> authors = null;
		if(artifactObj.getAuthors() == null) {
			authors = new ArrayList<Author>();
		}
		else {
			authors = artifactObj.getAuthors();
		}
       	for(Element authorElement: headerElement.getChildren()) {
    			if(authorElement.getName().equals("author")) {
    				Author author = new Author();
    				String  fullName = "";
    				boolean correspAuthor = false;
	       			for(Element authorInfo: authorElement.getChildren()) {
	       				String nodeName = authorInfo.getName().trim();
	       				String nodeValue = normalCharConvertor.normalCharConversion(authorInfo.getValue().trim());
	       				log.debug(nodeName+"->"+nodeValue);
	       				if(nodeName.equals("cross-ref")) {
	       					List<Element> crossRefInfo = authorInfo.getChildren();
	    	    					for(Element supInfo: crossRefInfo) {
	    	    						String supValue = normalCharConvertor.normalCharConversion(supInfo.getValue().trim());
	    		    					if(supValue.equals("*")){
	    		    						correspAuthor = true;
	    		    						log.debug("this is correspondence author");
	    		    					}
	    		    					else {
	    		    						author.setSupValue(supValue);
	    		    					}
	    	    					}
	       				}
	       				else  {
	       					switch(nodeName) {
	       						case "given-name" : 
	       											author.setGiven_name(nodeValue);
	       											authInd.setGiven_name(nodeValue);
	       						                    fullName += " "+nodeValue;
	       						                    break;
	       						case "surname" : 	
	       											author.setFamily_name(nodeValue);
	       											authInd.setFamily_name(nodeValue);
	       											fullName += " "+nodeValue;
	       											break;
	       						case "e-address" : author.setEmail(nodeValue);
	       						                    authInd.setEmail(nodeValue);        
	       										   break;
	       					}
	       				}
	       			}
	       			
	       			authInd.setCorrespAuthor(correspAuthor);
	    			author.setCorrespAuthor(correspAuthor);
	    			authInd.setFullName(fullName);
	    			author.setFullName(fullName);
	       			//author.setOrganization(new ArrayList<Organization>());
	       			boolean authorExist = false;
	       			for(Author auth : authors) {
	    				if(auth.getFamily_name().equalsIgnoreCase(author.getFamily_name()) && ((auth.getGiven_name() != null && author.getGiven_name() != null)? auth.getGiven_name().equalsIgnoreCase(author.getGiven_name()) : true)){ //added check in case if artifact object already exist in db
	    					/*if(auth.getOrganization() == null && author.getOrganization() != null) {
	    						auth.setOrganization(author.getOrganization());
	    					}
	    					if(auth.getDegrees() == null && author.getDegrees() != null) {
	    						auth.setDegrees(author.getDegrees());
	    					}*/
	    					if(auth.getPrefix() == null || auth.getPrefix().equals("")){
	    						authInd.setPrefix(author.getPrefix());
	    						auth.setPrefix(author.getPrefix());
	    					}
	    					if(auth.getAlt_name() == null || auth.getAlt_name().equals("")){
	    						authInd.setAlt_name(author.getAlt_name());
	    						auth.setAlt_name(author.getAlt_name());
	    					}
	    					if(auth.getInitials() == null || auth.getInitials().equals("")){
	    						authInd.setInitials(author.getInitials());
	    						auth.setInitials(author.getInitials());
	    					}
	    					if(auth.getOrcid() == null || auth.getOrcid().equals("")){
	    						authInd.setOrcid(author.getOrcid());
	    						auth.setOrcid(author.getOrcid());
	    					}
	    					if(auth.getEmail() == null || auth.getEmail().equals("")){
	    						authInd.setEmail(author.getEmail());
	    						auth.setEmail(author.getEmail());
	    					}
	    					
	    					if(auth.getSuffix() == null || auth.getSuffix().equals("")){
	    						authInd.setSuffix(author.getSuffix());
	    						auth.setSuffix(author.getSuffix());
	    					}
	    					if(auth.getUrl() == null || auth.getUrl().equals("")){
	    						authInd.setUrl(author.getUrl());
	    						auth.setUrl(author.getUrl());
	    					}
	    					if(auth.getSupValue() == null ) {
	    						authInd.setSupValue(author.getSupValue());
	    						auth.setSupValue(author.getSupValue());
	    					}
	    					authInd.setCorrespAuthor(author.getCorrespAuthor());
	    					auth.setCorrespAuthor(author.getCorrespAuthor());
	    					authorExist = true;
	    					break;
	    				}
	    			}	
	       			
	       			if(authors.size() == 0 ||!authorExist) {  //add new author object if there is no author in list or author does not exist in the list
	       				authors.add(author);
	       				if(fullName!=null&&authInd.getFullName()!=null){
		       				ad.create(authInd);
		       				}
	       			}
    			}
    			else if(authorElement.getName().equals("affiliation")) {
    				String labelValue = "";
    				for(Element affInfo: authorElement.getChildren()) {
    					if(affInfo.getName().equals("label")) {
    						labelValue = affInfo.getValue().trim();
    					}
    					else if(affInfo.getName().equals("affiliation")) {
    						log.debug("labelvalue-->"+labelValue);
    						StringBuilder orgSB= new StringBuilder();
    						@SuppressWarnings("unused")
							String orgAddr = "", orgState = "", orgPostalCode = "", orgCountry = "", orgCity = "";
    						for(Element element: affInfo.getChildren()) {
    							String nodeName = element.getName().trim();
    							String nodeValue = normalCharConvertor.normalCharConversion(removeWhiteSpaces(element.getValue().trim()));
    	    					if(nodeName.equals("organization"))
    	    						orgSB.append(nodeValue+",");
    	    					else{
    	    						log.debug(nodeName+"->"+nodeValue);
    	    						switch(nodeName) {
    	    							case "address-line" : orgAddr = nodeValue; break;
    	    							case "state" : orgState = nodeValue; break;
    	    							case "postal-code" : orgPostalCode = nodeValue; break;
    	    							case "country" : orgCountry = nodeValue; break;
    	    							case "city" :orgCity = nodeValue; break;
    	    						
    	    						}
    	    					}
       					}
    						String orgName = orgSB.toString();
    						
    						log.debug("organization-->"+orgName);
    						for(Author author : authors) {
    							/*
    							 * set each author object's organization value in two cases
    							 * 1. if cur rent affiliation has no labelvalue and author from authorlist has no supvalue and author correspondence value is false
    							 * 2. if there is some supvalue of author that is equal to current affiliation's label value
    							 * 3. there could be multiple sup value associated to a single author, so split it and check with all values
    							 */
    							String [] supValueArray = null;
    							if(author.getSupValue() != null) {
    								if(author.getSupValue().contains(",")) {
    									supValueArray = author.getSupValue().split(",");
    								}
    								else {
    									supValueArray = new String[]{author.getSupValue()};
    								}
    							}
    							boolean matchedSupFlag= false;
    							if(supValueArray != null) {
	    							for(String sup : supValueArray) {
	    								if(labelValue.equalsIgnoreCase(sup)) {   //if author's any supvalue is equal to current affiliation value then matchedSupFlag will be true
	    									matchedSupFlag = true;
	    								}
	    							}
    							}
//    							if((labelValue.equals("") && author.getSupValue() == null && !author.getCorrespAuthor() ) || (author.getSupValue() != null && labelValue.equals(author.getSupValue().trim())) ){  
								if((labelValue.equals("") && supValueArray == null && !author.getCorrespAuthor() ) || matchedSupFlag ){  
    								List<Organization> orgList = author.getOrganization();
    								Organization orgObj = null;
									//log.debug("setting affiliation of author-->"+author.getGiven_name());
									OrganizationDao od = ctx.getBean("organizationDao", OrganizationDao.class);
									orgObj = od.readByName(orgName);
									if(orgObj == null) {
										List<DivisionName> divisionList = new ArrayList<DivisionName>();
										orgObj = new Organization(orgName, divisionList, "", orgAddr, null, null);
										od.create(orgObj);
									}
									boolean orgExist = false;
    								for(Organization org: orgList) {   //if author already has organization list then compare organization
    									if(org.getName().equalsIgnoreCase(orgName)) {
    										orgExist = true;
    										break;
    									}
    								}
    								if(orgList.size() == 0 || !orgExist) {   
    									orgList.add(orgObj);
    								}
    							}
    						}
    					}
    				}
    			}
    			else if(authorElement.getName().equals("correspondence")) {
    				String label = "", address_line ="", city = "", state = "", state_code = "",country = "", country_code = "", postal_code = "",tel="",fax = "";
    				
    				String corresValue = normalCharConvertor.normalCharConversion(removeWhiteSpaces(authorElement.getValue()));
    				log.debug("corrs->"+corresValue);
    				String [] corresArray;
    				if(corresValue.indexOf(";") > -1){
    					corresArray = corresValue.split(";");
    				}
    				else {
    					corresArray = new String[1];
    					corresArray[0] = corresValue;
    				}
    				for(String value : corresArray) {
    					if(value.indexOf("Tel.:") > -1) {
    						tel = value.substring(value.indexOf("Tel.:")+5);
    						log.debug("tel-->"+tel);
    					}
    					else if(value.toLowerCase().indexOf("fax:") > -1) {
    						fax = value.substring(value.toLowerCase().indexOf("fax.:")+5);
    						if(fax.contains("."))
    							fax = fax.substring(0,fax.lastIndexOf("."));
    						log.debug("fax-->"+fax);
    					}
    					else {
    						if(value.indexOf(":") > -1) {
    							value = value.substring(value.indexOf(":")+1);
    						}
    						if(value.indexOf(".") > -1) {
    							value = value.substring(0, value.lastIndexOf("."));
    						}
    						address_line = value;
    						log.debug("address line-->"+address_line);
    					}
    				}
    				Address address = new Address(label, address_line, city, state, state_code, country, country_code, postal_code, tel, fax);
    				for(Author author: authors) {
    					if(author.getCorrespAuthor()) 
    						if(author.getAddress() == null)    //added check in case if artifact object already exist in db
    							author.setAddress(address);
    				}
    			}
    		}
       	for(Author author : authors) {
       		author.setSupValue(null);
       	}
       	ad.create(authInd);
       	return authors;
       }

	public void test() {
		try {
			//parseFile("\\\\172.16.2.128\\FMS_Archive2\\Elsevier\\journal\\ANIFEE\\13207\\Archive\\00018\\tx1.xml");
			//parseFile("d:/fms/tx1.xml");
			
			//parseFile("C:\\Users\\59987\\Desktop\\TOMBK0005566\\9788490228937\\BODY\\B9788490228937000093\\main.xml");
			parseFile("D:\\fms\\Book-Dataset\\Almoallimenglish_s300\\s300\\K0006167\\TOMBK0006167\\9780702062339\\BODY\\B978070206233900008X\\main.xml");
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
public String getInputStreamContent(InputStream inputStream){
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer, "UTF-8");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return writer.toString();
	}
	
	public ArrayList<File> getZipFile(File rootFile, ArrayList<File> zipFiles){
		if(rootFile.isDirectory()){
			File [] fileList = rootFile.listFiles();
			for(File file : fileList){
				if(file.isDirectory()){
					getZipFile(file, zipFiles);
				}
				else if(file.isFile() && file.getName().toLowerCase().endsWith(".zip")){
					zipFiles.add(file);
				}
			}
		}
		return zipFiles;
	}
	
	public void getInputStream(File zip){
		ZipFile zipFile = null;
		try {
			if(zip.exists() && zip.isFile() && zip.getName().toLowerCase().endsWith(".zip")){
				zipFile = new ZipFile(zip);
			    Enumeration<? extends ZipEntry> entries = zipFile.entries();
			    while(entries.hasMoreElements()){
			        ZipEntry entry = entries.nextElement();
			        String entryName = entry.getName();
			        if(entryName.trim().toLowerCase().endsWith(".xml")){
			        	try {
							parseFile(zipFile.getInputStream(entry));
						} catch (JDOMException e) {
							e.printStackTrace();
						}
			        }
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(zipFile != null)
					zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getInputStream(String zipPath){
		
		getInputStream(new File(zipPath));
	}
 
    public static void main(String[] args){
    	
    	XMLExtractionJob.DTD_PATH = "c:/dtd"; //Local
    	//XMLExtractionJob.DTD_PATH = "C:\\MongoDBImport\\tools\\dtd"; //Live
    	XMLExtractionJob xmlExtractorObj = new XMLExtractionJob();
    	String rootPath = "D:\\Datasets\\Medkow"; //Local
    	//String rootPath = "C:\\MongoDBImport\\Datasets"; //Live
    	String processedFilePath = "Processed";
    	ArrayList<File> zipFiles = new ArrayList<File>();
    	zipFiles = xmlExtractorObj.getZipFile(new File(rootPath), zipFiles);
    	
    	DateFormat df = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss");
    
    	Date startDate = new Date();
    	statusLogger.info("Processing started on : "+df.format(startDate));
    	
    		//storecountry();
    		
    
     
			for(File file : zipFiles){
				try {
					if(file.isFile() && file.getName().toLowerCase().endsWith(".zip")){
						try {
							String readyFilePath = file.getParent()+"\\"+FilenameUtils.getBaseName(file.getName())+".ready.xml";
							if(new File(readyFilePath).exists()){
								statusLogger.info("Started file : "+file.getName());
								refCount = 0;
								xmlExtractorObj.getInputStream(file);
								statusLogger.info("Total reference in this dataset is : "+refCount);
								File destination = new File(file.getParent()+"\\"+processedFilePath);
								if(!destination.exists()){
									destination.mkdirs();
								}
								FileUtils.copyFileToDirectory(new File(readyFilePath), destination);
								FileUtils.forceDelete(file);
								FileUtils.forceDelete(new File(readyFilePath));
								statusLogger.info("Finished file : "+file.getName());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		Date endDate = new Date();
		statusLogger.info("Processing finished on : "+df.format(endDate));
		statusLogger.info("-------------------------------------------------------------------------------------------");
		System.out.println("Start Time : "+df.format(startDate)+"----------- End Time : "+df.format(endDate));
    }

	public static void storecountry() {
	
		new Parsecountry(ctx);
		
	}
}