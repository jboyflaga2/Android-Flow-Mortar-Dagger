package jboyflaga2.com.starterproject.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import jboyflaga2.com.starterproject.R;
import jboyflaga2.com.starterproject.core.DaggerService;
import jboyflaga2.com.starterproject.core.MainComponent;

/**
 * Created by User-01 on 12/24/2016.
 */

public class AnotherView extends LinearLayout {

    @Inject
    AnotherScreen.Presenter presenter;

    public AnotherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //DaggerService.<AnotherScreen.Component>getDaggerComponent(context).inject(this);
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

    @OnClick(R.id.back_button)
    protected void onBackButtonClick() {
        Flow.get(AnotherView.this).goBack();
    }
}
