package jboyflaga2.com.starterproject.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import jboyflaga2.com.starterproject.R;
import jboyflaga2.com.starterproject.core.DaggerService;
import jboyflaga2.com.starterproject.core.MainComponent;

public class MainView extends LinearLayout {
    @Inject
    MainScreen.Presenter presenter;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //DaggerService.<MainScreen.Component>getDaggerComponent(context).inject(this);
        DaggerService.<MainComponent>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.get_contact_button)
    protected void onGetContactButtonClick() {
        presenter.getContact();
    }

    @OnClick(R.id.go_to_another_view_button)
    protected void onGoTOAnotherViewButtonClick() {
        presenter.goToAnotherView();
    }

    public void goToScreen(Object screen) {
        Flow.get(MainView.this).set(screen);
    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
