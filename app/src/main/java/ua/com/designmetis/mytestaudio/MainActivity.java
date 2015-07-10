package ua.com.designmetis.mytestaudio;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity implements Runnable  {

    private TextView statusText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        statusText = (TextView) findViewById(R.id.tvStatus);
        Button recordButton = (Button) findViewById(R.id.buttonRecord);
        Button playButton = (Button) findViewById(R.id.buttonPlay);

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                record_thread();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Thread thread = new Thread(MainActivity.this);
                thread.start();
            }
        });
    }

    String text_string;
    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            updateResultsInUi(text_string);
        }
    };

    private void updateResultsInUi(String update_txt) {
        statusText.setText(update_txt);
    }

    // ����� ������
    private void record_thread() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                text_string = "Record";
                mHandler.post(mUpdateResults);
                record();
                text_string = "End";
                mHandler.post(mUpdateResults);
            }
        });
        thread.start();
    }

    // ��������� ������ �����
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int frequency = 11025; // ��

    // ������ ������ ������
    int bufferSize = 50 * AudioTrack.getMinBufferSize(frequency,
            AudioFormat.CHANNEL_OUT_MONO, audioEncoding);

    // ������� ������ AudioRecord ��� ������ �����
    public AudioRecord audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC, frequency,
            AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);

    // ������� ������ AudioTrack � ���� �� �����������
    public AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
            frequency, AudioFormat.CHANNEL_OUT_MONO, audioEncoding, 4096,
            AudioTrack.MODE_STREAM);

    // ����� ��� �����
    short[] buffer = new short[bufferSize];

    // ������� ������ �����
    public void record() {
        try {
            // �������� ������
            audioRecord.startRecording();
            // ������ ���� � �����
            audioRecord.read(buffer, 0, bufferSize);
            // ������� ������
            audioRecord.stop();
        } catch (Throwable t) {
            Log.e("AudioDemo", "������ 1");
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        int i = 0;
        while (i < bufferSize) {
            audioTrack.write(buffer, i++, 1);
        }
        return;
    }

    // �������� ��� �����
    @Override
    protected void onPause(){
        if(audioTrack!=null){
            if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
                audioTrack.pause();
            }
        }
        super.onPause();
    }
}
