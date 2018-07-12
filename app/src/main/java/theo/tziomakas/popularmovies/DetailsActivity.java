package theo.tziomakas.popularmovies;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
public class DetailsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();

        String id = i.getStringExtra("movieId");
    }
}
