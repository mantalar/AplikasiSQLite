package id.go.kominfo.mantala.aplikasisqlite;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import id.go.kominfo.mantala.aplikasisqlite.adapter.FriendAdapter;
import id.go.kominfo.mantala.aplikasisqlite.model.Friend;
import id.go.kominfo.mantala.aplikasisqlite.utils.FriendDB;

public class MainActivity extends AppCompatActivity {
    public static final String MODE = "mode";
    public static final String FRIEND = "friend";
    public static final String FRIENDS = "friends";
    public static final int ADD_MODE = 0;
    public static final int VIEW_MODE = 1;
    public static final int EDIT_MODE = 2;

    private final List<Friend> mList = new ArrayList<>();
    private FriendAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar1));

        if (savedInstanceState != null) {
            mList.clear();
            //noinspection unchecked
            mList.addAll((Collection<? extends Friend>)
                    Objects.requireNonNull(savedInstanceState.getSerializable(MainActivity.FRIENDS)));
        }

        mAdapter = new FriendAdapter(mList, this);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(this::onItemLongClick);
        listView.setOnItemClickListener(this::onItemClick);

        findViewById(R.id.fab).setOnClickListener(this::addItem);
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            if (result.getData().getIntExtra(MainActivity.MODE, -1) == MainActivity.ADD_MODE) {
                try (FriendDB db = new FriendDB(this)) {
                    Friend friend = (Friend) result.getData().getSerializableExtra(MainActivity.FRIEND);
                    assert friend != null;
                    db.insert(friend);
                }
            } else if (result.getData().getIntExtra(MainActivity.MODE, -1) == MainActivity.EDIT_MODE) {
                try (FriendDB db = new FriendDB(this)) {
                    Friend friend = (Friend) result.getData().getSerializableExtra(MainActivity.FRIEND);
                    assert friend != null;
                    db.update(friend);
                }
            }
        }
    });

    private void addItem(View view) {
        Intent intent = new Intent(this, AddAndViewActivity.class);
        intent.putExtra(MainActivity.MODE, MainActivity.ADD_MODE);
        resultLauncher.launch(intent);
    }

    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, AddAndViewActivity.class);
        intent.putExtra(MainActivity.MODE, MainActivity.VIEW_MODE);
        intent.putExtra(MainActivity.FRIEND, mList.get(i));
        startActivity(intent);
    }

    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        CharSequence[] items = {"Edit", "Delete"};
        int[] checked = {-1};

        new AlertDialog.Builder(this)
                .setTitle("Your Action")
                .setSingleChoiceItems(items, checked[0], (dialogInterface, i1) -> checked[0] = i1)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", (dialogInterface, i1) -> {
                    switch (checked[0]) {
                        case 0: //edit
                            Intent intent = new Intent(this, AddAndViewActivity.class);
                            intent.putExtra(MainActivity.MODE, MainActivity.EDIT_MODE);
                            intent.putExtra(MainActivity.FRIEND, mList.get(i));
                            resultLauncher.launch(intent);
                            break;
                        case 1: //delete
                            new AlertDialog.Builder(this)
                                    .setTitle("Confirm")
                                    .setMessage("Delete " + mList.get(i).toString() + "?")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("Yes", (dialogInterface1, i2) -> {
                                        try (FriendDB db = new FriendDB(this)) {
                                            Friend friend = mList.get(i);
                                            db.delete(friend.getId());
                                            mList.remove(i);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }).show();
                            break;
                    }
                }).show();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mi_about) {
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage(R.string.about_msg)
                    .setPositiveButton("OK", null).show();
        } else if (item.getItemId() == R.id.mi_exit) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Close App?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes", (dialogInterface, i) -> finish()).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MainActivity.FRIENDS, (Serializable) mList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try (FriendDB db = new FriendDB(this)) {
            mList.clear();
            mList.addAll(db.getAllFriends());
            mAdapter.notifyDataSetChanged();
        }
    }
}