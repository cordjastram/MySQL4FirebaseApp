package com.cjastram.mysql4firebase.android;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class M4FActivity extends AppCompatActivity implements M4FContract.View {

    private static final int RC_SIGN_IN = 123;

    M4FContract.Presenter presenter;

    @BindView(R.id.tiet_db_statement)
    TextInputEditText etDbStatement;

    @BindView(R.id.tiet_parameter_1)
    TextInputEditText tietParameter1;

    @BindView(R.id.tiet_parameter_2)
    TextInputEditText tietParameter2;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.tiet_message)
    TextInputEditText tietMessage;

    @BindView(R.id.lv_result)
    ListView listView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rb_sql)
    RadioButton rbSql;

    @BindView(R.id.rb_prepared_statement)
    RadioButton rbPreparedStmt;

    @BindView(R.id.rb_stored_procedure)
    RadioButton rbStoredProcedure;

    android.view.View[] mControlArray;

    Menu mMenu;

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        presenter.submit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mControlArray = new android.view.View[]{etDbStatement, tietParameter1, tietParameter2, tietMessage, rbPreparedStmt, rbSql, rbStoredProcedure };
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.setView(null);
        this.presenter = null;
    }

    public void onRadioButtonClicked(android.view.View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_sql:
                if (checked)
                    presenter.setSelectedDbStatement(0);
                    break;
            case R.id.rb_prepared_statement:
                if (checked)
                    presenter.setSelectedDbStatement(1);
                    break;
            case R.id.rb_stored_procedure:
                if (checked)
                    presenter.setSelectedDbStatement(2);
                   break;
        }
    }


    private void initPresenter() {
        if (presenter == null) {
            presenter = M4FApplication.getPresenter();
            presenter.setView(this);
            presenter.init();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        updateMenu(  presenter.isAuthenticated() );
        return true;
    }


    @Override
    public void updateMenu(boolean isAuthenticated) {
        if (mMenu != null && mMenu.size() > 1) {
            boolean isVisible = isAuthenticated;
            mMenu.getItem(0).setVisible(!isAuthenticated);
            mMenu.getItem(1).setVisible(isAuthenticated);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login:
                login();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void setParameterValue(int index, String value) {
        switch(index){
            case 0:
                tietParameter1.setText(value);
                break;
            case 1:
                tietParameter2.setText(value);
                break;
        }
    }


    @Override
    public void info(String text) {
         Toast.makeText(this, text, 1000).show();
    }

    public void login() {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder().build(),
                RC_SIGN_IN);
    }

    void logout() {
        presenter.logout();
    }


    @Override
    public void setDbStatementText(String text) {
        this.etDbStatement.setText(text);
    }


    @Override
    public void setMessage(String message) {
        this.tietMessage.setText(message);
    }


    @Override
    public int getSelectedDbStatementIndex() {
        int index = 0;

        if ( rbPreparedStmt.isChecked() )
            index = 1;

        if ( rbStoredProcedure.isChecked() )
            index = 2;

        return index;
    }


    @Override
    public void setSelectedDbStatementIndex(int index) {
        rbSql.setSelected(index == 0);
        rbPreparedStmt.setSelected( index == 1);
        rbStoredProcedure.setSelected(index == 2);
    }


    @Override
    public void setResult(List<String> result) {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result);
        listView.setAdapter(itemsAdapter);
    }


    @Override
    public String getParameterValue(int index) {
        String value;

        switch (index) {
            case 0:
                value = tietParameter1.getText().toString();
                break;
            case 1:
                value = tietParameter2.getText().toString();
                break;
            default:
                return "";
        }
        return value;
    }


    @Override
    public void enableControls(boolean isEnabled) {
        for (android.view.View view : mControlArray) {
            view.setEnabled(isEnabled);
        }
        this.listView.setVisibility( isEnabled ? android.view.View.VISIBLE: android.view.View.INVISIBLE );
    }


}
