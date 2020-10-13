package max.util;

import max.coreSources.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class OrganizationEntrySerializable implements Serializable {
    private static final long serialVersionUID = -770990256082901507L;
    private final int key;
    private final Organization organization;

    public OrganizationEntrySerializable(int key, Organization organization) {
        this.key = key;
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    public int getKey() {
        return key;
    }

    /*TO USE IN THE TABLEVIEW */
    public Integer getId() {
        return organization.getId();
    }

    public String getName() {
        return organization.getName();
    }

    public Coordinates getCoordinates() {
        return organization.getCoordinates();
    }

    public ZonedDateTime getCreationDate() {
        return organization.getCreationDate();
    }

    public Long getAnnualTurnover() {
        return organization.getAnnualTurnover();
    }

    public Address getOfficialAddress() {
        return organization.getOfficialAddress();
    }

    public OrganizationType getType() {
        return organization.getType();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OrganizationEntrySerializable)) return false;
        if (obj == this) return true;
        OrganizationEntrySerializable objOrganization = (OrganizationEntrySerializable) obj;
        return this.getKey() == objOrganization.getKey() && this.getOrganization().equals(objOrganization.getOrganization());
    }

    public Color getColor() {
        return organization.getColor();
    }
}