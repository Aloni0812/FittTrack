package by.zhogol.fittrack.workout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import by.zhogol.fittrack.R
import by.zhogol.fittrack.database.Workout

class WorkoutExpandableListAdapter(
    private val context: Context,
    private val workouts: List<Workout>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return workouts.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1 // Для простоты будем показывать только одну строку с деталями
    }

    override fun getGroup(groupPosition: Int): Any {
        return workouts[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return workouts[groupPosition] // Дочерние данные — это сама тренировка
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group_item, parent, false)
        val workoutName = groupView.findViewById<TextView>(R.id.workoutName)
        workoutName.text = workouts[groupPosition].name
        return groupView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val childView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_child_item, parent, false)
        val workoutDetails = childView.findViewById<TextView>(R.id.workoutDetails)
        val workout = workouts[groupPosition]
        val details = "Duration: ${workout.duration} min\nFrequency: ${workout.frequency}"
        workoutDetails.text = details
        return childView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
