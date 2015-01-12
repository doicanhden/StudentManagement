package tmg.labs.studentmanagement.controller;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import tmg.labs.studentmanagement.App;
import tmg.labs.studentmanagement.DatabaseHandler;
import tmg.labs.studentmanagement.R;
import tmg.labs.studentmanagement.model.Student;
import tmg.labs.studentmanagement.util.ImageLoader;
import tmg.labs.studentmanagement.util.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class StudentActivity extends Activity {
  protected static final int REQUEST_SELECT_FILE = 2;
  protected static final int REQUEST_TAKE_PHOTO = 1;

  private DatabaseHandler mDatabase;
  private EditText mEditTextName;
  private DatePicker mDatePickerBirthday;
  private EditText mEditTextAddress;
  private EditText mEditTextClassname;
  private ImageView mImageViewPhoto;
  private int mIntCurrentStudentId = 0;
  private String mPhotoPath = null;
  private String mCapturedPhotoPath = null;
  private ImageLoader mImageLoader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);

    mEditTextName = (EditText) findViewById(R.id.edittext_student_name);
    mDatePickerBirthday = (DatePicker) findViewById(R.id.datepicker_student_birthday);
    mEditTextAddress = (EditText) findViewById(R.id.edittext_student_address);
    mEditTextClassname = (EditText) findViewById(R.id.edittext_student_classname);

    mImageViewPhoto = (ImageView) findViewById(R.id.imageview_student_photo);
    mImageViewPhoto.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (mPhotoPath != null) {
          File image = new File(mPhotoPath);

          Intent intent = new Intent();
          intent.setAction(Intent.ACTION_VIEW);
          intent.setDataAndType(Uri.fromFile(image), "image/*");

          startActivity(intent);
        }
        else {
          Toast.makeText(StudentActivity.this, R.string.photo_none,
              Toast.LENGTH_SHORT).show();
        }
      }
    });
    registerForContextMenu(mImageViewPhoto);

    Button buttonAdd = (Button) findViewById(R.id.button_student_add);
    buttonAdd.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Student student = getStudent();
        if (assertStudent(student)) {
          mDatabase.insertStudent(student);
          finish();
        }
      }
    });

    Button buttonUpdate = (Button) findViewById(R.id.button_student_update);
    buttonUpdate.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Student student = getStudent();
        if (assertStudent(student)) {
          mDatabase.updateStudent(student);
          finish();
        }
      }
    });

    Button buttonBack = (Button) findViewById(R.id.button_back);
    buttonBack.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });

    final Intent intent = getIntent();
    if (intent != null) {
      mIntCurrentStudentId = intent.getIntExtra("student_id", 0);

      if (mIntCurrentStudentId > 0) {
        this.setTitle(R.string.title_activity_student_details);

        buttonAdd.setVisibility(View.GONE);
        buttonBack.setVisibility(View.VISIBLE);
        buttonUpdate.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    final App app = (App) getApplicationContext();
    mDatabase = app.getDbConnection();
    mImageLoader = app.getImageLoader();

    if (mIntCurrentStudentId > 0) {
      final Student student = mDatabase.getStudent(mIntCurrentStudentId);
      setStudent(student);
    }
  }

  @Override
  public void onCreateContextMenu(
      ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    if (v.getId() == R.id.imageview_student_photo) {
      menu.setHeaderTitle(R.string.title_photo_view);
      getMenuInflater().inflate(R.menu.photo, menu);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    int menuItemId = item.getItemId();
    switch (menuItemId) {
    case R.id.menu_photo_take:
      dispatchTakePhotoIntent();
      break;
    case R.id.menu_photo_choose:
      dispatchSelectPhotoIntent();
      break;
    case R.id.menu_photo_clear:
      mPhotoPath = null;
      mImageViewPhoto.setImageResource(R.drawable.default_photo);
      break;
    default:
      return false;
    }

    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
      case REQUEST_TAKE_PHOTO:
        mPhotoPath = mCapturedPhotoPath;
        break;
      case REQUEST_SELECT_FILE:
        mPhotoPath = Utilities.getPath(this, data.getData());
        break;
      default:
        return;
      }

      updatePhoto();
    }
  }

  private void updatePhoto() {
    if (mPhotoPath != null) {
      mImageLoader.displayImage(mImageViewPhoto, mPhotoPath,
          R.drawable.default_photo);
    }
    else {
      mImageViewPhoto.setImageResource(R.drawable.default_photo);
    }
  }

  protected void dispatchTakePhotoIntent() {
    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (intent.resolveActivity(getPackageManager()) != null) {
      try {
        // Create the File where the photo should go
        final File file = Utilities.createImageFile();

        mCapturedPhotoPath = file.getAbsolutePath();
        // Continue only if the File was successfully created
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  protected void dispatchSelectPhotoIntent() {
    final Intent intent = new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    intent.setType("image/*");

    startActivityForResult(Intent.createChooser(intent, "Select File"),
        REQUEST_SELECT_FILE);
  }

  protected boolean assertStudent(Student student) {
    if (student.isEmpty()) {
      final Toast toast = Toast.makeText(this, R.string.message_missing_info,
          Toast.LENGTH_SHORT);

      toast.show();

      return false;
    }

    return true;
  }

  protected void setStudent(Student student) {
    if (student != null) {
      mPhotoPath = student.getPhotoPath();

      updatePhoto();

      mEditTextName.setText(student.getName());

      Calendar birthday = Calendar.getInstance();
      birthday.setTime(student.getBirthday());

      mDatePickerBirthday.updateDate(birthday.get(Calendar.YEAR), birthday
          .get(Calendar.MONTH), birthday.get(Calendar.DATE));

      mEditTextAddress.setText(student.getAddress());
      mEditTextClassname.setText(student.getClassname());
    }
  }

  protected Student getStudent() {

    final Calendar birthday = Calendar.getInstance();
    birthday.set(mDatePickerBirthday.getYear(), mDatePickerBirthday.getMonth(),
        mDatePickerBirthday.getDayOfMonth());

    Student student = new Student(mEditTextName.getText().toString(), birthday
        .getTime(), mEditTextAddress.getText().toString(), mEditTextClassname
        .getText()
        .toString());

    student.setPhotoPath(mPhotoPath);

    if (mIntCurrentStudentId != 0) {
      student.setId(mIntCurrentStudentId);
    }

    return student;
  }
}
