package tmg.labs.studentmanagement.controller;

import java.util.List;

import tmg.labs.studentmanagement.model.Student;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class StudentListAdapter extends BaseAdapter {
	private final Activity mContext;
	private final int mLayoutId;
	private final List<Student> mStudentList;

	public StudentListAdapter(Activity context, int layoutId,
			List<Student> objects) {

		this.mContext = context;
		this.mLayoutId = layoutId;
		this.mStudentList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		StudentItem item;

		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = mContext.getLayoutInflater();
			convertView = inflater.inflate(mLayoutId, parent, false);

			// well set up the ViewHolder
			item = new StudentItem(convertView);

			// store the holder with the view.
			convertView.setTag(item);
		} else {
			// we've just avoided calling findViewById() on resource everytime
			// just use the viewHolder
			item = (StudentItem) convertView.getTag();
		}

		// object item based on the position
		item.setStudent(position + 1, mStudentList.get(position));

		return convertView;
	}

	@Override
	public int getCount() {
		if (mStudentList == null) {
			return 0;
		}

		return mStudentList.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (mStudentList == null) {
			return null;
		}

		return mStudentList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		if (mStudentList == null) {
			return 0;
		}

		return mStudentList.get(arg0).getId();
	}
}
