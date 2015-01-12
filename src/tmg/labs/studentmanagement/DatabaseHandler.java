package tmg.labs.studentmanagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tmg.labs.studentmanagement.model.Student;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
  protected SimpleDateFormat mDateFormat = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US);

  public static final String DATABASE_NAME = "student.db";
  public static final String STUDENTS_TABLE_NAME = "students";
  public static final String STUDENTS_COLUMN_ID = "id";
  public static final String STUDENTS_COLUMN_AVATAR_PATH = "avatar";
  public static final String STUDENTS_COLUMN_NAME = "name";
  public static final String STUDENTS_COLUMN_BIRTHDAY = "birthday";
  public static final String STUDENTS_COLUMN_ADDRESS = "address";
  public static final String STUDENTS_COLUMN_CLASSNAME = "classname";

  public DatabaseHandler(
      Context context) {
    super(context, DATABASE_NAME, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table "
      +
      STUDENTS_TABLE_NAME
      +
      "(id integer primary key, name text, avatar text, birthday text, address text, classname text)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
    onCreate(db);
  }

  public boolean insertStudent(Student student) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      ContentValues contentValues = new ContentValues();
      contentValues.put(STUDENTS_COLUMN_NAME, student.getName());
      contentValues.put(STUDENTS_COLUMN_AVATAR_PATH,
          student.getPhotoPath());
      contentValues.put(STUDENTS_COLUMN_BIRTHDAY,
          mDateFormat.format(student.getBirthday()));
      contentValues.put(STUDENTS_COLUMN_ADDRESS, student.getAddress());
      contentValues
                   .put(STUDENTS_COLUMN_CLASSNAME, student.getClassname());

      db.insert(STUDENTS_TABLE_NAME, null, contentValues);

      return true;
    }
    finally {
      db.close();
    }

  }

  public List<Student> getAllStudents() {
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery("select * from " + STUDENTS_TABLE_NAME,
        null);
    try {
      return toStudentList(cursor);
    }
    finally {
      cursor.close();
      db.close();
    }
  }

  public Student getStudent(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery("select * from " + STUDENTS_TABLE_NAME
      + " where id=" + id, null);
    try {
      List<Student> studentList = toStudentList(cursor);
      return studentList.isEmpty() ? null : studentList.get(0);
    }
    finally {
      cursor.close();
      db.close();
    }
  }

  public int numberOfRows() {
    SQLiteDatabase db = this.getReadableDatabase();
    try {
      return (int) DatabaseUtils.queryNumEntries(db, STUDENTS_TABLE_NAME);
    }
    finally {
      db.close();
    }
  }

  public boolean updateStudent(Student student) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      ContentValues contentValues = new ContentValues();
      contentValues.put(STUDENTS_COLUMN_NAME, student.getName());
      contentValues.put(STUDENTS_COLUMN_AVATAR_PATH,
          student.getPhotoPath());
      contentValues.put(STUDENTS_COLUMN_BIRTHDAY,
          mDateFormat.format(student.getBirthday()));
      contentValues.put(STUDENTS_COLUMN_ADDRESS, student.getAddress());
      contentValues
                   .put(STUDENTS_COLUMN_CLASSNAME, student.getClassname());

      db.update(STUDENTS_TABLE_NAME, contentValues, "id = ? ",
          new String[] {Integer.toString(student.getId())});
      return true;
    }
    finally {
      db.close();
    }
  }

  public Integer deleteStudent(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      return db.delete(STUDENTS_TABLE_NAME, "id = ? ",
          new String[] {Integer.toString(id)});
    }
    finally {
      db.close();
    }
  }

  protected List<Student> toStudentList(Cursor cursor) {
    List<Student> studentList = new ArrayList<Student>();
    try {
      if (cursor != null && cursor.moveToFirst()) {

        final int idxId =
            cursor.getColumnIndex(DatabaseHandler.STUDENTS_COLUMN_ID);
        final int idxName =
            cursor.getColumnIndex(DatabaseHandler.STUDENTS_COLUMN_NAME);
        final int idxAvatarPath =
            cursor.getColumnIndex(DatabaseHandler.STUDENTS_COLUMN_AVATAR_PATH);
        final int idxBirthday =
            cursor.getColumnIndex(DatabaseHandler.STUDENTS_COLUMN_BIRTHDAY);
        final int idxAddress =
            cursor.getColumnIndex(DatabaseHandler.STUDENTS_COLUMN_ADDRESS);
        final int idxClassname =
            cursor.getColumnIndex(DatabaseHandler.STUDENTS_COLUMN_CLASSNAME);

        do {
          final Student student = new Student();

          student.setId(cursor.getInt(idxId));
          student.setName(cursor.getString(idxName));
          student.setPhotoPath(cursor.getString(idxAvatarPath));

          Calendar birthday = Calendar.getInstance();
          birthday.setTime(mDateFormat.parse(cursor.getString(idxBirthday)));

          student.setBirthday(birthday);

          student.setAddress(cursor.getString(idxAddress));

          student.setClassname(cursor.getString(idxClassname));

          studentList.add(student);

        }
        while (cursor.moveToNext());
      }
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return studentList;

  }
}
