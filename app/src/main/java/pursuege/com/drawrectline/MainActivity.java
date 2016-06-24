package pursuege.com.drawrectline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    private DrawLineView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view= (DrawLineView) findViewById(R.id.main_draw_line_view);
    }

    public void onclickFileColor(View v) {
        view.setFillRect();
    }
}
