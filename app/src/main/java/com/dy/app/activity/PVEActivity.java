package com.dy.app.activity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileOutputStream;

public class PVEActivity extends FragmentHubActivity {
    public static final String TAG = "PVEActivity";

    private Process process;
    private Thread outputThread;
    private EngineMessageListener engineMessageListener;
    private volatile boolean readingOutput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startEngine();
        setUp();
        doTest();
    }

    private void setUp() {
        String evalFilePath = getEvalFilePath();
        if (evalFilePath != null) {
            sendEngineCommand("setoption name EvalFile value " + evalFilePath);
        } else {
            Log.w(TAG, "NNUE network not found. Put a .nnue file under assets/engine, assets/eval or assets/test.");
        }

        String smallNnuePath = getEvalSmallFilePath();
        if (smallNnuePath != null) {
            sendEngineCommand("setoption name EvalFileSmall value " + smallNnuePath);
        }
    }

    private void doTest() {
        setEngineMessageListener(line -> {
            Log.d(TAG, "ENGINE: [RECEIVED]\t" + line);
        });

        Thread tester = new Thread(() -> {
            try {
                sendEngineCommand("uci");
                Thread.sleep(200);
                sendEngineCommand("isready");
                Thread.sleep(200);
                sendEngineCommand("ucinewgame");
                Thread.sleep(100);
                sendEngineCommand("position startpos");
                Thread.sleep(100);
                sendEngineCommand("go movetime 1000");
            } catch (InterruptedException ignored) {}
        });
        tester.start();
    }

    private String getEvalFilePath() {
        // .nnue file in assets/engine/eval
        return ensureEvalFromAssetsDir("engine/eval");
    }

    private String getEvalSmallFilePath() {
        // .nnue file in assets/engine/eval_small
        return ensureEvalFromAssetsDir("engine/eval_small");
    }

    private String ensureEvalFromAssetsDir(String assetDir) {
        File destDir = new File(getFilesDir(), "engine");
        if (!destDir.exists() && !destDir.mkdirs()) {
            return null;
        }

        try {
            String[] names = getAssets().list(assetDir);
            if (names == null || names.length == 0) return null;
            for (String name : names) {
                if (!name.endsWith(".nnue")) continue;
                File dest = new File(destDir, name);
                if (dest.exists() && dest.length() > 0) {
                    return dest.getAbsolutePath();
                }
                String assetPath = assetDir.endsWith("/") ? (assetDir + name) : (assetDir + "/" + name);
                try (InputStream in = getAssets().open(assetPath);
                     FileOutputStream out = new FileOutputStream(dest)) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) != -1) {
                        out.write(buf, 0, r);
                    }
                    out.flush();
                    return dest.getAbsolutePath();
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    private void startEngine() {
        String path = getApplicationContext().getApplicationInfo().nativeLibraryDir + "/lib_stockfish.so";
        File file = new File(path);

        try {
            process = new ProcessBuilder(file.getPath())
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        readingOutput = true;
        outputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process processOut = process;
                if (processOut == null) {
                    return;
                }

                try (BufferedReader out = new BufferedReader(new InputStreamReader(processOut.getInputStream()))) {
                    String data;
                    while (readingOutput && (data = out.readLine()) != null) {
                        Log.d(TAG, data);
                        EngineMessageListener listener = engineMessageListener;
                        if (listener != null) {
                            String finalData = data;
                            runOnUiThread(() -> listener.onEngineMessage(finalData));
                        }
                    }
                } catch (IOException e) {
                    // ignore
                }
            }
        });
        outputThread.start();
    }

    public void sendEngineCommand(String command) {
        Log.d(TAG, "ENGINE: [SENT]\t" + command);
        String cmd = command + "\n";
        try {
            Process ep = process;
            if (ep != null) {
                ep.getOutputStream().write(cmd.getBytes());
                ep.getOutputStream().flush();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    protected void onDestroy() {
        stopEngine();
        super.onDestroy();
    }

    private void stopEngine() {
        readingOutput = false;
        try {
            Process ep = process;
            if (ep != null) {
                try {
                    ep.getOutputStream().write(("quit\n").getBytes());
                    ep.getOutputStream().flush();
                } catch (IOException ignored) {}
                try {
                    ep.getOutputStream().close();
                } catch (IOException ignored) {}
            }
        } catch (Exception ignored) {}

        if (outputThread != null && outputThread.isAlive()) {
            try {
                outputThread.join(1000);
            } catch (InterruptedException ignored) {}
        }

        if (process != null) {
            try {
                process.waitFor();
            } catch (InterruptedException ignored) {}
            if (process.isAlive()) {
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
            process = null;
        }
        engineMessageListener = null;
    }

    public interface EngineMessageListener {
        void onEngineMessage(String line);
    }

    public void setEngineMessageListener(EngineMessageListener listener) {
        this.engineMessageListener = listener;
    }
}


