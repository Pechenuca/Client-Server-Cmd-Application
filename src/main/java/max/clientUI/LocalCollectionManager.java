package max.clientUI;

import max.util.OrganizationEntrySerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocalCollectionManager {
    private final List<OrganizationEntrySerializable> localList;

    public LocalCollectionManager() {
        localList = new ArrayList<>();
    }

    public LocalCollectionManager(List<OrganizationEntrySerializable> list) {
        localList = list;
    }

    public OrganizationEntrySerializable getByID(int id) {
        return  localList.stream()
                .filter(organizationEntry -> organizationEntry.getOrganization().getId() == id)
                .findAny()
                .orElse(null);
    }

    public OrganizationEntrySerializable getByKey(int key) {
        return  localList.stream()
                .filter(organizationEntry -> organizationEntry.getKey() == key)
                .findAny()
                .orElse(null);
    }

    public List<OrganizationEntrySerializable> getLocalList() {
        return localList;
    }


    public long getSumNumFields() {
        return localList.stream()
                .mapToLong(e ->
                        (long) (e.getCoordinates().getX()
                                + e.getCoordinates().getY()
                                + e.getAnnualTurnover()))
                .sum();
    }

    public long getSumNames() {
        return localList.stream()
                .mapToLong(e ->
                        (long) (e.getOrganization().getName().length())
                                + e.getOrganization().getAnnualTurnover()
                                + e.getOrganization().getType().ordinal()
                                + e.getOrganization().getOfficialAddress().ordinal())
                .sum() << 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalCollectionManager that = (LocalCollectionManager) o;
        return this.getSumNumFields() == that.getSumNumFields() &&
                this.getSumNames() == that.getSumNames();
    }

    @Override
    public int hashCode() {
        return Objects.hash(localList);
    }
}