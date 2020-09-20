package javaProject.util;

import javaProject.coreSources.Organization;
import java.io.Serializable;

/**
 * Class for sending back the entries of the map serialized
 * */
public class ListEntrySerializable implements Serializable {
    private static final long serialVersionUID = -770990256082901507L;
    private int key;
    private Organization organization;
    public ListEntrySerializable(int key, Organization organization) {
        this.key = key;
        this.organization = organization;
    }
    public Organization getOrganization() {
        return organization;
    }

    public int getKey() {
        return key;
    }
}