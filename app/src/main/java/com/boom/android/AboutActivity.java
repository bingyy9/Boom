package com.boom.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boom.android.log.Dogger;
import com.boom.android.util.AndroidVersionManager;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.FilesDirUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    private static final String EMAIL_ADDRESS_GOOGLE = "bingyy9@gmail.com";
    private static final String EMAIL_ADDRESS_126 = "bingyy9@126.com";

    View root;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_copy_rights)
    TextView tvCopyRights;
    @BindView(R.id.tv_contact_us)
    View tvContactUs;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvVersion.setText(getResources().getString(R.string.version, AndroidVersionManager.getVersion()));
        tvCopyRights.setText(getResources().getString(R.string.copy_rights, DataUtils.getYear()));
        tvContactUs.setOnClickListener((v)->{
            contactUs();
        });
    }

    private void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        String[] to;
        if(BoomHelper.enableGoogleService()){
            to = new String[]{EMAIL_ADDRESS_GOOGLE};
        } else {
            to = new String[]{EMAIL_ADDRESS_126};
        }
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, buildReportSubject());
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body));
        ArrayList<Uri> logsUri = getLogsUri();
        if(logsUri != null && logsUri.size() > 0) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, logsUri);
        }
        startActivity(intent);
    }

    private String buildReportSubject() {
        String version = AndroidVersionManager.getVersion();
        String productionName = getString(R.string.app_name);
        String versionNo = getString(R.string.version2, version);
        String date = DateFormat.getDateTimeInstance().format(new Date());
        return getString(R.string.email_subject, productionName, versionNo, date);
    }

    private ArrayList<Uri> getLogsUri(){
        ArrayList<Uri> logUris = new ArrayList<>();
        Uri uri = gatherZipLog();
        if (null == uri) {
            Dogger.e(Dogger.BOOM, "Cannot compress log file", "AboutActivity", "f");
            return null;
        }

        logUris.add(uri);
        return logUris;
    }

    public Uri gatherZipLog() {
        try {
            String outputPath = FilesDirUtil.getCacheZipLogPath(this);

            FileOutputStream dest = new FileOutputStream(outputPath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            // zip log
            ArrayList<File> logList = gatherLogUris();
            for (File log : logList)
                zipOneFile(log, out);

            // append anr file
            File anrFile = new File("/data/anr/traces.txt");
            if (anrFile.exists())
                zipOneFile(anrFile, out);

            out.close();

            return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + FilesDirUtil.fileProvider, new File(outputPath));
        } catch (Exception e) {
            Dogger.e(Dogger.BOOM, "Cannot gather zip log: ", "AboutActivity", "gatherZipLog", e);
            return null;
        }
    }

    public ArrayList<File> gatherLogUris() {
        ArrayList<File> logUris = new ArrayList<>();
        File folder = FilesDirUtil.getLogFile(this);
        if (folder == null || !folder.exists()) {
            return null;
        }
        String[] childs = folder.list();
        if (childs == null || childs.length == 0) {
            return null;
        }
        File tempFile = null;
        for (String child : childs) {
            if (child.endsWith(".dmp") || child.endsWith(".txt")) {
                tempFile = new File(folder, child);
                if (tempFile.isFile()) {
                    logUris.add(tempFile);
                }
            }
        }

        return logUris;
    }

    private void zipOneFile(File file, ZipOutputStream outputStream) {
        try {
            Dogger.i(Dogger.BOOM, "add:" + file, "AboutActivity", "zipOneFile");
            FileInputStream fi = new FileInputStream(file);
            int bufferLenght = 1024 * 1024 * 10;
            byte[] data = new byte[bufferLenght];

            BufferedInputStream origin = new BufferedInputStream(fi, bufferLenght);

            ZipEntry entry = new ZipEntry(file.getName());
            outputStream.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, bufferLenght)) != -1) {
                outputStream.write(data, 0, count);
            }
            origin.close();
        } catch (Exception e) {
            Dogger.e(Dogger.BOOM, "Cannto zip file " + file, "AboutActivity", "zipOneFile", e);
        }
    }

}