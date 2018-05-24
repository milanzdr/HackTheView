package com.eventiotic.hacktheview.models;

public class OSMNodeTags {
    String name;
    String source;
    String wikidata;
    String wikipedia;
    String ele;
    String natural;
    String place;
    String tourism;
    String waterway;
    String nodeType;
    String access;
    String fee;
    String population;
    String description;

    public String getName() {
        if(this.name !=null && !this.name.isEmpty()) {
            return name;
        } else {
            return "";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setWikidata(String wikidata) {
        this.wikidata = wikidata;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public void setEle(String ele) {
        this.ele = ele;
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public String getWaterway() {
        return waterway;
    }

    public void setWaterway(String waterway) {
        this.waterway = waterway;
        if(waterway!=null) {
            this.setNodeType(waterway);
        }
    }

    public void setPlace(String place) {
        this.place = place;
        if(place!=null) {
            this.setNodeType(place);
        }
    }

    public void setTourism(String tourism) {
        this.tourism = tourism;
        if(tourism!=null) {
            this.setNodeType(tourism);
        }
    }

    public String getTourism() {
        return tourism;
    }

    public void setNatural(String natural) {
        this.natural = natural;
        if(natural!=null) {
            this.setNodeType(natural);
        }
    }

    public String getNatural() {
        return natural;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return source;
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

    public String getAccess() {
        return access;
    }

    public String getFee() {
        return fee;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }
}
