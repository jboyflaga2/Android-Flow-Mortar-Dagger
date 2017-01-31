package jboyflaga2.com.starterproject.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

import jboyflaga2.com.starterproject.R;
import jboyflaga2.com.starterproject.android.ActionBarOwner;
import jboyflaga2.com.starterproject.android.ActivityResultPresenter;
import jboyflaga2.com.starterproject.android.ActivityResultRegistrar;
import jboyflaga2.com.starterproject.android.AndroidModule;
import jboyflaga2.com.starterproject.core.Layout;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.functions.Action0;

@Layout(R.layout.main_view)
public class MainScreen {

    @dagger.Component(modules = AndroidModule.class)
    @Singleton
    public interface Component {
        void inject(MainView t);
    }

    @Singleton
    static class Presenter extends ViewPresenter<MainView>  implements ActivityResultPresenter.ActivityResultListener {

        private final ActivityResultPresenter activityResultPresenter;
        private final ActivityResultRegistrar activityResultRegistrar;
        private final ActionBarOwner actionBar;

        @Inject
        Presenter(ActivityResultPresenter activityResultPresenter, ActivityResultRegistrar activityResultRegistrar, ActionBarOwner actionBar){
            this.activityResultPresenter = activityResultPresenter;
            this.activityResultRegistrar = activityResultRegistrar;
            this.actionBar = actionBar;
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            activityResultRegistrar.register(scope, this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            ActionBarOwner.Config actionBarConfig = new ActionBarOwner.Config(true, false, "Main Screen",
                    new ActionBarOwner.MenuAction("Hi", new Action0() {
                        @Override public void call() {
                            Toast.makeText(getView().getContext(), "Clicked Hi From Main Screen", Toast.LENGTH_LONG).show();
                        }
                    }));

            actionBar.setConfig(actionBarConfig);
        }

        @Override
        protected void onSave(Bundle outState) {
        }

        //region "ActivityResultPresenter.ActivityResultListener"
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case 1001:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri contactData = data.getData();
                        Cursor c =  getView().getContext().getContentResolver().query(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            getView().showToast("Contact Name: " + name);
                        }
                    }
                    break;
            }
        }
        //endregion

        public void getContact() {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            activityResultPresenter.startActivityForResult(1001, intent);
        }

        public void goToAnotherView() {
            getView().goToScreen(new AnotherScreen());
        }
    }
}
