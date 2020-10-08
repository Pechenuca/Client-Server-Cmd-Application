package max.managers;

import max.coreSources.Organization;
import max.util.ListEntrySerializable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CollectionManager {
    private Integer nextIDToAdd = 1;
    private HashMap<Integer, Organization> collection;
    private Date collectionCreationDate;
    private final Lock mainLock;

    /**
     * Конструктор - создает объект класса CollectionManager для работы с коллекцией, создает непустую коллекцию с его датой создания и следующим свободным номером
     * @param collection - Хэшмэп, представляющая коллекцию экземпляров класса Dragon
     */
    public CollectionManager(HashMap<Integer, Organization> collection) {
        this.collection = collection;
        this.collectionCreationDate = new Date();
        this.mainLock = new ReentrantLock();
    }

    public void clear() {
        this.getCollection().clear();
    }

    /**
     * Функция сортировки коллекции по ключу
     *
     * @return возвращает коллекцию
     */
    public List<ListEntrySerializable> sortByKey() {
        return this.getCollection()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Функция сортировки коллекции
     *
     * @return возвращает отсортированную по ID {@link Organization #id} коллекцию
     */
    public List<ListEntrySerializable> sortById() {

        return this.getCollection()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(x -> x.getValue().getId()))
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Функция сортировки коллекции
     *
     * @return возвращает отсортированную по имени {@link Organization #name} коллекцию
     */
    public List<ListEntrySerializable> sortByName() {

        return this.getCollection()
                .entrySet()
                .stream()
                .sorted((x, y) -> x.getValue().getName().compareTo(y.getValue().getName()))
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Функция сортировки коллекции
     *
     * @return возвращает отсортированную по дате создания элемента коллекцию
     */
    public List<ListEntrySerializable> sortByCreationDate() {

        return this.getCollection()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Функция изменения коллекции
     *
     * @param key             - ключ, представляющий экземпляр класса Organization внутри коллекции
     * @param newOrganization - экземпляр класса Organization
     * @return возвращает коллекцию с добавлением нового элемента
     */
    public Object insert(Integer key, Organization newOrganization) {
        newOrganization.setId(nextIDToAdd);
        nextIDToAdd += 1;

        return this.getCollection().putIfAbsent(key, newOrganization);
    }

    /**
     * Функция изменения коллекции
     *
     * @param id              - номер обновляемого экземпляра класса Organization
     * @param newOrganization - новый экземпляр класса Organization с номером id
     * @return возвращает коллекцию с измененным элементом
     */
    public Object update(Integer id, Organization newOrganization)
    {
        Optional<Map.Entry<Integer, Organization>> oldOrganizationKey =
                this.getCollection()
                        .entrySet()
                        .stream()
                        .filter(orgEntry -> orgEntry.getValue().getId().equals(id))
                        .findFirst();

        if (oldOrganizationKey.isPresent())
            newOrganization.setId(id);

        return oldOrganizationKey.map(integerOrgEntry ->
                this.getCollection().replace(integerOrgEntry.getKey(), newOrganization)).orElse(null);
    }

    /**
     * Функция изменения коллекции - удаление элемента по ключу
     *      *
     * @param key - ключ, представляющий экземпляр класса Organization внутри коллекции
     * @return возвращает измененную коллекцию
     */
    public Object removeKey(Integer key) {
        return this.getCollection().remove(key);
    }

    /**
     * Функция изменения коллекции - изменение элемента коллекции в случае если ключ экземпляра меньше заданного
     *
     * @param key             - ключ, представляющий экземпляр класса Organization внутри коллекции
     * @param newOrganization - экземпляр класса Organization
     * @return возвращает измененную коллекцию
     */
    public Object replaceIfLower(Integer key, Organization newOrganization) {
        if (!this.getCollection().containsKey(key))
            return null;

        if (newOrganization.compareTo(this.getCollection().get(key)) > 0) {
            newOrganization.setId(nextIDToAdd);
            nextIDToAdd += 1;
            return this.getCollection().replace(key, newOrganization);
        }
        return null;
    }

    /**
     * Функция изменения коллекции - удаление элементов коллекции, ключ которых больше заданного
     *
     * @param key - ключ, представляющий экземпляр класса Organization внутри коллекции
     * @return возвращает измененную коллекцию
     */
    public void removeGreaterKey(Integer key) {
        this.getCollection()
                .entrySet()
                .removeIf(orgEntry -> orgEntry.getKey() > key);
    }

    /**
     * Функция изменения коллекции - удаление элементов коллекции , ключ которых меньше заданного
     *
     * @param key - ключ, представляющий экземпляр класса Organization внутри коллекции
     * @return возвращает измененную коллекцию
     */
    public void removeLowerKey(Integer key) {
        this.getCollection()
                .entrySet()
                .removeIf(organizationEntry -> key > organizationEntry.getKey());
    }


    /**
     * Функция фильтрации коллекции -  поиск элементов, с именем name, содержащим данную подстроку
     *
     * @param name - строка для поиска экземпляров класса Organization по имени
     * @return возвращает измененную коллекцию
     */
    public List<ListEntrySerializable> filterContainsName(String name) {
        return this.getCollection()
                .entrySet()
                .stream()
                .filter(organization -> organization.getValue().getName().contains(name))
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Функция фильтрации коллекции - поиск элементов, с именем name, начинающимся с данной подстроки
     *
     * @return возвращает измененную коллекцию
     */

    public List<ListEntrySerializable> getSerializableList() {
        return this.getCollection()
                .entrySet()
                .stream()
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Функция получения коллекции
     *
     * @return возвращает коллекцию
     */
    public HashMap<Integer, Organization> getCollection() {
        return this.collection;
    }

    /**
     * Функция получения значения поля
     *
     * @return возвращает дату инициализации коллекции
     */
    public Date getColCreationDate() {
        return collectionCreationDate;
    }

    /**
     * Функция получения значения хэшкода экземпляров класса
     *
     * @return возвращает хэшкод
     */
    @Override
    public int hashCode() {
        int result = 25;
        result += this.getColCreationDate().hashCode();
        result >>= 4;
        result += (this.getCollection().hashCode());
        return result;
    }


    /**
     * Функция получения информации о коллекции
     *
     * @return возвращает строку с информацией о коллекции
     */
    @Override
    public String toString() {
        return "Type of Collection: " + this.getCollection().getClass() +
                "\nCreation Date: " + collectionCreationDate.toString() +
                "\nAmount of elements: " + this.getCollection().size();
    }

    public List<ListEntrySerializable> filterStartsWithName(String name) {
        String regex = "^(" + name + ").*$";
        return this.getCollection()
                .entrySet()
                .stream()
                .filter(organization -> organization.getValue().getName().matches(regex))
                .map(e -> new ListEntrySerializable(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
    /*
     * Функция сравнения экземпляров класса
     * @return возвращает ЛОЖЬ, если экземпляры не равны, и ПРАВДА, если экземпляры равны
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CollectionManager)) return false;
        if (obj == this) return true;
        CollectionManager objCManager = (CollectionManager) obj;
        return this.getCollection().equals(objCManager.getCollection()) &&
                this.getColCreationDate().equals(objCManager.getColCreationDate());
    }

    /**
     * Функция изменения коллекции - удаление элементов коллекции, ключ которых больше заданного
     * @param keys - ключи, представляющий экземпляр класса Dragon внутри коллекции
     */
    public void removeOnKey(int[] keys)
    {
        mainLock.lock();
        try {
            for (int key: keys)
                this.getCollection().remove(key);
        } finally {
            mainLock.unlock();
        }
    }

}