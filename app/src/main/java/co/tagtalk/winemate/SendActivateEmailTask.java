package co.tagtalk.winemate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class SendActivateEmailTask extends AsyncTask<Integer, Void, Boolean> {

    private LoginActivity activity;
    private boolean gotException;

    public SendActivateEmailTask(LoginActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
        Boolean response = false;

        try{

            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            WineMateServices.Client client = new WineMateServices.Client(protocol);

            response = client.sendActivateEmail(params[0]);

        } catch (TException x) {
            x.printStackTrace();
            gotException = true;
        }
        transport.close();

        return response;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (gotException || !success) {
            Toast.makeText(activity, R.string.login_activity_send_activate_email_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog.
        ProgressDialog progress;
        progress = ProgressDialog.show(activity, activity.getString(R.string.dialog_title_sending_email),
                activity.getString(R.string.dialog_text_be_patient), true);
        activity.setProgressDialog(progress);

        Intent intent = new Intent();
        /*
        intent.setClassName("co.tagtalk.winemate",
        "co.tagtalk.winemate.SignUpSuccessActivity");
        Pass in extras to update title and description text.
        */
        intent.putExtra("updateSuccessTitle", activity.getString(R.string.sent_activation_email_success_title));
        intent.putExtra("updateSuccessText", activity.getString(R.string.sent_activation_email_success_notice));

        activity.startActivity(intent);
        activity.finish();
    }
}
