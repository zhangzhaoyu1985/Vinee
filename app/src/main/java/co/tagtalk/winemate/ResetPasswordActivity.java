package co.tagtalk.winemate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.tagtalk.winemate.thriftfiles.User;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button resetPasswordButton     = (Button)findViewById(R.id.reset_password_button);

        final EditText resetEmail = (EditText)findViewById(R.id.reset_password_email);

        if (resetPasswordButton != null) {
            resetPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidResetInfo(resetEmail)) {
                        User user = new User();
                        user.email    = resetEmail.getText().toString();

                        final ResetPasswordTask resetPasswordTask = new ResetPasswordTask(ResetPasswordActivity.this);

                        resetPasswordTask.execute(user);
                    }
                }
            });
        }

    }

    private boolean isValidResetInfo(EditText email) {
        if (email == null || email.getText().toString().isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, R.string.email_no_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
