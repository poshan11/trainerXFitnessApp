package com.cecs453.trainerx.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.cecs453.trainerx.model.Exercise;
import com.cecs453.trainerx.R;
import com.cecs453.trainerx.WorkoutTemplateFragment;

import java.util.List;

public class CustomGridAdapter extends BaseAdapter {
    private List<Exercise> list;
    private LayoutInflater inflater;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    public CustomGridAdapter(List<Exercise> list, Activity activity) {
        this.list = list;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = activity.getFragmentManager();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            //root has to be null here
            view = inflater.inflate(R.layout.workout_select_button_layout, null, false);
        }
        Button button = view.findViewById(R.id.grid_item);
        button.setText(list.get(i).getName());
        button.setBackgroundResource(R.drawable.button_shape);

        if (list.get(i) != null) {
            if (list.get(i).getIsCustom())
                button.setBackgroundResource(R.drawable.button_custom_shape);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutTemplateFragment workoutTemplateFragment = new WorkoutTemplateFragment();

                Bundle args = new Bundle();
                args.putString("excName", list.get(i).getName());
                workoutTemplateFragment.setArguments(args);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentWorkoutPlaceholder, workoutTemplateFragment, "workoutTemplate");
                fragmentTransaction.addToBackStack("workoutTemplate");
                fragmentTransaction.commit();

            }
        });

        return view;
    }
}
