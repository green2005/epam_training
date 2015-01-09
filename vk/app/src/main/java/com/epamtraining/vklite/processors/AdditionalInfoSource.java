package com.epamtraining.vklite.processors;
import java.io.InputStream;

public interface AdditionalInfoSource {
   public InputStream getAdditionalInfo(String href) throws Exception;
}
