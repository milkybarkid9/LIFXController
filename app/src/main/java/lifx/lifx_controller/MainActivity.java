package lifx.lifx_controller;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String baseUrl="https://api.lifx.com/v1/";
    public static String auth;

    private Button toggleLightButton;
    private Button initButton;
    private Button redButton;
    private Button blueButton;
    private Button submitTokenButton;

    private EditText tokenEditText;

    private LightObj allLights;
    private List<LightObj> lights;
    private List<LightObj> groups;
    private List<LightObj> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init light lists
        lights = new ArrayList<>();
        groups = new ArrayList<>();
        locations = new ArrayList<>();
        new InitLight("all", allLights).execute();

        toggleLightButton = (Button) findViewById(R.id.toggleLightButton);
        toggleLightButton.setOnClickListener(toggleLightButtonHandler);
        initButton = (Button) findViewById(R.id.initButton);
        initButton.setOnClickListener(initButtonHandler);
        redButton = (Button) findViewById(R.id.redButton);
        redButton.setOnClickListener(redButtonHandler);
        blueButton = (Button) findViewById(R.id.blueButton);
        blueButton.setOnClickListener(blueButtonHandler);
        submitTokenButton = (Button) findViewById(R.id.submitTokenButton);
        submitTokenButton.setOnClickListener(submitTokenButtonHandler);

        tokenEditText = (EditText) findViewById(R.id.tokenEditText);
    }

    private int getLightIndex(String selector, List<LightObj> lightObjs){
        if(! lightObjs.isEmpty())
            for (int i = 0; i < lightObjs.size(); i++ ){
                if (lightObjs.get(i).getSelector().equals(selector))
                    return i;
            }
        return -1;
    }

    View.OnClickListener toggleLightButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            new ToggleLight(allLights).execute();
        }
    };

    View.OnClickListener initButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            new InitLight("all", allLights).execute();
            toggleLightButton.setAlpha(1f);
            toggleLightButton.setClickable(true);
        }
    };

    View.OnClickListener redButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            new SetState(allLights, null, "red", null, null).execute();
        }
    };

    View.OnClickListener blueButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            new SetState(allLights, null, "blue", null, null).execute();
        }
    };

    View.OnClickListener submitTokenButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            auth = tokenEditText.getText().toString();
            tokenEditText.setText("");
            System.out.println("Got auth token: "+auth);
        }
    };


    private class InitLight extends AsyncTask<LightObj, Void, Integer> {
        private LightObj lightObj;
        private String selector;

        InitLight(String selector, LightObj lightObj){
            this.lightObj = lightObj;
            this.selector = selector;
        }

        @Override
        protected Integer doInBackground(LightObj... name) {
            lightObj = new LightObj(selector);
            allLights = lightObj;
            return 0;
        }
    }

    private class ToggleLight extends AsyncTask<LightObj, Void, Integer> {
        private LightObj lightObj;

        ToggleLight(LightObj lightObj){
            this.lightObj = lightObj;
        }

        @Override
        protected Integer doInBackground(LightObj... name) {
            if ( lightObj != null ) {
                lightObj.toggle();
            }else {
                System.out.println("Lights not initialised");
            }
            return 0;
        }
    }

    private class SetState extends AsyncTask<LightObj, Void, Integer> {
        private LightObj lightObj;
        private Integer state;
        private String colour;
        private Double brightness;
        private Double duration;

        SetState(LightObj lightObj, Integer state, String colour, Double brightness, Double duration){
            this.lightObj = lightObj;
            this.state = state;
            this.colour = colour;
            this.brightness = brightness;
            this.duration = duration;
        }

        @Override
        protected Integer doInBackground(LightObj... name) {
            if ( lightObj != null ) {
                lightObj.setState(state, colour, brightness, duration);
            }else {
                System.out.println("Lights not initialised");
            }
            return 0;
        }
    }
}
