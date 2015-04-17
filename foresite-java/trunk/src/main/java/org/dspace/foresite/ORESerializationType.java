package org.dspace.foresite;

public enum ORESerializationType {
    RDFXML,
    RDFXMLABBR,
    NTRIPLE,
    N3,
    TURTLE,
    RDFA,
    ATOM1;

    public String getType() {
        switch (this) {
            case RDFXML:
                return "RDF/XML";
            case ATOM1:
                return "ATOM-1.0";
            case RDFXMLABBR:
                return "RDF/XML-ABBREV";
            case N3:
                return "N3";
            case NTRIPLE:
                return "N-TRIPLE";
            case RDFA:
                return "RDFa";
            case TURTLE:
                return "TURTLE";
        }
        return null;
    }

}
