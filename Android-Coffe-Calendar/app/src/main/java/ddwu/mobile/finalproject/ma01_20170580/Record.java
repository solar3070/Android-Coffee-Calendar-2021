package ddwu.mobile.finalproject.ma01_20170580;

import java.io.Serializable;

public class Record implements Serializable {

    private Long _id;
    private String date;
    private String cafe;
    private String address;
    private String menu;
    private String memo;
    private String path;

    public Record() { }

    public Record(Long _id, String date, String cafe, String area, String menu, String memo, String photoPath) {
        this._id = _id;
        this.date = date;
        this.cafe = cafe;
        this.address = area;
        this.menu = menu;
        this.memo = memo;
        this.path = photoPath;
    }

    public Long get_id() {
        return _id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getCafe() {
        return cafe;
    }
    public void setCafe(String cafe) {
        this.cafe = cafe;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getMenu() {
        return menu;
    }
    public void setMenu(String menu) {
        this.menu = menu;
    }
    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
