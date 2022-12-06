package ftp.cmd;

@FunctionalInterface
public interface Command {
    void execute(String arg);
}
