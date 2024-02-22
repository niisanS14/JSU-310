package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    

    // Column headers used in CSV file and JSON object keys
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        
        // JSON object containers
       
        JsonObject scheduletypeObj = new JsonObject();
        JsonObject courseObj = new JsonObject();
        JsonObject subjectObj = new JsonObject();
        JsonObject jsonCsv = new JsonObject();
        
        JsonArray sectionArray = new JsonArray();     
        
       
           
        // Create an iterator for the list of CSV rows to facilitate, retrieving the column headers from the first row
        Iterator<String[]> iterator = csv.iterator();      
        String[] headerObj = iterator.next();      
        
       
        // Initialize a map using column names as keys and column indices as values
        HashMap<String, Integer> header = new HashMap<>(); 
        
        for(int i = 0; i < headerObj.length; i++)   {
            header.put(headerObj[i], i);   
        }

        
        // Iterate through the rows of the CSV file
        while(iterator.hasNext()){
                   
                headerObj = iterator.next();    
                

                // Retrieve the schedule type for the current record
                String scheduletype = headerObj[header.get(TYPE_COL_HEADER)];
                if (!scheduletypeObj.containsKey(scheduletype)) {     
                    scheduletypeObj.put(scheduletype, headerObj[header.get(SCHEDULE_COL_HEADER)]);    
                }

                // Store course details in a JsonObject
                JsonObject courseMap = new JsonObject(); 
                
                // Splits the course number from the subject id
                String[] courseNum = headerObj[header.get(NUM_COL_HEADER)].split(" "); 
                
                // Adds relevant course details to the courseMap
                courseMap.put(SUBJECTID_COL_HEADER, courseNum[0]);  
                courseMap.put(NUM_COL_HEADER, courseNum[1]);   
                courseMap.put(DESCRIPTION_COL_HEADER, headerObj[header.get(DESCRIPTION_COL_HEADER)]); 
                courseMap.put(CREDITS_COL_HEADER, Integer.valueOf(headerObj[header.get(CREDITS_COL_HEADER)])); 
                
                courseObj.put(headerObj[header.get(NUM_COL_HEADER)], courseMap);
                
                // Check if the subject ID is present if not it adds it to the object
                if(!subjectObj.containsKey(courseNum[0])) {      
                    subjectObj.put(courseNum[0], headerObj[header.get(SUBJECT_COL_HEADER)]);  
                }
                
               
                JsonObject sectionObj = new JsonObject();  
                String[] instructors = headerObj[header.get(INSTRUCTOR_COL_HEADER)].split(", ");     // Split instructor names by comma and add as an array to the section object
                sectionObj.put(INSTRUCTOR_COL_HEADER, instructors);      
                
                
                sectionArray.add(sectionObj);    // Adds the section object to the section array
                sectionObj.put(CRN_COL_HEADER, Integer.parseInt(headerObj[header.get(CRN_COL_HEADER)])); // Parses the CRN as an Integer
                sectionObj.put(SUBJECTID_COL_HEADER, courseNum[0]);       // Adds the subject ID to the section object
                sectionObj.put(NUM_COL_HEADER, courseNum[1]);    // Adds the course number to the section object
                sectionObj.put(SECTION_COL_HEADER, headerObj[header.get(SECTION_COL_HEADER)]);    // Adds the section number to the section object
                sectionObj.put(TYPE_COL_HEADER, headerObj[header.get(TYPE_COL_HEADER)]);     // Adds the type to the section object
                sectionObj.put(START_COL_HEADER, headerObj[header.get(START_COL_HEADER)]);     // Adds the start time to the section object
                sectionObj.put(END_COL_HEADER, headerObj[header.get(END_COL_HEADER)]);     // Adds the end time to the section object
                sectionObj.put(DAYS_COL_HEADER, headerObj[header.get(DAYS_COL_HEADER)]);     // Adds the days to the section object
                sectionObj.put(WHERE_COL_HEADER, headerObj[header.get(WHERE_COL_HEADER)]);     // Adds the location to the section object
                
              
                  
        }
        // Adds the subject, section, and course objects to the jsonCsv object
        jsonCsv.put("subject", subjectObj);   
        jsonCsv.put("section", sectionArray); 
        jsonCsv.put("course", courseObj);      
        jsonCsv.put("scheduletype", scheduletypeObj);   
        
        // Serialize the complete JSON object to a string for output
        return Jsoner.serialize(jsonCsv);
        
    }
    
    public String convertJsonToCsvString(JsonObject json) {
      
        // Extracts the schedule types, course details, and subject information
        JsonObject courseObj = (JsonObject)json.get("course");
        JsonObject scheduletype = (JsonObject)json.get("scheduletype"); 
        JsonObject subjectObj = (JsonObject)json.get(SUBJECT_COL_HEADER); 
        JsonArray courseSection = (JsonArray)json.get(SECTION_COL_HEADER);     
             
        
        // Creates new string writer and csv writer with tab delimeter and no escape character
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n"); 

        
        
        // CSV file headers 
        List<String> header = List.of(CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, 
            SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, 
            START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, 
            WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER);
        
        csvWriter.writeNext(header.toArray(String[]::new));    
        
        
        for(int i = 0; i < courseSection.size(); i++){      // Iterate through each section in the JSON array
            
            
            JsonObject record = (JsonObject) courseSection.get(i); 
            

            // Constructs the unique key to fetch course details from courseObj
            String courseKey = record.get(SUBJECTID_COL_HEADER) + " " + record.get(NUM_COL_HEADER);
            
            // Extracts data from: crn, subject, num, description, section, type, credits, start, end, days, where, schedule, instructor
            String crn = record.get(CRN_COL_HEADER).toString();
            String subject = (String) subjectObj.get(record.get(SUBJECTID_COL_HEADER));
            String num = record.get(SUBJECTID_COL_HEADER) + " " + record.get(NUM_COL_HEADER);
            String section = (String) record.get(SECTION_COL_HEADER);
            String type = (String) record.get(TYPE_COL_HEADER);
            String start = (String) record.get(START_COL_HEADER);
            String end = (String) record.get(END_COL_HEADER);
            String days = (String) record.get(DAYS_COL_HEADER);
            String where = (String) record.get(WHERE_COL_HEADER);
            String schedule = (String)scheduletype.get(record.get(TYPE_COL_HEADER));
            String instructor = (String) String.join(", ", (List) record.get(INSTRUCTOR_COL_HEADER));
            
           
            JsonObject course = (JsonObject) courseObj.get(num);
            String credits = course.get(CREDITS_COL_HEADER).toString();
            String description = (String) course.get(DESCRIPTION_COL_HEADER);
            
            // Constructs the CSV row
            String[] csvRow = new String[]{crn, subject, num, description, section, type, credits, start, end, days, where, schedule, instructor};
            
            csvWriter.writeNext(csvRow);       
        
        }
        
        return writer.toString();    
        
    }

    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n"); // Create a new CSV writer with the tab delimiter
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}