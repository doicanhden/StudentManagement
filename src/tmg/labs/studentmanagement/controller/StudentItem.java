package tmg.labs.studentmanagement.controller;

import java.util.Date;

import tmg.labs.studentmanagement.App;
import tmg.labs.studentmanagement.R;
import tmg.labs.studentmanagement.model.Student;
import tmg.labs.studentmanagement.util.ImageLoader;
import tmg.labs.studentmanagement.util.Utilities;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StudentItem {
  private final Context mContext;
  private final ImageLoader mImageLoader;

  private final ImageView mImageViewPhoto;
  private final TextView mTextViewName;
  private final TextView mTextViewId;
  private final TextView mTextViewDescription;

  public StudentItem(
      View v) {
    this.mContext = v.getContext();

    this.mImageLoader = App.getIntance().getImageLoader();

    this.mImageViewPhoto = (ImageView) v
        .findViewById(R.id.imageview_student_photo);
    this.mTextViewName = (TextView) v.findViewById(R.id.textview_student_name);
    this.mTextViewId = (TextView) v.findViewById(R.id.textview_student_id);
    this.mTextViewDescription = (TextView) v
        .findViewById(R.id.textview_description);

  }

  public void setStudent(int index, Student student) {
    // assign values if the object is not null
    if (student != null) {
      final Resources resources = mContext.getResources();

      mImageLoader.displayImage(mImageViewPhoto, student.getPhotoPath(),
          R.drawable.default_photo);
      mTextViewId.setText(resources.getString(R.string.format_id, index));
      mTextViewName.setText(student.getName());

      final Date birthday = student.getBirthday();
      if (birthday != null) {
        final int age = Utilities.calcAge(birthday);
        if (age > 0) {
          mTextViewDescription.setText(resources.getString(
              R.string.format_birthday_with_age, birthday, age));
        }
        else {
          mTextViewDescription.setText(resources.getString(
              R.string.format_birthday, birthday));
        }
      }
      else {
        mTextViewDescription.setText(null);
      }
    }
  }
}
