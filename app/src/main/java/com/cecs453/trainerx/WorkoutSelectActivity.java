package com.cecs453.trainerx;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cecs453.trainerx.adapters.WorkoutLogAdapter;
import com.cecs453.trainerx.model.Exercise;
import com.cecs453.trainerx.ui.ExpandableHeaderItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class WorkoutSelectActivity extends AppCompatActivity {

    public static final String TAG = "WorkoutSelectActivity";
    public static final int sets = 5;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private FirebaseFirestore db;
    private ArrayList<String> mItemTypes = new ArrayList<>();
    private Map<String, List<Exercise>> items = new HashMap<>();
    private ArrayList<String> exercises = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private List<AbstractFlexibleItem> mItems = new ArrayList<>();
    private WorkoutLogAdapter adapter;
    private RecyclerView recyclerView;
    private String docID;

    /*
     * Creates a special expandable item which is also a Header.
     * The subItems will have linked its parent as Header!
     */
    public static ExpandableHeaderItem newExpandableSectionItem(int exercises) {
        ExpandableHeaderItem expandableItem = new ExpandableHeaderItem("EH" + exercises);
        expandableItem.setTitle("Expandable Header " + exercises);
        /*for (int j = 1; j <= sets; j++) {
            SubItem subItem = new SubItem(expandableItem.getId() + "-SB" + j);
            subItem.setTitle("Sub Item " + j);
            // NOTE: In case you want to retrieve the parent, you can implement ISectionable
            // then, assign the Header: ExpandableHeaderItem instance.
            //subItem.setHeader(expandableItem);
            expandableItem.addSubItem(subItem);
        }*/
        return expandableItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_select);

        docID = getIntent().getExtras().getString("DocId");
        Log.e("@workActivity Docid", docID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Customize Workout");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = FirebaseFirestore.getInstance();
        readData();

        recyclerView = findViewById(R.id.recycler_view);

        createExpandableSectionsDatabase(3);

        adapter = new WorkoutLogAdapter(mItems, WorkoutSelectActivity.this);
        adapter.addListener(new FlexibleAdapter.OnItemClickListener() {

            @Override
            public boolean onItemClick(View view, int position) {
                return true;
            }
        });
        adapter.expandItemsAtStartUp()
                .setAutoCollapseOnExpand(false)
                .setAutoScrollOnExpand(true)
                .setAnimateToLimit(Integer.MAX_VALUE) //Size limit = MAX_VALUE will always animate the changes
                .setAnimationOnForwardScrolling(true)
                .setAnimationOnReverseScrolling(true);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new FlexibleItemDecoration(this)
                .addItemViewType(R.layout.recycler_expandable_header_item)
                .withOffset(4));

        adapter.setLongPressDragEnabled(true) //Enable long press to drag items
                .setHandleDragEnabled(true) //Enable handle drag
                .setStickyHeaders(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wokout_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // handle arrow click here
        if (id == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                finish(); // close this activity and return to preview activity (if there is any)
            }
        }

        if (id == R.id.action_stopwatch) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted

                Log.d(TAG, "permission floating");
                startService(new Intent(WorkoutSelectActivity.this, FloatingViewService.class));
            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(WorkoutSelectActivity.this).create();
                alertDialog.setTitle("Permission to draw over other apps required!");
                alertDialog.setMessage("*To show the stopwatch, System Overlay permission has to be granted.\n\n" +
                        "*This permission has to be granted only once.\n\n" +
                        "*Please tap on the icon to see the stopwatch after granting the permission.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "GRANT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DENY",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void readData() {
        db.collection("exercises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String name = (String) document.get("name");
                                String type = (String) document.get("type");
                                Boolean isCustom = (Boolean) document.get("isCustom");
                                if (isCustom == null)
                                    isCustom = false;
                                if (TextUtils.isEmpty(name)) {
                                    Log.w(TAG, "Error getting name");
                                } else {
                                    exercises.add(name);
                                    if (items.containsKey(type)) {
                                        items.get(type).add(new Exercise(type, name, null, isCustom));
                                    } else {
                                        ArrayList<Exercise> temp = new ArrayList<>();
                                        temp.add(new Exercise(type, name, null, isCustom));
                                        items.put(type, temp);
                                    }
                                }
                            }
                            mItemTypes.addAll(items.keySet());
                            addInitialFragment();
                        } else {

                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(WorkoutSelectActivity.this, "Failed to get clients", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addInitialFragment() {
        WorkoutSelectFragment workoutListFragment = new WorkoutSelectFragment(items, mItemTypes, exercises);
        fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args.putString("DocId", docID);
        workoutListFragment.setArguments(args);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentWorkoutPlaceholder, workoutListFragment, "workoutList");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /*
     * List of Expandable items (headers/sections) with SubItems with Header attached.
     */
    public void createExpandableSectionsDatabase(int size) {
        /*for (int i = 0; i < size; i++) {
            mItems.add(newExpandableSectionItem(i + 1));//With expansion level 0
        }*/

        ExpandableHeaderItem expandableItem = new ExpandableHeaderItem("1");
        expandableItem.setTitle("StairMaster");

        ExpandableHeaderItem expandableItem2 = new ExpandableHeaderItem("2");
        expandableItem2.setTitle("Wall Squat ");

        ExpandableHeaderItem expandableItem3 = new ExpandableHeaderItem("3");
        expandableItem3.setTitle("Push Ups ");

        ExpandableHeaderItem expandableItem4 = new ExpandableHeaderItem("4");
        expandableItem4.setTitle("Bench Press");

        ExpandableHeaderItem expandableItem5 = new ExpandableHeaderItem("5");
        expandableItem5.setTitle("Shoulder Press");

        mItems.add(expandableItem);
        mItems.add(expandableItem2);
        mItems.add(expandableItem3);
        mItems.add(expandableItem4);
        mItems.add(expandableItem5);
    }

}
