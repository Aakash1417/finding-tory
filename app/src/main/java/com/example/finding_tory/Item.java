package com.example.finding_tory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Represents an item with various attributes such as purchase date, description, make, model, serial number,
 * estimated value, comments, and tags. This class is designed to be serializable to facilitate easy storage and retrieval.
 */
public class Item implements Serializable {
    private String id;
    private Date purchaseDate;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private float estimatedValue;
    private String comment;
    private ArrayList<String> itemTags;
    private ArrayList<String> imageLinks;

    /**
     * No-args constructor, required for deserialization from Firestore.
     * Firestore creates instances of data model classes when retrieving data from db
     */
    public Item() {
        throw new IllegalArgumentException();
    }

    /**
     * Constructs an Item with specified attributes.
     *
     * @param purchaseDate   The purchase date of the item.
     * @param description    The description of the item.
     * @param make           The make of the item.
     * @param model          The model of the item.
     * @param estimatedValue The estimated value of the item.
     * @param serialNumber   The serial number of the item.
     * @param comment        A comment about the item.
     * @param itemTags       A list of tags associated with the item.
     */
    public Item(Date purchaseDate, String description, String make, String model, float estimatedValue, String serialNumber,
                String comment, ArrayList<String> itemTags, ArrayList<String> imageLinks) {

        errorHandleItemInput(purchaseDate.toString(), description, String.valueOf(estimatedValue));

        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.imageLinks = imageLinks;
        this.setItemTags(itemTags);
    }

    /**
     * Copy the attributes of another item to this item
     *
     * @param copy the item to be copied
     */
    public void updateItem(Item copy) {
        this.purchaseDate = copy.getPurchaseDate();
        this.description = copy.getDescription();
        this.make = copy.getMake();
        this.model = copy.getModel();
        this.serialNumber = copy.getSerialNumber();
        this.estimatedValue = copy.getEstimatedValue();
        this.comment = copy.getComment();
        this.itemTags = copy.getItemTags();
        this.imageLinks = copy.getImageLinks();
    }

    /**
     * Gets the purchase date of item
     *
     * @return purchase date
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the purchase date of the item
     *
     * @param purchaseDate new purchase date
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Gets the description (name) of the item
     *
     * @return the item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the item
     *
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the make of the item
     *
     * @return the item make
     */
    public String getMake() {
        return make;
    }

    /**
     * Gets the date of the item
     *
     * @return the item purchase date
     */
    public Date getDate() {
        return purchaseDate;
    }

    /**
     * Sets the make of the item
     *
     * @param make new make
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Gets the model of the item
     *
     * @return the item model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the item
     *
     * @param model new model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the serial number of the item
     *
     * @return the item serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the item
     *
     * @param serialNumber new serial number
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the estimated value of the item
     *
     * @return the estimated value
     */
    public float getEstimatedValue() {
        return estimatedValue;
    }

    /**
     * Sets the estimated value of the item
     *
     * @param estimatedValue new estimated value
     */
    public void setEstimatedValue(float estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    /**
     * Gets the the comment made about the item
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment made for item
     *
     * @param comment new comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the tags attached to an item
     *
     * @return list of tags
     */
    public ArrayList<String> getItemTags() {
        return itemTags;
    }

    /**
     * displays tags as a space separated list
     *
     * @return string to display tags
     */
    public String getTagsString() {
        StringBuilder tags_string = new StringBuilder();
        for (String tag : this.itemTags) {
            tags_string.append(tag).append(" ");
        }
        return tags_string.toString();
    }

    /**
     * Sets the tags to a new list of tags
     *
     * @param itemTags new list of tags
     */
    public void setItemTags(ArrayList<String> itemTags) {
        this.itemTags = new ArrayList<>();
        for (String s : itemTags) {
            this.addItemTag(s);
        }
        this.SortItemTag();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sort the tags in alphanumeric order
     */
    public void SortItemTag() {
        Collections.sort(this.itemTags);
    }

    /**
     * Sets the tags to a new list of tags
     *
     * @param itemTags new list of tags
     */
    public void addItemTag(String itemTags) {
        if (!this.itemTags.contains(capitalizeFirstLetter(itemTags))) {
            this.itemTags.add(capitalizeFirstLetter(itemTags));
        }
        SortItemTag();
    }

    /**
     * Capitalizes the first letter of a string and makes the rest lowercase.
     *
     * @param str The string to be processed.
     * @return The processed string with the first letter capitalized and the rest lowercase.
     */
    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public ArrayList<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(ArrayList<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    /**
     * Checks inputs to ensure that the data being entered is valid
     *
     * @param purchaseDate   the date that user inputs
     * @param description    the description users inputs
     * @param estimatedValue the estimated value that user inputs
     * @return a string with error message or empty string (no errors)
     * @throws ParseException
     */
    public static String errorHandleItemInput(String purchaseDate, String description, String estimatedValue) {
        if (description.trim().equals("")) return "Item Description cannot be empty";

        Date current = new Date();
        try {
            if (current.before(new SimpleDateFormat("yyyy-MM-dd").parse(purchaseDate)))
                return "Date cannot be in the future";
        } catch (Exception e) {
            return "";
        }
        if (estimatedValue.trim().equals("")) return "Estimated value cannot be empty";
        if (Float.parseFloat(estimatedValue) <= 0) return "Cannot have a negative Estimated value";

        return "";
    }
}
