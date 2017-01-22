package jboyflaga2.com.starterproject.core;

import javax.inject.Singleton;

import jboyflaga2.com.starterproject.android.AndroidModule;
import jboyflaga2.com.starterproject.ui.AnotherView;
import jboyflaga2.com.starterproject.ui.MainView;

@dagger.Component(modules = AndroidModule.class)
@Singleton
public interface MainComponent {
    void inject(MainView view);
    void inject(AnotherView view);
    void inject(MainActivity mainActivity);
}
