package javaProject.coreSources;



import javaProject.exception.FieldException;
import javaProject.util.ZonedDateTimeSerializer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@XmlRootElement(name = "organization")
@XmlAccessorType(XmlAccessType.FIELD)
    public class Organization implements Comparable<Organization>, Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    @XmlJavaTypeAdapter(value = ZonedDateTimeSerializer.class)
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private String fullName; //Поле может быть null
    private OrganizationType type; //Поле не может быть null
    private Address officialAddress; //Поле не может быть null

    public Organization(int id, String name, Long x, Float y, Coordinates coordinates, ZonedDateTime creationDate, Long annualTurnover, String fullName, OrganizationType type, Address officialAddress) throws JAXBException {
        this.id = id;
        this.name = name;
        this.coordinates = new Coordinates(x, y);
        this.creationDate = ZonedDateTime.now();
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.type = type;
        this.officialAddress = new Address();
    }
    public Organization() {
        this.setCreationDate();
    }

    public Organization(String name, Coordinates coordinates, String fullName, Long annualTurnover, Address address, OrganizationType oType) {
        this.name = name;
        this.creationDate = ZonedDateTime.now();
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.officialAddress = new Address();
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate() {
        this.creationDate = ZonedDateTime.now();
    }
    public String getFormattedCDate() {
        return this.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss.SSS-z"));
    }
    public Long getAnnualTurnover() {
        return annualTurnover;
    }

    public void setAnnualTurnover(Long annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public Address getOfficialAddress() {
        return officialAddress;
    }

    public void setOfficialAddress(Address officialAddress) {
        this.officialAddress = officialAddress;
    }

    @Override
    public int compareTo(Organization organization) {
        long difference = this.getCreationDate().compareTo(organization.getCreationDate());

        if(difference > 0) return 1;
        else if(difference < 0) return -1;
        else return 0;
    }

    @Override
    public String toString() {
        return "javaProject.coreSources.Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", annualTurnover=" + annualTurnover +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                ", officialAddress=" + officialAddress +
                '}';
    }


    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result += (this.getId()) << 2;
        result += this.getCoordinates().hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Organization)) return false;
        if (obj == this) return true;
        Organization objOrg = (Organization) obj;
        return this.getId().equals(objOrg.getId()) &&
                this.getName().equals(objOrg.getName()) &&
                this.getCoordinates().equals(objOrg.getCoordinates()) &&
                this.getType().equals(objOrg.getType());

    }
}


