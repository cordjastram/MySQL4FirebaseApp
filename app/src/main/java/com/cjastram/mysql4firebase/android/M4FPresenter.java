package com.cjastram.mysql4firebase.android;

import com.cjastram.mysql4firebase.android.model.DbStatement;
import com.cjastram.mysql4firebase.android.model.Parameter;
import com.cjastram.mysql4firebase.android.model.QueueItem;
import com.cjastram.mysql4firebase.android.model.SQLRequest;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjastram on 05.10.2017.
 */


class M4FPresenter implements M4FContract.Presenter {

    M4FContract.View mView;

    boolean isAuthenticated;

    String uid;

    String path;

    List<DbStatement> dbStatementList = new ArrayList<>();

    private M4FDataSource m4FDataSource;

    M4FPresenter(M4FDataSource m4FDataSource) {
       this.m4FDataSource = m4FDataSource;
    }

    @Override
    public void setView(M4FContract.View mView) {
        this.mView = mView;
    }


    @Override
    public void submit() {

        if ( m4FDataSource.isAuthenticated() ) {

            DbStatement dbStatement = this.dbStatementList.get(mView.getSelectedDbStatementIndex());

            QueueItem queueItem = new QueueItem();

            this.m4FDataSource.saveQueueItem(queueItem);

            SQLRequest request = new SQLRequest();
            request.dbStatementName = dbStatement.name;
            request.parameterList = dbStatement.parameterList;

            for (int i = 0; i < request.parameterList.size(); i++) {
                request.parameterList.get(i).value = mView.getParameterValue(i);
            }
            this.m4FDataSource.saveSQLRequest(request);
        } else {
            mView.info("Please login!");
        }
    }

    @Override
    public void authStateChanged(boolean isAuthenticated) {

        if ( this.mView != null ) {
            this.mView.enableControls(isAuthenticated);
            this.mView.updateMenu(isAuthenticated);
        }
    }

    @Override
    public void init() {
        fillDbStatementList();
        setSelectedDbStatement(0);
        mView.updateMenu( m4FDataSource.isAuthenticated() );
    }

    @Override
    public void logout() {
        m4FDataSource.signOut();
        mView.info("Logged out!");
    }

    @Override
    public void setSelectedDbStatement(int index) {

        DbStatement dbStatement = this.dbStatementList.get(index);

        mView.setDbStatementText( dbStatement.statement );
        mView.setParameterValue(0, "");
        mView.setParameterValue(1, "");
        mView.setMessage("");
        mView.setResult( new ArrayList<String>() );
        mView.setSelectedDbStatementIndex(index);

        switch( dbStatement.parameterList.size() )
        {
            case 2:
                mView.setParameterValue(1, dbStatement.parameterList.get(1).value );
            case 1:
                mView.setParameterValue(0, dbStatement.parameterList.get(0).value );
        }
    }

    @Override
    public void setSQLRequest(SQLRequest request) {
        if (request != null) {
            mView.setMessage(request.message);
            mView.setResult(request.result);
            mView.setParameterValue(0, "");
            mView.setParameterValue(1, "");
            switch( request.parameterList.size() )
            {
                case 2:
                    mView.setParameterValue(1, request.parameterList.get(1).value );
                case 1:
                    mView.setParameterValue(0, request.parameterList.get(0).value );
            }
        }
    }

    @Override
    public boolean isAuthenticated() {
        return this.m4FDataSource.isAuthenticated();
    }

    @Override
    public void error(String text) {
        mView.info(text);
    }

    void fillDbStatementList() {

        DbStatement dbStatement = new DbStatement();
        dbStatement = new DbStatement();
        dbStatement.name = "plain_sql";
        dbStatement.type = DbStatement.PLAIN_SQL;
        dbStatement.statement = "";
        dbStatement.parameterList.add(Parameter.inParameter(1, Types.VARCHAR, "select count(*) from customers"));

        this.dbStatementList.add(dbStatement);

        dbStatement = new DbStatement();
        dbStatement.name = "select_customers";
        dbStatement.type = DbStatement.QUERY;
        dbStatement.statement = "select * from customers where city = ? limit ?";
        dbStatement.parameterList.add(Parameter.inParameter(1, Types.VARCHAR, "London"));
        dbStatement.parameterList.add(Parameter.inParameter(2, Types.INTEGER, "2"));
        this.dbStatementList.add(dbStatement);

        dbStatement = new DbStatement();
        dbStatement.name = "stored_procedure";
        dbStatement.type = DbStatement.CALLABLE;
        dbStatement.statement = "{call sp_employees_cursor( ? )}";
        dbStatement.parameterList.add(Parameter.inParameter(1, Types.VARCHAR, "London"));
        this.dbStatementList.add(dbStatement);
    }

}

