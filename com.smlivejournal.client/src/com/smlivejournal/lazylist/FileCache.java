package com.smlivejournal.lazylist;

import java.io.File;

import android.app.Application;
import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {	
       // 	android.os.Environment.
      //  getFilesDir()
          String cachePath =  android.os.Environment.getExternalStorageDirectory()+
		        "/Android/data/" + context.getApplicationInfo().packageName + "/cache/";
        	
        	cacheDir=new File(cachePath,
            		"files");
        }else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
     }
    
    public String getCachePath(){
    	return cacheDir.getAbsolutePath();
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}