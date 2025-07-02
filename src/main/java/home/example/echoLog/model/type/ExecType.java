package home.example.echoLog.model.type;

public enum ExecType {
    ECHO("echo"),
    MKDIR("mkdir"),
    CD("cd"),
    LS("ls"),
    TREE("tree");

    private final String type;
    ExecType(String type) {
        this.type = type;
    }
}
