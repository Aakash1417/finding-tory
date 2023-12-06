package com.example.finding_tory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestAll {
    @Test
    public void testAddAndDisplayItems() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "Laptop", "Dell", "XPS", 1000f, "123ABC", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Phone", "Apple", "iPhone 12", 800f, "456DEF", "Personal phone", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);

        Assert.assertEquals(2, inventory.getItems().size());
        Assert.assertTrue(inventory.getItems().contains(item1));
        Assert.assertTrue(inventory.getItems().contains(item2));
    }

    @Test
    public void testApplyFilterAndSort() throws ParseException {
        Inventory inventory = new Inventory("Test Inventory");
        Calendar calendar = Calendar.getInstance();

        // Set the date to January 1st
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date jan1 = calendar.getTime();

        // Set the date to January 5th
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        Date jan2 = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        Date jan5 = calendar.getTime();

        Item item1 = new Item(jan1, "Laptop", "Dell", "Model1", 1000f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(jan2, "Phone", "Apple", "iPhone 12", 800f, "SN2", "Personal phone", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);

        Filter filter = new Filter(jan1, jan5, "", "", new ArrayList<>());
        Sort sort = new Sort("Date", "Descending");

        inventory.setFilter(filter);
        inventory.setSort(sort);
        inventory.filterItems();

        assertEquals(2, inventory.getDisplayedItems().size());
        assertEquals(item2, inventory.getDisplayedItems().get(0));
    }

    @Test
    public void testUserManagingMultipleInventories() {
        User user = new User("user1", "John Doe", "pass123");
        Inventory inventory1 = new Inventory("Home Inventory");
        Inventory inventory2 = new Inventory("Office Inventory");

        user.addInventory(inventory1);
        user.addInventory(inventory2);

        assertEquals(2, user.getInventories().size());
        assertTrue(user.getInventories().contains(inventory1));
        assertTrue(user.getInventories().contains(inventory2));
    }

    @Test
    public void testLedgerManagingInvalidUser() {
        Ledger ledger = Ledger.getInstance();
        User invalidUser = null;

        ledger.setUser(invalidUser);
        assertNull(ledger.getUser());
    }

    @Test
    public void testRemoveInventoryFromLedger() {
        Ledger ledger = Ledger.getInstance();
        Inventory inventory = new Inventory("Test Inventory");

        ledger.deleteInventory(inventory);

        assertFalse(ledger.getInventories().contains(inventory));
    }

    @Test
    public void testInvalidSortType() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "First", "Samsung", "Galaxy Tab", 500f, "789GHI", "Personal tablet", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Second", "Samsung", "Galaxy Tab S", 600f, "789GHI", "Updated tablet", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        Sort invalidSort = new Sort("InvalidType", "Ascending");

        inventory.setSort(invalidSort);
        inventory.sortItems();
        assertEquals(inventory.getDisplayedItems().get(0).getDescription(), "First");
    }

    @Test
    public void testUpdateItemInInventory() {
        Inventory inventory = new Inventory("Test Inventory");
        Item originalItem = new Item(new Date(), "Tablet", "Samsung", "Galaxy Tab", 500f, "789GHI", "Personal tablet", new ArrayList<>(), new ArrayList<>());
        Item updatedItem = new Item(new Date(), "Tablet", "Samsung", "Galaxy Tab S", 600f, "789GHI", "Updated tablet", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(originalItem);
        inventory.set(0, updatedItem);

        assertEquals("Galaxy Tab S", inventory.get(0).getModel());
        assertEquals(600f, inventory.get(0).getEstimatedValue());
    }

    @Test
    public void testAddEmptyItem() {
        Inventory inventory = new Inventory("Test Inventory");
        // Simulate adding an item with an unsupported data type in one of its fields
        // Assuming 'comment' field is expected to be a String
        Item item = new Item();

        inventory.addItem(item);

        Filter filter1 = new Filter();
        inventory.setFilter(filter1);
        inventory.filterItems();

        assertTrue(inventory.getItems().contains(item));
    }

    @Test
    public void testFilterItemsByDateRange() throws ParseException {
        Inventory inventory = new Inventory("Test Inventory");
        Calendar calendar = Calendar.getInstance();

        // Set the date to January 1st
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date jan1 = calendar.getTime();

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date Dec31 = calendar.getTime();

        Filter dateFilter = new Filter(jan1, Dec31, "", "", new ArrayList<>());

        inventory.setFilter(dateFilter);
        inventory.filterItems();

        // Assuming inventory has items within this date range
        assertTrue(inventory.getDisplayedItems().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLedgerManagingMultipleUsers() {
        Ledger ledger = Ledger.getInstance();
        User user1 = new User("user1", "John Doe", "pass123");
        User user2 = new User("user2", "Jane Doe", "pass456");

        ledger.setUser(user1);
        ledger.setUser(user2); // This should fail as Ledger is a singleton and should not manage multiple users
    }

    @Test
    public void testSortItemsByValue() {
        Inventory inventory = new Inventory("Test Inventory");
        Sort valueSort = new Sort("Value", "Ascending");

        // Adding items to inventory with different estimated values
        Item item1 = new Item(new Date(), "Laptop", "Dell", "Model A", 1200f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Smartphone", "Apple", "iPhone", 800f, "SN2", "Personal phone", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Tablet", "Samsung", "Galaxy Tab", 600f, "SN3", "Personal tablet", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);

        inventory.setSort(valueSort);
        inventory.sortItems();

        // Verify that items are sorted by estimated value in ascending order
        ArrayList<Item> sortedItems = inventory.getDisplayedItems();
        assertTrue(sortedItems.size() == 3);
        assertTrue(sortedItems.get(0).getEstimatedValue() <= sortedItems.get(1).getEstimatedValue());
        assertTrue(sortedItems.get(1).getEstimatedValue() <= sortedItems.get(2).getEstimatedValue());
    }


    // created a test that uses functions outside the class definitions
    @Test
    public void testAddAndRemoveTagsInInventory() {
//        Inventory inventory = new Inventory("Test Inventory");
//        ArrayList<String> tagsToAdd = new ArrayList<>(Arrays.asList("Electronics", "Furniture"));
//        inventory.addTagsToInventory(tagsToAdd);
//
//        assertTrue(inventory.getAllTags().containsAll(tagsToAdd));
//
//        // Remove tag and test
//        String tagToRemove = "Electronics";
//        inventory.removeTagFromInventory(tagToRemove);
//        assertFalse(inventory.getAllTags().contains(tagToRemove));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFilterCriteria() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "Laptop", "Dell", "Model A", 1200f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Smartphone", "Apple", "iPhone", 800f, "SN2", "Personal phone", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Tablet", "Samsung", "Galaxy Tab", 600f, "SN3", "Personal tablet", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);

        Filter invalidFilter = new Filter(null, null, "", "InvalidMake", new ArrayList<>());

        inventory.setFilter(invalidFilter);
        inventory.filterItems();
        assertEquals(inventory.getDisplayedItems().size(), 0);
    }

    @Test
    public void testUpdateUserProfile() {
        User user = new User("user1", "John Doe", "pass123");
        String newName = "Jane Doe";
        String newPassword = "newpass456";

        user.setName(newName);
        user.setPassword(newPassword);

        assertEquals(newName, user.getName());
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    public void testEmptyInventoryHandling() {
        Inventory emptyInventory = new Inventory("Empty Inventory");
        emptyInventory.filterItems();
        emptyInventory.sortItems();

        assertTrue(emptyInventory.getDisplayedItems().isEmpty());
    }

    @Test
    public void testAddDuplicateItem() {
        Inventory inventory = new Inventory("Test Inventory");
        Item duplicateItem = new Item(new Date(), "Laptop", "Dell", "XPS", 1000f, "123ABC", "Work laptop", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(duplicateItem);
        inventory.addItem(duplicateItem);

        assertEquals(2, inventory.getCount()); // Should allow adding duplicates
    }

    @Test
    public void testAddRemoveMultipleItems() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "Laptop", "Dell", "XPS", 1000f, "123ABC", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Phone", "Apple", "iPhone 12", 800f, "456DEF", "Personal phone", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        assertEquals(2, inventory.getItems().size());

        inventory.removeItem(item1);
        inventory.removeItem(item2);
        assertEquals(0, inventory.getItems().size());
    }

    @Test
    public void testUpdateItemDetails() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item = new Item(new Date(), "Laptop", "Dell", "XPS", 1000f, "123ABC", "Work laptop", new ArrayList<>(), new ArrayList<>());
        inventory.addItem(item);

        Item updatedItem = new Item(item.getPurchaseDate(), "Laptop", "Dell", "XPS 15", 1200f, item.getSerialNumber(), "Updated work laptop", new ArrayList<>(), new ArrayList<>());
        inventory.set(0, updatedItem); // Assuming 'set' method replaces the item at the specified index

        assertEquals("XPS 15", inventory.get(0).getModel());
        assertEquals(1200f, inventory.get(0).getEstimatedValue());
    }

    @Test
    public void testFilterByMultipleTags() {
        Inventory inventory = new Inventory("Test Inventory");
        ArrayList<String> tags = new ArrayList<>(Arrays.asList("Electronics", "Work"));
        Filter filter = new Filter(null, null, "", "", tags);

        inventory.setFilter(filter);
        inventory.filterItems(); // Assuming this method updates the displayed items based on the filter

        for (Item item : inventory.getDisplayedItems()) {
            assertTrue(item.getItemTags().containsAll(tags));
        }
    }

    @Test
    public void testInvalidRemoveItem() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "Laptop", "Dell", "Model A", 1200f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Smartphone", "Apple", "iPhone", 800f, "SN2", "Personal phone", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Tablet", "Samsung", "Galaxy Tab", 600f, "SN3", "Personal tablet", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);

        Item nonInventoryItem = new Item(new Date(), "Non-existing item", "Unknown", "ModelX", 0f, "000XYZ", "Non-existing", new ArrayList<>(), new ArrayList<>());

        inventory.removeItem(nonInventoryItem);
        assertEquals(inventory.getDisplayedItems().size(), 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemFutureDate() throws ParseException {
        Inventory inventory = new Inventory("Test Inventory");

        // Preparing data for the future item
        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.DATE, 1); // Setting a date to tomorrow
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String futureDateString = sdf.format(futureDate.getTime());

        // Checking for errors
        String error = Item.errorHandleItemInput(futureDateString, "Future Item", "500");
        if (!error.isEmpty()) {
            throw new IllegalArgumentException(error);
        }

        // If no error, add the item to the inventory
        Item futureItem = new Item(futureDate.getTime(), "Future Item", "FutureBrand", "FutureModel", 500f, "789JKL", "Future item", new ArrayList<>(), new ArrayList<>());
        inventory.addItem(futureItem);
    }


    @Test
    public void testUserDeletingInventory() {
        User user = new User("user1", "John Doe", "pass123");
        Inventory inventory = new Inventory("User's Inventory");
        user.addInventory(inventory);

        assertEquals(1, user.getInventories().size());

        user.getInventories().remove(inventory); // User deletes the inventory
        assertTrue(user.getInventories().isEmpty());
    }

    @Test
    public void testSortByNonExistingField() {
        Inventory inventory = new Inventory("Test Inventory");

        Item item1 = new Item(new Date(), "Laptop", "Dell", "Model A", 1200f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Smartphone", "Apple", "iPhone", 800f, "SN2", "Personal phone", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Tablet", "Samsung", "Galaxy Tab", 600f, "SN3", "Personal tablet", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);

        Sort valuesort = new Sort("Value", "Ascending");
        inventory.setSort(valuesort);
        inventory.sortItems();

        ArrayList<Item> correctItems = inventory.getDisplayedItems();


        Sort invalidSort = new Sort("NonExistingField", "Ascending");
        inventory.setSort(invalidSort);
        inventory.sortItems();

        ArrayList<Item> newItems = inventory.getDisplayedItems();
        assertEquals(correctItems, newItems);
    }

    @Test
    public void testAddDuplicateSerialNumbers() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "Laptop", "Dell", "XPS", 1000f, "123ABC", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Laptop", "Dell", "XPS", 1000f, "123ABC", "Another laptop", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);

        assertEquals(2, inventory.getItems().size());
        assertEquals(item1.getSerialNumber(), item2.getSerialNumber());
    }

    @Test
    public void testInventorySerialization() throws IOException, ClassNotFoundException {
        Inventory inventory = new Inventory("Test Inventory");

        // Adding items to the inventory
        Item item1 = new Item(new Date(), "Laptop", "Dell", "Model A", 1200f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Smartphone", "Apple", "iPhone", 800f, "SN2", "Personal phone", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);

        // Serialize inventory
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(inventory);
        out.flush();
        byte[] inventoryData = bos.toByteArray();

        // Deserialize inventory
        ByteArrayInputStream bis = new ByteArrayInputStream(inventoryData);
        ObjectInputStream in = new ObjectInputStream(bis);
        Inventory deserializedInventory = (Inventory) in.readObject();

        // Compare basic properties
        assertEquals(inventory.getInventoryName(), deserializedInventory.getInventoryName());

        // Compare items
        assertEquals(inventory.getItems().size(), deserializedInventory.getItems().size());
        for (int i = 0; i < inventory.getItems().size(); i++) {
            Item originalItem = inventory.getItems().get(i);
            Item deserializedItem = deserializedInventory.getItems().get(i);

            assertEquals(originalItem.getDescription(), deserializedItem.getDescription());
            assertEquals(originalItem.getMake(), deserializedItem.getMake());
            assertEquals(originalItem.getModel(), deserializedItem.getModel());
            assertEquals(originalItem.getEstimatedValue(), deserializedItem.getEstimatedValue(), 0.01);
            assertEquals(originalItem.getSerialNumber(), deserializedItem.getSerialNumber());
        }
    }


    @Test
    public void testUserProfileUpdate() {
        User user = new User("user1", "John Doe", "pass123");
        Inventory inventory1 = new Inventory("Home Inventory");
        Inventory inventory2 = new Inventory("Office Inventory");

        user.addInventory(inventory1);
        user.addInventory(inventory2);

        // Simulate user profile update
        user.setName("Jane Doe");
        user.setPassword("newpass456");

        // Assert that inventories are still linked to the user after the update
        assertEquals("Jane Doe", user.getName());
        assertEquals(2, user.getInventories().size());
        assertTrue(user.getInventories().contains(inventory1));
        assertTrue(user.getInventories().contains(inventory2));
    }

    @Test
    public void testLedgerOnUserLogout() {
        Ledger ledger = Ledger.getInstance();
        User user = new User("user1", "John Doe", "pass123");
        ledger.setUser(user);
        assertEquals(user, ledger.getUser());

        // Simulate user logout
        ledger.setUser(null);

        assertNull(ledger.getUser());
    }

    @Test
    public void testComplexFilterSort() {
        Inventory inventory = new Inventory("Test Inventory");

        // Adding items to the inventory
        Item item1 = new Item(new Date(), "Dell Laptop", "Dell", "Model D1", 1500f, "SN1", "High-end laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Apple iPhone", "Apple", "Model A1", 1000f, "SN2", "Smartphone", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Desk", "IKEA", "Model D2", 200f, "SN3", "Office desk", new ArrayList<>(), new ArrayList<>());
        Item item4 = new Item(new Date(), "Dining Table", "IKEA", "Model D3", 700f, "SN4", "Wooden dining table", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);
        inventory.addItem(item4);

        // Filter for descriptions starting with 'D' and sort by value in descending order
        Filter complexFilter = new Filter(null, null, "D", "", new ArrayList<>());
        Sort complexSort = new Sort("Value", "Descending");

        inventory.setFilter(complexFilter);
        inventory.setSort(complexSort);
        inventory.filterItems();
        inventory.sortItems();

        // Asserting the first item should be the most valuable item with description starting with 'D'
        Item firstItem = inventory.getDisplayedItems().get(0);
        assertTrue(firstItem.getDescription().startsWith("D"));
        assertTrue(firstItem.getEstimatedValue() >= item4.getEstimatedValue()); // Assuming item4 is the next most valuable item starting with 'D'
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAddItemInvalidValue() {
        Inventory inventory = new Inventory("Test Inventory");
        Item invalidItem = new Item(new Date(), "Item", "Brand", "Model", -100f, "SN123", "Invalid value item", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(invalidItem);
    }

    @Test
    public void testUserMultipleInventoriesDifferentFilters() {
        User user = new User("user1", "John Doe", "pass123");
        Inventory inventory1 = new Inventory("Home Inventory");
        Inventory inventory2 = new Inventory("Work Inventory");

        // Adding items to inventory1
        Item homeItem1 = new Item(new Date(), "Television", "Samsung", "ModelX", 500f, "SNHome1", "Home Electronics", new ArrayList<>(), new ArrayList<>());
        Item homeItem2 = new Item(new Date(), "Sofa", "Ikea", "ModelY", 300f, "SNHome2", "Home Furniture", new ArrayList<>(), new ArrayList<>());
        inventory1.addItem(homeItem1);
        inventory1.addItem(homeItem2);

        // Adding items to inventory2
        Item workItem1 = new Item(new Date(), "Laptop", "Dell", "Latitude", 1200f, "SNWork1", "Work Electronics", new ArrayList<>(), new ArrayList<>());
        Item workItem2 = new Item(new Date(), "Office Chair", "Herman Miller", "Aeron", 1000f, "SNWork2", "Office Furniture", new ArrayList<>(), new ArrayList<>());
        inventory2.addItem(workItem1);
        inventory2.addItem(workItem2);

        user.addInventory(inventory1);
        user.addInventory(inventory2);

        // Setting up filters
        Filter homeFilter = new Filter(null, null, "Home", "", new ArrayList<>()); // Filter for items with "Home" in the description
        Filter workFilter = new Filter(null, null, "Work", "", new ArrayList<>()); // Filter for items with "Work" in the description

        inventory1.setFilter(homeFilter);
        inventory2.setFilter(workFilter);

        inventory1.filterItems();
        inventory2.filterItems();

        // Assertions for inventory1 and inventory2 matching their respective filters
        assertTrue(inventory1.getDisplayedItems().stream().allMatch(item -> item.getDescription().contains("Home")));
        assertTrue(inventory2.getDisplayedItems().stream().allMatch(item -> item.getDescription().contains("Work")));
    }

    @Test
    public void testSortOrderUpdate() {
        Inventory inventory = new Inventory("Test Inventory");

        // Adding items to the inventory
        Item item1 = new Item(new Date(), "Apple iPhone", "Apple", "Model A1", 1000f, "SN2", "Smartphone", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Dell Laptop", "Dell", "Model D1", 1500f, "SN1", "High-end laptop", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Bose Headphones", "Bose", "Model B1", 300f, "SN3", "Noise-cancelling headphones", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);

        // Set sort order to ascending by description
        Sort sort = new Sort("Description", "Ascending");
        inventory.setSort(sort);
        inventory.sortItems();

        // Change sort order to descending by description
        sort = new Sort("Description", "Descending");
        inventory.setSort(sort);
        inventory.sortItems();

        // Check if the items are sorted in descending order by description
        List<Item> displayedItems = inventory.getDisplayedItems();
        assertTrue(displayedItems.size() >= 2);
        assertTrue(displayedItems.get(0).getDescription().compareTo(displayedItems.get(1).getDescription()) > 0);
        assertTrue(displayedItems.get(1).getDescription().compareTo(displayedItems.get(2).getDescription()) > 0);
    }

    @Test
    public void testFilterBySpecialCharDescription() {
        Inventory inventory = new Inventory("Test Inventory");

        // Adding items to the inventory, one of which includes special characters in its description
        Item item1 = new Item(new Date(), "Item #1", "Brand1", "Model1", 100f, "SN1", "First item", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Special#%Item", "Brand2", "Model2", 200f, "SN2", "Second item with special characters", new ArrayList<>(), new ArrayList<>());
        Item item3 = new Item(new Date(), "Item 3", "Brand3", "Model3", 300f, "SN3", "Third item", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2);
        inventory.addItem(item3);

        // Filter for descriptions containing '#%'
        Filter filter = new Filter(null, null, "#%", "", new ArrayList<>());
        inventory.setFilter(filter);
        inventory.filterItems();

        // Assert that the displayed items match the filter criteria
        assertTrue(inventory.getDisplayedItems().stream().anyMatch(item -> item.getDescription().contains("#%")));
    }

    @Test
    public void testCrossInventoryItemTransfer() {
        Inventory inventory1 = new Inventory("Inventory 1");
        Inventory inventory2 = new Inventory("Inventory 2");
        Item item = new Item(new Date(), "Transferable Item", "Brand", "Model", 100f, "SN123", "Transfer item", new ArrayList<>(), new ArrayList<>());

        inventory1.addItem(item);
        inventory2.addItem(item);
        inventory1.removeItem(item);

        assertEquals(0, inventory1.getItems().size());
        assertEquals(1, inventory2.getItems().size());
    }

    @Test
    public void testUserSerialization() throws IOException, ClassNotFoundException {
        User user = new User("user123", "John Doe", "password123");
        Inventory inventory = new Inventory("Test Inventory");
        user.addInventory(inventory);

        // Serialize User
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(user);
        out.flush();
        byte[] userData = bos.toByteArray();

        // Deserialize User
        ByteArrayInputStream bis = new ByteArrayInputStream(userData);
        ObjectInputStream in = new ObjectInputStream(bis);
        User deserializedUser = (User) in.readObject();

        assertEquals(user.getUsername(), deserializedUser.getUsername());
        assertEquals(user.getInventories().size(), deserializedUser.getInventories().size());
    }

    @Test
    public void testUpdateItemTags() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item = new Item(new Date(), "Laptop", "Dell", "Model", 1000f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        inventory.addItem(item);

        // Update tags
        ArrayList<String> newTags = new ArrayList<>(Arrays.asList("Electronics", "Office"));
        item.setItemTags(newTags);

        // Assert tags are updated in the inventory
        assertEquals(newTags, inventory.getItems().get(0).getItemTags());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemWithDuplicateSerialNumber() {
        Inventory inventory = new Inventory("Test Inventory");
        Item item1 = new Item(new Date(), "Laptop", "Dell", "Model A", 1000f, "SN1", "Work laptop", new ArrayList<>(), new ArrayList<>());
        Item item2 = new Item(new Date(), "Smartphone", "Apple", "iPhone", 800f, "SN1", "Personal phone", new ArrayList<>(), new ArrayList<>());

        inventory.addItem(item1);
        inventory.addItem(item2); // This should throw an exception as the serial number "SN1" already exists
    }

    @Test
    public void testClearFilterAndSort() {
        Inventory inventory = new Inventory("Test Inventory");
        Filter filter = new Filter(new Date(), new Date(), "Description", "Make", new ArrayList<>());
        Sort sort = new Sort("Date", "Descending");

        inventory.setFilter(filter);
        inventory.setSort(sort);
        inventory.filterItems();
        inventory.sortItems();

        // Clear filter and sort
        inventory.setFilter(new Filter());
        inventory.setSort(new Sort());
        inventory.filterItems();
        inventory.sortItems();

        assertEquals(inventory.getItems().size(), inventory.getDisplayedItems().size());
    }

}
