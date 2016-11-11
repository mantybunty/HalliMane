package com.example.nata.hallimane;

/**
 * Created by nata on 27/5/16.
 */
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.regex.Pattern;
public class Validation extends AppCompatActivity{

    //Regular Expression
    //nata u can change this whenever u want
    //standord mail = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\[A-Za-z]{2,})$";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z-0-9-]+(\\.[A-Za-z]{2,})$";//*(\\[A-Za-z]{2,})$";
    // private static final String PHONE_REGEX = "^[0-9]{10}";
    private static final String PASSWORD_REGEX = "^[a-zA-Z0-9*]{8,15}";
    private static final String USER_NAME = "^[A-Za-z0-9.*]{4,15}";

    //Error message
    private static final String REQUIREQ_MSG = "required";
    private static final String EMAIL_MSG = "invalid email ";
    private static final String PHONE_MSG = "##########";
    private static final String PASSWORD_MSG ="(8<Password Length<16) and can use * as special character";
    private static final String USER_MSG = "Username may contain (a-z) (*.) and (numbers), 4<length<15";

    //call this method when u need username validation
    public static boolean isUsername(EditText editText,boolean required){
        return isValid(editText,USER_NAME,USER_MSG,required);
    }
    //call this method when u need email validation
    public static boolean isEmailAddress(EditText editText,boolean required){
        return isValid(editText,EMAIL_REGEX,EMAIL_MSG,required);
    }

    /*
    //call this method when u need phone validation
    public static boolean isPhoneNumber(EditText editText,boolean required){
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }*/

    public static boolean isPassword(EditText editText,boolean required){
        return isValid(editText, PASSWORD_REGEX, PASSWORD_MSG, required);
    }
    //return true if the input field is valid based on the parameters passed
    public static boolean isValid(EditText editText,String regex,String errMsg,boolean required){
        String text = editText.getText().toString().trim();

        //clearing the error, if it was preveously set by some other values
        editText.setError(null);

        //text required and editText is blank,so return false
        if(required && !hasText(editText))
            return false;
        //pattern doesn't match so returning false
        if(required &&!Pattern.matches(regex,text)){
            editText.setError(errMsg);
            return false;
        }
        return true;
    }

    //check the input field has any text or not
    //return true if it contains txt otherwise false
    public static boolean hasText(EditText editText){
        String text= editText.getText().toString().trim();
        editText.setError(null);

        //length 0  means there is no tect
        if(text.length()==0){
            editText.setError(REQUIREQ_MSG);
            return false;
        }
        return true;
    }
}
