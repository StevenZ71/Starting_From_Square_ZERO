public class Main{
    private static Game game = new Game();
    public static void main(String[] args){
        System.out.println(game);
        game.runGame();
    }
    public static Game getGame(){return game;}
    public String toString(){return "MainClass";}
    public boolean equals(Object other){return this==other;}
}
//Credits:
// https://github.com/javacodingcommunity/Snake-Game-Java/tree/main helped me start
// Ryi Snow on YouTube helped me draw images and not just manually draw shapes on screen
// Sprites were made using Scratch costume paint and PiskelApp