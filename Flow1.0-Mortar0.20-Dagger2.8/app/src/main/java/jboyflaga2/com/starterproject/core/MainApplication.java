
package jboyflaga2.com.starterproject.core;

import android.app.Application;
import mortar.MortarScope;

public class MainApplication extends Application {
    private MortarScope rootScope;

    @Override
    public Object getSystemService(String name) {
        if (rootScope == null) rootScope = MortarScope.buildRootScope().build("Root");

        return rootScope.hasService(name) ? rootScope.getService(name) : super.getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
