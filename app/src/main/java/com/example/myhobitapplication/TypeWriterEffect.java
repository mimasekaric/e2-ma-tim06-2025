package com.example.myhobitapplication;
import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import android.os.Handler;
public class TypeWriterEffect extends androidx.appcompat.widget.AppCompatTextView{

    private CharSequence mytext;
    private int myIndex;
    private long myDelay = 150;

    public TypeWriterEffect(Context context){
        super(context);
    }

    public TypeWriterEffect(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
    }

    private Handler handler = new Handler();

    private Runnable characterAdder = new Runnable(){
        @Override
        public void run(){
            setText(mytext.subSequence(0,myIndex++));

            if(myIndex <= mytext.length()){
                handler.postDelayed(characterAdder, myDelay);
            }
        }
    };

    public void animateText(CharSequence text){
        mytext= text;
        myIndex = 0;

        setText("");
        handler.removeCallbacks(characterAdder);

        handler.postDelayed(characterAdder, myDelay);
    }

    public void setCharacterDelay( long d){
        myDelay = d;
    }
}
