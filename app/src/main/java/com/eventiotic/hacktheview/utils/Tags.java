package com.eventiotic.hacktheview.utils;

public class Tags {
    String name;
    String source;
    String wikidata;
    String wikipedia;
    String ele;

    public String getName() {
        if(this.name !=null && !this.name.isEmpty()) {
            return name;
        } else {
            return "";
        }
    }

    public String getSource() {
        return source;
    }

    public String getWikidata() {
        return wikidata;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public Integer getEle() {
        if(isInteger(this.ele)) {
            return Integer.parseInt(this.ele);
        } else {
            return 0;
        }
    }

    boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }
}
