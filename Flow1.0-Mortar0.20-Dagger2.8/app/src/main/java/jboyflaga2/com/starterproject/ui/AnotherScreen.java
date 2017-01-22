package jboyflaga2.com.starterproject.ui;

import android.os.Bundle;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

import jboyflaga2.com.starterproject.R;
import jboyflaga2.com.starterproject.android.ActionBarOwner;
import jboyflaga2.com.starterproject.core.Layout;
import mortar.ViewPresenter;
import rx.functions.Action0;

/**
 * Created by User-01 on 12/24/2016.
 */

@Layout(R.layout.another_view)
public class AnotherScreen {

//    @Singleton
//    @dagger.Component(modules = AndroidModule.class)
//    public interface Component {
//        void inject(AnotherView view);
//    }

    @Singleton
    static class Presenter extends ViewPresenter<AnotherView> {

        private final ActionBarOwner actionBar;

        @Inject
        Presenter(ActionBarOwner actionBar) {
            this.actionBar = actionBar;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            ActionBarOwner.Config actionBarConfig = new ActionBarOwner.Config(true, true, "Another Screen",
                    new ActionBarOwner.MenuAction("Hi", new Action0() {
                        @Override public void call() {
                            Toast.makeText(getView().getContext(), "Clicked Hi From Another Screen", Toast.LENGTH_LONG).show();
                        }
                    }));

            actionBar.setConfig(actionBarConfig);
        }

        @Override
        protected void onSave(Bundle outState) {

        }
    }
}
