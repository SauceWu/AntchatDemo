package me.example.sauce.anychattest;

/**
 * Version ${versionName}
 * Created by sauce on 16/10/19.
 */

public class IdCardBean {

    /**
     * address : 山东省莱西市院上镇栾家会村176号
     * birthday : 1991年11月9日
     * id_number : 370285199111092913
     * name : 栾永胜
     * people :
     * sex : 男
     * type : 第二代身份证
     */

    private String address;
    private String birthday;
    private String id_number;
    private String name;
    private String people;
    private String sex;
    private String type;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IdCardBean{" +
                "address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                ", id_number='" + id_number + '\'' +
                ", name='" + name + '\'' +
                ", people='" + people + '\'' +
                ", sex='" + sex + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
