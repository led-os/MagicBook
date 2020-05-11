package com.key.magicbook.document;

/**
 * created by key  on 2020/5/11
 */
public class ParseDocumentCreator {

    public static  final  String STATION_TOP = "https://www.dingdiann.com/";
    public static  final  String STATION_TOP_TWO= "https://www.booktxt.net/";
    public static  ParseDocument getParseDocument(String baseUrl){
        switch (baseUrl){
            case STATION_TOP:
                return new TopPointParseDocumentCreator().createParseDocument();
            case STATION_TOP_TWO:
                return new TopPointTwoParseDocumentCreator().createParseDocument();
            default:
                return null;
        }
    }




    public static class TopPointParseDocumentCreator extends ParseDocumentFactory{

        @Override
        public ParseDocument createParseDocument() {
            return  new TopPointParseDocument();
        }
    }



    public static class TopPointTwoParseDocumentCreator extends ParseDocumentFactory{

        @Override
        public ParseDocument createParseDocument() {
            return  new TopPointTwoParesDocument();
        }
    }
}
