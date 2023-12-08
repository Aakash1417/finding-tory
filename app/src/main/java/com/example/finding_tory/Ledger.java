package com.example.finding_tory;

import java.util.ArrayList;

public class Ledger {
    private static final Ledger instance = new Ledger(new ArrayList<Inventory>(), new User());
    private static String GLOBAL_NAME;
    private static String GLOBAL_USERNAME;
    private ArrayList<Inventory> inventories;
    private User user;

    private Ledger(ArrayList<Inventory> inventories, User user) {
        this.inventories = inventories;
        this.user = user;
    }

    public static Ledger getInstance() {
        return instance;
    }

    public ArrayList<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<Inventory> inventories) {
        this.inventories = inventories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (this.user != null) {
            throw new UnsupportedOperationException();
        } else {
            this.user = user;
        }
    }

    public void setUserNames(String name, String usrname) {
        GLOBAL_NAME = name;
        GLOBAL_USERNAME = usrname;
    }

    public String getUsrIDName() {
        return GLOBAL_USERNAME;
    }

    public String getUsrName() {
        return GLOBAL_NAME;
    }

    public void deleteInventory(Inventory inventory) {
        inventories.remove(inventory);
    }
}
