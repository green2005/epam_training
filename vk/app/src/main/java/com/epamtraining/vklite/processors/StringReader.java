package com.epamtraining.vklite.processors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringReader {
    public String readFromStream(InputStream stream) throws Exception {
        BufferedReader br = null;
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(stream);
            br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                builder.append(s);
            }
            return builder.toString();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } ;
}
