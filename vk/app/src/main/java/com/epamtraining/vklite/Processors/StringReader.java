package com.epamtraining.vklite.Processors;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringReader {
    public String readFromStream(InputStream stream){
        BufferedReader br = null;
        InputStreamReader  reader = null;
        String result = null;
        try{
            reader = new InputStreamReader(stream);
            br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String s = "";
            try {
                while ((s = br.readLine())!= null ){
                    builder.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }
        finally {
            try{
                if (br != null)
                br.close();
                if (reader != null){
                    reader.close();
                }                
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    };
}
