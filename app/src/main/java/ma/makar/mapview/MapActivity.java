package ma.makar.mapview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ma.makar.mapview.di.DependencyResolver;
import ma.makar.mapview.views.MapView;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        DependencyResolver resolver = DependencyResolver.create(this);

        MapView view = (MapView) findViewById(R.id.view_map);
        view.attachToView(
                resolver.getTileRepository(),
                resolver.getTilePreLoader(),
                resolver.getTileResource(),
                resolver.getCoordinatePool());
    }
}
