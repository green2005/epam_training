package com.epam.training.image;


public interface Displayer<ProcessingResult, DestinationView> {
    void displayResult(ProcessingResult processingResult, DestinationView view);
    String getUrl(DestinationView destinationView);
    void setUrl(String url, DestinationView destinationView);
    int getSize(ProcessingResult processingResult);
}
