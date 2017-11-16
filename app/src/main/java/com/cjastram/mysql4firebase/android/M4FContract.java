package com.cjastram.mysql4firebase.android;


import java.util.List;

import com.cjastram.mysql4firebase.android.model.SQLRequest;

import java.util.List;

/**
 * Created by cjastram on 25.10.2017.
 */

interface M4FContract {


    interface View {

        void setParameterValue(int index, String parameter);

        String getParameterValue(int index);

        int getSelectedDbStatementIndex();

        void setSelectedDbStatementIndex(int index);

        void info(String text);

        void login();

        void setDbStatementText(String text);

        void setMessage(String message);

        void setResult(List<String> result);

        void enableControls(boolean isEnabled);

        void updateMenu(boolean isAuthenticated);
    }

    public interface Presenter {

        void submit();

        void authStateChanged(boolean isAuthenticated);

        void init();

        void logout();

        void setView(M4FContract.View view);

        void setSelectedDbStatement(int index);

        void setSQLRequest(SQLRequest request);

        boolean isAuthenticated();

        void error(String msg);

    }
}
