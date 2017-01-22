package jboyflaga2.com.starterproject.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import javax.inject.Inject;

import flow.Direction;
import flow.Flow;
import flow.KeyChanger;
import flow.KeyDispatcher;
import flow.State;
import flow.TraversalCallback;
import jboyflaga2.com.starterproject.android.ActionBarOwner;
import jboyflaga2.com.starterproject.android.ActivityResultPresenter;
import jboyflaga2.com.starterproject.android.AndroidModule;
import jboyflaga2.com.starterproject.ui.MainScreen;
import mortar.MortarScope;
import mortar.MortarScopeDevHelper;
import mortar.bundler.BundleServiceRunner;

import static jboyflaga2.com.starterproject.android.ActionBarOwner.*;

public class MainActivity extends AppCompatActivity
        implements ActivityResultPresenter.View, Activity {

    private MortarScope activityScope;
    private MenuAction actionBarMenuAction;

    @Inject
    ActivityResultPresenter activityResultPresenter;

    @Inject
    ActionBarOwner actionBarOwner;

    @Override
    protected void attachBaseContext(Context baseContext) {
        baseContext = Flow.configure(baseContext, this)
                .dispatcher(KeyDispatcher.configure(this, new Changer()).build())
                .defaultKey(new MainScreen())
                .install();

        super.attachBaseContext(baseContext);
    }

    @Override
    public Object getSystemService(String name) {
        activityScope = MortarScope.findChild(getApplicationContext(), getScopeName());

        AndroidModule androidModule = new AndroidModule(this, this);
        //MainComponent mainComponent = DaggerMainComponent.builder().androidModule(androidModule).build();
        MainComponent mainComponent = DaggerService.createComponent(MainComponent.class, androidModule);

        if (activityScope == null) {
            activityScope = MortarScope.buildChild(getApplicationContext())
                    .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                    .withService(DaggerService.SERVICE_NAME, mainComponent)
                    .build(getScopeName());
        }

        return activityScope.hasService(name) ? activityScope.getService(name)
                : super.getSystemService(name);
    }

    //region "Activity Life Cycle Overrides"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        DaggerService.<MainComponent>getDaggerComponent(this).inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            MortarScope activityScope = MortarScope.findChild(getApplicationContext(), getScopeName());
            if (activityScope != null) activityScope.destroy();
        }

        super.onDestroy();
    }
    //endregion

    //region "Other overriden methos of MainActivity"
    @Override
    public void onBackPressed() {
        if (!Flow.get(this).goBack()) {
            //super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            default:
                activityResultPresenter.onActivityResultReceived(requestCode, resultCode, data);
        }
    }
    //endregion

    //region "Implementation of interfaces
    @Override
    public MortarScope getMortarScope() {
        return activityScope;
    }

    @Override
    public void startNewActivity(Intent intent) {
        this.startActivity(intent);
    }

    @Override
    public void startNewActivityForResult(int requestCode, Intent intent) {
        this.startActivityForResult(intent, requestCode);
    }
    //endregion

    //region "ActionBar"
    /** Configure the action bar menu as required by {@link Activity}. */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (actionBarMenuAction != null) {
            menu.add(actionBarMenuAction.title)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem menuItem) {
                            actionBarMenuAction.action.call();
                            return true;
                        }
                    });
        }
        menu.add("Log Scope Hierarchy")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        Log.d(this.getClass().getName(), MortarScopeDevHelper.scopeHierarchyToString(activityScope));
                        Toast.makeText(getContext(), MortarScopeDevHelper.scopeHierarchyToString(activityScope), Toast.LENGTH_LONG).show();
                        //Toast.makeText(getContext(), "Hi from MainActivity", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                return Flow.get(this).goBack();
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region "Implementation of ActionBarOwner.Activity"
    @Override
    public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(enabled);
    }

    @Override
    public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(enabled);
        actionBar.setHomeButtonEnabled(enabled);
    }

    @Override
    public void setMenu(MenuAction action) {
        if (action != actionBarMenuAction) {
            actionBarMenuAction = action;
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
    //endregion

    //region "Helpers"
    private String getScopeName() {
        return getClass().getName();
    }
    //endregion

    private final class Changer implements KeyChanger {

        @Override
        public void changeKey(@Nullable State outgoingState, @NonNull State incomingState,
                              @NonNull Direction direction, @NonNull Map<Object, Context> incomingContexts,
                              @NonNull TraversalCallback callback) {

            Object key = incomingState.getKey();
            Context context = incomingContexts.get(key);

            if (outgoingState != null) {
                ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
                outgoingState.save(view.getChildAt(0));
            }

            View view;
            if (key.getClass().isAnnotationPresent(Layout.class)) {
                int layoutId = key.getClass().getAnnotation(Layout.class).value();
                view = showLayout(context, layoutId);
            } else {
                view = showKeyAsText(context, key, null);
            }
            incomingState.restore(view);
            setContentView(view);
            callback.onTraversalCompleted();
        }

        private View showLayout(Context context, @LayoutRes int layout) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(layout, null);
        }

        private View showKeyAsText(Context context, Object key, @Nullable final Object nextScreenOnClick) {
            TextView view = new TextView(context);
            view.setText(key.toString());

            if (nextScreenOnClick == null) {
                view.setOnClickListener(null);
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Flow.get(v).set(nextScreenOnClick);
                    }
                });
            }
            return view;
        }
    }
}
