package tmg.labs.studentmanagement.model;

import java.util.Date;

public class Student {
  private int id;
  private String photoPath;
  private String name;
  private String address;
  private String classname;
  private Date birthday;

  public Student() {

  }

  public boolean isEmpty() {
    return name == null || name.equals("");
  }

  public Student(
      String name, Date birthday, String address, String classname) {
    super();
    this.name = name;
    this.birthday = birthday;
    this.address = address;
    this.classname = classname;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getPhotoPath() {
    return photoPath;
  }

  public void setPhotoPath(String path) {
    this.photoPath = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

}
