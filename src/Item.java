import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Item{
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private String name;
    private BufferedImage image;
    private boolean removable;
    private boolean interactable;
    private int index;
    private int mapX;
    private int mapY;
    private int size = 1;
    private static Game game;
    private static BufferedImage[] images;
    public Item(){
        images = new BufferedImage[15];
        images[0] = image("box");
        images[1] = image("ribbon");
        images[2] = image("bread");
        images[3] = images[0];
        images[4] = image("teleportPad");
        images[5] = images[4];
        images[6] = image("brokenBox");
        images[7] = image("rippedPaper");
        images[8] = images[7];
        images[9] = images[7];
        images[10] = images[7];
        images[11] = image("log");
        images[12] = image("mossLog");
        images[13] = image("mossRock");
        images[14] = image("cutMossPillar");
    }
    public Item(String n, int x, int y, int mX, int mY){
        name = n;
        xPos = x;
        yPos = y;
        mapX = mX;
        mapY = mY;
        if(name.equals("box")){
            index = 0;
            width = 25;
            height = 25;
            interactable = true;
        }
        else if(name.equals("ribbon")){
            index = 1;
            width = 32;
            height = 15;
            interactable = true;
        }
        else if(name.equals("bread")){
            index = 2;
            width = 48;
            height = 15;
            interactable = true;
        }
        else if(name.equals("boxCoin")){
            index = 3;
            width = 25;
            height = 25;
            interactable = true;
        }
        else if(name.equals("teleportPad")){
            index = 4;
            width = 25;
            height = 25;
            size = 2;
            interactable = true;
        }
        else if(name.equals("unusableTeleportPad")){
            index = 5;
            width = 25;
            height = 25;
            size = 2;
            interactable = true;
        }
        else if(name.equals("brokenBox")){
            index = 6;
            width = 25;
            height = 25;
            interactable = true;
        }
        else if(name.equals("rippedPaper1")){
            index = 7;
            width = 50;
            height = 50;
            interactable = true;
        }
        else if(name.equals("rippedPaper2")){
            index = 8;
            width = 50;
            height = 50;
            interactable = true;
        }
        else if(name.equals("rippedPaper3")){
            index = 9;
            width = 50;
            height = 50;
            interactable = true;
        }
        else if(name.equals("rippedPaper4")){
            index = 10;
            width = 50;
            height = 50;
            interactable = true;
        }
        else if(name.equals("log")){
            index = 11;
            width = 50;
            height = 50;
            interactable = false;
        }
        else if(name.equals("mossLog")){
            index = 12;
            width = 50;
            height = 50;
            interactable = false;
        }
        else if(name.equals("mossRock")){
            index = 13;
            width = 50;
            height = 50;
            interactable = false;
        }
        else if(name.equals("cutMossPillar")){
            index = 14;
            width = 50;
            height = 100;
            interactable = false;
        }
        else{
            index = -1;
            width = 250;
            height = 250;
        }
        image = images[index];
    }
    public void alterDimension(int w, int h){
        width = w;
        height = h;
    }
    private BufferedImage image(String fileName){
        BufferedImage image = null;
        try {image = ImageIO.read(getClass().getResourceAsStream("/items/" + fileName + ".png"));} catch (IOException e) {}
        return image;
    }
    public int width(){return width;}
    public int height(){return height;}
    public int index(){return index;}
    public static void setGame(Game g){
        game = g;
    }

    public String name() {return name;}
    public int size(){return size;}

    public int getxPos(){return xPos + mapX*1375 + game.getGameScreen().getScrollX();}
    public int getyPos(){return yPos + mapY*775 + game.getGameScreen().getScrollY();}
    public void moveX(int x){xPos += x;}
    public void moveY(int y){yPos += y;}
    public void remove(){
        removable = true;
        if(name.equals("ribbon")){
            Main.getGame().getInventory().add("ribbon");
        }
        if(name.equals("bread")){
            Main.getGame().getInventory().add("bread");
        }
    }
    public boolean isRemovable(){return removable;}
    public BufferedImage getImage(){return image;}
    public void rotate(int degrees){
        image = game.getGameScreen().rotateImage(degrees, image);
    }
    public int getMapX(){return mapX;}
    public int getMapY(){return mapY;}
    public boolean interactable(){return interactable;}
    public String toString(){
        return name;
    }
    public boolean equals(Object other){
        return other==this;
    }
}
