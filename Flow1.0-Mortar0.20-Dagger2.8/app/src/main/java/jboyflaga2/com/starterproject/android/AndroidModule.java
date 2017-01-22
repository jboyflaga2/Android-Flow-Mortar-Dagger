package jboyflaga2.com.starterproject.android;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by User-01 on 1/22/2017.
 */

@Module
public class AndroidModule {

    private final  ActivityResultPresenter.View activityResultPresenterView;
    private final ActionBarOwner.Activity actionBarOwnerActivity;

    public AndroidModule(ActivityResultPresenter.View activityResultPresenterView,
                      ActionBarOwner.Activity actionBarOwnerActivity) {
        this.activityResultPresenterView = activityResultPresenterView;
        this.actionBarOwnerActivity = actionBarOwnerActivity;
    }

    @Provides
    @Singleton
    ActivityResultPresenter provideActivityResultPresenter() {
        ActivityResultPresenter activityResultPresenter = new ActivityResultPresenter();
        activityResultPresenter.takeView(activityResultPresenterView);
        return activityResultPresenter;
    }

    @Provides
    @Singleton
    static ActivityResultRegistrar provideIntentLauncher(ActivityResultPresenter presenter) {
        return presenter;
    }

    @Provides
    @Singleton
    ActionBarOwner provideActionBarOwner() {
        ActionBarOwner actionBarOwner = new ActionBarOwner();
        actionBarOwner.takeView(actionBarOwnerActivity);
        return actionBarOwner;
    }
}
