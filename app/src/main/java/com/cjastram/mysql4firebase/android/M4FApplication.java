package com.cjastram.mysql4firebase.android;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cjastram.mysql4firebase.android.model.QueueItem;
import com.cjastram.mysql4firebase.android.model.SQLRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by cjastram on 17.09.2017.
 */

public class M4FApplication extends Application implements M4FDataSource {

    private final static String TAG = "M4FApplication";

    private static M4FApplication mApp;

    private M4FContract.Presenter presenter;

    private FirebaseUser firebaseUser;

    private DatabaseReference dbReferenceRequest;

    private DatabaseReference dbRefQueueItem;

    public static M4FContract.Presenter getPresenter( ) {
        return mApp.presenter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth.getInstance().addAuthStateListener(this);
        presenter = new M4FPresenter(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.i(TAG,"authStateChanged()");
        this.firebaseUser = firebaseAuth.getCurrentUser();
        if( this.firebaseUser == null ) {
            if ( dbReferenceRequest != null ) {
                dbReferenceRequest.removeEventListener(this);
            }
        } else {
            FirebaseDatabase.getInstance().getReference("db_statement").keepSynced(true);
            dbRefQueueItem = FirebaseDatabase.getInstance().getReference("queue");
            dbReferenceRequest = FirebaseDatabase.getInstance().getReference("user_data/" + firebaseUser.getUid() + "/sql_request");
            dbReferenceRequest.addValueEventListener(this);
        }
        presenter.authStateChanged( this.firebaseUser != null  );
    }

    @Override
    public void saveQueueItem(QueueItem item) {
        item.dbPathToProcess = "user_data/" + this.firebaseUser.getUid() + "/sql_request";
        dbRefQueueItem.push().setValue(item, this);
    }

    @Override
    public void saveSQLRequest(SQLRequest request) {
         request.message = "Try to connect to database ...";
         this.presenter.setSQLRequest(request);
         dbReferenceRequest.setValue(request, this);
    }

    @Override
    public boolean isAuthenticated() {
        return this.firebaseUser != null;
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        SQLRequest request = dataSnapshot.getValue(SQLRequest.class);
        this.presenter.setSQLRequest(request);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        this.presenter.error(databaseError.getMessage() + " " + databaseError.getDetails());
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if ( databaseError == null ) {
            Log.i(TAG,"onComplete");
        } else {
            String msg = "Error :"+  databaseError.getMessage() + " - " + databaseError.getDetails();
            Log.e(TAG, msg);
            this.presenter.error(msg);

        }
    }
}


