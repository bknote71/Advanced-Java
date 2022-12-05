package core.inet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.*;

public class PooledWobLogger {

    public static void main(String[] args) {
        final ExecutorService es = Executors.newFixedThreadPool(4);

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                final String line = br.readLine();
                if (line.equals("end"))
                    break;
                final LookupTask lookupTask = new LookupTask(line);
                final Future<String> future = es.submit(lookupTask);
                final LogEntry logEntry = new LogEntry(line, future);
                es.submit(new PrintTask(logEntry));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class LookupTask implements Callable<String> {
        String line;

        public LookupTask(String line) {
            this.line = line;
        }

        @Override
        public String call() throws Exception {
            final InetAddress address = InetAddress.getByName(line);
            return "hostname: " + address.getHostName();
        }
    }

    static class LogEntry {
        String original;
        Future<String> future;
        public LogEntry(String original, Future<String> future) {
            this.original = original; this.future = future;
        }
    }

    static class PrintTask implements Runnable {
        LogEntry logEntry;
        public PrintTask(LogEntry logEntry) {
            this.logEntry = logEntry;
        }

        @Override
        public void run() {
            final String line;
            try {
                line = logEntry.future.get();
                System.out.println(line);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(logEntry.original);
            }
        }
    }

}
