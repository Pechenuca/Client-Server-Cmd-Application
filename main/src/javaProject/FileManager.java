package javaProject;

import com.sun.org.apache.xpath.internal.operations.Or;
import javaProject.exception.EmptyFileException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javaProject.coreSources.Organization;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileManager {

    private final JAXBContext xmlContext;
    private final Marshaller jaxbMarshaller;
    private final Unmarshaller jaxbUnmarshaller;
    private final File xmlOrgs;

    public FileManager() throws JAXBException {
        xmlContext = JAXBContext.newInstance(CollectionMapper.class);
        jaxbMarshaller = xmlContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbUnmarshaller = xmlContext.createUnmarshaller();
        xmlOrgs = null;
    }


    /**
     * Конструктор - создает объект класса FileManager
     * @param dataFilePath - строка, содержащая путь до файла с данными
     * @throws FileNotFoundException В случае если файл нельзя найти.
     */
    public FileManager(String dataFilePath) throws FileNotFoundException, JAXBException {

        if (dataFilePath == null || !(new File(dataFilePath).exists())) {

            throw new FileNotFoundException("There is not such file!");
        } else this.xmlOrgs = new File(dataFilePath);


        xmlContext = JAXBContext.newInstance(CollectionMapper.class);
        jaxbMarshaller = xmlContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbUnmarshaller = xmlContext.createUnmarshaller();
    }

    /**
     * Функция сохранения коллекции в формате xml в файл
     * @param collection - Хэшмэп, содержащий коллекцию экземпляров класса Organization
     * @throws IOException
     * @throws JAXBException
     */
    public void SaveCollectionInXML(HashMap<Integer, Organization> collection) throws IOException, JAXBException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getXmlDragons())))) {
            CollectionMapper orgsMap = new CollectionMapper();
            orgsMap.setCollection(collection);
            jaxbMarshaller.marshal(orgsMap, writer);
        }

    }
    public File assertFileIsUsable(String dataFilePath) throws InvalidPathException, IOException {

        String filePath = Paths.get(dataFilePath).toAbsolutePath().toString();
        File fileToRetrieve = new File(filePath);
        if (!fileToRetrieve.exists())
            throw new FileNotFoundException("There is not such file!");
        else if (fileToRetrieve.length() == 0)
            throw new EmptyFileException("File is empty!");

        if (!fileToRetrieve.canRead() || !fileToRetrieve.canWrite())
            throw new SecurityException();

        return fileToRetrieve;
    }
    /**
     * Функция получения коллекции из файла
     * @return - возвращает collection - Хэшмэп, содержащий коллекцию экземпляров класса Organization
     */
    public HashMap<Integer, Organization> getCollectionFromFile() throws IOException, JAXBException {
        HashMap<Integer, Organization> collection = new HashMap<Integer, Organization>();
        String dataStr = this.getStrFromFile("");

        if (!dataStr.equals("")) {
            StringReader reader = new StringReader(dataStr);
            collection = ((CollectionMapper) jaxbUnmarshaller.unmarshal(reader)).getCollection();
        }
        return collection;
    }
    /**
     * Функция получения коллекции из файла
     * @return - возвращает collection - Хэшмэп, содержащий коллекцию экземпляров класса Dragon
     */
    public HashMap<Integer, Organization> getCollectionFromFile(String filePath) throws IOException, InvalidPathException, JAXBException {
        HashMap<Integer, Organization> collection = new HashMap<Integer, Organization>();
        String dataStr = this.getStrFromFile(filePath);

        if (!dataStr.equals("")) {
            StringReader reader = new StringReader(dataStr);
            collection = ((CollectionMapper) jaxbUnmarshaller.unmarshal(reader)).getCollection();
        }
        return collection;
    }

    /**
     * Функция получения строки из файла
     * @param filePath - путь к файлу с данными
     * @return - возвращает dataStr - строку с данными
     */
    public String getStrFromFile(String filePath) throws IOException {
        File fileToRetrieve;
        if (filePath.equals(""))
            fileToRetrieve = this.getXmlDragons();
        else
            fileToRetrieve = new File(filePath);

        if (!fileToRetrieve.exists())
            throw new FileNotFoundException("There is not such file!");
        else if (fileToRetrieve.length() == 0)
            throw new EmptyFileException("File is empty!");

        if (!fileToRetrieve.canRead() || !fileToRetrieve.canWrite())
            throw new SecurityException();

        String dataStr = "";
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToRetrieve));
             ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            int result;
            while((result = bufferedInputStream.read()) != -1)
                buf.write((byte) result);

            dataStr = buf.toString();
        }

        return dataStr;
    }


    public JAXBContext getJABXmlContext() {
        return xmlContext;
    }
    public File getXmlDragons() {
        return xmlOrgs;
    }

    @XmlRootElement(name="orgs_map")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class CollectionMapper {
        private HashMap<Integer, Organization> organizations = new HashMap<>();

        public HashMap<Integer, Organization> getCollection() {
            return organizations;
        }

        public void setCollection(HashMap<Integer, Organization> collection) {
            this.organizations = collection;
        }
    }
}