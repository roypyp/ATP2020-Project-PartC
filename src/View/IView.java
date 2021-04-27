package View;


import java.io.IOException;

public interface IView {
    void Save() throws IOException;
    void Load() throws IOException, ClassNotFoundException;
    void New();
    void displayMaze(int[][] maze);
}
