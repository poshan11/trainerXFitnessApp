package com.cecs453.trainerx;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cecs453.trainerx.adapters.CustomGridAdapter;
import com.cecs453.trainerx.model.Exercise;
import com.cecs453.trainerx.ui.ExpandableGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkoutSelectFragment extends Fragment {

    private ArrayList<String> itemTypes;
    private Map<String, List<Exercise>> items;
    private ArrayList<String> exercises;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout ll;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private String docID;
    private Bundle args;
    private CustomGridAdapter customGridAdapter;

    public WorkoutSelectFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public WorkoutSelectFragment(Map<String, List<Exercise>> items, ArrayList<String> itemTypes, ArrayList<String> exercises) {
        this.items = items;
        this.itemTypes = itemTypes;
        this.exercises = exercises;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        args = getArguments();
        docID = args.getString("DocId");
        Log.e("DocID:!@ select frag", docID);
        DocId.getInstance().setId(docID);


        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);

        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        fragmentManager = getActivity().getFragmentManager();

        Button cancel = view.findViewById(R.id.WorkoutCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        Button customize = view.findViewById(R.id.WorkoutCustomise);
        customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), AddCustomExerciseActivity.class).putStringArrayListExtra("types", itemTypes), 1);
            }
        });

        ll = view.findViewById(R.id.linearLayoutGrid);
        for (int i = 0; i < itemTypes.size(); i++) {
            addGridToLayout(itemTypes.get(i), items.get(itemTypes.get(i)));
        }
        addAdapter();


        return view;
    }

    private void addAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, exercises);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkoutTemplateFragment workoutTemplateFragment = new WorkoutTemplateFragment();

                args.putString("DocId", docID);
                args.putString("excName", parent.getItemAtPosition(position).toString());
                Log.e("In ADD ADAPTER", docID);
                workoutTemplateFragment.setArguments(args);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentWorkoutPlaceholder, workoutTemplateFragment, "workoutTemplate");
                fragmentTransaction.addToBackStack("workoutTemplate");
                fragmentTransaction.commit();
            }
        });
    }

    public void addGridToLayout(String title, List<Exercise> itemList) {
        TextView tv = View.inflate(getActivity(), R.layout.textview_layout, null).findViewById(R.id.text_view);
        tv.setText(title);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.material_text_color_white_text));

        LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(tv, lay);

        ExpandableGridView gridView = View.inflate(getActivity(), R.layout.grid_view, null).findViewById(R.id.workoutgrid);
        gridView.setExpanded(true);
        gridView.setLayoutParams(new GridView.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
        gridView.setNumColumns(3);
        gridView.setHorizontalSpacing(40);
        gridView.setVerticalSpacing(10);
        customGridAdapter = new CustomGridAdapter(itemList, getActivity());
        gridView.setAdapter(customGridAdapter);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(gridView, params);
    }
}
