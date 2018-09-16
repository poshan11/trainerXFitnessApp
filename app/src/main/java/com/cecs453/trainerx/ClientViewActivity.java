package com.cecs453.trainerx;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cecs453.trainerx.adapters.ViewClientsAdapter;
import com.cecs453.trainerx.model.Client;
import com.cecs453.trainerx.model.Workout;
import com.cecs453.trainerx.model.WorkoutExercise;
import com.cecs453.trainerx.ui.SimpleItem;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.silencedut.taskscheduler.TaskScheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class ClientViewActivity extends AppCompatActivity implements FlexibleAdapter.OnItemClickListener {
    public static final String TAG = "ClientViewActivity";
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private static List<AbstractFlexibleItem> data;
    CompactCalendarView compactCalendarView;
    private Button createNewWorkout;
    private Button notes;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private TextView calendarYear;
    private TextView summary;
    private List<Client> clientList = new ArrayList<>();
    private ViewClientsAdapter adapter;
    private int currentActivatedPosition = -1;
    private String fname, lname, Docid;
    private ImageView clientImageView;
    private TextView clientNameTextView;
    private LinearLayout linearLayoutClientView;
    private boolean snapshotListenerAdded = false;
    private List<WorkoutExercise> exerciseslist = new ArrayList<>();
    private List<Workout> workoutList = new ArrayList<>();

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Client");
        toolbar.setTitleTextColor(Color.WHITE);

        clientImageView = findViewById(R.id.clientImageView);
        clientNameTextView = findViewById(R.id.clientNameTextView);
        linearLayoutClientView = findViewById(R.id.linearLayoutClientView);
        summary = findViewById(R.id.summary);

        createNewWorkout = findViewById(R.id.newWorkout);
        notes = findViewById(R.id.notesButton);
        Button addClient = findViewById(R.id.buttonAddClient);
        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClientViewActivity.this, AddClientActivity.class));
            }
        });

        compactCalendarView = findViewById(R.id.compactcalendar_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            readData();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(ClientViewActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addAdapter() {
        adapter = new ViewClientsAdapter(data, this, clientList);
        adapter.setMode(SelectableAdapter.Mode.SINGLE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new FlexibleItemDecoration(this)
                .withDivider(R.drawable.divider, R.layout.list_clients)
                .withDrawOver(true));

        FastScroller fastScroller = findViewById(R.id.fast_scroller);
        fastScroller.setAutoHideEnabled(true);        //true is the default value!
        fastScroller.setAutoHideDelayInMillis(1000L); //1000ms is the default value!
        fastScroller.setMinimumScrollThreshold(70); //0 pixel is the default value! When > 0 it mimics the fling gesture
        // The color (accentColor) is automatically fetched by the FastScroller constructor, but you can change it at runtime
        // fastScroller.setBubbleAndHandleColor(Color.RED);
        adapter.setFastScroller(fastScroller);
    }

    //TODO run this on background thread
    private void readData() {
        data = new ArrayList<>();

        com.silencedut.taskscheduler.Task task = new com.silencedut.taskscheduler.Task<String>() {
            @Override
            public String doInBackground() {
                final String[] result = new String[1];
                db.collection("customers")
                        .orderBy("firstName")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Docid = (String) document.getId();
                                        fname = (String) document.get("firstName");
                                        lname = (String) document.get("lastName");
                                        String imageurl = (String) document.get("imageUrl");

                                        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname)) {
                                            //NOOP
                                        } else {
                                            data.add(new SimpleItem(fname, lname, (String) document.get("imageUrl"), Docid));
                                            clientList.add(new Client(Docid, fname, lname, (String) document.get("imageUrl")));
                                        }
                                    }
                                    addAdapter();
                                    //TODO handle this better
                                    /*if (!snapshotListenerAdded) {
                                        addSnapshotListener();
                                        snapshotListenerAdded = true;
                                    }*/
                                    result[0] = "success";
                                } else {
                                    result[0] = "failure";
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                    Toast.makeText(ClientViewActivity.this, "Failed to get clients. Relaunch application.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Log.d(TAG, Arrays.toString(data.toArray()));
                return result[0];
            }

            @Override
            public void onSuccess(String result) {
                if (result.equals("success")) {
                    Log.d(TAG, "Async OnSuccess: " + result);
                } else {
                    Log.d(TAG, "Async OnSuccess: " + result);
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                Log.d(TAG, "Fail:" + throwable.getMessage());
            }

            @Override
            public void onCancel() {
                super.onCancel();
                // callback when the task is canceled
            }

        };
        TaskScheduler.execute(task);
    }

    void addSnapshotListener() {
        db.collection("customers").orderBy("firstName").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                }
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    fname = (String) document.get("firstName");
                    lname = (String) document.get("lastName");
                    Docid = (String) document.getId();

                    if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname)) {
                        //NOOP
                    } else {
                        data.add(new SimpleItem(fname, lname, (String) document.get("imageUrl"), (String) document.getId()));
                        clientList.add(new Client(Docid, fname, lname, (String) document.get("imageUrl")));
                    }
                }
                if (!queryDocumentSnapshots.isEmpty())
                    adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onItemClick(View view, int position) {
        if (position != currentActivatedPosition) setActivatedPosition(position);

        //remove previousEvents from calander.
        compactCalendarView.removeAllEvents();

        //clear workoutList
        workoutList.clear();
        summary.setText("");

        updateView(position);

        return true;
    }

    private void updateView(int position) {
        Client client = clientList.get(position);
        Glide.with(this).load(client.getImageURL()).into(clientImageView);
        String name = client.getfName() + " " + client.getlName();
        Docid = client.getDocId();
        clientNameTextView.setText(name);
        linearLayoutClientView.setVisibility(View.VISIBLE);


        createNewWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClientViewActivity.this, WorkoutSelectActivity.class);
                Bundle args = new Bundle();
                args.putString("DocId", Docid);
                i.putExtras(args);
                DocId.getInstance().setId(Docid);
                startActivity(i);

            }
        });

        //display client notes on click
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClientViewActivity.this, String.format("Age: 38 yrs \t Height: 6'1'' \n Issues: Had Left Shoulder Surgery recently."), Toast.LENGTH_SHORT).show();

            }
        });
        //get workout logs from db for that customer.
        db.collection("customers")
                .whereEqualTo("firstName", client.getfName())
                .whereEqualTo("lastName", client.getlName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("WORKOUTLOG", document.getId() + " => " + document.getData());

                                //id of the document
                                final String docId = document.getId();
                                Task<QuerySnapshot> messageRef = db
                                        .collection("customers")
                                        .document(document.getId())
                                        .collection("workouts")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
//                                                    List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        String workoutId = document.getId();
                                                        Workout workout = new Workout();

                                                        workout.setCustomerId(document.getString("customer"));
                                                        workout.setTrainer(document.getString("trainer"));
                                                        workout.setID(workoutId);

                                                        if (document.get("date") != null) {
                                                            workout.setDate((double) document.get("date"));

                                                            //convertDateToMilliSec
                                                            Long dateinMSec = convertDateToMsec((double) document.get("date"));
                                                            //addtoCalendar
                                                            addToCalendar(dateinMSec);
                                                        } else {

                                                            workout.setDate(5.54747367437372E8);

                                                            //convertDateToMilliSec
                                                            Long dateinMSec = convertDateToMsec(5.54747367437372E8);
                                                            //addtoCalendar
                                                            addToCalendar(dateinMSec);
                                                        }

                                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                                        //get Exercises
                                                        db.collection("customers")
                                                                .document(docId)
                                                                .collection("workouts")
                                                                .document(workoutId)
                                                                .collection("exercises")
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        String exerciseId = document.getId();
                                                                        WorkoutExercise workoutExercise = new WorkoutExercise();

                                                                        workoutExercise.setID(exerciseId);
                                                                        workoutExercise.setName(document.getString("name"));
                                                                        workoutExercise.setOrder((long) document.get("order"));
                                                                        workoutExercise.setType(document.getString("type"));

                                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                                        exerciseslist.add(workoutExercise);
                                                                    }
                                                                }
                                                            }
                                                        });

                                                        workout.setExerciseList(exerciseslist);
                                                        workoutList.add(workout);
                                                        //clear exerciseList to add next set of exercises for next workout.
                                                        exerciseslist.clear();
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                summary.setText("");

                //set calandar title
                calendarYear = findViewById(R.id.calendarYearMonth);
                String[] s1 = dateClicked.toString().split(" ");
                String day = s1[1];
                String year = s1[5];
                String s2 = day + " " + year;
                calendarYear.setText(s2);
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);

                //update summary box
                StringBuilder sb = new StringBuilder();
                for (Workout workout : workoutList) {
                    sb.append("Trainer: ");
                    sb.append(workout.getTrainer());
                    sb.append("\n");

                    for (WorkoutExercise workoutExercise : workout.getExerciseList()) {
                        sb.append(workoutExercise.getName());
                        sb.append("\t   ");
                        sb.append("x3");
                        sb.append("\t   ");
                        sb.append(workoutExercise.getType());
                        sb.append("\n");
                    }
                }

                summary.setText(sb.toString());

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarYear = findViewById(R.id.calendarYearMonth);
                String[] s1 = firstDayOfNewMonth.toString().split(" ");
                String day = s1[1];
                String year = s1[5];
                String s2 = day + " " + year;
                calendarYear.setText(s2);
                Log.d(TAG, "SCROLLED! ");
            }
        });
    }

    private void setActivatedPosition(int position) {
        currentActivatedPosition = position;
        adapter.toggleSelection(position); //Important!
    }

    private void addToCalendar(long date) {
        Event ev = new Event(Color.BLUE, date);
        compactCalendarView.addEvent(ev);

        Calendar cal = Calendar.getInstance();

        //set date to current
        calendarYear = findViewById(R.id.calendarYearMonth);
        String s2 = new SimpleDateFormat("MMM").format(cal.getTime());
        s2 = s2 + " " + new SimpleDateFormat("YYYY").format(cal.getTime());
        Log.d("TIME", s2);
        calendarYear.setText(s2);

    }

    private Long convertDateToMsec(double date) {
        //for some reason we have to add 31 years and/or 1 day to get the current date
        long myLong = ((long) (date * 1000));
        myLong += 978264706000L + 86400000L;

        return myLong;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // handle arrow click here
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (id == R.id.action_stopwatch) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted

                Log.d(TAG, "permission floating");
                startService(new Intent(ClientViewActivity.this, FloatingViewService.class));
            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(ClientViewActivity.this).create();
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

}
