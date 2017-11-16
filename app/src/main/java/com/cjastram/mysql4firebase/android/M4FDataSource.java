package com.cjastram.mysql4firebase.android;

import com.cjastram.mysql4firebase.android.model.QueueItem;
import com.cjastram.mysql4firebase.android.model.SQLRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by cjastram on 24.10.2017.
 */

public interface M4FDataSource extends FirebaseAuth.AuthStateListener, ValueEventListener, DatabaseReference.CompletionListener {

    void saveQueueItem(QueueItem item);

    void saveSQLRequest(SQLRequest request);

    boolean isAuthenticated();

    void signOut();

 }
