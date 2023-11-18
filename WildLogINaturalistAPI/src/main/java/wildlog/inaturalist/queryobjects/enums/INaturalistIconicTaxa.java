package wildlog.inaturalist.queryobjects.enums;

/**
 * Used to for [GET/observations] to filter on iconic taxa. Can be used multiple times. <br/>
 * Example in URL: ?iconic_taxa[]=Fungi&iconic_taxa[]=Mammalia
 */
public enum INaturalistIconicTaxa {
    Plantae,
    Animalia,
    Mollusca,
    Reptilia,
    Aves,
    Amphibia,
    Actinopterygii,
    Mammalia,
    Insecta,
    Arachnida,
    Fungi,
    Protozoa,
    Chromista ,
    unknown;
}
