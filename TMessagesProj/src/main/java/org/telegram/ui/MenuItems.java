package org.telegram.ui;

/**
 * Created by craterzone on 28/11/14.
 */
public class MenuItems {

    private String name;
    private int icon;
    private boolean isTextShow;
    private String count;
    private int id;

    public MenuItems(String name, int icon, boolean isTextShow, String count, int id) {
        this.name = name;
        this.icon = icon;
        this.isTextShow = isTextShow;
        this.count = count;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isTextShow() {
        return isTextShow;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTextShow(boolean isTextShow) {
        this.isTextShow = isTextShow;
    }

    public String getCount(){
        return count;
    }

    public void setCount(String count){
        this.count = count;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
