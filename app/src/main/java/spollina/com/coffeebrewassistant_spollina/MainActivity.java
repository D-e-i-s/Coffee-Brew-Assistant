package spollina.com.coffeebrewassistant_spollina;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SeekBar ouncesWaterSeekBar;
    private Switch measurementStyle;
    private TextView currentOuncesWaterTextView;
    private TextView currentTablespoonsCoffeeTextView;
    private TextView brewCountdownTextView;
    private TextView brewCountdown;
    private ImageView frenchPressWaterImage;
    private ImageView steamLines;
    private Button startBrewTimerButton;
    private static long millisecondsToRun = 240000; // default is 4 minutes, or 240 seconds

    public static boolean usingUSMeasurements = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ouncesWaterSeekBar = (SeekBar) findViewById(R.id.waterAmountSeekBar);
        measurementStyle = (Switch) findViewById(R.id.measurementStyle);
        currentOuncesWaterTextView = (TextView) findViewById(R.id.waterAmountTextView);
        currentTablespoonsCoffeeTextView = (TextView) findViewById(R.id.coffeeAmountTextView);
        brewCountdownTextView = (TextView) findViewById(R.id.brewCountdown);
        startBrewTimerButton = (Button) findViewById(R.id.brewButton);
        frenchPressWaterImage = (ImageView) findViewById(R.id.frenchPressWater);
        steamLines = (ImageView) findViewById(R.id.steamLines);


        // Seek bar listener
        ouncesWaterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // SeekBar uses multiples of 2

                if(!measurementStyle.isChecked())
                {
                    int ozAmount = i * 2;
                    currentOuncesWaterTextView.setText(getString(R.string.US_oz, String.format(java.util.Locale.US, "%d", ozAmount)));

                    // Determines whether fraction needs to be used or not

                    if (ozAmount % 6 == 0)
                    {
                        currentTablespoonsCoffeeTextView.setText(getString(R.string.US_Tbsp, String.format(java.util.Locale.US, "%d", (ozAmount / 6))));
                    }
                    else
                    {
                        currentTablespoonsCoffeeTextView.setText(getString(R.string.US_Tbsp_fraction, String.format(java.util.Locale.US, "%d", (ozAmount / 6)), String.format(java.util.Locale.US, "%d", (ozAmount % 6) / 2)));
                    }
                }
                else
                {
                    int mlAmount = i * 2;
                    currentOuncesWaterTextView.setText(getString(R.string.metric_ml, String.format(java.util.Locale.US, "%.0f", mlAmount * 29.55)));
                    currentTablespoonsCoffeeTextView.setText(getString(R.string.metric_grams, String.format(java.util.Locale.US, "%.1f", (mlAmount * 2.38))));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void switchOnClick(View v) {
        Switch measurementStyle = (Switch)v;
        if(measurementStyle.isChecked()) {
            measurementStyle.setText(R.string.metric_measurement);
            usingUSMeasurements = false;

            // Used to update to new conversion values
            ouncesWaterSeekBar.setProgress(ouncesWaterSeekBar.getProgress() + 1);
            ouncesWaterSeekBar.setProgress(ouncesWaterSeekBar.getProgress() - 1);
        }
        else {
            measurementStyle.setText(R.string.US_measurement);
            usingUSMeasurements = true;

            // Used to update to new conversion values
            ouncesWaterSeekBar.setProgress(ouncesWaterSeekBar.getProgress() + 1);
            ouncesWaterSeekBar.setProgress(ouncesWaterSeekBar.getProgress() - 1);
        }

    }

    // Used to prevent button from being clicked twice.  startBrewTimerButton.setEnabled(false) didn't prevent multiple initial clicks from queuing the timer multiple times
    boolean handledTimerButtonClicked = false;
    public void brewButtonOnClick(View v) {

        //Sets colors back to default blue, in case someone decides to run the timer twice
        frenchPressWaterImage.setColorFilter(Color.rgb(147, 177, 170));

        if(!handledTimerButtonClicked) {
            handledTimerButtonClicked = true;
            steamLines.setVisibility(View.INVISIBLE);
            //reset waters color values

            new CountDownTimer(millisecondsToRun, 1000) {

                // Water    R: 147 G: 177 B: 170        (147,177,170)
                // Coffee   R: 87 G: 56 B: 26           (87, 56, 26)
                int initRed = 147; int initGreen = 177; int initBlue = 170;
                int minRed = 87; int minGreen = 56; int minBlue = 26;

                public void onTick(long millisUntilFinished) {

                    long secondsUntilFinished = millisUntilFinished / 1000;

                    // Handle timer display updating
                    if (secondsUntilFinished % 60 < 10)
                    {
                        brewCountdownTextView.setText(secondsUntilFinished / 60 + " : " + "0" + secondsUntilFinished % 60);
                    }
                    else
                    {
                        brewCountdownTextView.setText(secondsUntilFinished / 60 + " : " + secondsUntilFinished % 60);
                    }

                    //Handle color changing
                    if(initRed > minRed)
                    {
                        initRed -= 4;
                    }
                    if(initGreen > minGreen)
                    {
                        initGreen -= 6;
                    }
                    if(initBlue > minBlue)
                    {
                        initBlue -= 9;
                    }
                    frenchPressWaterImage.setColorFilter(Color.rgb(initRed, initGreen, initBlue));
                }

                public void onFinish() {
                    // Show timer completion message
                    brewCountdownTextView.setText(R.string.brew_button_complete);
                    // Make steam lines appear above french press
                    steamLines.setVisibility(View.VISIBLE);
                    // Play sound to notify user of timer completion
                    MediaPlayer alarm = MediaPlayer.create(MainActivity.this,R.raw.alarm_beep);
                    alarm.start();
                    // Allow brew button to be clicked again
                    handledTimerButtonClicked = false;
                }
            }.start();
        }
    }
}
