package ftp.cmd;

public final class CommandLine {
    Command command;
    boolean auth;

    public CommandLine(Command command, boolean auth) {
        this.command = command;
        this.auth = auth;
    }

    public Command command() {
        return command;
    }

    public boolean auth() {
        return auth;
    }
}
