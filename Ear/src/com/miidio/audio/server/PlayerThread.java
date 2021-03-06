package com.miidio.audio.server;

import javax.sound.sampled.SourceDataLine;
import java.io.InputStream;

/**
 * @author hchu
 */
public class PlayerThread extends Thread {

    private SourceDataLine line;
    private InputStream in;
    private boolean stop;
    private boolean endWithNoData;

    public PlayerThread(SourceDataLine line, InputStream in, boolean endWhileNoData) {
        this.line = line;
        this.in = in;
        this.endWithNoData = endWhileNoData;
    }

    public synchronized boolean isStop() {
        return stop;
    }

    public synchronized void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        this.setStop(false);
        int read;
        byte[] buffer = new byte[1024 * 64];
        try {
            while (!this.isStop()) {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    line.write(buffer, 0, read);
                    line.drain();
                } else if (endWithNoData){
                    this.setStop(true);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            line.close();
        }
    }
}
