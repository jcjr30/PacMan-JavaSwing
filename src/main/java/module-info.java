module PacManSwing {
    requires java.desktop;

    opens com.jcjr30.pacman;
    opens com.jcjr30.boardCreator;

    exports com.jcjr30.pacman;
    exports com.jcjr30.boardCreator;
}