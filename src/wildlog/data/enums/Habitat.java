package wildlog.data.enums;


public enum Habitat {
    SUCCULENT_KAROO("Succulent Karoo", "Succulents (thick fleshy leaves), annuals (spring flowers), bulbs, tubers, etc."),
    NAMA_KAROO("Nama Karoo","Covers most of the central plateau and forms a transition between the Cape flora and the tropical savanna."),
    FYNBOS("Fynbos","Evergreen heathlands and shrublands whith fine-leafed low shrubs and leafless tufted grasslike plants. Trees and grasses are rare."),
    GRASSLAND("Grassland","Grasses dominate the vegetation and woody plants are absent or rare."),
    SAVANNA("Savanna","Wooded grasslands of the tropics and subtropics."),
    THICKET("Thicket","Closed shrubland to low forest dominated by trees, shrubs and vines."),
    FORREST("Forest","Indigenous evergreen and semi-deciduous closed forests of the coastal lowlands and escarpment slopes."),
    WETLAND("Wetland","Inland and coastal habitats (mountain sponges, midland marshes, swamp forests, estuaries). Linked by rivers and streams. High water table, water-carrying soil."),
    COASTAL("Coastal","Sandy beaches, sand dunes and rocky shores."),
    MARINE("Marine","Coral reefs, kelp beds and the open sea."),
    OTHER("Other", "Other"),
    NONE("None", "None");
    
    private String text;
    private String description;
    
    Habitat(String inText, String inDescription) {
        text = inText;
        description = inDescription;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String description() {
        return description;
    }

    public void fix(String inText) {
        text = inText;
    }

    public static Habitat getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(SUCCULENT_KAROO.text)) return SUCCULENT_KAROO;
        if (inText.equalsIgnoreCase(NAMA_KAROO.text)) return NAMA_KAROO;
        if (inText.equalsIgnoreCase(FYNBOS.text)) return FYNBOS;
        if (inText.equalsIgnoreCase(GRASSLAND.text)) return GRASSLAND;
        if (inText.equalsIgnoreCase(SAVANNA.text)) return SAVANNA;
        if (inText.equalsIgnoreCase(THICKET.text)) return THICKET;
        if (inText.equalsIgnoreCase(FORREST.text)) return FORREST;
        if (inText.equalsIgnoreCase(WETLAND.text)) return WETLAND;
        if (inText.equalsIgnoreCase(COASTAL.text)) return COASTAL;
        if (inText.equalsIgnoreCase(MARINE.text)) return MARINE;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
