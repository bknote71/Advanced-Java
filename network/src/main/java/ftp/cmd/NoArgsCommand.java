package ftp.cmd;

@FunctionalInterface
public interface NoArgsCommand extends Command {
    void execute(); // 구현해야 할 인터페이스

    @Override
    default void execute(String arg) {
        execute();
    }
}
