package tmg.labs.studentmanagement.controller;

import java.util.ArrayList;
import java.util.List;

import tmg.labs.studentmanagement.App;
import tmg.labs.studentmanagement.DatabaseHandler;
import tmg.labs.studentmanagement.R;
import tmg.labs.studentmanagement.model.Student;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class StudentManagementActivity extends Activity {
  List<Student> mStudentList = null;
  StudentListAdapter mAdapterStudents = null;
  ListView mListViewStudents = null;
  DatabaseHandler mDatabase = null;
  Button mButtonRemove = null;

  @Override
  protected void onResume() {
    super.onResume();
    mDatabase = App.getIntance().getDbConnection();

    updateListView();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student_management);

    Button buttonAdd = (Button) findViewById(R.id.button_student_show_add);
    buttonAdd.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(StudentManagementActivity.this,
            StudentActivity.class);

        startActivity(intent);
      }
    });
    mButtonRemove = (Button) findViewById(R.id.button_student_remove_first);
    mButtonRemove.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (!mStudentList.isEmpty()) {
          int studentId = mStudentList.get(0).getId();

          deleteStudent(studentId);
        }
      }
    });

    mStudentList = new ArrayList<Student>();

    mAdapterStudents = new StudentListAdapter(this, R.layout.student_item,
        mStudentList);

    mListViewStudents = (ListView) findViewById(R.id.listview_student);
    mListViewStudents.setEmptyView(findViewById(R.id.listview_student_empty));
    mListViewStudents.setAdapter(mAdapterStudents);
    mListViewStudents.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(
          AdapterView<?> parent, View view, int position, long id) {
        int studentId = mStudentList.get(position).getId();

        showStudentDetail(studentId);
      }
    });
    registerForContextMenu(mListViewStudents);
  }

  @Override
  public void onCreateContextMenu(
      ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.listview_student) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

      menu.setHeaderTitle(mStudentList.get(info.position).getName());

      getMenuInflater().inflate(R.menu.student_item, menu);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    Student student = mStudentList.get(info.position);

    switch (item.getItemId()) {
    case R.id.menu_student_item_detail:
      showStudentDetail(student.getId());
      break;
    case R.id.menu_student_item_delete:
      deleteStudent(student.getId());
      break;
    default:
      return false;
    }

    return true;
  }

  protected void updateListView() {
    if (mDatabase != null) {
      List<Student> studentList = mDatabase.getAllStudents();

      mStudentList.clear();
      mStudentList.addAll(studentList);

      mAdapterStudents.notifyDataSetChanged();

      if (mStudentList.isEmpty()) {
        mButtonRemove.setVisibility(View.GONE);
      }
      else {
        mButtonRemove.setVisibility(View.VISIBLE);
      }
    }
  }

  protected void showStudentDetail(int studentId) {
    Intent intent = new Intent(StudentManagementActivity.this,
        StudentActivity.class);
    intent.putExtra("student_id", studentId);

    startActivity(intent);
  }

  protected void deleteStudent(int studentId) {
    if (mDatabase != null) {
      mDatabase.deleteStudent(studentId);
      updateListView();
    }
  }
}
