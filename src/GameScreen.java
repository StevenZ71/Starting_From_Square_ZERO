import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class GameScreen extends JPanel{
    private BufferedImage wall, lock, ground, ground2, ground3, ground4, ground5,grass, sidewalk, tile, water, horizontalCliff, verticalCliff, cornerCliff, coin, duck, pathLeftRight, pathUpDown, pathLeftDown, pathLeftUp,
            pathRightDown, pathRightUp, pathConvergeUp, pathConvergeDown, pathConvergeLeft, pathConvergeRight, pathConvergeMiddle,quest,clock,note,hpBar;
    private boolean inStaticEffect;
    private int titleTick[] = new int[3];
    private BufferedImage title[];
    private BufferedImage[] skills;
    private BufferedImage[] passives;
    private BufferedImage[] buildings;
    private BufferedImage[] mazes = new BufferedImage[4];
    private BufferedImage[] cities;
    private BufferedImage[] specialMap = new BufferedImage[2];
    private BufferedImage[] specialImages = new BufferedImage[2];
    private BufferedImage[] statuses = new BufferedImage[3];
    private BufferedImage[] accessories;
    /**0 is player color, 1 is 0,0,0 black, 9 is pure white*/
    private Color[] colors;

    private static ArrayList<Integer> enemyNums = new ArrayList<>();
    private Person player;
    private ArrayList<Person> npcList;
    private ArrayList<Person> enemyList;
    private ArrayList<Attack> attacks;
    private ArrayList<Attack> effects;
    private ArrayList<Person> allyList;
    private Game frame;
    private int theWall = 1;
    private int optionsX = 645;
    private int textOptionBoxHeight = 25;
    private int arrowSpeed, arrowY;
    private String message;
    private boolean inChat;
    private boolean inChoosing;
    private boolean canInteract;
    private boolean menuVisible, menuTicking, menuTickIn;
    private boolean realSettingUp = true;
    private double clockThingy, hourClockThingy;
    private int npcTouched, textPart;
    /**MAP KEY 0 = Meadow, 1 = Town, - = Nothing, 2 = not implemented meadow
     * - - - 1 - - - - -
     * 1 0 0 1 0 0 0 0 1    <--- Y=0
     * - - - 1 0 0 - - 1    <--- Y=1
     * - - 0 0 2 2 2 2 1    <--- The path will go from here to illusionist town
     * - - 0 0 - - - - 0    <--- Y=3
     * - - 1 1 0 0 0 0 1    <--- Y=4
     * - - 1 1 - - - - -    <--- Y=5
     * */
    private int mapNumX, mapNumY = 1;
    private int menu, menuSelectY, menuSelectX, menuScrollY, menuTick;

    //56 (across) by 32 (down) map
    /*14,18*/
    private final int rows = 32;
    /*25,30*/
    private final int columns = 56;
    private final double zoom= 1;
    private final int squareSize = (int)(zoom*25);
    private int special = -1;
    private int[][] map = new int[rows+1][columns+1];
    private int[][] collisions = new int[rows+1][columns+1];
    private int[] upperBounds = new int[columns+1];
    private int[] lowerBounds = new int[columns+1];
    private int xPosSkills, xPosSkills2;
    private boolean skillsBounce;
    private int skillNum;
    private boolean typing;
    private int typed;
    private int mainMenuX;
    private String location;
    private int locationY;
    private boolean locationDown;
    private int locationTick;
    private boolean instructions;
    private BufferedImage theMap;
    private BufferedImage[][] wholeMap = new BufferedImage[100][100];
    private BufferedImage[][] mapBuildings = new BufferedImage[100][100];
    private int[][] allCollisions = new int[3200][5600];
    private boolean imageGet;
    private boolean touchedPreviously;
    private int lastX, lastY, pointX, pointY;
    private BufferedImage lastImage;
    private int[] last = {-1,0};
    //    private boolean mapChanging;
    private BufferedImage[] staticEffect;
    private BufferedImage[] bar;
    private int scrollX, scrollY=-800;
    private boolean settingUp = true;
    private boolean buildingsImage = false;
    private int defeats;
    private int messageTick;
    private boolean darkScreen;
    private boolean darkScreenIn;
    private int darkScreenTick;
    private boolean barricaded;
    private Person moving;
    private int instruction;
    public GameScreen(Game window){
        this.frame = window;
        setDoubleBuffered(true);
        player = window.getPlayer();
        npcList = window.getNpcList();
        enemyList = window.getEnemyList();
        attacks = window.getAttacks();
        effects = window.getEffects();
        allyList = window.getAllyList();
        try {wall = ImageIO.read(getClass().getResourceAsStream("/mapAssets/wall.png"));} catch (IOException e) {}
        try {lock = ImageIO.read(getClass().getResourceAsStream("/misc/lock.png"));} catch (IOException e) {}
        try {ground = ImageIO.read(getClass().getResourceAsStream("/mapAssets/ground.png"));} catch (IOException e) {}
        try {ground2 = ImageIO.read(getClass().getResourceAsStream("/mapAssets/mossGround.png"));} catch (IOException e) {}
        try {ground3 = ImageIO.read(getClass().getResourceAsStream("/mapAssets/groundGrass.png"));} catch (IOException e) {}
        try {ground4 = ImageIO.read(getClass().getResourceAsStream("/mapAssets/grassGround2.png"));} catch (IOException e) {}
        try {ground5 = ImageIO.read(getClass().getResourceAsStream("/mapAssets/groundGrass2.png"));} catch (IOException e) {}

        try {grass = ImageIO.read(getClass().getResourceAsStream("/mapAssets/grass.png"));} catch (IOException e) {}
        try {sidewalk = ImageIO.read(getClass().getResourceAsStream("/mapAssets/sidewalk.png"));} catch (IOException e) {}
        try {tile = ImageIO.read(getClass().getResourceAsStream("/mapAssets/tile.png"));} catch (IOException e) {}
        try {water = ImageIO.read(getClass().getResourceAsStream("/mapAssets/water.png"));} catch (IOException e) {}
        try {horizontalCliff = ImageIO.read(getClass().getResourceAsStream("/mapAssets/grassCliff.png"));} catch (IOException e) {}
        //verticalCliff = rotateImage(90,horizontalCliff);
        try {verticalCliff = ImageIO.read(getClass().getResourceAsStream("/mapAssets/grassCliff2.png"));} catch (IOException e) {}

        try {cornerCliff = ImageIO.read(getClass().getResourceAsStream("/mapAssets/cornerGrassCliff.png"));} catch (IOException e) {}

        try {note = ImageIO.read(getClass().getResourceAsStream("/misc/message.png"));} catch (IOException e) {}
        buildings = new BufferedImage[10];
        try {buildings[0] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/house.png"));} catch (IOException e) {}
        try {buildings[1] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/house1.png"));} catch (IOException e) {}
        try {buildings[2] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/house2.png"));} catch (IOException e) {}
        try {buildings[3] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/house3.png"));} catch (IOException e) {}
        try {buildings[4] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/apartment1.png"));} catch (IOException e) {}
        try {buildings[5] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/apartment2.png"));} catch (IOException e) {}
        try {buildings[6] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/apartment3.png"));} catch (IOException e) {}
        try {buildings[7] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/rssBuilding.png"));} catch (IOException e) {}
        try {buildings[8] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/house4.png"));} catch (IOException e) {}
        try {buildings[9] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/house5.png"));} catch (IOException e) {}
        try {specialImages[0] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/brokenFloor.png"));} catch (IOException e) {}
        try {coin = ImageIO.read(getClass().getResourceAsStream("/misc/coin.png"));} catch (IOException e) {}
        try {hpBar = ImageIO.read(getClass().getResourceAsStream("/misc/hpBar.png"));} catch (IOException e) {}
        try {
            pathLeftRight = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathLeftRight.png"));} catch (IOException e) {}
        try {
            pathUpDown = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathUpDown.png"));} catch (IOException e) {}
        try {
            pathLeftDown = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathLeftDown.png"));} catch (IOException e) {}
        try {
            pathLeftUp = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathLeftUp.png"));} catch (IOException e) {}
        try {
            pathRightDown = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathRightDown.png"));} catch (IOException e) {}
        try {
            pathRightUp = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathRightUp.png"));} catch (IOException e) {}
        try {
            pathConvergeRight = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathConvergeRight.png"));} catch (IOException e) {}
        try {
            pathConvergeDown = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathConvergeDown.png"));} catch (IOException e) {}
        try {
            pathConvergeUp = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathConvergeUp.png"));} catch (IOException e) {}
        try {
            pathConvergeLeft = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathConvergeLeft.png"));} catch (IOException e) {}
        try {
            pathConvergeMiddle = ImageIO.read(getClass().getResourceAsStream("/mapAssets/pathConvergeMiddle.png"));} catch (IOException e) {}
        title = new BufferedImage[11];
        try {title[0] = ImageIO.read(getClass().getResourceAsStream("/misc/titleScreen.png"));} catch (IOException e) {}
        try {title[1] = ImageIO.read(getClass().getResourceAsStream("/misc/bgTitleScreen.png"));} catch (IOException e) {}
        try {title[2] = ImageIO.read(getClass().getResourceAsStream("/misc/justNewGame.png"));} catch (IOException e) {}
        try {title[3] = ImageIO.read(getClass().getResourceAsStream("/misc/justContinueGame.png"));} catch (IOException e) {}
        try {title[4] = ImageIO.read(getClass().getResourceAsStream("/misc/justHowToPlay.png"));} catch (IOException e) {}
        try {title[5] = ImageIO.read(getClass().getResourceAsStream("/misc/newGame.png"));} catch (IOException e) {}
        try {title[6] = ImageIO.read(getClass().getResourceAsStream("/misc/continueGame.png"));} catch (IOException e) {}
        try {title[7] = ImageIO.read(getClass().getResourceAsStream("/misc/howToPlay.png"));} catch (IOException e) {}
        try {title[8] = ImageIO.read(getClass().getResourceAsStream("/misc/controls.png"));} catch (IOException e) {}
        try {title[9] = ImageIO.read(getClass().getResourceAsStream("/misc/playerInfo.png"));} catch (IOException e) {}
        try {title[10] = ImageIO.read(getClass().getResourceAsStream("/misc/extraFunctions.png"));} catch (IOException e) {}
        try {quest = ImageIO.read(getClass().getResourceAsStream("/misc/quest.png"));} catch (IOException e) {}
        try {clock = ImageIO.read(getClass().getResourceAsStream("/misc/clock.png"));} catch (IOException e) {}
        try {duck = ImageIO.read(getClass().getResourceAsStream("/misc/duck.png"));} catch (IOException e) {}
        skills = new BufferedImage[window.skillList().length];
        try {skills[0] = ImageIO.read(getClass().getResourceAsStream("/skills/slash.png"));} catch (IOException e) {}
        try {skills[1] = ImageIO.read(getClass().getResourceAsStream("/skills/fiveYearBurn.png"));} catch (IOException e) {}
        try {skills[2] = ImageIO.read(getClass().getResourceAsStream("/skills/lightning.png"));} catch (IOException e) {}
        try {skills[3] = ImageIO.read(getClass().getResourceAsStream("/skills/dash.png"));} catch (IOException e) {}
        try {skills[4] = ImageIO.read(getClass().getResourceAsStream("/attacks/shield.png"));} catch (IOException e) {}
        try {skills[5] = ImageIO.read(getClass().getResourceAsStream("/attacks/gambit.png"));} catch (IOException e) {}
        try {skills[6] = ImageIO.read(getClass().getResourceAsStream("/skills/break.png"));} catch (IOException e) {}
        try {skills[7] = ImageIO.read(getClass().getResourceAsStream("/skills/thunderDragon.png"));} catch (IOException e) {}
        try {skills[8] = ImageIO.read(getClass().getResourceAsStream("/skills/magicRelease.png"));} catch (IOException e) {}
        try {skills[9] = ImageIO.read(getClass().getResourceAsStream("/skills/healing.png"));} catch (IOException e) {}
        try {skills[10] = ImageIO.read(getClass().getResourceAsStream("/attacks/explosion3.png"));} catch (IOException e) {}
        try {skills[11] = ImageIO.read(getClass().getResourceAsStream("/skills/bread.png"));} catch (IOException e) {}
        try {skills[12] = ImageIO.read(getClass().getResourceAsStream("/skills/revolvingBladeSkill.png"));} catch (IOException e) {}
        try {skills[13] = ImageIO.read(getClass().getResourceAsStream("/skills/singularity.png"));} catch (IOException e) {}
        try {skills[14] = ImageIO.read(getClass().getResourceAsStream("/skills/flashingStrikes.png"));} catch (IOException e) {}
        try {skills[15] = ImageIO.read(getClass().getResourceAsStream("/skills/benevolentBladeOfTheHero.png"));} catch (IOException e) {}
        passives = new BufferedImage[window.passiveList().length];
        try {passives[0] = ImageIO.read(getClass().getResourceAsStream("/skills/adventurer.png"));} catch (IOException e) {}
        try {passives[1] = ImageIO.read(getClass().getResourceAsStream("/skills/squareOfFire.png"));} catch (IOException e) {}
        try {passives[2] = ImageIO.read(getClass().getResourceAsStream("/skills/swordMaster.png"));} catch (IOException e) {}
        try {passives[3] = ImageIO.read(getClass().getResourceAsStream("/skills/maniac.png"));} catch (IOException e) {}
        try {passives[4] = ImageIO.read(getClass().getResourceAsStream("/skills/vampire.png"));} catch (IOException e) {}
        //try {passives[5] = ImageIO.read(getClass().getResourceAsStream("/skills/.png"));} catch (IOException e) {}
        try {mazes[0] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map002002.png"));} catch (IOException e) {}
        try {mazes[1] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map002003.png"));} catch (IOException e) {}
        try {mazes[2] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map003002.png"));} catch (IOException e) {}
        try {mazes[3] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map003003.png"));} catch (IOException e) {}
        cities = new BufferedImage[34];
        try {cities[0] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map050034.png"));} catch (IOException e) {}
        try {cities[1] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map050035.png"));} catch (IOException e) {}
        try {cities[2] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map050036.png"));} catch (IOException e) {}
        try {cities[3] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map051034.png"));} catch (IOException e) {}
        try {cities[4] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map051035.png"));} catch (IOException e) {}
        try {cities[5] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map051036.png"));} catch (IOException e) {}
        try {cities[6] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map052034.png"));} catch (IOException e) {}
        try {cities[7] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map052035.png"));} catch (IOException e) {}
        try {cities[8] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map052036.png"));} catch (IOException e) {}
        try {cities[9] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map055033.png"));} catch (IOException e) {}
        try {cities[10] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map055034.png"));} catch (IOException e) {}
        try {cities[11] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map055035.png"));} catch (IOException e) {}
        try {cities[12] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map055036.png"));} catch (IOException e) {}
        try {cities[13] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map055037.png"));} catch (IOException e) {}
        try {cities[14] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map056033.png"));} catch (IOException e) {}
        try {cities[15] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map056034.png"));} catch (IOException e) {}
        try {cities[16] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map056035.png"));} catch (IOException e) {}
        try {cities[17] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map056036.png"));} catch (IOException e) {}
        try {cities[18] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map056037.png"));} catch (IOException e) {}
        try {cities[19] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map060034.png"));} catch (IOException e) {}
        try {cities[20] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map060035.png"));} catch (IOException e) {}
        try {cities[21] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map060036.png"));} catch (IOException e) {}
        try {cities[22] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map061034.png"));} catch (IOException e) {}
        try {cities[23] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map061035.png"));} catch (IOException e) {}
        try {cities[24] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map061036.png"));} catch (IOException e) {}
        try {cities[25] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map067034.png"));} catch (IOException e) {}
        try {cities[26] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map067035.png"));} catch (IOException e) {}
        try {cities[27] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map067036.png"));} catch (IOException e) {}
        try {cities[28] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map068034.png"));} catch (IOException e) {}
        try {cities[29] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map068035.png"));} catch (IOException e) {}
        try {cities[30] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map068036.png"));} catch (IOException e) {}
        try {cities[31] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map069034.png"));} catch (IOException e) {}
        try {cities[32] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map069035.png"));} catch (IOException e) {}
        try {cities[33] = ImageIO.read(getClass().getResourceAsStream("/mapAssets/map069036.png"));} catch (IOException e) {}
        try {statuses[0] = ImageIO.read(getClass().getResourceAsStream("/misc/burned.png"));} catch (IOException e) {}
        try {statuses[1] = ImageIO.read(getClass().getResourceAsStream("/misc/stunned.png"));} catch (IOException e) {}
        try {statuses[2] = ImageIO.read(getClass().getResourceAsStream("/misc/bleeding.png"));} catch (IOException e) {}

        accessories = new BufferedImage[5];
        try {accessories[0] = ImageIO.read(getClass().getResourceAsStream("/accessories/sword.png"));} catch (IOException e) {}
        try {accessories[1] = ImageIO.read(getClass().getResourceAsStream("/accessories/staff.png"));} catch (IOException e) {}
        try {accessories[2] = ImageIO.read(getClass().getResourceAsStream("/accessories/ribbon.png"));} catch (IOException e) {}
        try {accessories[3] = ImageIO.read(getClass().getResourceAsStream("/accessories/crimsonSword.png"));} catch (IOException e) {}
        try {accessories[4] = ImageIO.read(getClass().getResourceAsStream("/accessories/shellStaff.png"));} catch (IOException e) {}

        staticEffect = new BufferedImage[8];
        for(int i = 0; i < staticEffect.length; i++){
            try {staticEffect[i] = ImageIO.read(getClass().getResourceAsStream("/misc/static" + (i+1) + ".png"));} catch (IOException e) {}
        }
        bar = new BufferedImage[6];
        for(int i = 0; i < bar.length; i++){
            try {bar[i] = ImageIO.read(getClass().getResourceAsStream("/misc/bar" + (i+1) + ".png"));} catch (IOException e) {}
        }
        locationY = 10;
        locationDown = true;
        locationTick = 0;
        initializeColors();
        //frame.setIconImage(titleScreen);
    }
    public BufferedImage rotateImage(int degree, BufferedImage image){
        double rad = Math.toRadians(degree);
        double sine = Math.abs(Math.sin(rad));
        double cosine = Math.abs(Math.cos(rad));
        int w = (int)(image.getWidth() * cosine + image.getHeight() * sine);
        int h = (int)(image.getHeight() * cosine + image.getWidth() * sine);
        BufferedImage rotated = new BufferedImage(w,h,image.getType());
        AffineTransform a = new AffineTransform();
        a.translate(w /2, h /2);
        a.rotate(rad,0,0);
        a.translate(-image.getWidth() /2, -image.getHeight() /2);
        AffineTransformOp rotate = new AffineTransformOp(a,AffineTransformOp.TYPE_BILINEAR);
        rotate.filter(image,rotated);
        return rotated;
    }
    private void initializeColors(){
        colors = new Color[100];
        colors[0] = new Color(0xFF8000);
        colors[1] = new Color(0x000000);
        colors[2] = new Color(0x3D7B6C);
        colors[3] = new Color(0x265549);
        colors[4] = new Color(0x6AD8BE);
        colors[5] = new Color(0x508C19);
        colors[6] = new Color(0x467A16);
        colors[7] = new Color(0x434348);
        colors[8] = new Color(0xD95C00);
        colors[9] = new Color(0xFFFFFF);
    }
    public int getScrollX(){
        return scrollX;
    }
    public int getScrollY(){
        return scrollY;
    }
    public void darkScreen(){
        darkScreen = true;
        darkScreenTick = 0;
        darkScreenIn = true;
    }
    public boolean isDarkScreen(){
        return (darkScreen && darkScreenIn) || (darkScreen && darkScreenTick > 5);
    }
    public boolean inDarkScreen(){return darkScreen;}
    public void setUp(){
        settingUp = true;
        realSettingUp = true;
        setMap(0,9,0,7);
        setMap(50,75,33,40);
        setSpecialMap(8,1,0);
        buildingsImage = false;
    }
    public void moreSetUp(){
        int tempX = mapNumX;
        int tempY = mapNumY;
        for(int x = 0; x < 9; x++){
            for(int y = 0; y < 7; y++){
                unbarricadeMap(x,y);
            }
        }
        for(int x = 50; x < 75; x++){
            for(int y = 33; y < 40; y++){
                unbarricadeMap(x,y);
            }
        }
        mapNumX = tempX;
        mapNumY = tempY;
        mapChange();
    }
    private void setSpecialMap(int mapX, int mapY, int index){
        special = index;
        goToMapXY(mapX,mapY);
        mapChange();
        for(int row = 0; row < map.length; row++){
            for(int column = 0; column < map[row].length; column++){
                if(collisions[row][column]!=1){
                    map[row][column] = 1000;
                }
            }
        }
        theMap = null;
        imageGet = false;
        repaint();
        specialMap[index] = getMapImage();
        special = -1;
    }
    private void setMap(int startX, int endX, int startY, int endY){
        for(int x = startX; x < endX; x++){
            for(int y = startY; y < endY; y++){
                location = "";
                goToMapXY(x,y);
                mapChange();
                theMap = null;
                imageGet = false;
                repaint();
                wholeMap[x][y] = getMapImage();
            }
        }
    }
    private void drawMap(Graphics2D g2d, int startX, int endX, int startY, int endY){
        for(int x = startX; x < endX; x++){
            for(int y = startY; y < endY; y++){
                if(Math.abs(x*1375 + scrollX)<1400 && Math.abs(y*775 + scrollY)<800) {
                    g2d.drawImage(wholeMap[x][y], x * 1375 + scrollX, y * 775 + scrollY, null);
                    checkMap(x,y);
                    checkEnemies(x,y);
                }
            }
        }
    }
    public void repaint(){
        //System.out.println("repainting");
        super.repaint();
    }
    public void doneWithSetUp(){
        settingUp = false;
        realSettingUp = false;
    }
    public BufferedImage[] getSkills(){return skills;}
    public BufferedImage[] getPassives(){return passives;}
    public ArrayList<Integer> getEnemyNums(){return enemyNums;}
    public int rows(){return rows;}
    public int columns(){return columns;}
    public double zoom(){return zoom;}
    public int squareSize(){return squareSize;}
    public void paintComponent(Graphics g){
        //System.out.println("ran");
        super.paintComponent(g);
        frame.increaseFps();
        //System.out.print("running");
        if(frame.getStoryLocation()==-1){
            drawMainMenu(g);
        }
        else {
            drawMap(g);
        }
    }

    @Override
    public void repaint(Rectangle r) {super.repaint(r);}
    public boolean needImage(){return !imageGet;}
    private void drawMainMenu(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
//        if(frame.play()){
//            g2d.drawImage(title[0],0,0,1400,800,null);
//        }
       if(instructions) {
            g2d.drawImage(title[8 + instruction], 0, 0, 960, 720, null);
        }
        else {
            mainMenuX += 3;
            if (mainMenuX > 960) {
                mainMenuX = 0;
            }
            g2d.drawImage(title[1], mainMenuX, 0, 960, 720, null);
            g2d.drawImage(title[1], mainMenuX - 960, 0, 960, 720, null);
            g2d.drawImage(title[0], 0, 0, 960, 720, null);
            BufferedImage image1 = title[2];
            BufferedImage image2 = title[3];
            BufferedImage image3 = title[4];
//            g2d.setColor(Color.BLUE);
            if (menuSelectY == 0) {
                titleTick[0] += (100-titleTick[0])/10;
                titleTick[1] += (-9-titleTick[1])/10;
                titleTick[2] += (-9-titleTick[2])/10;
                image1 = title[5];
//                g2d.drawImage(title[5],titleTick[0],-20+titleTick[0],960-titleTick[0]*2,720-titleTick[0]*2,null);
//                g2d.drawImage(title[3],titleTick[1],30+titleTick[1],960-titleTick[1]*2,720-titleTick[1]*2,null);
//                g2d.drawImage(title[4],titleTick[2],80+titleTick[2],960-titleTick[2]*2,720-titleTick[2]*2,null);
//                g2d.fillPolygon(new int[]{360, 370, 360}, new int[]{335, 345, 355}, 3);
//                g2d.fillPolygon(new int[]{590, 580, 590}, new int[]{335, 345, 355}, 3);
            } else if(menuSelectY == 1){
                titleTick[1] += (100-titleTick[1])/10;
                titleTick[0] += (-9-titleTick[0])/10;
                titleTick[2] += (-9-titleTick[2])/10;
                image2 = title[6];
//                g2d.drawImage(title[2],titleTick[0],-20+titleTick[0],960-titleTick[0]*2,720-titleTick[0]*2,null);
//                g2d.drawImage(title[6],titleTick[1],30+titleTick[1],960-titleTick[1]*2,720-titleTick[1]*2,null);
//                g2d.drawImage(title[4],titleTick[2],80+titleTick[2],960-titleTick[2]*2,720-titleTick[2]*2,null);
//                g2d.fillPolygon(new int[]{350, 360, 350}, new int[]{390, 400, 410}, 3);
//                g2d.fillPolygon(new int[]{600, 590, 600}, new int[]{390, 400, 410}, 3);
            }
            else{
                titleTick[2] += (100-titleTick[2])/10;
                titleTick[0] += (-9-titleTick[0])/10;
                titleTick[1] += (-9-titleTick[1])/10;
                image3 = title[7];
//                g2d.drawImage(title[2],titleTick[0],-20+titleTick[0],960-titleTick[0]*2,720-titleTick[0]*2,null);
//                g2d.drawImage(title[3],titleTick[1],30+titleTick[1],960-titleTick[1]*2,720-titleTick[1]*2,null);
//                g2d.drawImage(title[7],titleTick[2],80+titleTick[2],960-titleTick[2]*2,720-titleTick[2]*2,null);
//                g2d.fillPolygon(new int[]{340, 350, 340}, new int[]{440, 450, 460}, 3);
//                g2d.fillPolygon(new int[]{610, 600, 610}, new int[]{440, 450, 460}, 3);
            }
            g2d.drawImage(image1,titleTick[0],-20+titleTick[0],960-titleTick[0]*2,720-titleTick[0]*2,null);
            if(!frame.hasCode()){
                setOpacity(g2d,0.5F);
            }
            g2d.drawImage(image2,titleTick[1],30+titleTick[1],960-titleTick[1]*2,720-titleTick[1]*2,null);
            if(!frame.hasCode()){
                setOpacity(g2d,1F);
            }
            g2d.drawImage(image3,titleTick[2],80+titleTick[2],960-titleTick[2]*2,720-titleTick[2]*2,null);
        }
    }
    public void toggleHowToPlay(){
        if(!instructions){
            instructions = true;
        }
        else if(instruction < 2){
            instruction++;
        }
        else {
            instruction = 0;
            instructions = false;
        }
    }
    public boolean inInstructions(){return instructions;}
    public void scroll(){
        //System.out.println(scrollX + " " + scrollY + " " + player.getxPos() + " "+ player.getyPos());
        int xChange = 700-player.getxPos();
        int yChange = 400-player.getyPos();
        boolean xPositive = false;
        boolean yPositive = false;
        boolean xCondition = scrollX < -700 || player.getxPos() > 700 || (scrollX < 0 && player.getxPos() < 700);
        boolean yCondition = scrollY < -400 || player.getyPos() > 400 || (scrollY < 0 && player.getyPos() < 400);
        if(mapNumX < 0){
            xCondition = false;
        }
        if(mapNumY < 0){
            yCondition = false;
        }
        if(mapNumX > 49){
            xCondition = scrollX < -69450 || player.getxPos() > 700 || (scrollX < -68750 && player.getxPos() < 700);
        }
        if(mapNumX < 3 && mapNumY < 2 && mapNumX > -1 && mapNumY > -1){
            yCondition = scrollY != -775;
            if(yCondition){
                yChange = -775 - scrollY;
            }
//            if(scrollX > 0){
//                xCondition = true;
//                xChange = -scrollX*10;
//            }
        }
        if(mapNumX > 3 && mapNumX < 8){
            yCondition = scrollY < -775 || player.getyPos() > 400;
            if(scrollY > -775){
                yCondition = true;
                yChange = -775 - scrollY;
            }
        }
        if(mapNumX==8){
            xCondition = scrollX > -11000 || player.getxPos() < 700; //|| (scrollX < -11000 && player.getxPos() < 700);
        }
        if(mapNumY==6){
            yCondition = scrollY > -4650 || player.getyPos() < 400;// || (scrollY > -5050 && player.getyPos() > 400);
        }
        if(mapNumY > 30){
            yCondition = scrollY < -23650 || player.getyPos() > 400 || (scrollY < -23250 && player.getyPos() < 400);
        }
        if(mapNumX >= 100000){
            xCondition = false;
        }
        if(mapNumY >= 100000){
            yCondition = false;
        }
//        if(frame.getStoryLocation()==27){
//            yCondition = false;
//            xCondition = scrollX > -82500 || player.getxPos()<700;
////            if(scrollX <= -82500 && player.getxPos() < 700){
////                xCondition = true;
////                xChange = -82500 - scrollX;
////                xChange*=5;
////            }
////            if(player.getxPos()<700 && scrollX + xChange>=-81825){
////                player.goToXY(700,player.getyPos());
////                xChange = 0;
//////                xCondition = true;
//////                xChange = -81825 - scrollX;
//////                xChange*=5;
////            }
//        }
        if(xChange > 0){
            xChange = (int)Math.ceil(xChange/10.0);
            xPositive = true;
        }
        else{
            xChange = (int)Math.ceil(-xChange/10.0);
        }
        if(yChange > 0){
            yChange = (int)Math.ceil(yChange/10.0);
            yPositive = true;
        }
        else{
            yChange = (int)Math.ceil(-yChange/10.0);
        }
        if(xCondition){
            if(xPositive) {
                player.moveX(xChange);
                scrollX += xChange;
            }
            else{
                player.moveX(-xChange);
                scrollX -= xChange;
            }
        }
        if(yCondition) {
            if(yPositive) {
                player.moveY(yChange);
                scrollY += yChange;
            }
            else{
                player.moveY(-yChange);
                scrollY -= yChange;
            }
        }
        if(mapNumX > -1 && mapNumX < 10) {
            if (scrollX > 0) {
                player.moveX(-scrollX);
                scrollX = 0;
            }
            if (scrollX < -11000) {
                player.moveX(-11000 - scrollX);
                scrollX = -11000;
            }
        }
        if(mapNumX > 49 && mapNumX < 1000){
            if (scrollX > -68750) {
                player.moveX(-68750 - scrollX);
                scrollX = -68750;
            }
//            if (scrollX < -11000) {
//                player.moveX(-11000 - scrollX);
//                scrollX = -11000;
//            }
        }
        if(mapNumY > -1 && mapNumY < 10) {
            if (scrollY > 0) {
                player.moveY(-scrollY);
                scrollY = 0;
            }
            if (scrollY < -4650) {
                player.moveY(-4650 - scrollY);
                scrollY = -4650;
            }
        }
        if(mapNumY > 30 && mapNumY < 1000) {
            if (scrollY > -25575) {
                player.moveY(-25575-scrollY);
                scrollY = -25575;
            }
//            if (scrollY < -4650) {
//                player.moveY(-4650 - scrollY);
//                scrollY = -4650;
//            }
        }
//        if(player.getxPos()<700 && frame.getStoryLocation()==27 && scrollX>=-81825){
//            player.goToXY(700,player.getyPos());
//        }
        //System.out.println(scrollX + " " + scrollY + " " + player.getxPos() + " "+ player.getyPos());
    }
    public void drawMap(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        if(!settingUp) {
            scroll();
        }
            clockThingy++;
            clockThingy %= 720;
            hourClockThingy++;
            hourClockThingy %= 21600;
            menuTick();
            player.setStats();
            player.addToAttackCount();
            player.comboCount();
            frame.loop();
//        if(buildingsImage){
//            drawBuildings(g2d);
//        }
            if (theMap == null) {
                drawMap(g2d);
                if (!imageGet) {
                    theMap = getMapImage();
                    drawOthers(g2d);
                }
//            mapChanging=false;
            } else {
                drawMap(g2d, 0, 9, 0, 7);
                drawMap(g2d, 50, 75, 33, 40);
                if ((mapNumX == -100 && mapNumY == -100) || (mapNumX >= 100000 && mapNumY >= 100000)) {
                    g2d.drawImage(theMap, 0, 0, null);
                    //drawMap(g2d);
                }
                drawSpecialMap(g2d, 8, 1, 0, 9);
                //System.out.println(scrollX + " " + scrollY);
                //g2d.drawImage(theMap, 0, 0, null);
                drawOthers(g2d);
            }
        g2d.dispose();
    }
    private void menuTick(){
        if(menuTicking) {
            if(menuTickIn) {
                //menuTick+=(25-menuTick)/5;
                //if(menuTick > 6){
                menuTickIn = false;
                //}
            }
            else{
                //menuTick+=(-10-menuTick)/5;
                //if(menuTick<=5){
                menuTicking = false;
                menuTick = 0;
                if(menu==0){
                    menu = menuSelectY + menuSelectX*3 + 1;
                    menuSelectY = 0;
                    menuScrollY = 0;
                    menuSelectX = 0;
                    xPosSkills=450;
                    xPosSkills2=450;
                    skillsBounce=false;
                }
                else if(menu==1){
                    if(menuSelectY == 0){
                        menu = 0;
                    }
                    else if(menuSelectY == 1){
                        player.increaseStat("hp");
                    }
                    else if(menuSelectY == 2){
                        player.increaseStat("physical");
                    }
                    else if(menuSelectY == 3){
                        player.increaseStat("magic");
                    }
                    else if(menuSelectY == 4){
                        player.resetStats();
                    }
                }
                else if (menu==2){
                    menuSelectX += (menuScrollY+menuSelectY)*3;
                    skillNum = menuSelectX;
                    menuSelectY = 0;
                    menu = 7;
                    xPosSkills=450;
                    xPosSkills2=450;
                    skillsBounce=false;
                }
                else if (menu==5){
                    menuSelectX += (menuScrollY+menuSelectY)*3;
                    skillNum = menuSelectX;
                    menuSelectY = 0;
                    menu = 8;
                    xPosSkills=450;
                    xPosSkills2=450;
                    skillsBounce=false;
                }
                else if (menu==7){
                    if(menuSelectY<4){
                        frame.equipSkill(skillNum,menuSelectY);
                    } else if(menuSelectY==4){
                        menuSelectY = 0;
                        menuSelectX = 0;
                        menu=2;
                        skillsBounce=false;
                    }
                }
                else if (menu==8){
                    if(menuSelectY<2){
                        frame.equipPassive(skillNum,menuSelectY);
                    } else if(menuSelectY==3){
                        menuSelectY = 0;
                        menuSelectX = 0;
                        menu=5;
                        skillsBounce=false;
                    }
                    else{
                        frame.unequipPassives();
                    }
                }
                //}
            }
        }
    }
    public void specialPress(){
        if(menu==1){
            if(menuSelectY == 1){
                player.increaseStat("hp");
            }
            else if(menuSelectY == 2){
                player.increaseStat("physical");
            }
            else if(menuSelectY == 3){
                player.increaseStat("magic");
            }
        }
    }
    public void checkMap(){
        for(int x = 0; x < 100; x++){
            for(int y = 0; y < 100; y++){
                if(Math.abs(x*1375 + scrollX)<1400 && Math.abs(y*775 + scrollY)<800) {
                    checkMap(x,y);
                }
            }
        }
    }
    private void drawSpecialMap(Graphics2D g2d, int x, int y, int index, int storyLocation){
        if(frame.getStoryLocation() < storyLocation){
            if(Math.abs(x*1375 + scrollX)<1400 && Math.abs(y*775 + scrollY)<800) {
                g2d.drawImage(specialMap[index], x * 1375 + scrollX, y * 775 + scrollY, null);
            }
        }
    }
    private void checkMap(int x, int y){
        int tempX = mapNumX;
        int tempY = mapNumY;
        if(x * 1375 + scrollX < player.getxPos() && x * 1375 + scrollX + 1375 > player.getxPos()){
            if(y * 775 + scrollY < player.getyPos() && y * 775 + scrollY + 775 > player.getyPos()){
                if(x!=mapNumX){
                    mapNumX = x;
                }
                if(y!=mapNumY){
                    mapNumY = y;
                }
                if(tempX!=mapNumX || tempY!=mapNumY){
                    System.out.println("Map Changed.");
                    String tempLocation = location;
                    mapChange();
                    if(!location.equals(tempLocation)) {
                        locationY = 10;
                        locationDown = true;
                        locationTick = 0;
                    }
                }
            }
        }
    }
    public int numEnemies(int mapX, int mapY){
        int count = 0;
        for(Person enemy : enemyList){
            if(enemy.getMapX()==mapX && enemy.getMapY()==mapY){
                count++;
            }
        }
        return count;
    }
    private void checkEnemies(int mapX, int mapY){
        int count = 0;
        for(Person enemy : enemyList){
            if(enemy.getMapX()==mapX && enemy.getMapY()==mapY){
                count++;
            }
        }
        int[] xs =     {1,2,4,4,5,5,7,6,4,4,7,6,4};
        int[] ys =     {1,1,1,2,1,2,5,5,5,3,6,6,6};
        int[] counts = {2,4,4,4,4,4,5,6,8,4,5,6,8};
        for(int i = 0 ; i < xs.length; i++){
            if(mapX==xs[i] && mapY==ys[i]){
                if(count < counts[i]){
                    respawnEnemy(-1, counts[i] - count, mapX, mapY);
                }
            }
        }
    }
    public void respawnEnemy(int spawnableTimes, int times, int mapX, int mapY) {
//        Person enemy;
//        if (Math.random() < 0.5) {
//            enemy = new Person("rangedEnemy", (int) (Math.random() * 1325) + squareSize, (int) (Math.random() * 725) + squareSize, hp, 250, spawnableTimes - 1, 0, damageStat, mapX, mapY);
//            enemy.setSpeed(4);
//        } else {
//            enemy = new Person("meleeEnemy", (int) (Math.random() * 1325) + squareSize, (int) (Math.random() * 725) + squareSize, hp, 200, spawnableTimes - 1, damageStat, 0, mapX, mapY);
//        }
//        enemyList.add(enemy);
//        int skillsLearned = (int) (Math.random() * 5);
//        int[] theSkills;
//        if(enemy.getType().equals("meleeEnemy")){
//            theSkills = new int[]{0,3,4,5,6,12};
//        }
//        else{
//            theSkills = new int[]{2,3,4,5,6,8,10};
//        }
//        for (int i = 0; i < skillsLearned; i++) {
//            if (Math.random() > 0.66) {
//                int rand = (int) (Math.random() * theSkills.length);
//                while (enemyList.get(enemyList.size() - 1).skillSetContain(theSkills[rand])) {
//                    rand = (int) (Math.random() * theSkills.length);
//                }
//                enemyList.get(enemyList.size() - 1).addSkills(frame.skillList()[theSkills[rand]], frame.getSkillCooldowns()[theSkills[rand]]);
//            }
//        }
        respawnEnemy(spawnableTimes,mapX,mapY,(int) (Math.random() * 1325) + squareSize,(int) (Math.random() * 725) + squareSize);
        times--;
        if (times > 0) {
            respawnEnemy(spawnableTimes, times,mapX,mapY);
        }
    }
    private void respawnEnemy(int spawnableTimes, int mapX, int mapY, int x, int y) {
        int hp = 0;
        int damageStat = 0;
        if (mapX == 2 && mapY == 1) {
            damageStat = 5;
        } else if (mapX == 4 && mapY == 1) {
            hp = 3;
            damageStat = 10;
        } else if (mapX == 4 && mapY == 2) {
            hp = 3;
            damageStat = 10;
        } else if (mapX == 5 && mapY == 1) {
            hp = 6;
            damageStat = 15;
        } else if (mapX == 5 && mapY == 2) {
            hp = 6;
            damageStat = 15;
        } else if (mapX == 8 && mapY == 1) {
            hp = 10;
            damageStat = 20;
        } else if (mapX == 7 && (mapY == 5 || mapY == 6)) {
            hp = 12;
            damageStat = 25;
        } else if (mapX == 6 && (mapY == 5 || mapY == 6)) {
            hp = 13;
            damageStat = 25;
        } else if (mapX == 4 && (mapY == 5 || mapY == 6)) {
            hp = 14;
            damageStat = 26;
        } else if (mapX == 4 && mapY == 3) {
            hp = 15;
            damageStat = 28;
        }
        else if (mapX == 60 && mapY == 35) {
            hp = player.getLevel();
            damageStat = player.getLevel();
        }
        Person enemy;
        if (Math.random() < 0.5) {
            enemy = new Person("rangedEnemy", x, y, hp, 250, spawnableTimes - 1, 0, damageStat, mapX, mapY);
            enemy.setSpeed(4);
        } else {
            enemy = new Person("meleeEnemy", x, y, hp, 200, spawnableTimes - 1, damageStat, 0, mapX, mapY);
        }
        enemyList.add(enemy);
        int skillsLearned = (int) (Math.random() * 5);
        int[] theSkills;
        if(enemy.getType().equals("meleeEnemy")){
            theSkills = new int[]{0,3,4,5,6,12};
        }
        else{
            theSkills = new int[]{2,3,4,5,6,8,10};
        }
        for (int i = 0; i < skillsLearned; i++) {
            if (Math.random() > 0.66) {
                int rand = (int) (Math.random() * theSkills.length);
                while (enemyList.get(enemyList.size() - 1).skillSetContain(theSkills[rand])) {
                    rand = (int) (Math.random() * theSkills.length);
                }
                enemyList.get(enemyList.size() - 1).addSkills(frame.skillList()[theSkills[rand]], frame.getSkillCooldowns()[theSkills[rand]]);
            }
        }
    }
    public void spawnAlly(String type, String name, int x, int y, int health, int detect, int respawn, int physical, int magic, int xMap, int yMap){
        Person ally = new Person(type,x,y,health,detect,respawn,physical,magic,xMap,yMap);
        ally.setDesc(name,"");
        allyList.add(ally);
    }
    private void drawStatuses(Graphics2D g2d, Person person){
        int x = person.getxPos()-3;
        int num = 0;
        if(person.isBurned()){
            num++;
        }
        if(person.isStunned()){
            num++;
        }
        if(person.isBleeding()){
            num++;
        }
        if(person.isBurned()){
            if(num>1){
                x-=16*(num-1);
            }
            g2d.drawImage(statuses[0],x,person.getyPos()-42,null);
            x+=32*(num-1);
        }
        if(person.isStunned()){
            if(num>1){
                x-=16*(num-1);
            }
            g2d.drawImage(statuses[1],x,person.getyPos()-42,null);
            x+=32*(num-1);
        }
        if(person.isBleeding()){
            if(num>1){
                x-=16*(num-1);
            }
            g2d.drawImage(statuses[2],x,person.getyPos()-42,null);
            x+=32*(num-1);
        }
    }
    private void drawOthers(Graphics2D g2d){
        drawItems(g2d);
        drawNpcs(g2d);
        drawEnemies(g2d);
        drawAllies(g2d);
        g2d.setColor(colors[0]);
        drawSquare(g2d, player.getxPos(), player.getyPos());
        drawStatuses(g2d,player);
        for(int x = 0; x < 10; x++){
            for(int y = 0; y < 10; y++){
                //System.out.println(scrollX + " " + scrollY);
                if(Math.abs(x*1375 + scrollX)<1425 && Math.abs(y*775 + scrollY)<825) {
                    //g2d.drawImage(mapBuildings[x][y], x * 1375 + scrollX , y * 775 + scrollY, null);
                }
            }
        }
        drawBuildings(g2d);
        drawAttacks(g2d);
        drawHpBars(g2d);
        g2d.setColor(Color.RED);
        g2d.drawImage(hpBar,0,0,180,56,null);
        if(player.getHpPercent()!=100){
            setOpacity(g2d,0.8F);
        }
        drawRoundedRect(g2d,56,6,(int)(player.getHpPercent()*1.2),29,20);
        setOpacity(g2d,1F);
        g2d.setColor(Color.BLUE);
        if(player.getEnergy()!=100){
            setOpacity(g2d,0.8F);
        }
        drawRoundedRect(g2d,60,35,(int)(player.getEnergy()*1.1),14,20);
        setOpacity(g2d,1F);
//        drawHpBar(g2d, 20, 0, player, 2);
//        drawEnergyBar(g2d, 20, 17, player, 2);
        if(player.getMeter()>0) {
            int xChange = frame.randomNum(4,-2);
            int yChange = frame.randomNum(2,0);
            drawMeter(g2d, 20 + xChange, 57 + yChange, player, 2);
            g2d.drawImage(bar[player.getMeter()%6],15 + xChange, 52 + yChange, 110, 20, null);
        }
        g2d.drawImage(coin, 25, 75, null);
        g2d.setColor(Color.black);
        typeText(g2d, "" + frame.checkPlayerMoney(), 55, 97, 2F);
//        new Color(0, 0, 0, 255); can set transparency
        drawCooldowns(g2d);
        drawPlace(g2d);
        drawNpcsData(g2d);
        drawText(g2d);
        if (menuVisible) {
            drawMenu(g2d);
        }
        drawEffects(g2d);
        g2d.setColor(colors[1]);

        typeText(g2d,"FPS: " + frame.getCurrentFps(),10,790,1.5F);
        if(player.getHp()<=0 || player.getDeadCounter() > 0){
            player.deadCount();
            g2d.setColor(new Color(0,0,0,player.getDeadCounter()));
            drawRect(g2d,0,0,1500,900);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) player.getDeadCounter() /255));
            g2d.setColor(Color.BLACK);
            g2d.drawImage(clock, 490, 200, null);
            int[] xs = new int[]{720, 735, 735, 720};
            int[] ys = new int[]{380, 380, 395, 395};
            int xDistance = (int) (125 * Math.cos((double) clockThingy /2));
            int yDistance = (int) (125 * Math.sin((double) clockThingy /2));
            xs[2] += xDistance;
            xs[3] += xDistance;
            ys[2] -= yDistance;
            ys[3] -= yDistance;
            g2d.fillPolygon(xs, ys, 4);
            xs = new int[]{720, 735, 735, 720};
            ys = new int[]{380, 380, 395, 395};
            xDistance = (int) (100 * Math.cos((double) hourClockThingy /360));
            yDistance = (int) (100 * Math.sin((double) hourClockThingy /360));
            xs[2] += xDistance;
            xs[3] += xDistance;
            ys[2] -= yDistance;
            ys[3] -= yDistance;
            g2d.fillPolygon(xs, ys, 4);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
        }
        if(darkScreen){
            if(darkScreenIn){
                darkScreenTick += 5;
                if (darkScreenTick > 255) {
                    darkScreenIn = false;
                    darkScreenTick = 0;
                    g2d.setColor(colors[1]);
                    drawRect(g2d, 0, 0, 1400, 800);
                } else {
                    g2d.setColor(new Color(0, 0, 0, darkScreenTick));
                    drawRect(g2d, 0, 0, 1400, 800);
                }
            }
            else {
                darkScreenTick += 5;
                if (darkScreenTick > 255) {
                    darkScreen = false;
                } else {
                    g2d.setColor(new Color(0, 0, 0, 255 - darkScreenTick));
                    drawRect(g2d, 0, 0, 1400, 800);
                }
            }
        }
    }
    private BufferedImage getMapImage(){
        BufferedImage map = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        imageGet = true;
        paintComponent(map.getGraphics());
        return map;
    }
    public void touchNpc(int indexOfNpc){
        npcTouched=indexOfNpc;
        canInteract = true;
        touchedPreviously = true;
    }
    private void drawMap(Graphics2D g2d){
        if(special > -1){
            g2d.drawImage(specialImages[special],0,0,1400,800,null);
        }
        for (int row = 0; row < rows; row++){
            for (int column = 0; column < columns; column++){
                if (map[row][column]==0) {
                    g2d.drawImage(grass,column*squareSize,row*squareSize,25,25,null);
//                    g2d.setColor(new Color(0x67B71F));
//                    drawSquare(g2d,column*squareSize,row*squareSize,25);
//                    g2d.setColor(new Color(0x508C19));
//                    drawRect(g2d,column*squareSize,row*squareSize,1,5);
//                    drawRect(g2d,column*squareSize+3,row*squareSize+1,1,5);
//                    drawRect(g2d,column*squareSize+10,row*squareSize+6,1,5);
//                    drawRect(g2d,column*squareSize+7,row*squareSize+3,1,4);
//                    drawRect(g2d,column*squareSize+12,row*squareSize+11,1,4);
//                    drawRect(g2d,column*squareSize+5,row*squareSize+9,1,5);
//                    drawRect(g2d,column*squareSize+20,row*squareSize+12,1,4);
//                    drawRect(g2d,column*squareSize+16,row*squareSize+2,1,5);
//                    drawRect(g2d,column*squareSize+3,row*squareSize+5,1,5);
//                    drawRect(g2d,column*squareSize+1,row*squareSize+13,1,5);
//                    drawRect(g2d,column*squareSize+7,row*squareSize+13,1,4);
//                    drawRect(g2d,column*squareSize+3,row*squareSize+23,1,5);
//                    drawRect(g2d,column*squareSize+3,row*squareSize+14,1,4);
//                    drawRect(g2d,column*squareSize+13,row*squareSize+15,1,5);
//                    drawRect(g2d,column*squareSize+15,row*squareSize,1,5);
//                    drawRect(g2d,column*squareSize+17,row*squareSize+7,1,5);
//                    drawRect(g2d,column*squareSize+22,row*squareSize+4,1,5);
//                    drawRect(g2d,column*squareSize+24,row*squareSize+8,1,5);
//                    drawRect(g2d,column*squareSize+13,row*squareSize+13,1,5);
//                    drawRect(g2d,column*squareSize+18,row*squareSize+16,1,5);
//                    drawRect(g2d,column*squareSize+20,row*squareSize+21,1,5);
                }
                else if (map[row][column]==1)
                {
                    //g2d.drawImage(wall,column*squareSize,row*squareSize,(int)Math.ceil(zoom*32),(int)Math.ceil(zoom*32),null);
                    g2d.setColor(colors[2]);
                    drawSquare(g2d,column*squareSize,row*squareSize,squareSize);
                    g2d.setColor(colors[3]);
                    drawRect(g2d,column*squareSize,row*squareSize+3,squareSize,1);
                    drawRect(g2d,column*squareSize,row*squareSize+8,squareSize,1);
                    drawRect(g2d,column*squareSize,row*squareSize+13,squareSize,1);
                    drawRect(g2d,column*squareSize,row*squareSize+18,squareSize,1);
                    drawRect(g2d,column*squareSize,row*squareSize+23,squareSize,1);
                    drawRect(g2d,column*squareSize+6,row*squareSize,1,4);
                    drawRect(g2d,column*squareSize+16,row*squareSize,1,4);
                    drawRect(g2d,column*squareSize+4,row*squareSize+4,1,4);
                    drawRect(g2d,column*squareSize+13,row*squareSize+4,1,4);
                    drawRect(g2d,column*squareSize+21,row*squareSize+4,1,4);
                    drawRect(g2d,column*squareSize+2,row*squareSize+9,1,4);
                    drawRect(g2d,column*squareSize+11,row*squareSize+9,1,4);
                    drawRect(g2d,column*squareSize+19,row*squareSize+9,1,4);
                    drawRect(g2d,column*squareSize+8,row*squareSize+14,1,4);
                    drawRect(g2d,column*squareSize+16,row*squareSize+14,1,4);
                    drawRect(g2d,column*squareSize+4,row*squareSize+19,1,4);
                    drawRect(g2d,column*squareSize+13,row*squareSize+19,1,4);
                    drawRect(g2d,column*squareSize+22,row*squareSize+19,1,4);
                    drawSquare(g2d,column*squareSize+6,row*squareSize+24,1);
                    drawSquare(g2d,column*squareSize+16,row*squareSize+24,1);
                    g2d.setColor(colors[4]);
                    drawRect(g2d,column*squareSize+5,row*squareSize,1,3);
                    drawRect(g2d,column*squareSize+15,row*squareSize,1,3);
                    drawRect(g2d,column*squareSize+24,row*squareSize,1,3);
                    drawRect(g2d,column*squareSize+3,row*squareSize+4,1,4);
                    drawRect(g2d,column*squareSize+12,row*squareSize+4,1,4);
                    drawRect(g2d,column*squareSize+20,row*squareSize+4,1,4);
                    drawRect(g2d,column*squareSize+1,row*squareSize+9,1,4);
                    drawRect(g2d,column*squareSize+10,row*squareSize+9,1,4);
                    drawRect(g2d,column*squareSize+18,row*squareSize+9,1,4);
                    drawRect(g2d,column*squareSize+7,row*squareSize+14,1,4);
                    drawRect(g2d,column*squareSize+15,row*squareSize+14,1,4);
                    drawRect(g2d,column*squareSize+24,row*squareSize+14,1,4);
                    drawRect(g2d,column*squareSize+3,row*squareSize+19,1,4);
                    drawRect(g2d,column*squareSize+12,row*squareSize+19,1,4);
                    drawRect(g2d,column*squareSize+21,row*squareSize+19,1,4);
                    drawRect(g2d,column*squareSize,row*squareSize+4,3,1);
                    drawRect(g2d,column*squareSize+5,row*squareSize+4,7,1);
                    drawRect(g2d,column*squareSize+14,row*squareSize+4,6,1);
                    drawRect(g2d,column*squareSize+22,row*squareSize+4,3,1);
                    drawRect(g2d,column*squareSize,row*squareSize+9,2,1);
                    drawRect(g2d,column*squareSize+3,row*squareSize+9,7,1);
                    drawRect(g2d,column*squareSize+12,row*squareSize+9,6,1);
                    drawRect(g2d,column*squareSize+20,row*squareSize+9,4,1);
                    drawRect(g2d,column*squareSize,row*squareSize+14,7,1);
                    drawRect(g2d,column*squareSize+9,row*squareSize+14,6,1);
                    drawRect(g2d,column*squareSize+17,row*squareSize+14,7,1);
                    drawRect(g2d,column*squareSize,row*squareSize+19,3,1);
                    drawRect(g2d,column*squareSize+5,row*squareSize+19,7,1);
                    drawRect(g2d,column*squareSize+14,row*squareSize+19,7,1);
                    drawRect(g2d,column*squareSize+23,row*squareSize+19,2,1);
                    drawRect(g2d,column*squareSize,row*squareSize+24,5,1);
                    drawRect(g2d,column*squareSize+7,row*squareSize+24,8,1);
                    drawRect(g2d,column*squareSize+17,row*squareSize+24,7,1);
                }
                else if (map[row][column]==2) {
                    g2d.drawImage(ground, column * squareSize, row * squareSize, (int) Math.ceil(zoom * 32), (int) Math.ceil(zoom * 32), null);
                }
                else if(map[row][column]==3){
                    g2d.setColor(colors[5]);
                    drawSquare(g2d,column*squareSize,row*squareSize,25);
                    g2d.setColor(colors[6]);
                    drawRect(g2d,column*squareSize,row*squareSize,1,25);
                    drawRect(g2d,column*squareSize+5,row*squareSize,1,25);
                    drawRect(g2d,column*squareSize+10,row*squareSize,1,25);
                    drawRect(g2d,column*squareSize+15,row*squareSize,1,25);
                    drawRect(g2d,column*squareSize+20,row*squareSize,1,25);
                    drawRect(g2d,column*squareSize,row*squareSize,25,1);
                    drawRect(g2d,column*squareSize,row*squareSize+5,25,1);
                    drawRect(g2d,column*squareSize,row*squareSize+10,25,1);
                    drawRect(g2d,column*squareSize,row*squareSize+15,25,1);
                    drawRect(g2d,column*squareSize,row*squareSize+20,25,1);
                }
                else if(map[row][column]==4){
                    g2d.setColor(colors[1]);
                    drawSquare(g2d,column*squareSize,row*squareSize,25);
                }
                else if(map[row][column]==5){
                    g2d.drawImage(sidewalk,column*squareSize,row*squareSize,null);
                }
                else if(map[row][column]==6){
                    g2d.setColor(colors[7]);
                    drawSquare(g2d,column*squareSize,row*squareSize,25);
                }
                else if(map[row][column]==8){
                    g2d.drawImage(tile,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==9){
                    g2d.drawImage(water,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==10){
                    g2d.drawImage(horizontalCliff,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==11){
                    g2d.drawImage(horizontalCliff,column*squareSize+25,row*squareSize,-25,25,null);
                }
                else if(map[row][column]==12){
                    g2d.drawImage(verticalCliff,column*squareSize,row*squareSize+25,25,-25,null);
                }
                else if(map[row][column]==13){
                    g2d.drawImage(verticalCliff,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==14){
                    g2d.drawImage(cornerCliff,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==15){
                    g2d.drawImage(cornerCliff,column*squareSize+25,row*squareSize,-25,25,null);
                }
                else if(map[row][column]==16){
                    g2d.drawImage(cornerCliff,column*squareSize,row*squareSize+25,25,-25,null);
                }
                else if(map[row][column]==17){
                    g2d.drawImage(cornerCliff,column*squareSize+25,row*squareSize+25,-25,-25,null);
                }
                else if(map[row][column]==18){
                    g2d.drawImage(ground2,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==19){
                    g2d.drawImage(ground3,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==20){
                    g2d.drawImage(ground3,column*squareSize+25,row*squareSize,-25,25,null);
                }
                else if(map[row][column]==21){
                    g2d.drawImage(ground4,column*squareSize,row*squareSize,25,25,null);
                }
                else if(map[row][column]==22){
                    g2d.drawImage(ground5,column*squareSize,row*squareSize,25,25,null);
                }
                //end of map draw
            }
        }
        if(mapNumX==2 && mapNumY==3){
            g2d.drawImage(mazes[0],0,0,1400,800,null);
        }
        if(mapNumX==2 && mapNumY==4){
            g2d.drawImage(mazes[1],0,0,1400,800,null);
        }
        if(mapNumX==3 && mapNumY==3){
            g2d.drawImage(mazes[2],0,0,1400,800,null);
        }
        if(mapNumX==3 && mapNumY==4){
            g2d.drawImage(mazes[3],0,0,1400,800,null);
        }
    }
    private void drawBuilding(Graphics2D g2d, int x, int y, int building, int mapX, int mapY){
        if(Math.abs(x + mapX*1375 + scrollX) < 1425 && Math.abs(y + mapY*775 + scrollY) < 825) {
            g2d.drawImage(buildings[building], x + mapX * 1375 + scrollX, y + mapY * 775 + scrollY, null);
        }
        int startHeight = 1;
//        if(building >= 4){
//            startHeight++;
//        }
        for(int row = y/25 + startHeight; row < y/25 + startHeight + 4; row++){
            for(int column = x/25; column < x/25 + 4; column++){
                barricade(row,column,mapX,mapY);
            }
        }
    }
    private void justDrawBuilding(Graphics2D g2d, int x, int y, int building, int mapX, int mapY){
        if(Math.abs(x + mapX*1375 + scrollX) < 1425 && Math.abs(y + mapY*775 + scrollY) < 825) {
            g2d.drawImage(buildings[building], x + mapX * 1375 + scrollX, y + mapY * 775 + scrollY, null);
        }
    }
    private void drawBuilding(Graphics2D g2d, int x, int y, int width, int height, int house, int mapX, int mapY){
        if(Math.abs(x + mapX*1375 + scrollX) < 1425 + width && Math.abs(y + mapY*775 + scrollY) < 825 + height) {
            g2d.drawImage(buildings[house],x + mapX * 1375 + scrollX,y + mapY * 775 + scrollY,width,height,null);
        }
        for(int row = y/25 + height/125; row < y/25 + height/25; row++){
            for(int column = x/25; column < x/25 + width/25; column++){
                barricade(row,column,mapX,mapY);
            }
        }
    }
    private void drawBuildings(Graphics2D g2d){

        drawRowsOfHouses(g2d,275,1,0,1);
        drawRowsOfHouses(g2d,50,1,0,1);
        drawRowsOfHouses(g2d,550,1,0,1);

        drawBuilding(g2d,300,25,200,250,3,3,1);
        drawBuilding(g2d,525,150,3,3,1);
        drawRowsOfHouses(g2d,500,0,3,1);
        drawRightHalfRowOfHouses(g2d,150,0,3,1);

        drawBuilding(g2d,575,225,200,250,0,3,2);

        drawBuilding(g2d,575,225,200,250,0,1,4);
        setMapBlock(18,24,7,1,4);
        setMapBlock(18,25,7,1,4);

        drawRowsOfHouses(g2d,325,0,3,0);
        drawRowsOfHouses(g2d,50,0,3,0);
        drawRowsOfHouses(g2d,600,0,3,0);
        if(frame.getStoryLocation()>8) {
            drawRowsOfHouses(g2d, 50, 2, 8, 1);
            drawRowsOfHouses(g2d, 325, 1, 8, 1);
            drawRowsOfHouses(g2d, 600, 2, 8, 1);
        }
//        justDrawBuilding(g2d,175,50,2,8,1);
//        justDrawBuilding(g2d,300,50,2,8,1);
//        justDrawBuilding(g2d,425,50,2,8,1);
//        justDrawBuilding(g2d,550,50,2,8,1);
//        justDrawBuilding(g2d,800,50,2,8,1);
//        justDrawBuilding(g2d,925,50,2,8,1);
//        justDrawBuilding(g2d,1050,50,2,8,1);
//        justDrawBuilding(g2d,1175,50,2,8,1);
//        justDrawBuilding(g2d,175,325,1,8,1);
//        justDrawBuilding(g2d,300,325,1,8,1);
//        justDrawBuilding(g2d,425,325,1,8,1);
//        justDrawBuilding(g2d,550,325,1,8,1);
//        justDrawBuilding(g2d,800,325,1,8,1);
//        justDrawBuilding(g2d,925,325,1,8,1);
//        justDrawBuilding(g2d,1050,325,1,8,1);
//        justDrawBuilding(g2d,1175,325,1,8,1);
//        justDrawBuilding(g2d,175,600,2,8,1);
//        justDrawBuilding(g2d,300,600,2,8,1);
//        justDrawBuilding(g2d,425,600,2,8,1);
//        justDrawBuilding(g2d,550,600,2,8,1);
//        justDrawBuilding(g2d,800,600,2,8,1);
//        justDrawBuilding(g2d,925,600,2,8,1);
//        justDrawBuilding(g2d,1050,600,2,8,1);
//        justDrawBuilding(g2d,1175,600,2,8,1);

        drawRowsOfHouses(g2d,0,1,8,2);
        drawRowsOfHouses(g2d,200,2,8,2);
        drawRowsOfHouses(g2d,400,1,8,2);
        drawRowsOfHouses(g2d,600,2,8,2);

        drawRowsOfHouses(g2d,75,1,8,3);
        drawRowsOfHouses(g2d,350,2,8,3);
        drawRowsOfHouses(g2d,625,1,8,3);

        drawRowsOfHouses(g2d,350,1,8,5);
        drawRowsOfHouses(g2d,75,1,8,5);
        drawRowsOfHouses(g2d,625,1,8,5);

        drawRowsOfHouses(g2d,275,8,3,5);
        drawRowsOfHouses(g2d,50,8,3,5);
        drawRowsOfHouses(g2d,550,8,3,5);

        drawBuilding(g2d,50,100,200,250,8,2,5);

        drawRowsOfHouses(g2d,350,8,3,6);
        drawRowsOfHouses(g2d,75,8,3,6);
        drawRowsOfHouses(g2d,625,8,3,6);

        drawRowsOfHouses(g2d,200,8,2,6);
        drawRowsOfHouses(g2d,400,8,2,6);
        drawRowsOfHouses(g2d,600,8,2,6);

        drawRowsOfHouses(g2d,100,1,5,3);
        drawRowsOfHouses(g2d,300,1,5,3);
        drawRowsOfHouses(g2d,500,1,5,3);

        drawRowsOfHouses(g2d,100,0,6,3);
        drawRowsOfHouses(g2d,300,0,6,3);
        drawRowsOfHouses(g2d,500,0,6,3);

        drawConsecutiveBuildings(g2d,25,0,4,14,50,34);
        drawConsecutiveBuildings(g2d,800,300,4,6,50,34);
        drawConsecutiveBuildings(g2d,800,600,4,6,50,34);
        drawConsecutiveBuildings(g2d,25,300,4,6,50,34);
        drawConsecutiveBuildings(g2d,25,600,4,6,50,34);

        drawConsecutiveBuildings(g2d,800,375,4,6,50,35);
        drawConsecutiveBuildings(g2d,800,75,4,6,50,35);
        drawConsecutiveBuildings(g2d,800,675,4,6,50,35);

        drawConsecutiveBuildings(g2d,800,200,4,6,50,36);
        drawConsecutiveBuildings(g2d,800,-100,4,6,50,36);
        drawConsecutiveBuildings(g2d,800,500,4,6,50,36);
        drawConsecutiveBuildings(g2d,25,200,4,6,50,36);
        drawConsecutiveBuildings(g2d,25,-100,4,6,50,36);
        drawConsecutiveBuildings(g2d,25,500,4,6,50,36);

        drawConsecutiveBuildings(g2d,200,300,4,6,51,34);
        drawConsecutiveBuildings(g2d,975,300,4,5,51,34);
        drawConsecutiveBuildings(g2d,-50,0,4,15,51,34);
        drawConsecutiveBuildings(g2d,975,600,4,5,51,34);
        drawConsecutiveBuildings(g2d,200,600,4,6,51,34);
        drawConsecutiveBuildings(g2d,-75,300,4,1,51,34);
        drawConsecutiveBuildings(g2d,-75,600,4,1,51,34);

        drawConsecutiveBuildings(g2d,975,375,4,5,51,35);
        drawConsecutiveBuildings(g2d,975,75,4,5,51,35);
        drawConsecutiveBuildings(g2d,975,675,4,5,51,35);
        drawConsecutiveBuildings(g2d,-75,375,4,1,51,35);
        drawConsecutiveBuildings(g2d,-75,75,4,1,51,35);
        drawConsecutiveBuildings(g2d,-75,675,4,1,51,35);
        drawBuilding(g2d,350,250,300,375,7,51,35);

        drawConsecutiveBuildings(g2d,200,200,4,6,51,36);
        drawConsecutiveBuildings(g2d,200,500,4,6,51,36);
        drawConsecutiveBuildings(g2d,975,200,4,5,51,36);
        drawConsecutiveBuildings(g2d,975,-100,4,5,51,36);
        drawConsecutiveBuildings(g2d,975,500,4,5,51,36);
        drawConsecutiveBuildings(g2d,-75,200,4,1,51,36);
        drawConsecutiveBuildings(g2d,-75,-100,4,1,51,36);
        drawConsecutiveBuildings(g2d,-75,500,4,1,51,36);

        drawConsecutiveBuildings(g2d,375,300,4,6,52,34);
        drawConsecutiveBuildings(g2d,-25,0,4,14,52,34);
        drawConsecutiveBuildings(g2d,375,600,4,6,52,34);
        drawConsecutiveBuildings(g2d,0,300,4,2,52,34);
        drawConsecutiveBuildings(g2d,0,600,4,2,52,34);

        drawConsecutiveBuildings(g2d,375,375,4,6,52,35);
        drawConsecutiveBuildings(g2d,375,75,4,6,52,35);
        drawConsecutiveBuildings(g2d,375,675,4,6,52,35);
        drawConsecutiveBuildings(g2d,0,375,4,2,52,35);
        drawConsecutiveBuildings(g2d,0,75,4,2,52,35);
        drawConsecutiveBuildings(g2d,0,675,4,2,52,35);

        drawConsecutiveBuildings(g2d,375,200,4,6,52,36);
        drawConsecutiveBuildings(g2d,375,-100,4,6,52,36);
        drawConsecutiveBuildings(g2d,375,500,4,6,52,36);
        drawConsecutiveBuildings(g2d,0,200,4,2,52,36);
        drawConsecutiveBuildings(g2d,0,-100,4,2,52,36);
        drawConsecutiveBuildings(g2d,0,500,4,2,52,36);

        drawConsecutiveBuildings(g2d,50,475,4,6,55,33);
        drawConsecutiveBuildings(g2d,875,475,4,6,55,33);
        drawConsecutiveBuildings(g2d,50,0,4,14,55,33);
        drawConsecutiveBuildings(g2d,50,150,5,14,55,33);

        drawConsecutiveBuildings(g2d,50,350,4,6,55,34);
        drawConsecutiveBuildings(g2d,875,350,4,6,55,34);
        drawConsecutiveBuildings(g2d,50,650,4,6,55,34);
        drawConsecutiveBuildings(g2d,875,650,4,6,55,34);
        drawConsecutiveBuildings(g2d,50,25,4,6,55,34);
        drawConsecutiveBuildings(g2d,875,25,4,6,55,34);

        drawConsecutiveBuildings(g2d,875,200,4,6,55,35);
        drawConsecutiveBuildings(g2d,875,575,4,6,55,35);
        drawConsecutiveBuildings(g2d,50,200,4,6,55,35);
        drawConsecutiveBuildings(g2d,50,575,4,6,55,35);

        drawConsecutiveBuildings(g2d,875,125,4,6,55,36);
        drawConsecutiveBuildings(g2d,875,450,4,6,55,36);
        drawConsecutiveBuildings(g2d,50,125,4,6,55,36);
        drawConsecutiveBuildings(g2d,50,450,4,6,55,36);
        drawConsecutiveBuildings(g2d,50,775,4,6,55,36);
        drawConsecutiveBuildings(g2d,875,775,4,6,55,36);

        drawConsecutiveBuildings(g2d,875,0,4,6,55,37);
        drawConsecutiveBuildings(g2d,875,325,4,6,55,37);
        drawConsecutiveBuildings(g2d,50,0,4,6,55,37);
        drawConsecutiveBuildings(g2d,50,325,4,6,55,37);
        drawConsecutiveBuildings(g2d,50,650,4,14,55,37);

        drawConsecutiveBuildings(g2d,0,475,4,1,56,33);
        drawConsecutiveBuildings(g2d,325,475,4,6,56,33);
        drawConsecutiveBuildings(g2d,-50,0,4,14,56,33);
        drawConsecutiveBuildings(g2d,-50,150,5,14,56,33);

        drawConsecutiveBuildings(g2d,0,350,4,1,56,34);
        drawConsecutiveBuildings(g2d,325,350,4,6,56,34);
        drawConsecutiveBuildings(g2d,0,650,4,1,56,34);
        drawConsecutiveBuildings(g2d,325,650,4,6,56,34);
        drawConsecutiveBuildings(g2d,0,25,4,1,56,34);
        drawConsecutiveBuildings(g2d,325,25,4,6,56,34);

        drawConsecutiveBuildings(g2d,325,200,4,6,56,35);
        drawConsecutiveBuildings(g2d,325,575,4,6,56,35);
        drawConsecutiveBuildings(g2d,0,200,4,1,56,35);
        drawConsecutiveBuildings(g2d,0,575,4,1,56,35);

        drawConsecutiveBuildings(g2d,325,125,4,6,56,36);
        drawConsecutiveBuildings(g2d,325,450,4,6,56,36);
        drawConsecutiveBuildings(g2d,0,125,4,1,56,36);
        drawConsecutiveBuildings(g2d,0,450,4,1,56,36);
        drawConsecutiveBuildings(g2d,0,775,4,1,56,36);
        drawConsecutiveBuildings(g2d,325,775,4,6,56,36);

        drawConsecutiveBuildings(g2d,325,0,4,6,56,37);
        drawConsecutiveBuildings(g2d,325,325,4,6,56,37);
        drawConsecutiveBuildings(g2d,0,0,4,1,56,37);
        drawConsecutiveBuildings(g2d,0,325,4,1,56,37);
        drawConsecutiveBuildings(g2d,-50,650,4,14,56,37);

        drawBuilding(g2d,1400,400,9,71,37);
        drawBuilding(g2d,300,100,9,71,37);
        drawBuilding(g2d,600,575,9,71,37);
        drawBuilding(g2d,900,150,9,71,37);
        drawBuilding(g2d,700,200,9,71,38);
        drawBuilding(g2d,300,550,9,71,38);
        drawBuilding(g2d,1100,400,9,71,38);
        drawBuilding(g2d,350,150,9,72,37);
        drawBuilding(g2d,1250,50,9,72,37);
        drawBuilding(g2d,825,450,9,72,37);
    }
    private void drawRowsOfHouses(Graphics2D g2d, int y, int house, int mapX, int mapY){
        drawLeftHalfRowOfHouses(g2d,y,house,mapX,mapY);
        drawRightHalfRowOfHouses(g2d,y,house,mapX,mapY);
    }
    private void drawLeftHalfRowOfHouses(Graphics2D g2d, int y, int house,int mapX, int mapY){
        drawBuilding(g2d,175,y,house,mapX,mapY);
        drawBuilding(g2d,300,y,house,mapX,mapY);
        drawBuilding(g2d,425,y,house,mapX,mapY);
        drawBuilding(g2d,550,y,house,mapX,mapY);
    }
    private void drawRightHalfRowOfHouses(Graphics2D g2d, int y, int house, int mapX, int mapY){
        drawBuilding(g2d,800,y,house,mapX,mapY);
        drawBuilding(g2d,925,y,house,mapX,mapY);
        drawBuilding(g2d,1050,y,house,mapX,mapY);
        drawBuilding(g2d,1175,y,house,mapX,mapY);
    }
    private void drawConsecutiveBuildings(Graphics2D g2d, int x, int y, int building, int num, int mapX, int mapY){
        for(int i = 0; i < num; i++){
            drawBuilding(g2d,x+i*100,y,building,mapX,mapY);
        }
    }
    private void drawItems(Graphics2D g2d){
        for(int i = 0; i < frame.getItems().size(); i++){
            Item item = frame.getItems().get(i);
            int xPos = item.getxPos();
            int yPos = item.getyPos();
            if(Math.abs(xPos) < 1425 && Math.abs(yPos) < 825 && !item.isRemovable()){
//                if(item.size()!=1){
                    g2d.drawImage(item.getImage(),xPos,yPos,item.width()*item.size(),item.height()*item.size(),null);
//                }
//                else g2d.drawImage(item.getImage(),xPos,yPos,null);
            }
        }
    }
    private void drawAttacks(Graphics2D g2d) {
        boolean paused = menuVisible;
        for (int i = 0 ; i < attacks.size(); i++) {
            Attack attack = attacks.get(i);
            //if(!paused) {
            attack.move();
            //}
            g2d.drawImage(attack.image(), attack.getXPos(), attack.getyPos(), attack.getWidth(), attack.getHeight(), null);
//            g2d.drawImage(attacks.get(i).image(), attacks.get(i).getXPos(), attacks.get(i).getyPos(),500,500,null); //make sure attacks are being drawn
        }
        //if(!paused) {
        for (int i = attacks.size() - 1; i > -1; i--) {
            if (attacks.get(i).attackDelete()) {
                attacks.remove(i);
            }
        }
        //}
    }
    private void drawCooldowns(Graphics2D g2d){
        int x = 550;
        int y = 650;
        for(int skillNum = 0; skillNum < 4; skillNum++){
            setOpacity(g2d,0.6F);
            g2d.setColor(colors[1]);
            drawSquare(g2d,x,y,75);
            g2d.setColor(colors[8]);
            g2d.drawRect(x,y,75,75);
            for(int i = 0; i < frame.skillList().length; i++){
                if(player.getSkill(skillNum).equals(frame.skillList()[i])){
                    g2d.drawImage(skills[i],x,y,75,75,null);
                }
            }
            g2d.setColor(colors[9]);
            typeText(g2d,skillNum+1 + "",x+4,y+18,2F);
            if(player.getRealCurrentCooldown(skillNum)>0){
                setOpacity(g2d,0.9F);
                g2d.setColor(new Color(0xE6000000));
                int[] xs = new int[]{x+37,0,x+75,x+75,x,x,x+37};
                int[] ys = new int[]{y+37,0,y,y+75,y+75,y,y};
                int degrees = (int)(player.getRealCurrentCooldown(skillNum)*360/player.getCooldown(skillNum) - 90);
//                System.out.println(degrees);
                float radians = (float)(player.getRealCurrentCooldown(skillNum)*6.28/player.getCooldown(skillNum) - 3.14/2);
                xs[1] = xs[0] + (int)(Math.cos(radians)*37*-1);
                ys[1] = ys[0] + (int)(Math.sin(radians)*37);
                //g2d.fillPolygon(xs, ys, xs.length);
                //System.out.println(radians);
                if(degrees > 240){//radians >= 4.186401224){//3.924601837){
                    ys[1] = y;
                    xs[1]+=(int)(Math.cos(radians)*37*-1);
                    if(xs[1] > x+75){
                        xs[1] = x+75;
                    }
                    //System.out.println("TOP RIGHT");
                }
//                else if(radians >= 3.662802448){
//                    xs[1] = x + 75;
//                    ys[1]+=(int)(Math.sin(radians)*37);
//                    if(ys[1] < y){
//                        ys[1] = y;
//                    }
//                    xs[2] = xs[1];
//                    ys[2] = ys[1];
//                    //System.out.println("RIGHT");
//                }
                else if(degrees > 120){//radians >= 2.35380551){
                    xs[1] = x + 75;
                    //ys[1]+=(int)(Math.sin(radians)*37);
                    if(ys[1] < y){
                        ys[1] = y;
                    }
                    xs[2] = xs[1];
                    ys[2] = ys[1];
                    //System.out.println("RIGHT");
                }
                else if(degrees > 60){//radians >= 1.306607959){
                    ys[1] = y + 75;
                    xs[1]+=(int)(Math.cos(radians)*37*-1);
                    if(xs[1] < x){
                        xs[1] = x;
                    }
                    xs[2] = xs[1];
                    ys[2] = ys[1];
                    xs[3] = xs[1];
                    //System.out.println("BOTTOM");
                }
//                else if(radians >= 0.787787144){
//                    xs[1] = x;
//                    ys[1]+=(int)(Math.sin(radians)*37);
//                    if(ys[1] > y+75){
//                        ys[1] = y+75;
//                    }
//                    xs[2] = xs[1];
//                    ys[2] = ys[1];
//                    xs[3] = xs[1];
//                    ys[3] = ys[1];
//                    xs[3] = xs[1];
//                    ys[4] = ys[1];
//                    //System.out.println("LEFT");
//                }
                else if(degrees > -60){//radians >= -0.787787144){
                    xs[1] = x;
                    //ys[1]+=(int)(Math.sin(radians)*37);
                    if(ys[1] > y+75){
                        ys[1] = y+75;
                    }
                    xs[2] = xs[1];
                    ys[2] = ys[1];
                    xs[3] = xs[1];
                    ys[3] = ys[1];
                    xs[3] = xs[1];
                    ys[4] = ys[1];
                    //System.out.println("LEFT");
                }
                else{
                    ys[1] = y;
                    xs[1]+=(int)(Math.cos(radians)*37*-1);
                    if(xs[1] < x){
                        xs[1] = x;
                    }
                    xs[2] = xs[1];
                    ys[2] = ys[1];
                    xs[3] = xs[1];
                    ys[3] = ys[1];
                    xs[4] = xs[1];
                    ys[4] = ys[1];
                    xs[5] = xs[1];
                    ys[5] = ys[1];
                }
                //g2d.setColor(new Color(0xFFFFFF));
                g2d.fillPolygon(xs, ys, xs.length);
                //drawSquare(g2d,x,y,75);
                g2d.setColor(colors[9]);
                typeText(g2d,player.getCurrentCooldown(skillNum)+"",x+35 - (player.getCurrentCooldown(skillNum) + "").length()*8,y+45,3F);
            }
            x+=77;
        }
        setOpacity(g2d,1F);
    }
    public void setOpacity(Graphics2D g2d, float opacity){
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    }
    private void drawPlace(Graphics2D g2d){
        g2d.setColor(Color.black);
        if(locationTick>20){
            setOpacity(g2d,1F - (float) (locationTick-19)/50);
        }
        if(locationY>-10){
            locationTick++;
            typeText(g2d,"-" + location + "-",620,locationY,2F);
            if(locationDown) {
                locationY+=(50-locationY)/10;
                if(locationTick > 40){
                    locationDown = false;
                }
            }
            else{
                //locationY -= (locationY+30)/10;
            }
        }
        if(locationTick>68){
            locationY = -10;
        }
        setOpacity(g2d,1F);
    }

    private void drawNpcs(Graphics2D g2d){
        if(touchedPreviously){
            messageTick+=(50-messageTick)/10;
            touchedPreviously = false;
        }
        else{
            messageTick+=(-10-messageTick)/10;
        }
        canInteract = false;
        for (int i = 0; i < npcList.size(); i++){
            //if (npcList.get(i).getMapX() == mapNumX && npcList.get(i).getMapY() == mapNumY) {
            Person npc = npcList.get(i);
            npc.move();
            if(npc.berserk()){
                g2d.setColor(new Color(0x00051C));
            }
            else if(npc.getType().contains("red")){
                g2d.setColor(Color.red);
            }
            else{
                g2d.setColor(Color.blue);
            }
            if (npc.getxPos() + squareSize >= player.getxPos() && npc.getxPos() <= player.getxPos() + squareSize && npc.getyPos() + squareSize >= player.getyPos() && npc.getyPos() <= player.getyPos() + squareSize && !inChat && !frame.isInStory()) {
                npcTouched = i;
                //System.out.println("touching npc");
                canInteract = true;
                touchedPreviously = true;
            }
            if(npc.getAccessories()!=null){
                for(int accessory : npc.getAccessories()){
                    if(npc.getDirection().equals("right") || npc.getLastDirection().equals("right")){
                        g2d.drawImage(accessories[accessory],npc.getxPos()-13,npc.getyPos()-13,50,50,null);
                    }
                    else{
                        g2d.drawImage(accessories[accessory],npc.getxPos()+37,npc.getyPos()-13,-50,50,null);
                    }
                }
            }
            if(npc.getType().equals("duck")){
                g2d.drawImage(duck,npc.getxPos(),npc.getyPos(),null);
            }
            else {
                drawSquare(g2d, npc.getxPos(), npc.getyPos());
            }
            //}
        }
        if(!canInteract){
            touchedPreviously = false;
        }
    }
    public void checkIn(){
        //System.out.print("checked");
        repaint();
    }
    private void drawNpcsData(Graphics2D g2d){
        Person npc;
        for (int i = 0; i < npcList.size(); i++){
            //if (npcList.get(i).getMapX() == mapNumX && npcList.get(i).getMapY() == mapNumY) {
            npc = npcList.get(i);
            int xPos = npc.getxPos();
            int yPos = npc.getyPos();
            int scale = 4;
            if(npc.getDisplayName().length() > 10){
                //scale = 5;
            }
            g2d.setColor(new Color(0x67343434, true));
            g2d.fillRect(xPos-npc.getDisplayName().length()*scale + 3,yPos-25,npc.getDisplayName().length()*scale*2+20,20);
            g2d.setColor(colors[9]);
            typeText(g2d, npc.getDisplayName(), xPos + 10 - npc.getDisplayName().length() * scale, yPos-10, 1.5F);
            //}
        }
        for(Person person : allyList){
            if(person.getDisplayName()!=null) {
                int scale = 4;
                if (person.getDisplayName().length() > 10) {
                    //scale = 5;
                }
                g2d.setColor(new Color(0x67343434, true));
                g2d.fillRect(person.getxPos() - person.getDisplayName().length() * scale + 3, person.getyPos() - 35, person.getDisplayName().length() * scale * 2 + 20, 20);
                g2d.setColor(colors[9]);
                typeText(g2d, person.getDisplayName(), person.getxPos() + 10 - person.getDisplayName().length() * scale, person.getyPos() - 20, 1.5F);
            }
        }
        if(messageTick>0){
            g2d.setColor(colors[9]);
            int width = 217;
            int height = 31;
            width*=messageTick/35.0;
            height*=messageTick/35.0;
            setOpacity(g2d,0.8F);
            if(npcTouched==1){
                g2d.drawImage(note, npcList.get(1).getItem().getxPos() + npcList.get(1).getItem().getImage().getWidth()/2 - width / 2, npcList.get(1).getItem().getyPos() - npcList.get(1).getItem().getImage().getHeight() - height / 2, width, height, null);
            }
            else {
                g2d.drawImage(note, npcList.get(npcTouched).getxPos() + 13 - width / 2, npcList.get(npcTouched).getyPos() - 50 - height / 2, width, height, null);
            }
            setOpacity(g2d,1F);
            //typeText(g2d,"Press X to interact", npcList.get(npcTouched).getxPos()-80-messageTick/3,npcList.get(npcTouched).getyPos()-50-messageTick/5,1+messageTick/25F);
        }
    }
    private void peopleActions(Graphics2D g2d, ArrayList<Person> people){
        for(int i = 0; i < people.size(); i++) {
            Person person = people.get(i);
            Person enemy = person.getEnemy();
            int personX = person.getxPos();
            int personY = person.getyPos();
            if(person.getType().contains("placeHolder")){
                person.setType(person.getType().substring(11));}
            if(people == enemyList && person.getType().contains("Enemy")){
                if(person.tookDamage()){
                    g2d.setColor(new Color(0xFF8686)); //replace this with spark effects later
                }
                else {
                    g2d.setColor(new Color(0xFF0000));
                }
                int diff = Math.abs(person.getxPos() - player.getxPos()) + Math.abs(person.getyPos() - player.getyPos());
                int target = -1;
                if(!allyList.isEmpty()){
                    for(int index = 0; index < allyList.size(); index++){
                        int currentDiff = Math.abs(person.getxPos() - allyList.get(index).getxPos()) + Math.abs(person.getyPos() - allyList.get(index).getyPos());
                        if(currentDiff < diff){
                            diff = currentDiff;
                            target = index;
                        }
                    }
                }
                if(target>=0) {
                    person.setEnemy(allyList.get(target));
                }
                else {
                    person.setEnemy(player);
                }
            }
            else{
                if(person.tookDamage()){
                    g2d.setColor(new Color(0x7E7EFF));
                }
                else {
                    g2d.setColor(new Color(0x0000FF));
                }
                g2d.setColor(Color.blue);
                if(!enemyList.contains(person.getEnemy())){
                    if(!enemyList.isEmpty()){
                        int diff = 100000;
                        int target = 0;
                        for(int index = 0; index < enemyList.size(); index++){
                            int currentDiff = Math.abs(person.getxPos() - enemyList.get(index).getxPos()) + Math.abs(person.getyPos() - enemyList.get(index).getyPos());
                            if(currentDiff < diff){
                                diff = currentDiff;
                                target = index;
                            }
                        }
                        person.setEnemy(enemyList.get(target));
                    }
                    else{
                        enemy = null;
                    }
                }
            }
            if(person.berserk()){
                g2d.setColor(new Color(0x00051C));
            }
            if(!person.inAttack()) person.incrementCount();
            if(person.movable() && !frame.isInStory() && enemy!=null){
                if(person.getType().contains("Boss") && person.getAttackCount() > 10 && !person.inAttack()){
                    int rand = (int)(Math.random()*4);
                    if(rand==0)person.setDirection("right");
                    if(rand==1)person.setDirection("left");
                    if(rand==2)person.setDirection("up");
                    if(rand==3)person.setDirection("down");
                    person.bossAttack();
                }
                person.checkSkill(1,10000);
                person.checkSkill(2,121);
                person.checkSkill(3,10000);
                person.checkSkill(4,10000);
                person.checkSkill(7,10000);
                if (person.getType().contains("melee")) {
                    if ((enemy.getxPos() + person.getDetectionRange() > person.getxPos() && enemy.getxPos() - person.getDetectionRange() < person.getxPos() && enemy.getyPos() + person.getDetectionRange() > person.getyPos() && enemy.getyPos() - person.getDetectionRange() < person.getyPos()) || people.get(i).getHpPercent() < 100) {
                        person.aggroOn();
                        if(person.inAttack()){
                            person.personAttacking();
                        }
                        else {
                            for(int m = 0; m < person.getSpeed(); m++) {
//                                if(person.needPath() && !barricaded) {
                                if(!frame.isGamePlaying()){
                                    person.pathFind();
                                    String path = person.pathFound(0);
                                    if(!path.isEmpty()){
                                        if(path.equals("left")){
                                            movePerson(-1,0,person);
                                        }
                                        else if(path.equals("right")){
                                            movePerson(1,0,person);
                                        }
                                        else if(path.equals("up")){
                                            movePerson(0,-1,person);
                                        }
                                        else{
                                            movePerson(0,1,person);
                                        }
                                    }
//                                if (!moved && person.getAttackCount() > 10 && m==0) {
                                    else if(person.getAttackCount() > 10){
                                        if (enemy.getxPos() < person.getxPos()) {
                                            person.setDirection("left");
                                        }
                                        if (enemy.getxPos() > person.getxPos()) {
                                            person.setDirection("right");
                                        }
                                        person.personAttack(i);
                                    }
                                }
                                else {
                                    boolean xMoreDiff = Math.abs(person.getxPos() - enemy.getxPos()) > Math.abs(person.getyPos() - enemy.getyPos());
                                    boolean moved = false;
                                    int shouldMove = 0;
                                    if (person.isPathFinding()) {
                                        if (person.getxPos() > enemy.getxPos()) {
                                            movePerson(-1, 0, person);
                                        } else {
                                            movePerson(1, 0, person);
                                        }
                                        if (personX != person.getxPos()) {
                                            person.setPathFinding(false);
                                        }
                                    }
                                    if (xMoreDiff || person.isPathFinding()) {
                                        if (person.getxPos() + squareSize - 1 < enemy.getxPos()) {
                                            movePerson(1, 0, person);
                                            shouldMove = 1;
                                            if (personX != person.getxPos()) {
                                                moved = true;
                                            }
                                        }
                                        if (person.getxPos() > enemy.getxPos() + squareSize - 1 && !moved) {
                                            movePerson(-1, 0, person);
                                            shouldMove = 1;
                                            if (personX != person.getxPos()) {
                                                moved = true;
                                            }
                                        }
                                    }
                                    if (person.getyPos() + squareSize - 1 < enemy.getyPos() && !moved && !(shouldMove == 1)) {
                                        movePerson(0, 1, person);
                                        shouldMove = 2;
                                        if (personY != person.getyPos()) {
                                            moved = true;
                                        }
                                    }
                                    if (person.getyPos() > enemy.getyPos() + squareSize - 1 && !moved && !(shouldMove == 1)) {
                                        movePerson(0, -1, person);
                                        shouldMove = 2;
                                        if (personY != person.getyPos()) {
                                            moved = true;
                                        }
                                    }
                                    if (!xMoreDiff) {
                                        if (person.getxPos() + squareSize - 1 < enemy.getxPos() && !moved && !(shouldMove == 2)) {
                                            movePerson(1, 0, person);
                                            shouldMove = 1;
                                            if (personX != person.getxPos()) {
                                                moved = true;
                                            }
                                        }
                                        if (person.getxPos() > enemy.getxPos() + squareSize - 1 && !moved && !(shouldMove == 2)) {
                                            movePerson(-1, 0, person);
                                            shouldMove = 1;
                                            if (personX != person.getxPos()) {
                                                moved = true;
                                            }
                                        }
                                    }
                                    if (!moved && shouldMove > 0) {
                                        if (shouldMove == 1) {
                                            if(!(person.getLastDirection().equals("up") && person.isPathFinding())) {
                                                if (person.getyPos() < enemy.getyPos() || (person.getLastDirection().equals("down") && person.isPathFinding())) {
                                                    movePerson(0, 1, person);
                                                    if (personY != person.getyPos()) {
                                                        moved = true;
                                                    }
                                                }
                                            }
                                            if(!moved && (person.getyPos() > enemy.getyPos() || (person.getLastDirection().equals("up") && person.isPathFinding()))){
                                                movePerson(0, -1, person);
                                                if (personY != person.getyPos()) {
                                                    moved = true;
                                                }
                                            }
                                            if (!moved) {
                                                int move = 1;
                                                if (person.getxPos() > person.getEnemy().getxPos()) {
                                                    move = -move;
                                                }
                                                int y = person.getyPos();
                                                boolean loop = true;
                                                while (loop) {
                                                    if (collisionCheck(person.getxPos(), y, 25, 25, move, 0) != 0) {
                                                        if (collisionCheck(person.getxPos(), y, 25, 25, 0, -1) == 0) {
                                                            y -= 1;
                                                        } else {
                                                            loop = false;
                                                            person.setLastDirection("down");
                                                            person.setDirection("down");
                                                            person.setPathFinding(true);
                                                        }
                                                    } else {
                                                        loop = false;
                                                    }
                                                }
                                                y = person.getyPos();
                                                loop = true;
                                                while (loop) {
                                                    if (collisionCheck(person.getxPos(), y, 25, 25, move, 0) != 0) {
                                                        if (collisionCheck(person.getxPos(), y, 25, 25, 0, 1) == 0) {
                                                            y += 1;
                                                        } else {
                                                            loop = false;
                                                            person.setLastDirection("up");
                                                            person.setDirection("up");
                                                            person.setPathFinding(true);
                                                        }
                                                    } else {
                                                        loop = false;
                                                    }
                                                }
                                            }
                                        } else {
                                            if (person.getxPos() < enemy.getxPos() && !person.getLastDirection().equals("left")) {
                                                movePerson(1, 0, person);
                                                if (personX != person.getxPos()) {
                                                    moved = true;
                                                }
                                            } else {
                                                movePerson(-1, 0, person);
                                                if (personX != person.getxPos()) {
                                                    moved = true;
                                                }
                                            }
                                        }
                                    }
                                    //if (!moved && person.getAttackCount() > 10 && m == 0) {
//                                    if(person.getAttackCount()>10){
                                    if (person.getAttackCount() > 10 && !moved && Math.abs(person.getxPos() - enemy.getxPos()) < 50 && Math.abs(person.getyPos() - enemy.getyPos()) < 50) {
                                        if (enemy.getxPos() < person.getxPos()) {
                                            person.setDirection("left");
                                        }
                                        if (enemy.getxPos() > person.getxPos()) {
                                            person.setDirection("right");
                                        }
                                        person.personAttack(i);
                                    }
                                    //}
                                }
                            }
                        }
                    }
                }
                else if (person.getType().contains("ranged")) {
                    if ((enemy.getxPos() + person.getDetectionRange() > person.getxPos() && enemy.getxPos() - person.getDetectionRange() < person.getxPos() && enemy.getyPos() + person.getDetectionRange() > person.getyPos() && enemy.getyPos() - person.getDetectionRange() < person.getyPos()) || people.get(i).getHpPercent() < 100) {
                        if(person.inAttack()){
                            person.personAttacking();
                        }
                        else {
                            boolean moved = false;
                            if (person.getxPos() + squareSize + 49 < enemy.getxPos()) {
                                movePerson(person.getSpeed(), 0, person);
                                if (personX != person.getxPos()) {
                                    moved = true;
                                    person.setDirection("right");
                                }
                            }
                            if (person.getxPos() > enemy.getxPos() + squareSize + 49 && !moved) {
                                movePerson(-person.getSpeed(), 0, person);
                                if (personX != person.getxPos()) {
                                    moved = true;
                                    person.setDirection("left");
                                }
                            }
                            if (person.getyPos() + squareSize + 49 < enemy.getyPos() && !moved) {
                                movePerson(0, person.getSpeed(), person);
                                if (personY != person.getyPos()) {
                                    moved = true;
                                    person.setDirection("down");
                                }
                            }
                            if (person.getyPos() > enemy.getyPos() + squareSize + 49 && !moved) {
                                movePerson(0, -person.getSpeed(), person);
                                if (personY != person.getyPos()) {
                                    moved = true;
                                    person.setDirection("up");
                                }
                            }
//                            if(!moved){
//                                boolean inLine = false;
//                                if (person.getxPos() + squareSize > enemy.getxPos() && person.getxPos() < enemy.getxPos() + squareSize) {
//                                    inLine = true;
//                                }
//                                if (person.getyPos() + squareSize > enemy.getyPos() && person.getyPos() < enemy.getyPos() + squareSize && !inLine) {
//                                    inLine = true;
//                                }
//                                if(!inLine) {
//                                    boolean xLess = person.getxPos() < enemy.getxPos();
//                                    boolean yLess = person.getyPos() < enemy.getyPos();
//                                    int xDiff = Math.abs(person.getxPos() - enemy.getxPos());
//                                    int yDiff = Math.abs(person.getyPos() - enemy.getyPos());
//                                    if (yDiff > xDiff) {
//                                        if (xLess) {
//                                            movePerson(person.getSpeed(), 0, person);
//                                        } else {
//                                            movePerson(-person.getSpeed(), 0, person);
//                                        }
//                                        if (person.getxPos() != person.getxPos()) {
//                                            moved = true;
//                                            if (yLess) {
//                                                person.setDirection("down");
//                                            } else {
//                                                person.setDirection("up");
//                                            }
//                                        }
//                                    } else {
//                                        if (yLess) {
//                                            movePerson(0, person.getSpeed(), person);
//                                        } else {
//                                            movePerson(0, -person.getSpeed(), person);
//                                        }
//                                        if (person.getyPos() != person.getyPos()) {
//                                            moved = true;
//                                            if (xLess) {
//                                                person.setDirection("right");
//                                            } else {
//                                                person.setDirection("left");
//                                            }
//                                        }
//                                    }
//                                }
//                            }
                            if (!moved && person.getAttackCount()>29) {
                                person.personAttack(i);
                            }
                        }
                    }
                }
            }
            if(people.size()>i) {
                if (person.getType().equals("dead")) {
                    person.setType("dead0");
                    if(people==enemyList){
                        player.getXp(person.xpGain());
                        defeats++;
                    }
                    enemy.changeMood(0.01);
                }
            }
            if (!(person.getType().equals("") || person.getType().contains("dead"))) {
                drawSquare(g2d, person.getxPos(), person.getyPos());
                drawStatuses(g2d,person);
            }
            if (person.getType().contains("dead")){ //death animation
                person.deadCount();
                g2d.setColor(new Color(255,0,0, person.getDeadCounter()));
                drawSquare(g2d, person.getxPos(), person.getyPos());
            }
        }
        for(int index = people.size()-1; index >= 0; index--){ //dead enemy deletion and respawn
            boolean dead = people.get(index).getType().contains("dead") && people.get(index).getDeadCounter()==0;
            boolean close;
            boolean xClose;
            boolean yClose;
            if(people.get(index).getxPos() < player.getxPos()) {
                xClose = player.getxPos()-people.get(index).getxPos() < 2850;
            }
            else{
                xClose = people.get(index).getxPos() - player.getxPos()< 2850;
            }
            if(people.get(index).getyPos() < player.getyPos()){
                yClose = player.getyPos()-people.get(index).getyPos() < 1650;
            }
            else{
                yClose = people.get(index).getyPos() - player.getyPos()< 1650;
            }
            close = xClose && yClose;
            if(dead || !close){
                Person enemy = people.get(index);
                if(dead) {
                    enemy.check();
                }
                if(people==enemyList && enemy.getType().contains("Enemy")) {
                    enemyNums.remove(index);
                }
                people.remove(index);
                if(enemy.respawnableTimes()!=0 && people==enemyList && dead){
                    //respawnEnemy(enemy.respawnableTimes(),1);
                    respawnEnemy(enemy.respawnableTimes(),1,enemy.getMapX(),enemy.getMapY());
                }
            }
        }
    }
    private void drawEnemies(Graphics2D g2d){
        peopleActions(g2d,enemyList);
        if(frame.getStoryLocation()==5 && numEnemies(8,1)==0 && !settingUp && !isDarkScreen() && npcList.get(0).getChatIndex() > 44){
            frame.forceExit();
            frame.advanceStory();
            frame.enterStory();
            frame.npcLeave(0);
        }
        if(frame.getStoryLocation()==27 && !settingUp){
            if(numEnemies(60,35)<14) {
                if(numEnemies(60,35) + defeats < 100) {
                    respawnEnemy(1, 60, 35, 1500, (int) (Math.random() * 724) + 25);
                    enemyList.get(enemyList.size() - 1).losePercentHp(1);
                }
                else if(numEnemies(60,35)==0){
                    frame.forceExit();
                    frame.advanceStory();
                    frame.enterStory();
                }
            }
            if(allyList.size()<16) {
                spawnAlly("meleeAlly",null,player.getxPos()-1400,frame.randomNum(724,25),player.getLevel(),2000,0,player.getLevel(),0,60,35);
                allyList.get(allyList.size() - 1).losePercentHp(1);
            }
        }
        if(frame.getStoryLocation()==38 && numEnemies(67,35)==0 && !settingUp && !isDarkScreen() && npcList.get(0).getChatIndex()>373){
            enterStory();
        }
    }
    private void drawAllies(Graphics2D g2d){
        peopleActions(g2d,allyList);
    }
    //    private void respawnEnemy(int spawnableTimes, int times){
//        int hp = 0;
//        int damageStat= 0;
//        if(mapNumX==2 && mapNumY==1){
//            damageStat=5;
//        }
//        else if(mapNumX==4 && mapNumY==1){
//            hp = 3;
//            damageStat = 10;
//        }
//        else if(mapNumX==4 && mapNumY==2){
//            hp = 3;
//            damageStat = 10;
//        }
//        else if(mapNumX==5 && mapNumY==1){
//            hp = 6;
//            damageStat = 15;
//        }
//        else if(mapNumX==5 && mapNumY==2){
//            hp = 6;
//            damageStat = 15;
//        }
//        else if(mapNumX==8 && mapNumY==1){
//            hp = 10;
//            damageStat = 20;
//        }
//        else if(mapNumX==7 && mapNumY==5){
//            hp = 12;
//            damageStat = 25;
//        }
//        else if(mapNumX==6 && mapNumY==5){
//            hp = 13;
//            damageStat = 25;
//        }
//        else if(mapNumX==4 && mapNumY==5){
//            hp = 14;
//            damageStat = 26;
//        }
//        else if(mapNumX==4 && mapNumY==3){
//            hp = 15;
//            damageStat = 28;
//        }
//        if(Math.random()<0.5){
//            Person enemy = new Person("placeHolderrangedEnemy", (int)(Math.random()*1000)+squareSize, (int)(Math.random()*700)+squareSize, hp,  250, spawnableTimes -1,0,damageStat,mapNumX,mapNumY);
//            enemy.setSpeed(4);
//            enemyList.add(enemy);
//        }
//        else {
//            enemyList.add(new Person("placeHoldermeleeEnemy", (int)(Math.random()*1000)+squareSize, (int)(Math.random()*700)+squareSize, hp,  200, spawnableTimes -1,damageStat,0,mapNumX,mapNumY));
//        }
//        int skillsLearned = (int)(Math.random()*5);
//        for(int i = 0; i < skillsLearned; i++) {
//            if (Math.random() > 0.66) {
//                int rand = (int) (Math.random() * 9);
//                while(enemyList.get(enemyList.size()-1).skillSetContain(rand))
//                {
//                    rand = (int) (Math.random() * 9);
//                }
//                enemyList.get(enemyList.size()-1).addSkills(frame.skillList()[rand], frame.getSkillCooldowns()[rand]);
//            }
//        }
//        times--;
//        if(times > 0){
//            respawnEnemy(spawnableTimes,times);
//        }
//    }
    private void drawHpBars(Graphics2D g2d){
        for(Person enemy : enemyList) {
            if(Math.abs(enemy.getxPos()) < 1425 && Math.abs(enemy.getyPos()) < 825) {
                if (!(enemy.getType().equals("") || enemy.getType().contains("dead"))) {
                    drawHpBar(g2d, enemy.getxPos() - 14, enemy.getyPos() - 10, enemy, 1);
                }
                if (enemy.getType().contains("Boss")) {
                    g2d.setColor(colors[1]);
                    typeText(g2d, enemy.getDisplayName(), 660, 25, 2.5F);
                    typeText(g2d, enemy.getDesc(), 560, 50, 2F);
                    drawHpBar(g2d, 350, 60, enemy, 15, 3, 20);
                }
            }
        }
        for(int i = 0; i < allyList.size(); i++) {
            Person enemy = allyList.get(i);
            if(Math.abs(enemy.getxPos()) < 1425 && Math.abs(enemy.getyPos()) < 825) {
                if (!(enemy.getType().equals("") || enemy.getType().contains("dead"))) {
                    drawHpBar(g2d, enemy.getxPos() - 14, enemy.getyPos() - 10, enemy, 1);
                }
            }
        }
    }
    private void drawEffects(Graphics2D g2d){
        int y = 80;
        boolean containsSkill = false;
        boolean containsPassive = false;
        for(Attack effect:effects){
            if(effect.getType().contains("skill")){
                containsSkill = true;
            }
            if(effect.getType().contains("passive")){
                containsPassive = true;
            }
        }
        boolean paused = menuVisible;
        for(int i = 0; i < effects.size(); i++){
            Attack effect = effects.get(i);
            //if(!paused) {
            effect.number();
            //}
            if(effect.getType().equals("whiteFlash")){
                if(effect.getMoveCounter()==1){
                    g2d.setColor(new Color(0xFF00FFFF));
                }
                else if(effect.getMoveCounter()==2){
                    g2d.setColor(new Color(0xED00FFFF, true));
                }
                else if(effect.getMoveCounter()==3){
                    g2d.setColor(new Color(0xDB00FFFF, true));
                }
                else if(effect.getMoveCounter()==4){
                    g2d.setColor(new Color(0xCC00FFFF, true));
                }
                else if(effect.getMoveCounter()==5){
                    g2d.setColor(new Color(0xBA00FFFF, true));
                }
                else if(effect.getMoveCounter()==6){
                    g2d.setColor(new Color(0xA800FFFF, true));
                }
                else if(effect.getMoveCounter()==7){
                    g2d.setColor(new Color(0x9900FFFF, true));
                }
                else if(effect.getMoveCounter()==8){
                    g2d.setColor(new Color(0x8700FFFF, true));
                }
                else if(effect.getMoveCounter()==9){
                    g2d.setColor(new Color(0x7500FFFF, true));
                }
                else if(effect.getMoveCounter()==10){
                    g2d.setColor(new Color(0x6600FFFF, true));
                }
                else if(effect.getMoveCounter()==11){
                    g2d.setColor(new Color(0x5400FFFF, true));
                }
                else if(effect.getMoveCounter()==12){
                    g2d.setColor(new Color(0x4200FFFF, true));
                }
                else if(effect.getMoveCounter()==13){
                    g2d.setColor(new Color(0x3300FFFF, true));
                }
                else if(effect.getMoveCounter()==14){
                    g2d.setColor(new Color(0x2100FFFF, true));
                }
                else if(effect.getMoveCounter()==15){
                    g2d.setColor(new Color(0xF00FFFF, true));
                }
                else{
                    g2d.setColor(new Color(0x4D000001, true));
                }
                drawRect(g2d,0,0,2000,2000);
            }
            else if(effect.getType().equals("obtained")){
                int x = 0;
                if(effect.getMoveCounter()<11){
                    x = effect.getMoveCounter()*5;
                }
                else if(effect.getMoveCounter()<51){
                    x = 50;
                }
                else{
                    x = 50 - (effect.getMoveCounter()-50) * 10;
                }
                g2d.setColor(new Color(0xFF000001, true));
                typeText(g2d,"Obtained:",-50 + x,y,1.5F);
                y+=20;
            }
            else if(effect.getType().contains("receiveMoney")){
                int x = 0;
                if(effect.getMoveCounter()<11){
                    x = effect.getMoveCounter()*5;
                }
                else if(effect.getMoveCounter()<51){
                    x = 50;
                }
                else{
                    x = 50 - (effect.getMoveCounter()-50) * 10;
                }
                g2d.drawImage(coin,x-50,y-14,20,20,null);
                g2d.setColor(new Color(0xFF000001, true));
                typeText(g2d,effect.getType().substring(12) + " Coins",-25 + x,y,1.5F);
                y+=20;
            }
            else if(effect.getType().contains("gainExp")) {
                g2d.setColor(new Color(0,0,0,255-effect.getMoveCounter()*8));
                typeText(g2d, "+" + effect.getType().substring(7) + " Xp", 650, 150-effect.getMoveCounter()*5, 1.5F);
            }
            else if(effect.getType().contains("gainMoney")) {
                g2d.setColor(new Color(0,0,0,255-effect.getMoveCounter()*8));
                if(Integer.parseInt(effect.getType().substring(9)) < 0){
                    typeText(g2d, effect.getType().substring(9) + " Coins", 650, 150-effect.getMoveCounter()*5, 1.5F);
                }
                else{
                    typeText(g2d, "+" + effect.getType().substring(9) + " Coins", 650, 150-effect.getMoveCounter()*5, 1.5F);
                }
            }
            else if(effect.getType().equals("thunderBoom")){
                g2d.setColor(new Color(250, 255, 118,255-effect.getMoveCounter()*8));
                drawCircle(g2d,effect.getXPos()+256-effect.getMoveCounter()*25,effect.getyPos()+256-effect.getMoveCounter()*25,effect.getMoveCounter()*50);
            }
            else if(effect.getType().contains("skill")){
                int w = effect.getWidth(); //+ (effect.getMoveCounter() * 2);
                int h = effect.getHeight(); //+ (effect.getMoveCounter() * 2);
                int x = 700;
                if(containsPassive){
                    x-=150;
                }
                g2d.setColor(new Color(0x382E2E));
                drawRoundedSquare(g2d,x-1-w/2,349-h/2,w+2,w/8);
                g2d.setColor(colors[9]);
                typeText(g2d,"New Skill Obtained!",x-(int)(w/1.5),340-h/2, (float) w/72);
                g2d.drawImage(effect.image(),x-w/2,350-h/2,w,h,null);
                g2d.setColor(new Color(0x6B4F4F));
                drawBorder(g2d,x-5-w/2,345-h/2,w+12,w+12,w/8,5);
            }
            else if(effect.getType().contains("passive")){
                int w = effect.getWidth(); //+ (effect.getMoveCounter() * 2);
                int h = effect.getHeight(); //+ (effect.getMoveCounter() * 2);
                int x = 700;
                if(containsSkill){
                    x+=150;
                }
                g2d.setColor(new Color(0x382E2E));
                drawRoundedSquare(g2d,x-1-w/2,349-h/2,w+2,w/8);
                g2d.setColor(colors[9]);
                typeText(g2d,"New Passive Obtained!",x-(int)(w/1.5),340-h/2, (float) w/72);
                g2d.drawImage(effect.image(),x-w/2,350-h/2,w,h,null);
                g2d.setColor(new Color(0x6B4F4F));
                drawBorder(g2d,x-5-w/2,345-h/2,w+12,w+12,w/8,5);
            }
            else if(effect.getType().equals("levelUp")) {
                int w = effect.getWidth()/5;
                int h = effect.getHeight()/5;
                g2d.setColor(new Color(0x382E2E));
                drawRoundedSquare(g2d,699-w/2,249-h/2,w+2,w/8);
                g2d.setColor(colors[9]);
                typeText(g2d,"Level Up!",690-(int)(w/1.5),240-h/2, (float) w/24);
                g2d.setColor(new Color(0xFFA000));
                typeText(g2d,"" + player.getLevel(),715-(int)(w/2.5) - (player.getLevel() + "").length()*5,275-(int)(w/5.5), (float) w/24);
                g2d.setColor(new Color(0x6B4F4F));
                drawBorder(g2d,695-w/2,245-h/2,w+12,w+12,w/8,5);
            }
            else if(effect.getType().equals("static")) {
                g2d.drawImage(staticEffect[(int)(Math.random()* staticEffect.length)],0,0,1415,850,null);
                if(effect.attackDeletable()){
                    inStaticEffect = false;
                }
                else{
                    inStaticEffect = true;
                }
            }
            else if(effect.getType().equals("couldn'tSave")){
                if(effect.getMoveCounter()>60){
                    g2d.setColor(colors[1]);
                }
                else {
                    g2d.setColor(new Color(0, 0, 0, (int) (4.25 * effect.getMoveCounter())));
                }
                typeText(g2d,"You are currently engaged in the Main Quest. You may not save the game at such times.",200,effect.getyPos(),2F);
            }
            else if(effect.getType().equals("saved")){
                if(effect.getMoveCounter()>60){
                    g2d.setColor(colors[1]);
                }
                else {
                    g2d.setColor(new Color(0, 0, 0, (int) (4.25 * effect.getMoveCounter())));
                }
                typeText(g2d,"Game successfully saved!",600,effect.getyPos(),2F);
            }
            else{
                g2d.drawImage(effect.image(), effect.getXPos(), effect.getyPos(), effect.getWidth(), effect.getHeight(), null);
            }
        }
        //if(!paused) {
        for (int i = effects.size() - 1; i > -1; i--) {
            if (effects.get(i).attackDelete()) {
                effects.remove(i);
            }
        }
        //}
        if (frame.getStoryLocation()==24) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2F));
            g2d.drawImage(staticEffect[(int)(Math.random()* staticEffect.length)],0,0,1415,850,null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }
    }
    private void drawHpBar(Graphics2D g2d, int x, int y, Person person, int size){
        g2d.setColor(Color.darkGray);
//        g2d.drawRect(x+(int)(size*2.5),y,size*50, (int)(size*7.5));
//        g2d.fillRect(x+(int)(size*2.5),y,size*50,(int)(size*7.5));
//        g2d.drawOval(x,y,size*5,(int)(size*7.5));
//        g2d.fillOval(x,y,size*5,(int)(size*7.5));
//        g2d.drawOval(x+50*size,y,5*size,(int)(size*7.5));
//        g2d.fillOval(x+50*size,y,5*size,(int)(size*7.5));
        drawRoundedRect(g2d,x,y,size*51,(int)(size*8.5),15);
        g2d.setColor(Color.RED);
//        g2d.drawRect(x+(int)(size*2.5),y,person.getHpPercent()/2*size, (int)(size*7.5));
//        g2d.fillRect(x+(int)(size*2.5),y,person.getHpPercent()/2*size,(int)(size*7.5));
//        if (person.getHpPercent() > 1) {
//            g2d.drawOval(x, y, 5*size, (int)(size*7.5));
//            g2d.fillOval(x, y, 5*size, (int)(size*7.5));
//        }
//        if (person.getHpPercent() > 99) {
//            g2d.drawOval(x+50*size, y, 5*size, (int)(size*7.5));
//            g2d.fillOval(x+50*size, y, 5*size, (int)(size*7.5));
//        }
        drawRoundedRect(g2d,x,y,person.getHpPercent()/2*size,(int)(size*7.5),15);
    }
    private void drawHpBar(Graphics2D g2d, int x, int y, Person person, int width, int height, int roundness){
        g2d.setColor(Color.darkGray);
        drawRoundedRect(g2d,x,y,width*51,(int)(height*8.5),roundness);
        g2d.setColor(Color.RED);
        drawRoundedRect(g2d,x,y,(int)(person.getRawHpPercent()/2*width),(int)(height*7.5),roundness);
    }
    private void drawEnergyBar(Graphics2D g2d, int x, int y, Person person, int size){
        g2d.setColor(Color.darkGray);
        drawRoundedRect(g2d,x,y,size*50,size*4,15);
        g2d.setColor(Color.BLUE);
        drawRoundedRect(g2d,x,y,person.getEnergy()/2*size,size*4,15);
    }
    private void drawMeter(Graphics2D g2d, int x, int y, Person person, int size){
        g2d.setColor(Color.darkGray);
        drawRoundedRect(g2d,x,y,size*50,size*4,15);
        g2d.setColor(Color.ORANGE);
        drawRoundedRect(g2d,x,y,person.getMeter()/20*size,size*4,15);
    }
    private void drawTextOptionsSelect(Graphics2D g2d, int y){
        drawRoundedRect(g2d,optionsX-1,y-1,1345-optionsX+2,textOptionBoxHeight+2,15);
    }
    private void drawTextOptionsBox(Graphics2D g2d, int y){ //draw option boxes that are centered.
        drawRoundedRect(g2d,optionsX,y,1345-optionsX,textOptionBoxHeight,15);
    }
    private void drawSquare(Graphics2D g2d, int x, int y){
        drawSquare(g2d, x, y, squareSize);
    }
    private void drawSquare(Graphics2D g2d, int x, int y, int size){
        //g2d.drawRect(x, y, size, size);
        g2d.fillRect(x ,y, size, size);
    }
    private void drawRect(Graphics2D g2d, int x, int y, int width, int height){
        //g2d.drawRect(x, y, width, height);
        g2d.fillRect(x ,y, width, height);
    }
    private void drawCircle(Graphics2D g2d, int x, int y, int size){
        g2d.drawOval(x,y,size,size);
        g2d.fillOval(x,y,size,size);
    }
    private void drawButton(Graphics2D g2d, int x, int y, int width, int height, int howRound, int selectX, int selectY, double size){
        if(menuSelectX==selectX && menuSelectY==selectY){
            int s = (int)(menuTick * size);
            drawRoundedRect(g2d,x+s,y+s,width-s*2,height-s*2,howRound);
        }
        else{
            drawRoundedRect(g2d,x,y,width,height,howRound);
        }
    }

    /**howRound should not be larger than the width or height*/
    private void drawRoundedRect(Graphics2D g2d, int x, int y, int width, int height, int howRound){
        g2d.drawRoundRect(x,y,width,height,howRound,howRound);
        g2d.fillRoundRect(x,y,width,height,howRound,howRound);
//        drawCircle(g2d,x,y,howRound);
//        drawCircle(g2d,x,y+height-howRound,howRound);
//        drawCircle(g2d,x+width-howRound,y,howRound);
//        drawCircle(g2d,x+width-howRound,y+height-howRound,howRound);
//        drawRect(g2d, x, y+howRound/2, howRound/4, height-howRound);
//        drawRect(g2d, x+width-howRound/4, y+howRound/2, howRound/4, height-howRound);
//        drawRect(g2d, x+howRound/2, y, width-howRound, howRound/4);
//        drawRect(g2d, x+howRound/2, y+height-howRound/4, width-howRound, howRound/4);
//        drawRect(g2d, x+howRound/4, y+howRound/4, width-howRound/2, height-howRound/2);
    }
    private void drawRoundedSquare(Graphics2D g2d, int x, int y, int size, int howRound){
        drawRoundedRect(g2d,x,y,size,size,howRound);
    }
    private void drawOval(Graphics2D g2d, int x, int y, int width, int height){
        g2d.drawOval(x,y,width,height);
        g2d.fillOval(x,y,width,height);
    }
    private void drawPolygon(Graphics2D g2d, int[] x, int[] y, int numPoints){
        g2d.drawPolygon(x,y,numPoints);
        g2d.fillPolygon(x,y,numPoints);
    }
    public void movePlayer(int x, int y){
        if(player.movable() && !menuVisible && !inChat)movePerson(x, y, player);
    }
    public void shovePerson(int x, int y, Person person){
        if(person.canGetHurt() && person.shovable()) {
            int collision = collisionCheck(person, x, y);
            if (collision == 0) {
                person.moveX(x);
                person.moveY(y);
            }
            if (collision != 0 && (x != 0 || y != 0)) {
                x *= 0.99;
                y *= 0.99;
                shovePerson(x, y, person);
            }
        }
//        if (x>0){
//            if (person.getxPos()>columns*squareSize-squareSize && person==player){
//                person.goToXY(0,person.getyPos());
//                mapChangeX(1);
//            }
//        }
//        else if(x<0){
//            if (person.getxPos()<0 && person==player){
//                person.goToXY(columns*squareSize-squareSize,person.getyPos());
//                mapChangeX(-1);
//            }
//        }
//        else if(y<0){
//            if (person.getyPos()<0 && person==player){
//                person.goToXY(person.getxPos(),rows*squareSize-squareSize);
//                mapChangeY(-1);
//            }
//        }
//        else if (y>0) {
//            if (person.getyPos()>rows*squareSize-squareSize-10 && person==player){
//                person.goToXY(person.getxPos(),0);
//                mapChangeY(1);
//            }
//        }
    }
    public void movePerson(int x, int y, Person person){
        int collision = collisionCheck(person,x,y);
        if(collision==0) {
            person.moveX(x);
            person.moveY(y);
        }
        if (x>0){
            person.setDirection("right");
        }
        else if(x<0){
            person.setDirection("left");
        }
        else if(y<0){
            person.setDirection("up");
        }
        else if (y>0) {
            person.setDirection("down");
        }
        if(collision!=0 && (x!=0 || y!=0)){
            x*=0.99;
            y*=0.99;
            movePerson(x,y,person);
        }
    }
    public int collisionCheck(int xPos1, int yPos1, int xPos2, int yPos2, int width1, int height1, int width2, int height2, int xChange, int yChange){//xPos1 and yPos1 represent moving entity
        xPos1+=xChange;
        yPos1+=yChange;
        if(xPos1 + width1 > xPos2 && xPos1 < xPos2 + width2 && yPos1 + height1 > yPos2 && yPos1 < yPos2 + height2){
            if(xChange!=0){
                return 1;
            }
            else{
                return 2;
            }
        }
        //System.out.println(xPos1 + " " + xPos2);
        return 0;
    }
    public int collisionCheck(Person person, int x, int y) {
        int xPos = person.getxPos();
        int yPos = person.getyPos();
        int width = 25;
        int height = 25;
        if(mapNumX < 100000 && mapNumY < 100000 && mapNumX > -1 && mapNumY > -1){
            int gridX = (Math.abs(scrollX) + xPos) / 25;
            int gridY = (Math.abs(scrollY) + yPos) / 25;
            for (int row = gridY - 5; row < gridY + 5; row++) {
                for (int column = gridX - 5; column < gridX + 5; column++) {
                    if (row < 0) {
                        row = 0;
                    }
                    if (column < 0) {
                        column = 0;
                    }
                    if (!(mapNumX < 100000 && mapNumY < 100000)) {
                        if (x != 0) {
                            return 1;
                        } else {
                            return 2;
                        }
                    }
                    if (allCollisions[row][column] == 1) {
                        int collideNum = collisionCheck(xPos, yPos, column * 25 + scrollX, row * 25 + scrollY, width, height, 25, 25, x, y);
                        //System.out.println(column * squareSize + scrollX + " " + (row*squareSize + scrollY));
                        if (collideNum != 0) {
                            return collideNum;
                        }
                    } else if (allCollisions[row][column] == 2) {
                        if (collisionCheck(xPos, yPos, column * 25 + scrollX, row * 25 + scrollY, width, height, 30, 25, x, y) != 0) {
                            if(person==player) {
                                mapNumX *= 100000;
                                mapNumY *= 100000;
                                setScrollXY(mapNumX, mapNumY, true);
                                theMap = null;
                                imageGet = false;
                                mapChange();
                                locationY = 10;
                                locationDown = true;
                                locationTick = 0;
                                //mapChange();
                                return 3;
                            }
                        }
                    } else if (allCollisions[row][column] == 3) {
                        if (collisionCheck(xPos, yPos, column * 25 + scrollX, row * 25 + scrollY, width, height, 25, 25, x, y) != 0) {
                            if(person==player){
                                int collideNum = collisionCheck(xPos, yPos, column * 25 + scrollX, row * 25 + scrollY, width, height, 25, 25, x, y);
                                //System.out.println(column * squareSize + scrollX + " " + (row*squareSize + scrollY));
                                if (collideNum != 0) {
                                    return collideNum;
                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            for(int row = 0; row < collisions.length; row++){
                for(int column = 0; column < collisions[row].length; column++){
                    if (collisions[row][column] == 1) {
                        int collideNum = collisionCheck(xPos, yPos, column * squareSize, row * squareSize, width, height, squareSize, squareSize, x, y);
                        //System.out.println(column * squareSize + scrollX + " " + (row*squareSize + scrollY));
                        if (collideNum != 0) {
                            return collideNum;
                        }
                    }
//                    else if (collisions[row][column] == 2) {
//                        if (collisionCheck(xPos, yPos, column * squareSize, row * squareSize, width, height, 30, squareSize, x, y) != 0) {
//                            mapNumX *= 100000;
//                            mapNumY *= 100000;
//                            setScrollXY(mapNumX, mapNumY, true);
//                            mapChange();
//                            return 3;
//                            //mapChange();
//                        }
//                    }
                }
            }
        }
        return 0;
    }
    public int collisionCheck(int xPos, int yPos, int width, int height, int x, int y) {
        if(mapNumX < 100000 && mapNumY < 100000 && mapNumX > -1 && mapNumY > -1){
            int gridX = (Math.abs(scrollX) + xPos)/25;
            int gridY = (Math.abs(scrollY) + yPos)/25;
            for (int row = gridY - 5; row < gridY + 5; row++) {
                for (int column = gridX - 5; column < gridX + 5; column++) {
                    if (row < 0) {
                        row = 0;
                    }
                    if (column < 0) {
                        column = 0;
                    }
                    if(!(mapNumX < 100000 && mapNumY < 100000)){
                        if(x!=0){
                            return 1;
                        }
                        else{
                            return 2;
                        }
                    }
                    if (allCollisions[row][column] == 1) {
                        int collideNum = collisionCheck(xPos, yPos, column * 25 + scrollX, row * 25 + scrollY, width, height, 25, 25, x, y);
                        //System.out.println(column * squareSize + scrollX + " " + (row*squareSize + scrollY));
                        if (collideNum != 0) {
                            return collideNum;
                        }
                    }
                }
//            if (x > 0 && (collisions[(int) Math.floor(1.0 * yPos / squareSize)][(xPos / squareSize) + 1] == 1 || collisions[(int) Math.ceil(1.0 * yPos / squareSize)][(xPos / squareSize) + 1] == 1)) {
//                return 1;
//            }
//            if (x < 0 && (collisions[(int) Math.floor(1.0 * yPos / squareSize)][(int) Math.ceil(1.0 * xPos / squareSize) - 1] == 1 || collisions[(int) Math.ceil(1.0 * yPos / squareSize)][(int) Math.ceil(1.0 * xPos / squareSize) - 1] == 1)) {
//                return 1;
//            }
//            if (y > 0 && (collisions[(yPos / squareSize) + 1][(int) Math.floor(1.0 * xPos / squareSize)] == 1 || collisions[(yPos / squareSize) + 1][(int) Math.ceil(1.0 * xPos / squareSize)] == 1)) {
//                return 2;
//            }
//            if (y < 0 && (collisions[(int) Math.ceil(1.0 * yPos / squareSize) - 1][(int) Math.floor(1.0 * xPos / squareSize)] == 1 || collisions[(int) Math.ceil(1.0 * yPos / squareSize) - 1][(int) Math.ceil(1.0 * xPos / squareSize)] == 1)) {
//                return 2;
//            }
            }
        }
        else{
            for(int row = 0; row < collisions.length; row++){
                for(int column = 0; column < collisions[row].length; column++){
                    if (collisions[row][column] == 1) {
                        int collideNum = collisionCheck(xPos, yPos, column * squareSize, row * squareSize, width, height, squareSize, squareSize, x, y);
                        //System.out.println(column * squareSize + scrollX + " " + (row*squareSize + scrollY));
                        if (collideNum != 0) {
                            return collideNum;
                        }
                    } else if (collisions[row][column] == 2) {
                        if (collisionCheck(xPos, yPos, column * squareSize, row * squareSize, width, height, 30, squareSize, x, y) != 0) {
                            mapNumX *= 100000;
                            mapNumY *= 100000;
                            setScrollXY(mapNumX, mapNumY, true);
                            mapChange();
                            return 3;
                            //mapChange();
                        }
                    }
                }
            }
        }
        return 0;
    }
    private void resetMap(){
        for (int row = 0; row < rows+1; row++){
            for (int column = 0; column < columns+1; column++){
                map[row][column] = 0;
                collisions[row][column]=0;
            }
        }
        //enemyList.clear();
        //enemyNums.clear();
//        attacks.clear();
//        effects.clear();
//        if(frame.getStoryLocation()==100){ //placeholder
//            for(Person ally : allyList){
//                ally.moveX(player.getxPos()-ally.getxPos());
//                ally.moveY(player.getyPos()-ally.getyPos());
//            }
//        }
//        else{
//            allyList.clear();
//        }
    }
    /**0 is grass, 1 is wall, 2 is ground, 3 is grass barricade, 4 is black barricade, 5 is sidewalk, 6 is road, 7 is house teleport, 8 is floor tile, 9 is water, 10 is cliffRight, 11 is cliffLeft, 12 is cliffUp, 13 is cliffDown, 14 is cornerRightDown, 15 is cornerLeftDown, 16 is cornerRightUp, 17 is cornerLeftUp, 18 is mossGround
     * 19 is groundGrass, 20 is grassGround, 21 is grassGround2, 22 is groundGrass2*/
    private void setMapBlock(int row, int column, int block){
        map[row][column] = block;
        if(block == 1 || block == 3 || block == 4 || (block > 9 && block < 18)){
            collisions[row][column] = 1;
            if(mapNumX > -1 && mapNumY > -1 && mapNumX < 100000 && mapNumY < 100000) allCollisions[mapNumY*31 + row][mapNumX*55 + column] = 1;
        }
        else if(block == 7){
            collisions[row][column] = 2;
            if(mapNumX > -1 && mapNumY > -1 && mapNumX < 100000 && mapNumY < 100000)allCollisions[mapNumY*31 + row][mapNumX*55 + column] = 2;
        }
        else{
            collisions[row][column] = 0;
            if(mapNumX > -1 && mapNumY > -1 && mapNumX < 100000 && mapNumY < 100000)allCollisions[mapNumY*31 + row][mapNumX*55 + column] = 0;
        }
    }
    private void setMapBlock(int row, int column, int block, int mapX, int mapY){
        if(mapX == mapNumX && mapY == mapNumY){
            map[row][column] = block;
            if (block == 1 || block == 3 || block == 4) {
                collisions[row][column] = 1;
                allCollisions[mapY * 31 + row][mapX * 55 + column] = 1;
            } else if (block == 7) {
                collisions[row][column] = 2;
                allCollisions[mapY * 31 + row][mapX * 55 + column] = 2;
            } else {
                collisions[row][column] = 0;
                allCollisions[mapY * 31 + row][mapX * 55 + column] = 0;
            }
        }
    }
    private void barricade(int row, int column){
        if(row > -1 && column > -1 && row < 32 && column < 56) collisions[row][column] = 1;
    }
    private void playerBarricade(int row, int column, int mapX, int mapY){
        if(allCollisions[row + mapY * 31][column + mapX * 55]==0) {
            allCollisions[row + mapY * 31][column + mapX * 55] = 3;
        }
    }
    public void barricade(int row, int column, int mapX, int mapY){
        allCollisions[row + mapY * 31][column + mapX * 55] = 1;
    }
    public void unBarricade(int row, int column, int mapX, int mapY){
        if(map[row][column]!=1 && map[row][column]!=3 && map[row][column]!=4 && !(map[row][column]>9 && map[row][column]<18)) {
            allCollisions[row + mapY * 31][column + mapX * 55] = 0;
        }
    }
    public void playerBarricadeMap(int mapX, int mapY){

        for(int column = 0; column < columns; column++){
            playerBarricade(0, column,mapX,mapY);
            playerBarricade(rows-1, column,mapX,mapY);
        }
        for(int row = 0; row < rows; row++){
            playerBarricade(row, 0,mapX,mapY);
            playerBarricade(row, columns-1,mapX,mapY);
        }
        barricaded = true;
    }
    public void setLastSpawnPoint(){
        lastX = mapNumX;
        lastY = mapNumY;
        pointX = player.getxPos() - mapNumX*1375 - scrollX;
        pointY = player.getyPos() - mapNumY*775 - scrollY;
    }
    private boolean setWall(int mapX, int mapY, String side, int wall){
        if(mapNumX == mapX && mapNumY == mapY){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (row == 0 && side.contains("top")) {
                        setMapBlock(row,column,wall);
                    }
                    if (row == 31 && side.contains("bottom")) {
                        setMapBlock(row,column,wall);
                    }
                    if (column == 0 && side.contains("left")) {
                        setMapBlock(row,column,wall);
                    }
                    if (column == 55 && side.contains("right")) {
                        setMapBlock(row,column,wall);
                    }
                }
            }
        }
        return mapNumX == mapX && mapNumY == mapY;
    }
    public void mapChange(){
//        mapChanging=true;
        resetMap();
        //System.out.print(last[0] + " " + last[1]);
        if(settingUp && realSettingUp){
            if (last[0] == mapNumX && last[1] == mapNumY) {
                imageGet = true;
                BufferedImage temp = theMap;
                theMap = lastImage;
                lastImage = temp;
                last[0] = mapNumX;
                last[1] = mapNumY;
            } else {
                lastImage = theMap;
                imageGet = false;
                theMap = null;
            }
        }
        if(mapNumX == -100 && mapNumY == -100){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    setMapBlock(row,column,2);
                    if(row == 0 || column == 0 || row == 31 || column == 55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            imageGet = false;
            theMap = null;
            locationY = 10;
            locationDown = true;
            locationTick = 0;
            location = "???";
        }
        else if(mapNumX == 0 && mapNumY == 1){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (column == 0 || (column == 55 && row <14) || (column == 55 && row >18) || row == 31 || row == 0) {
                        setMapBlock(row,column,1);
                    }
                    else if(column==55){
                        setMapBlock(row,column,19);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "General Town";
        }
        else if(setWall(0,2,"top",1)){}
        else if(mapNumX == 1 && mapNumY == 1){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if ((column == 0 && row <14) || (column == 0 && row >18)) {
                        setMapBlock(row,column,1);
                    }
                    else if(column==0){
                        setMapBlock(row,column,19);
                    }
                    else if(row == 0 || row == 31){
                        setMapBlock(row,column,3);
                    }
                }
            }
            //respawnEnemy(-1,2);
            //enemyList.add(new Person("meleeBoss1", 400, 100, player.getLevel()*3, 1000, 0, player.getLevel()*2, player.getLevel()));
            location = "Meadow";
        }
        else if(setWall(1,2,"top",3)){
            setMapBlock(0,0,1);
        }
        else if(mapNumX == 2 && mapNumY == 1){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if ((column == 55 && row >18) || (column == 55 && row <14)) {
                        setMapBlock(row,column,1);
                    }
                    else if(column==55){
                        setMapBlock(row,column,19);
                    }
                    else if (row == 0 || row == 31) {
                        setMapBlock(row,column,3);
                    }
                }
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(setWall(2,2,"top",3)){}
        else if(mapNumX == 3 && mapNumY == 1){
            for (int row = 0; row < rows; row++){
                if(frame.getStoryLocation()<2 && !settingUp){
                    frame.forceExit();
                    frame.advanceStory();
                    frame.enterStory();
                }
                for (int column = 0; column < columns; column++){
                    if ((column == 55 && row <14) || (column == 55 && row >18) || (column == 0 && row <14) || (column == 0 && row >18)) {
                        setMapBlock(row,column,1);
                    }
                    else if(column==55){
                        setMapBlock(row,column,19);
                    }
                    else if(column==0){
                        setMapBlock(row,column,20);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "RedFire Town";
        }
        else if(mapNumX == 3 && mapNumY == 2){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (column == 0 || row == 31 || column==55) {
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                    if(frame.getStoryLocation()<10)
                    {
                        if((row==31 && column>25 && column<32)) {
                            setMapBlock(row,column,4);
                        }
                    }
                    else{
                        if((row==31 && column>25 && column<32)) {
                            setMapBlock(row,column,2);
                        }
                    }
                }
            }
            setLastSpawnPoint();
            location = "RedFire Town";
        }
        else if(mapNumX == 3 && mapNumY == 0){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (column == 0 || row == 0 || column==55) {
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "RedFire Town";
        }
        else if(setWall(4,0,"left",1)){}
        else if(mapNumX == 4 && mapNumY == 1){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if ((column == 0 && row <14) || (column == 0 && row >18)) {
                        setMapBlock(row,column,1);
                    }
                    else if(column==0){
                        setMapBlock(row,column,19);
                    }
                    else if(row == 0){
                        setMapBlock(row,column,3);
                    }
                }
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(mapNumX == 4 && mapNumY == 2){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if (row == 31 || column == 0) {
                        setMapBlock(row,column,1);
                    }
                }
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(mapNumX == 5 && mapNumY == 1){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 55 && row <7 && row > 0) || (column == 55 && row >25) || row==0){
                        setMapBlock(row,column,3);
                    }
                }
                setMapBlock(6,55,3);
                setMapBlock(11,55,3);
                setMapBlock(16,55,3);
                setMapBlock(21,55,3);
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(mapNumX == 5 && mapNumY == 2){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if (row == 31 || column == 55) {
                        setMapBlock(row,column,1);
                    }
                }
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(mapNumX == 6 && mapNumY == 1){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if (row == 0) {
                        setMapBlock(row,column,3);
                    }
                    if((column == 0 && row <7 && row > 0) || (column == 0 && row >25) || row == 31){
                        setMapBlock(row,column,3);
                    }
                }
            }
            addRow(0,12,6,3);
            addColumn(3,6,12,3);
            addRow(3,12,3,3);
            addRow(0,17,11,3);
            addColumn(1,11,17,3);
            addRow(0,22,16,3);
            addRow(22,29,6,3);
            addRow(29,37,16,3);
            addRow(37,55,6,3);
            addColumn(6,16,22,3);
            addColumn(6,16,29,3);
            addColumn(6,16,37,3);
            addColumn(1,13,32,3);
            addColumn(2,13,34,3);
            addRow(32,34,13,3);
            addRow(0,40,21,3);
            addColumn(14,21,40,3);
            addRow(40,55,14,3);
            addRow(0,13,26,3);
            addColumn(26,31,13,3);
            addColumn(26,31,16,3);
            addColumn(26,31,39,3);
            addRow(16,39,26,3);
            addColumn(21,31,44,3);
            addColumn(21,26,48,3);
            addRow(48,55,21,3);
            addRow(48,55,26,3);
            location = "Meadow";
        }
        else if(mapNumX == 7 && mapNumY == 1){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || row == 0){
                        setMapBlock(row,column,3);
                    }
                    if ((column == 55 && row >18) || (column == 55 && row <14)) {
                        setMapBlock(row,column,1);
                    }
                }
            }
            addRow(0,13,6,3);
            addColumn(6,10,7,3);
            addRow(7,22,10,3);
            addColumn(1,8,20,3);
            addColumn(2,10,22,3);
            addColumn(4,13,25,3);
            addColumn(2,12,28,3);
            addColumn(4,13,31,3);
            addColumn(2,13,34,3);
            addColumn(2,13,37,3);
            addRow(22,37,2,3);
            addRow(0,34,14,3);
            addRow(37,48,14,3);
            addColumn(14,26,48,3);
            addRow(0,30,21,3);
            addRow(0,30,26,3);
            addRow(48,54,26,3);
            addColumn(21,24,30,3);
            addColumn(21,31,38,3);
            addRow(0,30,26,3);
            location = "Meadow";
        }
        else if(setWall(6,2,"topleft",3)){}
        else if(setWall(7,2,"top",3)){}
        else if(mapNumX == 8 && mapNumY == 1){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (column == 55 || (column == 0 && row <14) || (column == 0 && row >18) || row==0) {
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            if(frame.getStoryLocation()<5 && !settingUp){
                darkScreen();
                frame.forceExit();
                frame.advanceStory();
                frame.enterStory();
            }
            else {
                setLastSpawnPoint();
            }
            location = "Toothpaste Town";
        }
        else if(mapNumX == 8 && mapNumY == 2){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (column == 55 || column == 0) {
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Toothpaste Town";
        }
        else if(mapNumX == 8 && mapNumY == 3){
            for (int row = 0; row < rows; row++){
                for (int column = 0; column < columns; column++){
                    if (column == 55 || column == 0 || (row==31 && column<26) || (row==31 && column>31)) {
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Toothpaste Town";
        }
        else if(mapNumX == 8 && mapNumY == 4){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column == 0 || column == 55){
                        setMapBlock(row,column,3);
                    }
                    if((row==0 && column<26) || (row==0 && column>31) || (row==31 && column<26) || (row==31 && column>31)) {
                        setMapBlock(row,column,1);
                    }
                }
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(mapNumX == 8 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 0 && row <14) || (column == 0 && row >18)|| column == 55 || row==31 || (row==0 && column<26) || (row==0 && column>31)){
                        setMapBlock(row,column,1);
                    }
                    else if(row==0){
                        setMapBlock(row,column,22);
                    }
                    else if(column==0){
                        setMapBlock(row,column,20);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            if(frame.getStoryLocation()<7 && !settingUp){
                frame.forceExit();
                frame.advanceStory();
                frame.enterStory();
                frame.npcLeave(1);
            }
            setLastSpawnPoint();
            location = "JohnsTown";
        }
        else if(mapNumX == 7 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 55 && row <14) || (column == 55 && row >18) || row==0){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,0);
                    }
                }
            }
            //respawnEnemy(-1,5);
            location = "Meadow";
        }
        else if(mapNumX == 6 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,0);
                    }
                }
            }
            //respawnEnemy(-1,6);
            location = "Meadow";
        }
        else if(mapNumX == 5 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,0);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 4 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 0 && row <14) || (column == 0 && row >18) || row==0){
                        setMapBlock(row,column,1);
                    }
                    else if(column==0){
                        setMapBlock(row,column,19);
                    }
                    else{
                        setMapBlock(row,column,0);
                    }
                }
            }
            //respawnEnemy(-1,8);
            location = "Meadow";
        }
        else if(mapNumX == 3 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 55 && row <14) || (column == 55 && row >18)|| row==0){
                        setMapBlock(row,column,1);
                    }
                    else if(column==55){
                        setMapBlock(row,column,19);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            if(frame.getStoryLocation()<9 && !settingUp){
                frame.forceExit();
                frame.enterStory();
            }
            setLastSpawnPoint();
            location = "The Forsaken";
        }
        else if(mapNumX == 2 && mapNumY == 5){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column ==0 || (row==0 && column < 25) || (row==0 && column > 31)){
                        setMapBlock(row,column,1);
                    }
                    else if(row==0){
                        setMapBlock(row,column,21);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            if(frame.getStoryLocation()<10 && !settingUp){
            }
            else {
                setLastSpawnPoint();
            }
            location = "The Forsaken";
        }
        else if(mapNumX == 3 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column == 55|| row==31){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "The Forsaken";
        }
        else if(mapNumX == 4 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column == 0|| row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 5 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 6 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 7 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 8 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || column==55 || row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 2 && mapNumY == 6){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column == 0|| row==31){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "The Forsaken";
        }
        else if(mapNumX == 2 && mapNumY == 3){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if (row == 0 || column == 0) {
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(settingUp) readMaze(0);
            location = "Meadow";
        }
        else if(mapNumX == 2 && mapNumY == 4){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 0 && row <14) || (column == 0 && row >18) || (row==31 && column < 25) || (row==31 && column > 31)){
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(settingUp)readMaze(1);
            location = "Meadow";
        }
        else if(mapNumX == 3 && mapNumY == 3){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 55 && row <14) || (column == 55 && row>18)|| (row==0 && column < 26) || (row==0 && column > 31)){
                        setMapBlock(row,column,1);
                    }
                    if(frame.getStoryLocation()<10 && !settingUp)
                    {
                        if((row==0 && column>25 && column<32)) {
                            setMapBlock(row,column,4);
                        }
                    }
                    else{
                        if((row==0 && column>25 && column<32)) {
                            setMapBlock(row,column,0);
                        }
                    }
                }
            }
            if(settingUp)readMaze(2);
            location = "Meadow";
        }
        else if(mapNumX == 3 && mapNumY == 4){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column == 55 || row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(settingUp)readMaze(3);
            location = "Meadow";
        }
        else if(mapNumX == 4 && mapNumY == 3){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 0 && row<14) || (column == 0 && row>18)|| row==0 || row==31 || (column == 55 && row<14) || (column == 55 && row>18)){
                        setMapBlock(row,column,1);
                    }
                }
            }
            //respawnEnemy(-1,4);
            location = "Meadow";
        }
        else if(mapNumX == 1 && mapNumY == 4){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 55 && row <14) || (column == 55 && row >18) || row==31 || row==0 || column==0){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Backwater Town";
        }
        else if(setWall(4,4,"topleft",1)){}
        else if(setWall(5,4,"top",1)){}
        else if(mapNumX == 5 && mapNumY == 3){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 0 && row<14) || (column == 0 && row>18)|| row==0 || row==31){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            if(frame.getStoryLocation()<12 && !settingUp){
                frame.forceExit();
                frame.advanceStory();
                frame.enterStory();
            }
            setLastSpawnPoint();
            location = "Root Town";
        }
        else if(mapNumX == 6 && mapNumY == 3){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || row==31 || column==55){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,2);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Root Town";
        }
        else if(setWall(6,4,"top",1)){}
        else if(setWall(7,3,"left",1)){}
        else if(mapNumX == 7 && mapNumY == 4){
            setMapBlock(0,0,1);
        }
        else if(mapNumX == 50 && mapNumY == 35){
            readCity(1);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(frame.getStoryLocation()<13 && !settingUp){
                frame.advanceStory();
                frame.forceExit();
                frame.enterStory();
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 50 && mapNumY == 34){
            readCity(0);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 || row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 50 && mapNumY == 36){
            readCity(2);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 || row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 51 && mapNumY == 34){
            readCity(3);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 51 && mapNumY == 35){
            readCity(4);
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 51 && mapNumY == 36){
            readCity(5);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 52 && mapNumY == 34){
            readCity(6);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || column==55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 52 && mapNumY == 35){
            readCity(7);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55 && (row < 15 || row > 21)){
                        setMapBlock(row,column,1);
                    }
                    if((column==55 && row<=21 && row>=15 && frame.getStoryLocation()<17)) {
                        setMapBlock(row,column,4);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(mapNumX == 52 && mapNumY == 36){
            readCity(8);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || column==55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "City of Remembrance";
        }
        else if(setWall(50,37,"top",1)){}
        else if(setWall(51,37,"top",1)){}
        else if(setWall(52,37,"top",1)){}
        else if(setWall(53,34,"left",1)){}
        else if(setWall(53,36,"topleft",1)){}
        else if(setWall(54,36,"top",1)){}
        else if(setWall(55,38,"top",1)){}
        else if(setWall(57,33,"left",1)){}
        else if(setWall(57,34,"left",1)){}
        else if(setWall(57,36,"left",1)){}
        else if(setWall(57,37,"left",1)){}
        else if(setWall(56,38,"left",1)){}
        else if(mapNumX == 53 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || row==0 || (column==0 && (row < 15 || row > 21))){
                        setMapBlock(row,column,1);
                    }
                    if(column==0 && row > 14 && row < 22){
                        setMapBlock(row,column,0);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 54 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || row==0 || (column==55 && (row < 15 || row > 21))){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 55 && mapNumY == 35){
            readCity(11);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 && (row < 15 || row > 21)){
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(frame.getStoryLocation()<18 && !settingUp){
                frame.advanceStory();
                frame.forceExit();
                frame.enterStory();
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 55 && mapNumY == 33){
            readCity(9);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 || row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(frame.getStoryLocation()<20 && !settingUp){
                frame.advanceStory();
                frame.forceExit();
                frame.enterStory();
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 55 && mapNumY == 34){
            readCity(10);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 55 && mapNumY == 36){
            readCity(12);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 55 && mapNumY == 37){
            readCity(13);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 || row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 56 && mapNumY == 35){
            readCity(16);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55 && (row < 15 || row > 21)){
                        setMapBlock(row,column,1);
                    }
                    if(column==55 && row >= 15 && row <= 21 && frame.getStoryLocation()<25){
                        setMapBlock(row,column,4);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 56 && mapNumY == 33){
            readCity(14);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55 || row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 56 && mapNumY == 34){
            readCity(15);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 56 && mapNumY == 36){
            readCity(17);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 56 && mapNumY == 37){
            readCity(18);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55 || row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Parallel City";
        }
        else if(mapNumX == 57 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || row==0 || (column==0 && (row < 15 || row > 21))){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 58 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 59 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || row==0 || (column==55 && (row < 15 || row > 21))){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 60 && mapNumY == 35){
            readCity(20);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 && (row < 15 || row > 21)){
                        setMapBlock(row,column,1);
                    }
                }
            }
            if(frame.getStoryLocation()<26 && !settingUp){
                frame.advanceStory();
                frame.forceExit();
                frame.enterStory();
                defeats = 0;
            }
            else{
                setLastSpawnPoint();
            }
            location = "Some City";
        }
        else if(mapNumX == 60 && mapNumY == 34){
            readCity(19);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 || row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Some City";
        }
        else if(mapNumX == 60 && mapNumY == 36){
            readCity(21);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0 && (row < 15 || row > 21)){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Some City";
        }
        else if(mapNumX == 61 && mapNumY == 35){
            readCity(23);
        }
        else if(mapNumX == 61 && mapNumY == 36){
            readCity(24);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || column == 55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Some City";
        }
        else if(mapNumX == 61 && mapNumY == 34){
            readCity(22);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || column == 55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Some City";
        }
        else if(mapNumX == 62 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || row == 31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 63 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || row == 31 || (column == 55 && (row<15 || row > 21))){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 64 && mapNumY == 34){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        if(row==0){
                            setMapBlock(row,column,14);
                        }
                        else if(row==30){
                            setMapBlock(row,column,16);
                        }
                        else {
                            setMapBlock(row, column, 10);
                        }
                    }
//                    else if(column==55){
//                        if(row==0){
//                            setMapBlock(row,column,15);
//                        }
//                        else if(row==31){
//                            setMapBlock(row,column,17);
//                        }
//                        else {
//                            setMapBlock(row, column, 11);
//                        }
//                    }
                    else if(row==0){
                        setMapBlock(row, column, 13);
                    }
                    else if(row==30){
                        setMapBlock(row,column,12);
                    }
                    else {
                        setMapBlock(row, column, 9);
                    }
                }
            }
            location = "null";
        }
        else if(mapNumX == 64 && mapNumY == 36){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        if(row==0){
                            setMapBlock(row,column,14);
                        }
                        else if(row==30){
                            setMapBlock(row,column,16);
                        }
                        else {
                            setMapBlock(row, column, 10);
                        }
                    }
//                    else if(column==55){
//                        if(row==0){
//                            setMapBlock(row,column,15);
//                        }
//                        else if(row==31){
//                            setMapBlock(row,column,17);
//                        }
//                        else {
//                            setMapBlock(row, column, 11);
//                        }
//                    }
                    else if(row==0){
                        setMapBlock(row, column, 13);
                    }
                    else if(row==30){
                        setMapBlock(row,column,12);
                    }
                    else {
                        setMapBlock(row, column, 9);
                    }
                }
            }
            location = "null";
        }
        else if(mapNumX == 64 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 0 && (row<15 || row > 21))){
                        setMapBlock(row,column,1);
                    }
                    if(row==31){
                        setMapBlock(row,column,12);
                    }
                }
            }
            if(frame.getStoryLocation()<29 && !settingUp){
                frame.advanceStory();
                frame.forceExit();
                frame.enterStory();
            }
            else {
                setLastSpawnPoint();
            }
            location = "Point";
        }
        else if(mapNumX == 65 && mapNumY == 34){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0){
                        setMapBlock(row, column, 13);
                    }
                    else if(row==30){
                        setMapBlock(row,column,12);
                    }
                    else {
                        setMapBlock(row, column, 9);
                    }
                }
            }
            location = "null";
        }
        else if(mapNumX == 65 && mapNumY == 36){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0){
                        setMapBlock(row, column, 13);
                    }
                    else if(row==30){
                        setMapBlock(row,column,12);
                    }
                    else {
                        setMapBlock(row, column, 9);
                    }
                }
            }
            location = "null";
        }
        else if(mapNumX == 65 && mapNumY == 35){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31){
                        setMapBlock(row,column,12);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Point";
        }
        else if(mapNumX == 66 && mapNumY == 34){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55){
                        if(row==0){
                            setMapBlock(row,column,15);
                        }
                        else if(row==30){
                            setMapBlock(row,column,17);
                        }
                        else {
                            setMapBlock(row, column, 11);
                        }
                    }
//                    else if(column==55){
//                        if(row==0){
//                            setMapBlock(row,column,15);
//                        }
//                        else if(row==31){
//                            setMapBlock(row,column,17);
//                        }
//                        else {
//                            setMapBlock(row, column, 11);
//                        }
//                    }
                    else if(row==0){
                        setMapBlock(row, column, 13);
                    }
                    else if(row==30){
                        setMapBlock(row,column,12);
                    }
                    else {
                        setMapBlock(row, column, 9);
                    }
                }
            }
            location = "null";
        }
        else if(mapNumX == 66 && mapNumY == 36){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55){
                        if(row==0){
                            setMapBlock(row,column,15);
                        }
                        else if(row==30){
                            setMapBlock(row,column,17);
                        }
                        else {
                            setMapBlock(row, column, 11);
                        }
                    }
//                    else if(column==55){
//                        if(row==0){
//                            setMapBlock(row,column,15);
//                        }
//                        else if(row==31){
//                            setMapBlock(row,column,17);
//                        }
//                        else {
//                            setMapBlock(row, column, 11);
//                        }
//                    }
                    else if(row==0){
                        setMapBlock(row, column, 13);
                    }
                    else if(row==30){
                        setMapBlock(row,column,12);
                    }
                    else {
                        setMapBlock(row, column, 9);
                    }
                }
            }
            location = "null";
        }
        else if(mapNumX == 66 && mapNumY == 35){
                for (int column = 0; column < 56; column++){
                    setMapBlock(31,column,12);
                }
            //setMapBlock(0,55,1);
            setLastSpawnPoint();
            location = "Point";
        }
        else if(mapNumX == 67 && mapNumY == 34){
            readCity(25);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        if(row==0){
                            setMapBlock(row,column,15);
                        }
                        else if(row==30){
                            setMapBlock(row,column,17);
                        }
                        else {
                            setMapBlock(row, column, 11);
                        }
                    }
                    if(column==1){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "another city";
        }
        else if(mapNumX == 67 && mapNumY == 36){
            readCity(27);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==0){
                        if(row==0){
                            setMapBlock(row,column,15);
                        }
                        else if(row==30){
                            setMapBlock(row,column,17);
                        }
                        else {
                            setMapBlock(row, column, 11);
                        }
                    }
                    if(column==1||row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "another city";
        }
        else if(setWall(67,37,"top",1)){

        }
        else if(setWall(67,33,"bottom",1)){

        }
        else if(mapNumX == 67 && mapNumY == 35){
            readCity(26);
            //3 by 3 city again. next town will be to the south bc that's lloyds hometown
//            for (int row = 0; row < 32; row++){
//                for (int column = 0; column < 56; column++){
//                    if((column == 0 && (row<15 || row > 21))){
//                        setMapBlock(row,column,1);
//                    }
//                }
//            }
            setMapBlock(0,0,1);
            setMapBlock(0,1,1);
            if(frame.getStoryLocation()<37) {
                enterStory();
            }
            else {
                setLastSpawnPoint();
            }
            location = "another city";
        }
        else if(mapNumX == 68 && mapNumY == 34){
            readCity(28);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row == 0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "another city";
        }
        else if(mapNumX == 68 && mapNumY == 35){
            readCity(29);
//            for (int row = 0; row < 32; row++){
//                for (int column = 0; column < 56; column++){
//                    if((column == 0 && (row<15 || row > 21))){
//                        setMapBlock(row,column,1);
//                    }
//                }
//            }
            setLastSpawnPoint();
            location = "another city";
        }
        else if(mapNumX == 68 && mapNumY == 36){
            readCity(30);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 && (column > 33 || column < 22)){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "another city";
        }
        else if(mapNumX == 69 && mapNumY == 34){
            readCity(31);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row == 0 || column == 55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "another city";
        }
        else if(mapNumX == 69 && mapNumY == 35){
            readCity(32);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if((column == 55 && (row<14 || row > 18))){
                        setMapBlock(row,column,1);
                    }
                    if(frame.getStoryLocation()<100){
                        if(row > 13 && row < 19){
                            setMapBlock(row,column,1);
                        }
                    }
                }
            }
            setLastSpawnPoint();
            location = "another city";
        }
        else if(mapNumX == 69 && mapNumY == 36){
            readCity(33);
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row == 31 || column == 55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            setLastSpawnPoint();
            location = "another city";
        }
        else if(mapNumX == 68 && mapNumY == 37){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 && (column > 33 || column < 22) || column==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }else if(mapNumX == 68 && mapNumY == 38){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || column==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 69 && mapNumY == 37){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 69 && mapNumY == 38){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 70 && mapNumY == 37){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || (column==55 && (row < 13 || row > 17))){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(mapNumX == 70 && mapNumY == 38){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || column==55){
                        setMapBlock(row,column,1);
                    }
                }
            }
            location = "Meadow";
        }
        else if(setWall(70,39,"top",1)){}
        else if(setWall(69,39,"top",1)){}
        else if(setWall(68,39,"top",1)){}
        else if(setWall(72,38,"left",1)){}
        else if(mapNumX == 71 && mapNumY == 37) {
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || (column==0 && (row < 13 || row > 17))){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,18);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Remains Of A Past";
        }
        else if(mapNumX == 72 && mapNumY == 37) {
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==0 || row ==31 || (column==55 && (row < 13 || row > 17))){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,18);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Remains Of A Past";
        }
        else if(mapNumX == 71 && mapNumY == 38) {
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(row==31 || column==0){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,18);
                    }
                }
            }
            setLastSpawnPoint();
            location = "Remains Of A Past";
        }
        else if(mapNumX == 100000 && mapNumY == 400000){
            for (int row = 0; row < 32; row++){
                for (int column = 0; column < 56; column++){
                    if(column==55 || row==31 || column==0 || row==0){
                        setMapBlock(row,column,1);
                    }
                    else{
                        setMapBlock(row,column,8);
                    }
                }
            }
            Person boss = new Person("meleeBoss1",400,400,player.getLevel()*1000,2000,0,player.getLevel(),player.getLevel(),100000,400000);
            boss.setDesc("Square","Defender of Houses");
            boss.addPassives(frame.passiveList()[0]);
            enemyList.add(boss);
            location = "House";
        }
        if(!settingUp) {

        }
    }
    public void enterStory(){
        if (!settingUp) {
            frame.advanceStory();
            frame.forceExit();
            frame.enterStory();
        }
    }
    //end of map Change
    private void readMaze(int index){
        for(int row = 0; row < mazes[index].getHeight(); row++){
            for(int column = 0; column<mazes[index].getWidth(); column++){
                if(mazes[index].getRGB(column,row)==-14641920){
                    setMapBlock(row,column,3,mapNumX,mapNumY);
                }
            }
        }
    }
    private void readCity(int index){
        for(int row = 0; row < cities[index].getHeight(); row++){
            for(int column = 0; column<cities[index].getWidth(); column++){
                if(cities[index].getRGB(column,row)==-2515812){
                    setMapBlock(row,column,5);
                }
                if(cities[index].getRGB(column,row)==-12369080){
                    setMapBlock(row,column,6);
                }
            }
        }
    }
    private void addColumn(int start, int end, int column, int block){
        for(int row = start; row <= end; row++){
            setMapBlock(row,column,block);
        }
    }
    private void addRow(int start, int end, int row, int block){
        for(int column = start; column <= end; column++){
            setMapBlock(row,column,block);
        }
    }
    public boolean itemOnMap(Item item){
        return mapNumX == item.getMapX() && mapNumY == item.getMapY();
    }
    public void barricade(){
        for(int column = 0; column < columns; column++){
            if(collisions[0][column]!=1){
                barricade(0, column);
            }
            if(collisions[rows-1][column]!=1){
                barricade(rows-1, column);
            }
        }
        for(int row = 0; row < rows; row++){
            if(collisions[row][0]!=1){
                barricade(row, 0);
            }
            if(collisions[row][columns-1]!=1){
                barricade(row, columns-1);
            }
        }
    }
    public void barricadeMap(int mapX, int mapY){
        for(int column = 0; column < columns; column++){
            barricade(0, column,mapX,mapY);
            barricade(rows-1, column,mapX,mapY);
        }
        for(int row = 0; row < rows; row++){
            barricade(row, 0,mapX,mapY);
            barricade(row, columns-1,mapX,mapY);
        }
        barricaded = true;
    }
    public void unbarricadeMap(int mapX, int mapY){
        mapNumX = mapX;
        mapNumY = mapY;
        settingUp = true;
        mapChange();
        settingUp = false;
        for(int column = 0; column < columns; column++){
            unBarricade(0, column,mapX,mapY);
            unBarricade(rows-1, column,mapX,mapY);
        }
        for(int row = 0; row < rows; row++){
            unBarricade(row, 0,mapX,mapY);
            unBarricade(row, columns-1,mapX,mapY);
        }
        barricaded = false;
    }
    public boolean isBarricaded(){
        return barricaded;
    }
    private void mapChangeX(int changeBy){
        mapNumX+=changeBy;
        mapChange();
    }
    private void mapChangeY(int changeBy){
        mapNumY+=changeBy;
        mapChange();
    }
    public void advanceText(int increment,boolean selection){
        if(!darkScreen && !inStaticEffect) {
            if (typing) {
                typed = message.length();
            } else {
                typed = 0;
                if (npcList.get(npcTouched).getLine(textPart).equals("select")) {
                    if (selection) {
                        textPart = npcList.get(npcTouched).select(menuSelectY);
                        inChoosing = false;
                    } else {
                        typed = message.length();
                    }
                } else {
                    textPart += increment;
                    if (npcList.get(npcTouched).getLine(textPart).equals("")) {
                        inChat = false;
                    }
                    if (npcList.get(npcTouched).getLine(textPart).equals("select")) {
                        inChoosing = true;
                        menuSelectY = 0;
                        menu = npcList.get(npcTouched).getOptions(textPart) * -1;
                        typed = message.length();
                    }
                }
                message = npcList.get(npcTouched).getLine(textPart);
            }
        }
    }
    public void interact(){
        if (player.movable()) {
            typed=0;
            textPart=npcList.get(npcTouched).getChatIndex();
            message = npcList.get(npcTouched).getLine(textPart);
            inChat = true;
        }
    }
    public void storyInteract(){
        typed=0;
        textPart=npcList.get(npcTouched).getChatIndex();
        message = npcList.get(npcTouched).getLine(textPart);
        inChat = true;
    }
    public int getTextPart(){
        return textPart;
    }
    private void drawText(Graphics2D g2d){
        if(inChat) {
            drawPortraits(g2d);
            g2d.setColor(new Color(0xCCEEE6E6, true));
            drawRoundedRect(g2d,55,625,1290,140,50);
            g2d.setColor(new Color(0xD97E00));
            g2d.drawRoundRect(51,621,1298,148,50,50);
            g2d.drawRoundRect(52,622,1296,146,50,50);
            g2d.drawRoundRect(53,623,1294,144,50,50);
            g2d.drawRoundRect(54,624,1292,142,50,50);
            g2d.drawRoundRect(55,625,1290,140,50,50);
            if(inChoosing) {
                int[] xs= {optionsX-3,optionsX-13,optionsX-13};
                int[] ys= new int[3];
                if (npcList.get(npcTouched).getOptions(textPart)==1){
                    ys[0] = 595;
                    ys[1] = 587;
                    ys[2] = 603;
                    g2d.setColor(colors[1]);
                    drawPolygon(g2d,xs,ys,3);
                    drawTextOptionsBox(g2d,610-textOptionBoxHeight);
                    g2d.setColor(new Color(0xE6EACDB3, true));
                    drawTextOptionsSelect(g2d,610-textOptionBoxHeight);
                    g2d.setColor(new Color(0xFF000000, true));
                    message = npcList.get(npcTouched).getLine(textPart-1);
                    typeText(g2d,npcList.get(npcTouched).getOptionLine(npcList.get(npcTouched).getOptions()),optionsX+5,605,1.5F);
                }
                else if (npcList.get(npcTouched).getOptions(textPart)==2){
                    g2d.setColor(colors[1]);
                    if(menuSelectY ==0){
                        ys[0] = 565;
                        ys[1] = 557;
                        ys[2] = 573;
                        drawTextOptionsSelect(g2d,610-textOptionBoxHeight*2-5);
                    }
                    else{
                        ys[0] = 595;
                        ys[1] = 587;
                        ys[2] = 603;
                        drawTextOptionsSelect(g2d,610-textOptionBoxHeight);
                    }
                    g2d.setColor(new Color(0xE6EACDB3, true));
                    drawTextOptionsBox(g2d,610-textOptionBoxHeight*2-5);
                    drawTextOptionsBox(g2d,610-textOptionBoxHeight);
                    g2d.setColor(colors[1]);
                    drawPolygon(g2d,xs,ys,3);
                    message = npcList.get(npcTouched).getLine(textPart-1);
                    typeText(g2d,npcList.get(npcTouched).getOptionLine(npcList.get(npcTouched).getOptions()),optionsX+5,575,1.5F);
                    typeText(g2d,npcList.get(npcTouched).getOptionLine(npcList.get(npcTouched).getOptions()+1),optionsX+5,605,1.5F);
                }
                else if (npcList.get(npcTouched).getOptions(textPart)==3){
                    g2d.setColor(colors[1]);
                    if(menuSelectY ==0){
                        ys[0] = 535;
                        ys[1] = 527;
                        ys[2] = 543;
                        drawTextOptionsSelect(g2d,610-textOptionBoxHeight*3-10);
                    }
                    else if(menuSelectY ==1){
                        ys[0] = 565;
                        ys[1] = 557;
                        ys[2] = 573;
                        drawTextOptionsSelect(g2d,610-textOptionBoxHeight*2-5);
                    }
                    else{
                        ys[0] = 595;
                        ys[1] = 587;
                        ys[2] = 603;
                        drawTextOptionsSelect(g2d,610-textOptionBoxHeight);
                    }
                    g2d.setColor(new Color(0xE6EACDB3, true));
                    drawTextOptionsBox(g2d,610-textOptionBoxHeight*3-10);
                    drawTextOptionsBox(g2d,610-textOptionBoxHeight*2-5);
                    drawTextOptionsBox(g2d,610-textOptionBoxHeight);
                    g2d.setColor(colors[1]);
                    drawPolygon(g2d,xs,ys,3);
                    message = npcList.get(npcTouched).getLine(textPart-1);
                    typeText(g2d,npcList.get(npcTouched).getOptionLine(npcList.get(npcTouched).getOptions()),optionsX+5,545,1.5F);
                    typeText(g2d,npcList.get(npcTouched).getOptionLine(npcList.get(npcTouched).getOptions()+1),optionsX+5,575,1.5F);
                    typeText(g2d,npcList.get(npcTouched).getOptionLine(npcList.get(npcTouched).getOptions()+2),optionsX+5,605,1.5F);
                }
            }
            typed++;
            typing=true;
            if(typed>message.length()){
                typed=message.length();
                typing=false;
            }
            typeText(g2d, message.substring(0,typed));
            if(!typing){
                typeText(g2d,"Press X to continue",1150,750,1.5F);
                int[] xs = {1310,1320,1315};
                int[] ys = {735,735,745};
                arrowY+=arrowSpeed;
                if(arrowY > 8){
                    arrowSpeed = -6;
                }
                arrowSpeed+=1;
                for(int y = 0; y < ys.length; y++){
                    ys[y] += arrowY;
                }
                g2d.fillPolygon(xs,ys,3);
            }
            if(!npcList.get(npcTouched).getDisplayName().equals("")) {
                g2d.setColor(new Color(0xD97E00));
                if (npcList.get(npcTouched).getDisplayName().equals("You")) {
                    drawRoundedRect(g2d, 1155, 615, 190, 30, 20);
                    g2d.setColor(Color.black);
                    typeText(g2d, npcList.get(npcTouched).getDisplayName(), 1225, 635, 2F);
                } else {
                    drawRoundedRect(g2d, 55, 615, 190, 30, 20);
                    g2d.setColor(Color.black);
                    int scale = 8;
                    if(npcList.get(npcTouched).getDisplayName().length() > 10){
                        scale = 6;
                    }
                    typeText(g2d, npcList.get(npcTouched).getDisplayName(), 150 - npcList.get(npcTouched).getDisplayName().length() * scale, 635, 2F);
                }
            }
            if(frame.isInStory() && frame.checkSkip()){
                g2d.setColor(colors[1]);
                typeText(g2d,"Press space to skip",630,600,2F);
            }
        }
    }

    private void typeText(Graphics2D g2d, String text, int x, int y, int limitX, int yChange, float size){
        Font theFont = g2d.getFont();
        theFont = theFont.deriveFont(theFont.getSize() * size);
        g2d.setFont(theFont);
        typeText(g2d, text, x, y, limitX, yChange);
        theFont = theFont.deriveFont(theFont.getSize()/size);
        g2d.setFont(theFont);
    }
    private void typeText(Graphics2D g2d,String text){
        g2d.setColor(colors[1]);
        Font theFont = g2d.getFont();
        theFont = theFont.deriveFont(theFont.getSize() * 2F);
        g2d.setFont(theFont);
        typeText(g2d, text, 80, 670);
        theFont = theFont.deriveFont(theFont.getSize()/2F);
        g2d.setFont(theFont);
    }
    private void typeText(Graphics2D g2d, String text, int x, int y, float size){
        Font theFont = g2d.getFont();
        theFont = theFont.deriveFont(theFont.getSize() * size);
        g2d.setFont(theFont);
        typeText(g2d, text, x, y);
        theFont = theFont.deriveFont(theFont.getSize()/size);
        g2d.setFont(theFont);
    }
    private void typeText(Graphics2D g2d, String text, int x, int y){
        typeText(g2d, text, x, y, 105, 25);
    }
    private void typeText(Graphics2D g2d, String text, int x, int y, int limitX, int yChange){
        String aSubstring = "";
        String aSecondSubstring;
        int index = 0;
        for(int i = 0; i < text.length(); i++) {
            aSecondSubstring = "";
            if (text.substring(i, i + 1).equals(" ") && i+1 < text.length()){
                boolean loop = true;
                for(int r = i+1; loop && r < text.length(); r++){
                    loop = !(text.substring(r,r+1).equals(" "));
                    aSecondSubstring+=text.substring(r,r+1);
                }
            }
            aSubstring += text.substring(i, i + 1);
            if(aSecondSubstring.length() + aSubstring.length() > limitX || text.substring(i,i+1).equals("_")){
                index += aSubstring.length();
                if(text.substring(i,i+1).equals("_")){
                    aSubstring = aSubstring.substring(0,aSubstring.length()-1);
                }
                g2d.drawString(aSubstring, x, y);
                y+=yChange;
                aSubstring = "";
            }
            if (index+aSubstring.length() == text.length()){
                g2d.drawString(aSubstring, x, y);
            }
            else if (aSubstring.length() > limitX && text.substring(i + 1, i + 2).equals(" ")) {
                g2d.drawString(aSubstring, x, y);
                index += aSubstring.length();
                y += yChange;
                aSubstring = "";
            }
            //g2d.drawString(aSubstring, x, y);
        }
    }
    public boolean checkInChat(){
        return inChat;
    }
    public boolean checkCanInteract(){
        return canInteract;
    }
    public boolean checkMenu(){
        return menuVisible;
    }
    public boolean checkSelect(){
        return inChoosing;
    }
    public void forceExitChat(){inChat = false;}
    public void menuClose(){
        menuVisible = false;
    }
    public void menuOpen(){
        menu=0;
        menuSelectY = 0;
        menuSelectX = 0;
        menuVisible = true;
    }
    public void moveMenu(int x,int y) {
        int numY = 0;
        int numX = 0;
        if (menu == 0) {
            numY = 3;
            numX = 2;
        } else if (menu == 1) {
            numY = 5;
        } else if (menu == 2) {
            numY = 4;
            numX = 3;
        } else if(menu==3) {
            numY = 3;
        }
        else if(menu==5){
            numY = 4;
            numX = 3;
        }
        else if(menu==7) {
            numY = 5;
        }
        else if(menu==8) {
            numY = 4;
        }
        else if(frame.getStoryLocation()==-1){
            numY = 3;
        }
        else{
            numY = Math.abs(menu); //for text choosing
        }
        menuSelectY += y;
        menuSelectX += x;
        if (menuSelectY < 0) {
            menuSelectY = 0;
            if((menu==2 || menu== 3) && menuScrollY > 0){
                menuScrollY+=y;
            }
        } else if (menuSelectY == numY) {
            if(menu==2){
                menuScrollY += y;
                if(menuScrollY+menuSelectY> frame.skillList().length/3){
                    menuScrollY-=y;
                }
            }
            if(menu==3){
                menuScrollY += y;
                if(menuScrollY+menuSelectY> frame.getQuests().size()){
                    menuScrollY-=y;
                }
            }
            if(menu==5){
                menuScrollY += y;
                if(menuScrollY+menuSelectY> frame.passiveList().length/3){
                    menuScrollY-=y;
                }
            }
            menuSelectY -= y;
        }
        if (menuSelectX < 0) {
            menuSelectX = 0;
        } else if (menuSelectX == numX) {
            menuSelectX -= x;
        }
    }
    private void drawBorder(Graphics2D g2d, int x, int y, int width, int height, int roundness, int borderSize){
        for(int i = 0; i < borderSize; i++){
            g2d.drawRoundRect(x+i,y+i,width-i*2,height-i*2,roundness,roundness);
        }
    }
    private void drawMenu(Graphics2D g2d){
        if(menu==0){
            g2d.setColor(new Color(0xE61F1E00, true));
            drawRoundedRect(g2d, 450,50,500,700,20);
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, 450,15,500,35,20);
            g2d.setColor(new Color(0x000FFFF));
            typeText(g2d,"Menu",650,45, 3F);
            if(!menuTicking) {
                if (menuSelectY == 0) {
                    drawRoundedRect(g2d, 459 + menuSelectX * 245, 59, 237, 222, 20);
                } else if (menuSelectY == 1) {
                    drawRoundedRect(g2d, 459 + menuSelectX * 245, 289, 237, 222, 20);
                } else if (menuSelectY == 2) {
                    drawRoundedRect(g2d, 459 + menuSelectX * 245, 519, 237, 222, 20);
                }
            }
            g2d.setColor(new Color(0xE6166060, true));
            drawButton(g2d, 460, 60, 235, 220,20,0,0,1);
            drawButton(g2d, 460, 290, 235, 220,20,0,1,1);
            drawButton(g2d, 460, 520, 235, 220,20,0,2,1);
            drawButton(g2d, 705, 60, 235, 220,20,1,0,1);
            drawButton(g2d, 705, 290, 235, 220,20,1,1,1);
            drawButton(g2d, 705, 520, 235, 220,20,1,2,1);
            g2d.drawImage(skills[1],460,290,235,220,null);
            g2d.drawImage(passives[3],705,290,235,220,null);
            g2d.drawImage(quest,500,530,960,720,null);
            g2d.setColor(new Color(0xE60B7ABB, true));
            drawRect(g2d, 520, 200,25,80);
            drawRect(g2d, 570, 150,25,130);
            drawRect(g2d, 620, 100,25,180);
            g2d.drawImage(lock,705,60,235,220,null);
            g2d.setColor(new Color(0x08E300));
            typeText(g2d,"Stats",530,180, 3F);
            typeText(g2d,"Skills",530,410, 3F);
            typeText(g2d,"Quests",530,640, 3F);
            typeText(g2d,"Locked",760,180, 10,30,2.5F);
            typeText(g2d,"Passives",750,410, 3F);
            typeText(g2d,"Save Game",745,640, 10,30,2.5F);
            g2d.setColor(colors[1]);
            drawBorder(g2d,445,5,510,750,20,10);
        }
        else if(menu==1){
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, 450,15,500,35,20);
            g2d.setColor(colors[9]);
            typeText(g2d,"Stats",650,45, 3F);
            g2d.setColor(new Color(0xE643560F, true));
            drawRoundedRect(g2d, 450,50,500,700,20);
            g2d.setColor(colors[9]);
            if(!menuTicking) {
                if (menuSelectY == 0) {
                    drawRoundedRect(g2d, 869, 54, 77, 27, 20);
                } else if (menuSelectY == 1) {
                    drawRoundedSquare(g2d, 679, 159, 22, 15);
                } else if (menuSelectY == 2) {
                    drawRoundedSquare(g2d, 679, 264, 22, 15);
                } else if (menuSelectY == 3) {
                    drawRoundedSquare(g2d, 679, 394, 22, 15);
                } else if (menuSelectY == 4) {
                    drawRoundedRect(g2d, 469, 519, 127,27, 15);
                }
            }
            g2d.setColor(new Color(0xE6166060, true));
            drawButton(g2d,870,55,75,25,20,0,0,0.5); //return to menu button
            //drawRect(g2d,460,60,300,300);
            drawButton(g2d,680,160,20,20,15,0,1,0.5);//hp stat button
            drawButton(g2d,680,265,20,20,15,0,2,0.5);
            drawButton(g2d,680,395,20,20,15,0,3,0.5);
            drawButton(g2d,470,520,125,25,15,0,4,0.25);
            g2d.setColor(new Color(0xE6064141, true));
            drawRect(g2d,575,115,300,20);
            g2d.setColor(new Color(0xE632A4A4, true));
            drawRect(g2d,575,115,(3*player.getXpPercent()),20);
            g2d.setColor(new Color(0xE6000001, true));
            drawPlus(g2d,684,168,4);
            drawPlus(g2d,684,273,4);
            drawPlus(g2d,684,403,4);
            typeText(g2d,"Reset Stats", 470, 540,2F);
            typeText(g2d,"Menu", 877, 75,2F);
            typeText(g2d,"Level: " + player.getLevel(), 465, 135,2F);
            typeText(g2d,"Available Stat Points:  " + player.getStatPoints(), 465, 155,2F);
            typeText(g2d,"Exp: " + player.getXp() + "/" + player.getXpNeeded(), 575, 130,1.7F);
            typeText(g2d,"Stats", 465, 115,2.5F);

            typeText(g2d,"Each point of vitality that you have increases your hp by 60. Every 3 vitality points increases your defense stat by 1.", 465, 195, 30,17,1.7F);
            typeText(g2d,"Each point of physical ability increases your sword stat by 2 and hp by 5. Every 5 physical ability points increases your defense stat by 1.", 465, 305, 30,17,1.7F);
            typeText(g2d,"Each point of magic ability that you have increases your magic stat by 2 and sword stat by 1.", 465, 435, 30,17,1.7F);
            typeText(g2d,"Base stats", 750, 160, 2.5F);
            typeText(g2d,"Health:  "+player.getMaxHpBefore(), 750, 180, 1.5F);
            typeText(g2d,"Sword:  "+player.getSwordBefore(), 750, 200, 1.5F);
            typeText(g2d,"Magic:  "+player.getMagicBefore(), 750, 220, 1.5F);
            typeText(g2d,"Defense:  "+player.getDefenseBefore(), 750, 240, 1.5F);
            typeText(g2d,"Mood level", 470, 570,2F);
            typeText(g2d,"Mood changes mostly based on your actions. Mood affects your magic stat. When green, it is positive and will increase magic. When red, it is negative and will decrease magic. However, something may happen when you hit the bottom.", 470, 590,40,17,1.5F);
            player.setStats();
            typeText(g2d,"Stats after multipliers", 750, 270, 15,25,2.5F);
            typeText(g2d,"Health:  "+player.getMaxHp(), 750, 320, 1.5F);
            typeText(g2d,"Sword:  "+player.getSword(), 750, 340, 1.5F);
            typeText(g2d,"Magic:  "+player.getMagic(), 750, 360, 1.5F);
            typeText(g2d,"Defense:  "+player.getDefense(), 750, 380, 1.5F);
            g2d.setColor(new Color(0x59FF00));
            typeText(g2d,"Vitality:  " + player.getHpStat(), 465, 175,2F);

            g2d.setColor(new Color(0xA65F14));
            typeText(g2d,"Physical Ability:  " + player.getPhysicalStat(), 465, 285,2F);

            g2d.setColor(new Color(0x020977));
            typeText(g2d,"Magic Ability:  " + player.getMagicStat(), 465, 415,2F);

            if(player.getMoodLevel()<0){
                g2d.setColor(new Color(0xE64D0606, true));
            }
            else{
                g2d.setColor(new Color(0xE673B009, true));
            }
            g2d.drawRect( 800, 558, (int)Math.abs(player.getMoodLevel()), 10);
            g2d.fillRect( 800, 558, (int)Math.abs(player.getMoodLevel()), 10);
            g2d.setColor(new Color(0x000000));
            drawBorder(g2d,445,5,510,750,20,10);
        }
        else if(menu==2){
            g2d.setColor(new Color(0x312D2D));
            drawRoundedRect(g2d, xPosSkills2,50,500,700,20);
            drawRoundedRect(g2d, xPosSkills,50,500,700,20);
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, xPosSkills,15,500,35,20);
            g2d.setColor(new Color(0x000FFFF));
            typeText(g2d,"Skills",xPosSkills+200,45, 3F);

            int indexX = 0;
            int indexY = 0;
            for(int y = 70; y <= 580; y+=170) {
                indexX=0;
                for (int x = xPosSkills+11; x <= xPosSkills+335; x+=162) {
                    g2d.setColor(new Color(0x00FFFF));
                    drawMenuSelect(g2d, indexY, indexX,x-1, y-1, 152, 152,20);
                    g2d.setColor(new Color(0xE6000000, true));
                    drawRoundedRect(g2d,x,y,150,150,20);
                    indexX++;
                }
                indexY++;
            }
            int y=70;
            for(int iY = menuScrollY; iY < menuScrollY+4; iY++){
                int x = xPosSkills+11;
                for (int iX = 0; iX < 3; iX++){
                    if(player.skillsContain(iY*3+iX)) {
                        g2d.drawImage(skills[iY*3+iX],x+2,y+2,146,146,null);
                    }
                    else{
                        g2d.setColor(Color.white);;
                        typeText(g2d,"?",x+50,y+110,8F);
                    }
                    g2d.setColor(new Color(0xE6000000, true));
                    g2d.drawRoundRect(x,y,150,150,20,20);
                    x+=162;
                }
                y+=170;
            }

            if(xPosSkills!=450){ //number is 202 and 712
                if(skillsBounce){
                    xPosSkills-=(xPosSkills-450)/5;
                    if(xPosSkills>444){
                        xPosSkills++;
                    }
                }
                else{
                    if(xPosSkills<166){
                        skillsBounce=true;
                    }
                    xPosSkills-=(xPosSkills-152)/3;
                }
            }
            if(xPosSkills2!=450){
                if(skillsBounce){
                    xPosSkills2+=(450-xPosSkills2)/5;
                    if(xPosSkills2<456){
                        xPosSkills2--;
                    }
                }
                else{
                    xPosSkills2+=(762-xPosSkills2)/3;
                }
            }
            g2d.setColor(new Color(0x000000));
            drawBorder(g2d,xPosSkills-5,5,510,750,20,10);
        }
        else if(menu==3){
            g2d.setColor(new Color(0xE61F1E00, true));
            drawRoundedRect(g2d, 450,50,500,700,20);
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, 450,15,500,35,20);
            g2d.setColor(new Color(0x000FFFF));
            typeText(g2d,"Quests",630,45, 3F);
            if(menuSelectY ==0){
                drawRoundedRect(g2d, 459, 59, 482, 222,20);
            }
            else if(menuSelectY ==1){
                drawRoundedRect(g2d, 459, 289, 482, 222,20);
            }
            else if(menuSelectY ==2){
                drawRoundedRect(g2d, 459, 519, 482, 222,20);
            }
            g2d.setColor(new Color(0xE6166060, true));
            drawRoundedRect(g2d, 460, 60, 480, 220,20);
            drawRoundedRect(g2d, 460, 290, 480, 220,20);
            drawRoundedRect(g2d, 460, 520, 480, 220,20);
            g2d.setColor(new Color(0xE6000000, true));
            drawRoundedRect(g2d, 460, 70, 480, 50,20);
            drawRoundedRect(g2d, 460, 300, 480, 50,20);
            drawRoundedRect(g2d, 460, 530, 480, 50,20);
            g2d.setColor(new Color(0xE6832900, true));
            drawRoundedRect(g2d, 460, 120, 480, 100,20);
            drawRoundedRect(g2d, 460, 350, 480, 100,20);
            drawRoundedRect(g2d, 460, 580, 480, 100,20);
            g2d.setColor(new Color(0xE6FF0000, true));
            drawRoundedRect(g2d, 460, 220, 480, 50,20);
            drawRoundedRect(g2d, 460, 450, 480, 50,20);
            drawRoundedRect(g2d, 460, 680, 480, 50,20);
            g2d.setColor(Color.white);
            int x = 470;
            int y = 115;
            for(int i = menuScrollY; i < frame.getQuests().size(); i++){
                typeText(g2d, frame.getQuests().get(i).getName(),x,y,4F);
                typeText(g2d, frame.getQuests().get(i).getDescription(),x,y+30,40,20,1.5F);
                typeText(g2d, frame.getQuests().get(i).getRequirements(),x,y+140,3F);
                y+=230;
            }
            if(frame.getQuests().size() < 3){
                typeText(g2d,"No Quest",x,575,4F);
                if(frame.getQuests().size() < 2){
                    typeText(g2d,"No Quest",x,345,4F);
                    if(frame.getQuests().isEmpty()){
                        typeText(g2d,"No Quest",x,115,4F);
                    }
                }
            }
            g2d.setColor(new Color(0x000000));
            drawBorder(g2d,445,5,510,750,20,10);
        }
        else if(menu==4){
            menuClose();
        }
        else if(menu==5){
            g2d.setColor(new Color(0x312D2D));
            drawRoundedRect(g2d, xPosSkills2,50,500,700,20);
            drawRoundedRect(g2d, xPosSkills,50,500,700,20);
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, xPosSkills,15,500,35,20);
            g2d.setColor(new Color(0x000FFFF));
            typeText(g2d,"Passive Abilities",xPosSkills+130,45, 3F);

            int indexY = 0;
            for(int y = 70; y <= 580; y+=170) {
                int indexX=0;
                for (int x = xPosSkills+11; x <= xPosSkills+335; x+=162) {
                    g2d.setColor(new Color(0x00FFFF));
                    drawMenuSelect(g2d, indexY, indexX,x-1, y-1, 152, 152,20);
                    g2d.setColor(new Color(0xE6000000, true));
                    drawRoundedRect(g2d,x,y,150,150,20);
                    indexX++;
                }
                indexY++;
            }
            int y=70;
            for(int iY = menuScrollY; iY < menuScrollY+4; iY++){
                int x = xPosSkills+11;
                for (int iX = 0; iX < 3; iX++){
                    if(player.passivesContain(iY*3+iX)) {
                        g2d.drawImage(passives[iY*3+iX],x+2,y+2,146,146,null);
                    }
                    else{
                        g2d.setColor(Color.white);;
                        typeText(g2d,"?",x+50,y+110,8F);
                    }
                    g2d.setColor(new Color(0xE6000000, true));
                    g2d.drawRoundRect(x,y,150,150,20,20);
                    x+=162;
                }
                y+=170;
            }

            if(xPosSkills!=450){ //number is 202 and 712
                if(skillsBounce){
                    xPosSkills-=(xPosSkills-450)/5;
                    if(xPosSkills>444){
                        xPosSkills++;
                    }
                }
                else{
                    if(xPosSkills<166){
                        skillsBounce=true;
                    }
                    xPosSkills-=(xPosSkills-152)/3;
                }
            }
            if(xPosSkills2!=450){
                if(skillsBounce){
                    xPosSkills2+=(450-xPosSkills2)/5;
                    if(xPosSkills2<456){
                        xPosSkills2--;
                    }
                }
                else{
                    xPosSkills2+=(762-xPosSkills2)/3;
                }
            }
            g2d.setColor(new Color(0x000000));
            drawBorder(g2d,xPosSkills-5,5,510,750,20,10);
        }
        else if(menu==6){
            if(barricaded){
                effects.add(new Attack("couldn'tSave",true,null));
            }
            else {
                frame.generateSaveCode();
                effects.add(new Attack("saved",true,null));
            }
            menuClose();
        }
        else if(menu==7){
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, xPosSkills,15,500,35,20);
            g2d.setColor(new Color(0x000FFFF));
            typeText(g2d,"Skills",xPosSkills+200,45, 3F);
            g2d.setColor(new Color(0x312D2D));
            drawRoundedRect(g2d, xPosSkills2,50,500,700,20);

            g2d.setColor(new Color(0x00FFFF));
            drawMenuSelect(g2d,0,menuSelectX,xPosSkills2+339,99,152,27,20);
            drawMenuSelect(g2d,1,menuSelectX,xPosSkills2+339,169,152,27,20);
            drawMenuSelect(g2d,2,menuSelectX,xPosSkills2+339,239,152,27,20);
            drawMenuSelect(g2d,3,menuSelectX,xPosSkills2+339,309,152,27,20);
            drawMenuSelect(g2d,4,menuSelectX,xPosSkills2+9 ,714, 482, 27,20);


            g2d.setColor(new Color(0xF39B75));
            drawRoundedRect(g2d, xPosSkills2+10,410,480,300,20);
            g2d.setColor(new Color(0xE6166060, true));
            drawRoundedRect(g2d,xPosSkills2+10,715,480,25,20); //return to menu button
            drawRoundedRect(g2d,xPosSkills2+340,100,150,25,20);
            drawRoundedRect(g2d,xPosSkills2+340,170,150,25,20);
            drawRoundedRect(g2d,xPosSkills2+340,240,150,25,20);
            drawRoundedRect(g2d,xPosSkills2+340,310,150,25,20);
            if(frame.indexOfSkill(0)>-1) g2d.drawImage(skills[frame.indexOfSkill(0)],xPosSkills2+395,130,30,30,null);
            if(frame.indexOfSkill(1)>-1)g2d.drawImage(skills[frame.indexOfSkill(1)],xPosSkills2+395,200,30,30,null);
            if(frame.indexOfSkill(2)>-1)g2d.drawImage(skills[frame.indexOfSkill(2)],xPosSkills2+395,270,30,30,null);
            if(frame.indexOfSkill(3)>-1)g2d.drawImage(skills[frame.indexOfSkill(3)],xPosSkills2+395,340,30,30,null);
            g2d.setColor(new Color(0xFFFFFF));
            typeText(g2d,"Return To Skills Select", xPosSkills2+100, 735,2F);
            typeText(g2d,"Set as Skill 1", xPosSkills2+355, 120,1.7F);
            typeText(g2d,"Set as Skill 2", xPosSkills2+355, 190,1.7F);
            typeText(g2d,"Set as Skill 3", xPosSkills2+355, 260,1.7F);
            typeText(g2d,"Set as Skill 4", xPosSkills2+355, 330,1.7F);
            g2d.setColor(new Color(0));
            if(player.skillsContain(skillNum)){
                String str = frame.skillList()[skillNum];
                if(frame.getSpecialMove()[skillNum]){
                    str+= " (Special Skill)";
                }
                typeText(g2d, str, xPosSkills2+20, 445,2F);
                g2d.drawImage(skills[skillNum],xPosSkills2+10,60,320,320,null);
            }
            else{
                typeText(g2d,"Not yet unlocked.", xPosSkills2+20, 445,2F);
                typeText(g2d,"?",xPosSkills2+130,300,20F);
            }
            typeDesc(g2d,0,470,"Focus your energy into your sword to unleash flying slashes.");
            typeDesc(g2d,1,470,"Conjure a ember that bursts, causing a fire rumored to be equal to a great fire years ago that devastated a town, dealing damage to oneself and scorching the surrounding environment. Burns enemies.");
            typeDesc(g2d,2,470,"Call down lightning around you to shock enemies who get too close to you. Stuns enemies.");
            typeDesc(g2d,3,470,"Dash to another place to dodge or to close distances.");
            typeDesc(g2d,4,470,"Concentrate all of your magic into a shield that nullifies all damage for a short duration.");
            typeDesc(g2d,5,470,"Bet all of your energy that you will take fatal damage in the next half second. If it succeeds, you heal a great amount of health.");
            typeDesc(g2d,6,470,"Break your limits, doubling your damage but halving your defense for a short duration.");
            typeDesc(g2d,7,470,"Focus all of your energy into lightning magic, and create a dragon that pulls and stuns enemies.");
            typeDesc(g2d,8,470,"Release magic in the form of projectiles in all directions.");
            typeDesc(g2d,9,470,"Recover health and energy.");
            typeDesc(g2d,10,470,"Blow up the area around you, pushing enemies away from you.");
            typeDesc(g2d,11,470,"bread.");
            typeDesc(g2d,12,470,"Shoot out three blades that levitate momentarily before returning.");
            typeDesc(g2d,13,470,"Draw all of your strength into the tip of your sword, dealing devastating single hit damage.");
            typeDesc(g2d,14,470,"Enter flashing strikes mode, each movement made by you makes you dash while striking anybody you pass by.");
            typeDesc(g2d,15,470,"Pour unstable energy into your sword, enlarging it, and swinging it. Basic attacks are bigger and sword based attacks do more damage for the next 10 seconds.");
            g2d.setColor(new Color(0xA65F14));
            typeDesc(g2d,0,530,"Physical Damage: Each slash deals damage equal to your sword stat.");
            typeDesc(g2d,12,530,"Physical Damage: Each blade deals damage equal to 1/5th of your sword stat.");
            typeDesc(g2d,13,530,"Physical Damage: Deal damage equal to 50x your sword stat in a single hit.");
            typeDesc(g2d,14,530,"Physical Damage: Each flashing strike deals damage equal to 2x your sword stat.");
            typeDesc(g2d,15,570,"Physical Damage: Single sword swing deals damage equal to 10x your sword stat.");
            g2d.setColor(new Color(0x020977));
            typeDesc(g2d,1,585,"Magic Damage: Does 1/5th of your magic stat per tick. Burn deals damage equals to your magic stat.");
            typeDesc(g2d,2,530,"Magic Damage: Each small strike does damage equal to 2x your magic stat. The big strike does damage equal to 3x your magic stat");
            typeDesc(g2d,7,530,"Magic Damage: Each zap from the dragon deals damage equal to your magic stat, while the explosion deals damage equal to 10x your magic stat");
            typeDesc(g2d,8,530,"Magic Damage: Each projectile deals damage equal to your magic stat");
            g2d.setColor(new Color(0x0040B4));
            for(int i = 0; i < frame.getEnergyCosts().length; i++){
                typeDesc(g2d,i,650,"Energy Cost: " + frame.getEnergyCosts()[i]);
            }
            g2d.setColor(new Color(0xA10000));
            for(int i = 0; i < frame.getSkillCooldowns().length; i++){
                typeDesc(g2d,i,670,"Cooldown: " + frame.getSkillCooldowns()[i] + " Seconds");
            }
            g2d.setColor(new Color(0x312D2D));
            drawRoundedRect(g2d, xPosSkills,50,500,700,20);
            g2d.setColor(new Color(0xE6000000, true));
            for(int y = 70; y <= 580; y+=170) {
                for (int x = xPosSkills+11; x <= xPosSkills+335; x+=162) {
                    drawRoundedRect(g2d,x,y,150,150,20);
                }
            }
            int y=70;
            for(int iY = menuScrollY; iY < menuScrollY+4; iY++){
                int x = xPosSkills+11;
                for (int iX = 0; iX < 3; iX++){
                    if(player.skillsContain(iY*3+iX)) {
                        g2d.drawImage(skills[iY*3+iX],x+2,y+2,146,146,null);
                    }
                    else{
                        g2d.setColor(Color.white);;
                        typeText(g2d,"?",x+50,y+110,8F);
                    }
                    g2d.setColor(new Color(0xE6000000, true));
                    g2d.drawRoundRect(x,y,150,150,20,20);
                    x+=162;
                }
                y+=170;
            }
            if(xPosSkills!=202){ //number is 202 and 712
                if(skillsBounce){
                    xPosSkills-=(xPosSkills-202)/5;
                }
                else{
                    if(xPosSkills<182){
                        skillsBounce=true;
                    }
                    xPosSkills-=(xPosSkills-172)/3;
                }
            }
            if(xPosSkills2!=707){
                if(skillsBounce){
                    xPosSkills2+=(707-xPosSkills2)/5;
                }
                else{
                    xPosSkills2+=(742-xPosSkills2)/3;
                }
            }
            g2d.setColor(new Color(0x000000));
            drawBorder(g2d,xPosSkills-5,5,510,750,20,10);
        }
        else if(menu==8){
            g2d.setColor(new Color(0xE69D9B3A, true));
            drawRoundedRect(g2d, xPosSkills,15,500,35,20);
            g2d.setColor(new Color(0x000FFFF));
            typeText(g2d,"Passive Abilities",xPosSkills+130,45, 3F);
            g2d.setColor(new Color(0x312D2D));
            drawRoundedRect(g2d, xPosSkills2,50,500,700,20);

            g2d.setColor(new Color(0x00FFFF));
            drawMenuSelect(g2d,0,menuSelectX,xPosSkills2+339,99,152,52,20);
            drawMenuSelect(g2d,1,menuSelectX,xPosSkills2+339,199,152,52,20);
            drawMenuSelect(g2d,2,menuSelectX,xPosSkills2+339,299,152,27,20);
            drawMenuSelect(g2d,3,menuSelectX,xPosSkills2+9 ,714, 482, 27,20);


            g2d.setColor(new Color(0xF39B75));
            drawRoundedRect(g2d, xPosSkills2+10,410,480,300,20);
            g2d.setColor(new Color(0xE6166060, true));
            drawRoundedRect(g2d,xPosSkills2+10,715,480,25,20); //return to menu button
            drawRoundedRect(g2d,xPosSkills2+340,100,150,50,20);
            drawRoundedRect(g2d,xPosSkills2+340,200,150,50,20);
            drawRoundedRect(g2d,xPosSkills2+340,300,150,25,20);
            if(frame.indexOfPassive(0)>-1) g2d.drawImage(passives[frame.indexOfPassive(0)],xPosSkills2+395,160,30,30,null);
            if(frame.indexOfPassive(1)>-1)g2d.drawImage(passives[frame.indexOfPassive(1)],xPosSkills2+395,260,30,30,null);
            g2d.setColor(new Color(0xFFFFFF));
            typeText(g2d,"Return To Passives Select", xPosSkills2+100, 735,2F);
            typeText(g2d,"Set as Passive", xPosSkills2+355, 120,1.7F);
            typeText(g2d,"Ability 1", xPosSkills2+375, 140,1.7F);
            typeText(g2d,"Set as Passive", xPosSkills2+355, 220,1.7F);
            typeText(g2d,"Ability 2", xPosSkills2+375, 240,1.7F);
            typeText(g2d,"Unequip All", xPosSkills2+355, 320,1.7F);
            g2d.setColor(new Color(0));
            if(player.passivesContain(skillNum)){
                typeText(g2d, frame.passiveList()[skillNum], xPosSkills2+20, 445,2F);
                g2d.drawImage(passives[skillNum],xPosSkills2+10,60,320,320,null);
            }
            else{
                typeText(g2d,"Not yet unlocked.", xPosSkills2+20, 445,2F);
                typeText(g2d,"?",xPosSkills2+130,300,20F);
            }
            typeDesc(g2d,0,470,"The standard recovery rate of a seasoned adventurer. Heals 1% of your max hp per second.");
            typeDesc(g2d,1,470,"Harness the power of fire, doubling burn damage. Unlocks special combos and chance to fire an additional searing slash when using basic attacks. Searing slashes burn enemies and deals extra damage.");
            typeDesc(g2d,2,470,"Demonstrate proficiency using your sword, doubling cut multiplier and causing enemies to bleed with each cut. Bleeding enemies move slightly slower.");
            typeDesc(g2d,3,470,"Consume hp every time you attack, while boosting your damage output.");
            typeDesc(g2d,4,470,"Drain the health of your enemies every time you land a hit on them.");
            typeDesc(g2d,5,470,"Blitz through your enemies. Gives 20% more speed and ");
            typeDesc(g2d,6,470,"Become a heavyweight fighter. Triples health and defense, but lowers speed by 20%.");
            typeDesc(g2d,7,470,"Use magic to augment your physical capabilities. Your physical stat and hp stat is increased by half of your magic stat.");
            g2d.setColor(new Color(0xA65F14));
            g2d.setColor(new Color(0x020977));
            g2d.setColor(new Color(0x0040B4));
            g2d.setColor(new Color(0x312D2D));
            drawRoundedRect(g2d, xPosSkills,50,500,700,20);
            g2d.setColor(new Color(0xE6000000, true));
            for(int y = 70; y <= 580; y+=170) {
                for (int x = xPosSkills+11; x <= xPosSkills+335; x+=162) {
                    drawRoundedRect(g2d,x,y,150,150,20);
                }
            }
            int y=70;
            for(int iY = menuScrollY; iY < menuScrollY+4; iY++){
                int x = xPosSkills+11;
                for (int iX = 0; iX < 3; iX++){
                    if(player.passivesContain(iY*3+iX)) {
                        g2d.drawImage(passives[iY*3+iX],x+2,y+2,146,146,null);
                    }
                    else{
                        g2d.setColor(Color.white);;
                        typeText(g2d,"?",x+50,y+110,8F);
                    }
                    g2d.setColor(new Color(0xE6000000, true));
                    g2d.drawRoundRect(x,y,150,150,20,20);
                    x+=162;
                }
                y+=170;
            }
            if(xPosSkills!=202){ //number is 202 and 712
                if(skillsBounce){
                    xPosSkills-=(xPosSkills-202)/5;
                }
                else{
                    if(xPosSkills<182){
                        skillsBounce=true;
                    }
                    xPosSkills-=(xPosSkills-172)/3;
                }
            }
            if(xPosSkills2!=707){
                if(skillsBounce){
                    xPosSkills2+=(707-xPosSkills2)/5;
                }
                else{
                    xPosSkills2+=(742-xPosSkills2)/3;
                }
            }
            g2d.setColor(new Color(0x000000));
            drawBorder(g2d,xPosSkills-5,5,510,750,20,10);
        }
    }
    private void typeDesc(Graphics2D g2d, int num, int y, String text){
        boolean typeDesc;
        if(menu==7){
            typeDesc = skillNum==num && player.skillsContain(skillNum);
        }
        else{
            typeDesc = skillNum==num && player.passivesContain(skillNum);
        }
        if(typeDesc){
            typeText(g2d,text,xPosSkills2+20,y,40,20,2F);
        }
    }
    private void drawMenuSelect(Graphics2D g2d, int menuChoiceY, int menuChoiceX, int x, int y, int width, int height, int howRound){
        if(menuSelectY == menuChoiceY && menuSelectX==menuChoiceX && !menuTicking)
        {
            drawRoundedRect(g2d, x, y, width, height,howRound);
        }
    }
    private void drawPlus(Graphics2D g2d, int x, int y, int size){
        drawSquare(g2d,x,y,size);
        drawSquare(g2d,x+size,y,size);
        drawSquare(g2d,x+size*2,y,size);
        drawSquare(g2d,x+size,y-size,size);
        drawSquare(g2d,x+size,y+size,size);
    }
    public void pressButton(){
        menuTick = 0;
        menuTicking = true;
        menuTickIn = true;
    }
    private void drawPortraits(Graphics2D g2d){
        if(npcList.get(npcTouched).getType().equals("storyteller")) {
            String name = npcList.get(npcTouched).getDisplayName();
            if (name.equals("You")) {
                g2d.setColor(new Color(0xFF8000));
                g2d.fillRect(1100, 420, 200, 200);
            }
            else if(name.contains("Red")) {
            }
            else{
                if(name.equals("Kuria")){
                    g2d.drawImage(accessories[3],0,320,400,400,null);
                }
                else if(name.equals("Lloyd")){
                    g2d.drawImage(accessories[4],0,320,400,400,null);
                }
                else if(name.equals("Rss Leader")){
                    g2d.drawImage(accessories[0],0,320,400,400,null);
                }
                g2d.setColor(Color.blue);
                g2d.fillRect(100, 420, 200, 200);
            }
        }
    }
    /**When requirement lines changes the lines*/
    public void changeTextPart(int num){
        textPart = num;
    }
    public int mainMenuSelect(){
        return menuSelectY;
    }
    public int getMapNumX(){return mapNumX;}
    public int getMapNumY(){return mapNumY;}
    public int getLastX(){return lastX;}
    public int getLastY(){return lastY;}
    public int getPointY(){return pointY;}
    public int getPointX(){return pointX;}
    public void setLast(int x, int y, int xP, int yP){
        lastX = x;
        lastY = y;
        pointX = xP;
        pointY = yP;
    }
    public void goToMapXY(int x, int y){
        mapNumX = x;
        mapNumY = y;
    }
    public void setScrollXY(int x, int y, boolean snap){
        if(snap) {
            scrollX = -x * 1375;
            scrollY = -y * 775;
            System.out.println(scrollX + " " + scrollY + " " + player.getxPos() + " " + player.getyPos() + " " + mapNumX + " " + mapNumY);
        }
        else{
            scrollX = x;
            scrollY = y;
            System.out.println(scrollX + " " + scrollY + " " + player.getxPos() + " "+ player.getyPos());
        }
    }
    public void goLastSpawnPoint(){
        mapNumX = lastX;
        mapNumY = lastY;
        scrollX = lastX * -1375;
        scrollY = lastY * -775;
        player.goToXY(pointX,pointY);
        String tempLocation = location;
        mapChange();
        if(!location.equals(tempLocation)) {
            locationY = 10;
            locationDown = true;
            locationTick = 0;
        }
    }
    public void printMap(){
        for(int column = 0; column < map[0].length; column++){
            for(int row = 0; row < map.length; row++){
                System.out.print(map[row][column] + " ");
            }
            System.out.println();
        }
        for(int[] row : map){
            for(int column : row){
                System.out.print(column + " ");
            }
            System.out.println();
        }
    }
    public int getNpcTouched(){
        return npcTouched;
    }
    public String toString(){
        return super.toString();
    }
    public boolean equals(Object other){
        return map==((GameScreen)other).map;
    }
}