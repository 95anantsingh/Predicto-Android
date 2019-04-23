package inc.poseidon.predicto;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    ImageButton medical, humanRes, fun;
    ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // toolbar.setLogo(R.drawable.ic_launcher_magneto);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
            */
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        medical = findViewById(R.id.sec1Button);
        humanRes = findViewById(R.id.sec2Button);
        fun = findViewById(R.id.sec3Button);
        logoImage = findViewById(R.id.home_logo);
        homeAnimation();

        medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        humanRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    private void homeAnimation(){
        medical.setY(2000);
        humanRes.setY(2000);
        fun.setY(2000);
        logoImage.setY(300);
        int delay = 1000;
        int delay2 = 1500;

        ObjectAnimator logosXAnim = ObjectAnimator.ofFloat(logoImage, "ScaleX", 0,1);
        ObjectAnimator logosYAnim = ObjectAnimator.ofFloat(logoImage, "ScaleY", 0,1);
        ObjectAnimator logoAlphaAnim = ObjectAnimator.ofFloat(logoImage, "Alpha", 0,1);
        ObjectAnimator logoYAnim = ObjectAnimator.ofFloat(logoImage, "Y", 300,0);
        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.setDuration(delay2);
        logoAnim.play(logoYAnim).after(logoAlphaAnim);
        logoAnim.playTogether(logosXAnim,logosYAnim,logoAlphaAnim);

        ObjectAnimator animYmedical = ObjectAnimator.ofFloat(medical, "Y", 1000,40);
        ObjectAnimator animAlphamedical = ObjectAnimator.ofFloat(medical,"Alpha",0.000f,1.000f);
        animYmedical.setDuration(delay);
        animAlphamedical.setDuration(delay);
        AnimatorSet medicalAnim = new AnimatorSet();
        medicalAnim.playTogether(animYmedical,animAlphamedical);

        ObjectAnimator animYfun = ObjectAnimator.ofFloat(fun, "Y", 1000,200);
        ObjectAnimator animAlphafun = ObjectAnimator.ofFloat(fun,"Alpha",0.000f,1.000f);
        animAlphafun.setDuration(delay);
        animYfun.setDuration(delay);
        AnimatorSet funAnim = new AnimatorSet();
        funAnim.setStartDelay(300);
        funAnim.playTogether(animYfun,animAlphafun);

        ObjectAnimator animYhumanRes = ObjectAnimator.ofFloat(humanRes, "Y", 1000,360);
        ObjectAnimator animAlphahumanRes = ObjectAnimator.ofFloat(humanRes,"Alpha",0.000f,1.000f);
        animAlphahumanRes.setDuration(delay);
        animYhumanRes.setDuration(delay);
        AnimatorSet humanResAnim = new AnimatorSet();
        humanResAnim.setStartDelay(600);
        humanResAnim.playTogether(animYhumanRes,animAlphahumanRes);

        AnimatorSet homeAnim = new AnimatorSet();
        homeAnim.play(logoAnim).before(medicalAnim);
        homeAnim.playTogether(medicalAnim,humanResAnim,funAnim);
        homeAnim.setInterpolator(new DecelerateInterpolator());
        homeAnim.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent =new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeAnimation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        homeAnimation();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_disease_1) {
            Intent intent =new Intent(this,DiabetesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_disease_2) {
            Intent intent =new Intent(this,BreastCancerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact_us) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
