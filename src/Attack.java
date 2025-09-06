


import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Attack {
    private ArrayList<Person> enemyList = game.getEnemyList();
    final private Person player = game.getPlayer();
    private int xPos;
    private int yPos;
    private int speed;
    private int lastX;
    private int lastY;
    private int moveCounter;
    private int moveMax;
    private int width;
    private int height;
    private boolean firedByPlayer;
    private String direction;
    private String type;
    private ArrayList<Person> peopleHit = new ArrayList<>();
    private BufferedImage image = null;
    private BufferedImage[] images = new BufferedImage[0];
    private static BufferedImage[] allImages;
    /**0 is burn*/
    private static BufferedImage[][] animations;
    private boolean animated;
    private boolean playerHit;
    private Person attacker;
    private int timeBetweenFrames,animationCount,imageNum;
    private int degrees;
    private static Game game;
    public Attack(int x, int y, int spd, String name, Person whoAttacked) {
        lastX = game.getGameScreen().getScrollX();
        lastY = game.getGameScreen().getScrollY();
        xPos = x;
        yPos = y;
        speed = spd;
        type = name;
        attacker = whoAttacked;
        direction = attacker.getDirection();
        firedByPlayer = attacker==player;
        initializeAttack();
    }
    public Attack(String name, boolean playerFired, Person whoAttacked){
        lastX = game.getGameScreen().getScrollX();
        lastY = game.getGameScreen().getScrollY();
        type = name;
        firedByPlayer = playerFired;
        attacker = whoAttacked;
        initializeEffects();
    }
    public Attack(){
        allImages = new BufferedImage[255];
        allImages[0] = image("slash1");
        allImages[1] = image("slashRight");
        allImages[2] = image("slashDown");
        allImages[3] = image("slashUp");
        for(int i = 4; i < 11; i++) {
            allImages[i] = image("explosion" + (i-3));
        }
        for(int i = 11; i < 17; i++) {
            allImages[i] = image("breadSlashes" + (i-10));
        }
        for(int i = 17; i < 29; i++) {
            allImages[i] = image("firePoof" + (i-16));
        }
        for(int i = 29; i < 43; i++) {
            allImages[i] = image("wisp" + (i-28));
        }
        for(int i = 43; i < 50; i++) {
            allImages[i] = image("searingSlash" + (i-42));
        }
        for(int i = 57; i < 60; i++){
            allImages[i] = image("flashingStrike" + (i-56));
        }
        for(int i = 62; i < 77; i++) {
            allImages[i] = image("fire" + (i-61));
        }
        for(int i = 77; i < 84; i++) {
            //allImages[i] = image("lightning" + (i-76));
            try {
                allImages[i] = ImageIO.read(getClass().getResourceAsStream("/attacks/" + "lightning" + (i-76) + ".png"));
            } catch (IOException e) {
            }
        }
        for(int i = 87; i < 115; i++) {
            allImages[i] = image("basicAttack" + (i-86));
        }
        for(int i = 115; i < 127; i++){
            allImages[i] = image("singularity" + (i-114));
        }
        allImages[129] = image("heal");
        allImages[130] = image("revolvingBlade");
        allImages[131] = image("ball");
        allImages[132] = image("shield");
        allImages[133] = image("gambit");
        for(int i = 134; i < 141; i++) {
            allImages[i] = image("break" + (i-133));
        }
        for(int i = 141; i < 145; i++){
            allImages[i] = image("breadMeteor" + (i-140));
        }
        for(int i = 145; i < 150; i++){
            allImages[i] = image("hitEffect" + (i-144));
        }
        allImages[151] = image("particle");
        allImages[153] = image("hRight");
        allImages[154] = image("hLeft");
        for(int i = 155; i < 165; i++) {
            allImages[i] = image("berserk" + "0" + (i-155));
        }
        for(int i = 165; i < 167; i++) {
            allImages[i] = image("berserk" + (i-155));
        }
        for(int i = 167; i < 191; i++) {
            allImages[i] = image("thunderDragon" + (i-167));
        }
        for(int i = 191; i < 201; i++) {
            allImages[i] = image("crimsonSpark" + (i-190));
        }
        for(int i = 201; i < 211; i++){
            allImages[i] = image("bBOTH0" + (i-201));
        }
        for(int i = 211; i < 236; i++){
            allImages[i] = image("bBOTH" + (i-201));
        }
        for(int i = 236; i < 244; i++){
            allImages[i] = image("fireRing" + (i-235));
        }
        for(int i = 244; i < 255; i++){
            allImages[i] = image("magma" + (i-243));
        }
        animations = new BufferedImage[50][1000];
        int count = 0;
        for(int rep = 0; rep < 19; rep++){
            for(int i = 29; i < 37; i++){
                animations[0][count] = allImages[i];
                count++;
            }
        }
        for(int i = 37; i < 43; i++){
            animations[0][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 43; i < 50; i++){
            animations[2][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 77; i < 84; i++){
            animations[3][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 87; i < 94; i++){
            animations[4][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 101; i < 108; i++){
            animations[5][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 167; i < 191; i++) {
            animations[6][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 4; i < 11; i++){
            animations[7][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 11; i < 17; i++){
            animations[8][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 145; i < 150; i++){
            animations[9][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 201; i < 236; i++){
            animations[10][count] = allImages[i];
            count++;
        }
        count = 0;
        for(int i = 244; i < 255; i++){
            animations[11][count] = allImages[i];
            count++;
        }
        //initializeStuffhere
    }

    public static void setGame(Game g) {
        game = g;
    }

    private BufferedImage image(String fileName){
        try {
            return ImageIO.read(getClass().getResourceAsStream("/attacks/" + fileName + ".png"));
        } catch (IOException e) {
            return null;
        }
    }
    private void addToAttacks(int num){
        BufferedImage[] tempImages = new BufferedImage[images.length + 1];
        for(int i = 0; i < images.length; i++){
            tempImages[i] = images[i];
        }
        tempImages[tempImages.length-1] = allImages[num];
        images = tempImages;
    }
    private void initializeAttack(){
        if (type.equals("slash")) {
            width=54;
            height=60;
            if(attacker.biggerSword()){
                width = 128;
                height = 128;
                yPos-=10;
                xPos-=5;
            }
            moveMax = 100;
            image = allImages[0];
            if(direction.equals("left")){
                setDegrees();
                rotateImage(degrees);
                height*=-1;
                yPos-=height;
            }
            else if(direction.equals("up")) {
                setDegrees();
                rotateImage(degrees);
            }
            else if(direction.equals("down")){
                setDegrees();
                rotateImage(degrees);
                width*=-1;
                xPos-=width;
            }
//            if (direction.equals("left")) {
//            } else if(direction.equals("right")){ //54 down 46 across
//                image = allImages[1];
//            } else if(direction.equals("down")){ //46 down 54 across
//                image = allImages[2];
//            } else if(direction.equals("up")){
//                image = allImages[3];
//            }
        }
        else if (type.equals("burn")){
            width=(int)(Math.random()*64)+32;
            height=(int)(Math.random()*64)+32;
            if(Math.random()<0.5){
                width*=-1;
            }
            width*=2;
            height*=2;
            moveMax = 158;
            images = animations[0];
//            for(int rep = 0; rep < 19; rep++){
//                for(int i = 29; i < 37; i++){
//                    addToAttacks(i);
//                }
//            }
//            for(int i = 37; i < 43; i++){
//                addToAttacks(i);
//            }
            animated = true;
            timeBetweenFrames = 0;
            image = images[0];
        }
        else if (type.equals("wisp")){
            width = 240;
            height = 180;
            moveMax = 138;
            for(int rep = 0; rep < 5; rep++){
                for(int i = 29; i < 37; i++){
                    addToAttacks(i);
                }
            }
            for(int i = 37; i < 43; i++){
                addToAttacks(i);
            }
            animated = true;
            timeBetweenFrames = 2;
            image = images[0];
            if(attacker.getHp()>attacker.getMagic()*2) attacker.recoilDamage(attacker.getMagic()*2);
        }
        else if(type.equals("pillarManager")){
            width = 0;
            height = 0;
            moveMax = 164;
        }
        else if(type.equals("fireRing")){
            width = 92;
            height = 40;
            xPos = attacker.getxPos()-50;
            yPos = attacker.getyPos()+25;
            for(int rep = 0; rep < 10; rep++) {
                for (int i = 236; i < 244; i++) {
                    addToAttacks(i);
                }
            }
            direction = "up";
            moveMax = 80;
            animated = true;
            image = images[0];
        }
        else if (type.equals("searingSlash")){
            width=140;
            height=170;
            moveMax = 14;
            if(attacker.getDirection().equals("right") || attacker.getLastDirection().equals("right")){
                direction = "right";
            }
            else {
                direction = "left";
            }
            images = animations[2];
            if(direction.equals("left")){
                xPos+=width/2 + 95;
                width*=-1;
            }
            animated = true;
            timeBetweenFrames = 1;
            image = images[0];
        }
        else if (type.equals("fire")){
            width=(int)(Math.random()*64)+64;
            height=(int)(Math.random()*16)+64;
            moveMax = 123;
            for(int rep = 0; rep < 13; rep++){
                for(int i = 62; i < 71; i++){
                    addToAttacks(i);
                }
            }
            for(int i = 61; i < 77; i++){
                addToAttacks(i);
            }
            animated = true;
            timeBetweenFrames = 0;
            image = images[0];
        }
        else if (type.equals("lightning")){
            width=54;
            height=59;
            moveMax = 14;
            images = animations[3];
            animated = true;
            timeBetweenFrames = 1;
            image = images[0];
        }
        else if(type.contains("lightning")){
            width=108;
            height=118;
            moveMax = 20;
            images = animations[3];
            animated = true;
            timeBetweenFrames = 1;
            image = images[0];
        }
        else if (type.equals("magma")){
            width=96;
            height=96;
            moveMax = 22;
            images = animations[11];
            animated = true;
            timeBetweenFrames = 2;
            image = images[0];
        }
        else if (type.equals("attack") || type.equals("attack2")){ //24 down, 17 across
            width=110;
            height=110;
            boolean biggerSword = attacker.biggerSword();
            if(!biggerSword){
                width = 73;
                height = 73;
                xPos+=15;
                yPos+=10;
            }
            moveMax = 14;
            if(direction.equals("up") || direction.equals("down")){
                direction = attacker.getLastDirection();
            }
            images = animations[4];
            if(direction.equals("left")) {
                xPos+=width/2 + 10;
                width*=-1;
            }
            if(type.equals("attack2")){
                yPos+=height/2 + 25;
                height*=-1;
            }
            animated = true;
            timeBetweenFrames = 1;
            image = images[0];
        }
        else if (type.equals("attack3")){ //21 down, 11 across
            width=156;
            height=126;
            boolean biggerSword = attacker.biggerSword();
            if(!biggerSword){
                width = 104;
                height = 84;
                xPos+=15;
                yPos+=10;
            }
            moveMax = 16;
            if(direction.equals("up") || direction.equals("down")){
                direction = attacker.getLastDirection();
            }
            images = animations[5];
            if(direction.equals("left")) {
                width*=-1;
                xPos+=50;
            }
            animated = true;
            timeBetweenFrames = 1;
            image = images[0];
        }
        else if(type.equals("magicBullet")){
            image = allImages[131];
            moveMax = 30;
            width = 25;
            height = 25;
        }
        else if(type.equals("shield")){
            image = allImages[132];
            moveMax = 6;
            width = 51;
            height = 51;
        }
        else if(type.equals("gambit")){
            image = allImages[133];
            moveMax = 15;
            width = 32;
            height = 32;
        }
        else if(type.equals("thunderDragon")){
            images = animations[6];
            animated = true;
            width = game.getWidth();
            height = game.getHeight();
            timeBetweenFrames = 2;
            moveMax = 72;
        }
        else if(type.equals("thunderBoom")){
            moveMax = 30;
        }
        else if(type.equals("bigSlash")){
            width=128;
            height=128;
            moveMax = 100;
            if(direction.equals("up") || direction.equals("down")){
                direction = attacker.getLastDirection();
            }
            if(direction.equals("right")) {
                xPos+=25;
                image = allImages[153];
            }
            else{
                xPos-=118;
                image = allImages[154];
            }
        }
        else if(type.equals("dashHit")){
            moveMax = 1;
        }
        else if(type.equals("explosion")){
            images = animations[7];
            moveMax = 7;
            animated = true;
            width = 32;
            height = 32;
        }
        else if(type.equals("breadSlashes")){
            images = animations[8];
            moveMax = 12;
            timeBetweenFrames = 1;
            animated = true;
            width =128;
            height = 84;
        }
        else if(type.equals("breadMeteor")){
            direction = "";
            for(int i = 141; i < 145; i++){
                addToAttacks(i);
            }
            degrees = (int)(Math.random()*45)+45;
            for(int i = 0; i < images.length; i++){
                rotateImage(images[i], degrees, i);
            }
            moveMax = 80;
            timeBetweenFrames = 1;
            animated = true;
        }
        else if(type.equals("breadMissile")){
            direction = "right";
            for(int i = 141; i < 145; i++){
                addToAttacks(i);
            }
            width = images[0].getWidth()/2;
            height = images[0].getHeight()/2;
            moveMax = 40;
            timeBetweenFrames = 1;
            animated = true;
        }
        else if(type.equals("berserk")){
            for(int i = 155; i < 167; i++){
                addToAttacks(i);
            }
            moveMax = 24;
            timeBetweenFrames = 1;
            animated = true;
            width = 62;
            height = 62;
            width+=500;
            height+=500;
            xPos-=250;
            yPos-=250;
        }
        else if(type.equals("revolvingBlade")){
            for(int i = 0; i < 12; i++) addToAttacks(130);
            BufferedImage[] temp = images;
            for(int i = 0; i < 12; i++){
                rotateImage(images[i], 30*i,i);
                temp[i] = images[i];
            }
            images = new BufferedImage[100];
            for(int i = 12; i < 100; i++){
                images[i] = temp[i%12];
            }
            moveMax = 100;
            width = 50;
            height = 50;
            animated = true;
            setDegrees();
        }
        else if(type.equals("flashingStrike")){
            for(int i = 57; i < 60; i++) addToAttacks(i);
            if(!attacker.getDirection().equals("right")) {
                if (attacker.getDirection().equals("left")) {
                    degrees = 180;
                }
                else if (attacker.getDirection().equals("up")) {
                    degrees = -90;
                }
                else if (attacker.getDirection().equals("down")) {
                    degrees = 90;
                }
                rotateImages();
            }
            animated = true;
            moveMax = 6;
            timeBetweenFrames = 1;
            width = 373;
            height = 38;
        }
        else if(type.equals("singularity")){
            for(int i = 115; i < 127; i++){
                addToAttacks(i);
            }
            animated = true;
            moveMax = 12;
            xPos = 0;
            width = game.getWidth();
            height = 25;
        }
        else if(type.equals("bBOTH")){
            images = animations[10];
            animated = true;
            timeBetweenFrames = 1;
            moveMax = 100;
            yPos-=360;
            if(direction.equals("up") || direction.equals("down")){
                direction = attacker.getLastDirection();
            }
            if(direction.equals("right")) {
                width = 480;
                xPos-=240;
            }
            else{
                width = -480;
                xPos+=240;
            }
            height = 360;
        }
    }
    private void initializeEffects(){
        if(type.equals("whiteFlash")){
            moveMax=138;
        }
        else if(type.contains("receiveMoney") || type.equals("obtained")){
            moveMax=60;
        }
        else if(type.contains("gainMoney") || type.contains("gainExp")){
            moveMax=30;
        }
        else if(type.equals("break")){
            moveMax=300;
        }
        else if(type.equals("redSpark")){
            xPos = attacker.getxPos()-40 + (int)(Math.random()*60);
            yPos = attacker.getyPos()-20 + (int)(Math.random()*30);
            moveMax = 10;
            animated = true;
            timeBetweenFrames = 0;
            for(int i = 191; i < 201; i++){//int i = 134; i < 141; i++){
                addToAttacks(i);
            }
            direction = "";
            degrees = game.randomNum(45,-90);
            rotateImages();
            width=50;
            height=50;
        }
        else if (type.contains("skill")){
            image = game.getGameScreen().getSkills()[Integer.parseInt(type.substring(5))];
            width = 96;
            height = 96;
            moveMax = 90;
        }
        else if (type.contains("passive")){
            image = game.getGameScreen().getPassives()[Integer.parseInt(type.substring(7))];
            width = 96;
            height = 96;
            moveMax = 90;
        }
        else if(type.equals("levelUp")){
            width = 48;
            height = 48;
            moveMax = 65;
        }
        else if(type.equals("static")){
            moveMax = 30;
        }
        else if(type.equals("static2")){
            moveMax = 30;
        }
        else if(type.equals("hitEffect")){
            images = animations[9];
            direction = "";
            image = images[0];
            width = 32;
            height = 32;
            xPos = attacker.getxPos()-3;
            yPos = attacker.getyPos()-3;
            moveMax = 10;
            animated = true;
            timeBetweenFrames = 1;
        }
        else if(type.equals("heal")){
            image = allImages[129];
            moveMax = 10;
            width = 15;
            height = 15;
            xPos = attacker.getxPos() + game.randomNum(25,-10);
            yPos = attacker.getyPos();
            direction = "up";
            speed = 5;
        }
        else if(type.equals("particle")) {
            image = allImages[151];
            width = 19;
            height = 19;
            int a = game.randomNum(360,0);
            xPos = (int)(attacker.getxPos() + 50 * Math.cos(Math.toRadians(a)));
            yPos = (int)(attacker.getyPos() + 50 * Math.sin(Math.toRadians(a)));
            direction = "";
            moveMax = 5;
            speed = 0;
        }
        else if(type.equals("couldn'tSave")){
            moveMax = 100;
            xPos = 600;
            yPos = 400;
        }
        else if(type.equals("saved")){
            moveMax = 100;
            xPos = 600;
            yPos = 400;
        }
    }
    public void move(){
        if(game.getGameScreen().getScrollX()!=lastX){
            xPos+=game.getGameScreen().getScrollX()-lastX;
        }
        if(game.getGameScreen().getScrollY()!=lastY){
            yPos+=game.getGameScreen().getScrollY()-lastY;
        }
        lastX = game.getGameScreen().getScrollX();
        lastY = game.getGameScreen().getScrollY();
        if(direction.equals("left")){
            xPos-=speed;
        }
        else if(direction.equals("right")){
            xPos+=speed;
        }
        else if(direction.equals("up")){
            yPos-=speed;
        }
        else if(direction.equals("down")){
            yPos+=speed;
        }
        else if(direction.isEmpty()){
            double d = Math.toRadians(degrees);
            xPos+=Math.cos(d) * speed;
            yPos+=Math.sin(d) * speed;
        }
        moveCounter++;
        if(animated){
            animationCount++;
            if(animationCount>timeBetweenFrames){
                animationCount=0;
                imageNum++;
                if(imageNum<images.length){
                    image=images[imageNum];
                    if(type.equals("breadMeteor")){
                        width = image.getWidth();
                        height = image.getHeight();
                    }
                }
            }
        }
        if(type.equals("attack3") && moveCounter==4 && attacker.biggerSword()) {
            attacker.setDirection(direction);
            Attack slash = new Attack(attacker.getxPos() + 10, attacker.getyPos() - 10, 25, "slash", attacker);
//            if (direction.equals("left")) {
//                slash.image = allImages[0];
//            } else if (direction.equals("right")) { //54 down 46 across
//                slash.image = allImages[1];
//            }
            game.getAttacks().add(slash);
        }
        if(type.equals("revolvingBlade")){
            if(moveCounter>20 && moveCounter < 70){
                speed = 0;
            }
            else if(moveCounter <= 20){
                speed = 15;
            }
            else{
                facePerson(attacker);
                speed = 15;
                if(touchingPerson(attacker)){
                    moveCounter = moveMax;
                }
                else if(moveCounter>82){
                    moveCounter-=12;
                }
            }
        }
        if(type.equals("bBOTH") && moveCounter==71){
            animated = false;
            xPos+=50;
            yPos+=50;
            speed = 75;
            image = allImages[153];
        }
        if(type.equals("particle")){
            xPos += (attacker.getxPos() - xPos)/5;
            yPos += (attacker.getyPos() - yPos)/5;
        }
        dealDamage();
    }
    private void dealDamage(){
        if(attacker==player || game.getAllyList().contains(attacker)) {
            for (int i = 0; i < enemyList.size(); i++) {
                Person enemy = enemyList.get(i);
                dealDamage(enemy);
            }
        }
        else{
//        if(game.getEnemyList().contains(attacker)) {
            dealDamage(player);
            for (int i = 0; i < game.getAllyList().size(); i++) {
                Person enemy;
                enemy = game.getAllyList().get(i);
                dealDamage(enemy);
            }
//        }
        }
        if(type.equals("levelUp")){
            if(moveCounter < 11) {
                width += (100 - moveCounter) / 3;
                height += (100 - moveCounter) / 3;
            }
            if(moveCounter == 11){
                width += (-moveCounter)*3;
                height += (-moveCounter)*3;
            }
        }
        else if(type.contains("skill")){
            if(moveCounter < 11) {
                width += (100 - moveCounter) / 7;
                height += (100 - moveCounter) / 7;
            }
            if(moveCounter == 11){
                width += (-moveCounter)*3;
                height += (-moveCounter)*3;
            }
            if(moveCounter > 59){
                width += (-moveCounter) / 10;
                height += (-moveCounter) / 10;
                if(width < 0){
                    width = 0;
                    height = 0;
                }
            }
        }
        else if(type.contains("passive")){
            if(moveCounter < 11) {
                width += (100 - moveCounter) / 7;
                height += (100 - moveCounter) / 7;
            }
            if(moveCounter == 11){
                width += (-moveCounter)*3;
                height += (-moveCounter)*3;
            }
            if(moveCounter > 59){
                width += (-moveCounter) / 10;
                height += (-moveCounter) / 10;
                if(width < 0){
                    width = 0;
                    height = 0;
                }
            }
        }
        else if(type.equals("couldn'tSave") || type.equals("saved")){
            if(moveCounter<61) {
                yPos += (-yPos) / 100;
            }
        }
    }
    private boolean touchingPerson(Person person){
        boolean xTouching = false;
        boolean yTouching = false;
        if(width<0){
            xTouching = person.getxPos() + 25 > xPos + width && person.getxPos() < xPos ;
        }
        else{
            xTouching = person.getxPos() + 25 > xPos && person.getxPos() < xPos + width;
        }
        if(height<0){
            yTouching = person.getyPos() + 25 > yPos + height && person.getyPos() < yPos;
        }
        else{
            yTouching = person.getyPos() + 25 > yPos && person.getyPos() < yPos + height;
        }
        return xTouching && yTouching;
    }
    private void dealDamage(Person enemy){
        if(type.equals("slash")){
            if(!peopleHit.contains(enemy)) {
                //if (direction.equals("left") || direction.equals("right")) {
                if (touchingPerson(enemy)){//enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + 46 *(width/64)&& enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + 54 *(width/64)) {
                    if(attacker.biggerSword()){
                        enemy.takeDamage(attacker.getSword(),attacker);
                    }
                    enemy.takeDamage(attacker.getSword(),attacker);
                    enemy.getCut(attacker);
                    shove(enemy,5);
                    enemy.getStunned(0.1);
                    peopleHit.add(enemy);
                }
                //} else {
//                    if (enemy.getxPos() + 25 > xPos && enemy.getyPos() < yPos + 46 *(width/64) && enemy.getyPos() + 25 > yPos && enemy.getxPos() < xPos + 54 *(width/64)) {
//                        if(attacker.biggerSword()){
//                            enemy.takeDamage(attacker.getSword(),attacker);
//                        }
//                        enemy.takeDamage(attacker.getSword(),attacker);
//                        enemy.getCut(attacker);
//                        shove(enemy,5);
//                        enemy.getStunned(0.1);
//                        peopleHit.add(enemy);
//                    }
                //}
            }
        }
        else if(type.equals("whiteFlash")){
            enemy.getBurned(10,attacker.getMagic(),attacker);
            enemy.takeDamage(attacker.getMagic()/5,attacker);
        }
        else if(type.equals("fireRing")){
            if(touchingPerson(enemy)){
                enemy.takeDamage(attacker.getMagic()/5,attacker);
                shove(enemy,5);
            }
        }
        else if(type.equals("searingSlash")){
            if (touchingPerson(enemy)){//enemy.getxPos() + 25 > xPos && enemy.getyPos() < yPos + width && enemy.getyPos() + 25 > yPos && enemy.getxPos() < xPos + height) {
                enemy.getBurned(2,attacker.getMagic(),attacker);
                enemy.takeDamage(attacker.getMagic(),attacker);
            }
        }
        else if(type.equals("lightning") || type.equals("lightningBig")){
            if(!peopleHit.contains(enemy)) {
                if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos + 22 * (height/59) && enemy.getyPos() < yPos + height && moveCounter>6) {
                    enemy.takeDamage(attacker.getMagic() * 2,attacker);
                    enemy.getStunned(0.5);
                    if(type.equals("lightningBig")){
                        enemy.takeDamage(attacker.getMagic(),attacker);
                        enemy.getStunned(1);
                    }
                    peopleHit.add(enemy);
                }
            }
        }
        else if(type.equals("magma")){
            if(!peopleHit.contains(enemy)) {
                if (enemy.getxPos() + 25 > xPos + 3 * (width/48) && enemy.getxPos() < xPos + 42 * (width/48) && enemy.getyPos() + 25 > yPos + 18 * (height/48) && enemy.getyPos() < yPos + height && moveCounter>6 && moveCounter < 19) {
                    enemy.takeDamage(attacker.getMagic() * 2,attacker);
                    peopleHit.add(enemy);
                }
            }
        }
        else if(type.equals("attack") || type.equals("attack2")){
            if(!peopleHit.contains(enemy)) {
                if (touchingPerson(enemy)){//enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                    enemy.takeDamage(attacker.getSword(),attacker);
                    if(attacker.biggerSword()){
                        enemy.takeDamage(attacker.getSword(),attacker);
                    }
                    peopleHit.add(enemy);
                    enemy.getCut(attacker);
                    shove(enemy,3);
                    enemy.getStunned(0.25);
                }
            }
        }
        else if(type.equals("attack3")){
            if (touchingPerson(enemy)){//enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                if(!peopleHit.contains(enemy)) {
                    enemy.takeDamage(attacker.getSword(), attacker);
                    if (attacker.biggerSword()) {
                        enemy.takeDamage(attacker.getSword(), attacker);
                    }
                    peopleHit.add(enemy);
                    enemy.getCut(attacker);
                    enemy.getStunned(0.1);
                }
                shove(enemy, 3);
            }
        }
        else if(type.equals("magicBullet")){
            if(!peopleHit.contains(enemy)) {
                if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + 25 && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + 25) {
                    enemy.takeDamage(attacker.getMagic(),attacker);
                    peopleHit.add(enemy);
                    moveCounter=moveMax;
                }
                int collision = 0;
                if(xPos>0 && yPos>0 && xPos<=1400 && yPos<=800) {
                    if (direction.equals("right") || direction.equals("left")) {
                        collision = game.checkCollision(xPos, yPos, width, height, speed, 0);
                    } else {
                        collision = game.checkCollision(xPos, yPos, width, height, 0, speed);
                    }
                }
                if(collision > 0){
                    moveCounter=moveMax;
                }
            }
        }
        else if(type.equals("shield")){
            xPos = attacker.getxPos()-13;
            yPos = attacker.getyPos()-13;
        }
        else if(type.equals("gambit")){
            xPos = attacker.getxPos()-3;
            yPos = attacker.getyPos()-32;
        }
        else if(type.equals("thunderDragon")){
            if(Math.random()>0.66){
                enemy.takeDamage(attacker.getMagic(),attacker);
                enemy.getStunned(0.3);
            }
            if(enemy.canGetHurt()) {
                if (enemy.getxPos() > 700) {
                    game.movePerson(-5, 0, enemy);
                } else if (enemy.getxPos() < 680){
                    game.movePerson(5, 0, enemy);
                }
            }
        }
        else if(type.equals("thunderBoom")){
            if(!peopleHit.contains(enemy)) {
                if (enemy.getxPos() + 25 > xPos + 256-moveCounter*25 && enemy.getxPos() < xPos + 256-moveCounter*25 + moveCounter*50 && enemy.getyPos() + 25 > yPos + 256-moveCounter*25 && enemy.getyPos() < yPos + 256-moveCounter*25 + moveCounter*50) {
                    enemy.takeDamage(attacker.getMagic() * 10,attacker);
                    enemy.getStunned(1.5);
                    peopleHit.add(enemy);
                }
            }
        }
        else if(type.equals("bigSlash")){
            if(!peopleHit.contains(enemy)) {
                if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + 118 && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + 128) {
                    enemy.takeDamage(attacker.getSword()*5,attacker);
                    peopleHit.add(enemy);
                    enemy.getCut(attacker);
                }
            }
        }
        else if(type.equals("dashHit")){
            if(!peopleHit.contains(enemy)) {
                if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + 25 && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + 25) {
                    peopleHit.add(enemy);
                    if(enemy.canGetHurt() && enemy.movable()){
                        if(attacker.getDirection().equals("left")){
                            game.getGameScreen().shovePerson(-5,0,enemy);
                        }
                        else if(attacker.getDirection().equals("right")){
                            game.getGameScreen().shovePerson(5,0,enemy);
                        }
                        else if(attacker.getDirection().equals("down")){
                            game.getGameScreen().shovePerson(0,5,enemy);
                        }
                        else if(attacker.getDirection().equals("up")){
                            game.getGameScreen().shovePerson(0,-5,enemy);
                        }
                    }
                }
            }
        }
        else if(type.equals("explosion")){
            if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + 25 * width/32 && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + 22 * height/32) {
                if(!peopleHit.contains(enemy)) {
                    enemy.takeDamage(attacker.getMagic() , attacker);
                    peopleHit.add(enemy);
                }
                boolean shoved = false;
                if(xPos < enemy.getxPos()){
                    shoved = true;
                    game.getGameScreen().shovePerson(5,0,enemy);
                }
                else if(xPos > enemy.getxPos()){
                    shoved = true;
                    game.getGameScreen().shovePerson(-5,0,enemy);
                }
                if(yPos < enemy.getyPos()){
                    shoved = true;
                    game.getGameScreen().shovePerson(0,5,enemy);
                }
                else if(yPos > enemy.getyPos()){
                    shoved = true;
                    game.getGameScreen().shovePerson(0,-5,enemy);
                }
                if(!shoved){
                    enemy.getStunned(0.25);
                }
            }
        }
        else if(type.equals("breadSlashes")){
            if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                if(!peopleHit.contains(enemy)) {
                    enemy.takeDamage(attacker.getSword() , attacker);
                    peopleHit.add(enemy);
                }
                if(xPos + width/2 < enemy.getxPos()){
                    game.getGameScreen().shovePerson(5,0,enemy);
                }
                else if(xPos + width/2 > enemy.getxPos()){
                    game.getGameScreen().shovePerson(-5,0,enemy);
                }
                enemy.getStunned(0.1);
            }
        }
        else if(type.equals("breadMeteor") || type.equals("breadMissile")){
            if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                if(!peopleHit.contains(enemy)) {
                    enemy.takeDamage(attacker.getMagic()/5, attacker);
                    peopleHit.add(enemy);
                }
                enemy.getStunned(0.1);
            }
        }
        else if(type.equals("revolvingBlade")){
            if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                enemy.takeDamage(attacker.getSword()/5, attacker);
                enemy.getCut(attacker);
                shove(enemy, 6);
            }
        }
        else if(type.equals("flashingStrike")){
            if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                if(!peopleHit.contains(enemy)) {
                    enemy.takeDamage(attacker.getSword()*2, attacker);
                    peopleHit.add(enemy);
                }
//                enemy.getStunned(0.1);
            }
        }
        else if(type.equals("singularity")){
            if (enemy.getxPos() + 25 > xPos && enemy.getxPos() < xPos + width && enemy.getyPos() + 25 > yPos && enemy.getyPos() < yPos + height) {
                if(!peopleHit.contains(enemy)) {
                    enemy.takeDamage(attacker.getSword()*50, attacker);
                    enemy.getCut(attacker);
                    peopleHit.add(enemy);
                }
            }
        }
        else if(type.equals("bBOTH")){
            if (touchingPerson(enemy)) {
                if(!peopleHit.contains(enemy) && moveCounter>60) {
                    enemy.takeDamage(attacker.getSword()*10,attacker);
                    peopleHit.add(enemy);
                    enemy.getCut(attacker);
                }
                if(moveCounter>60) {
                    shove(enemy, 10);
                }
            }
        }
    }
    public void number(){
        moveCounter++;
        dealDamage();
    }
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public String getType(){
        return type;
    }
    public int getXPos(){return xPos;}
    public int getyPos(){return yPos;}
    public String getDirection(){return direction;}
    public BufferedImage image(){return image;}
    public boolean attackDeletable(){
        return moveCounter>=moveMax || (!game.getAllyList().contains(attacker) && !game.getEnemyList().contains(attacker) && player!=attacker);
    }
    public boolean attackDelete(){
        if(!game.getAllyList().contains(attacker) && !game.getEnemyList().contains(attacker) && player!=attacker && attacker!=null){
            return true;
        }
        if(type.equals("break")){
            if(moveCounter%1==0){
                game.getAttacks().add(new Attack("redSpark",attacker==player,attacker));
            }if(moveCounter>=moveMax) {
                attacker.setAttackBooster(1);
            }
        }
//        if(type.equals("thunderDragon") && moveCounter>=moveMax){
//            game.getEffects().add(new Attack(xPos,yPos+200,0,"thunderBoom",attacker));
//        }
        if(type.equals("magicBullet") && moveCounter>=moveMax){
            game.getAttacks().add(new Attack(xPos,yPos,0,"explosion",attacker));
        }
        if(type.equals("breadMeteor") && (moveCounter>=moveMax || !peopleHit.isEmpty())){
            Attack boom = new Attack(xPos+80,yPos+20,0,"explosion",attacker);
            boom.multiplySize(3);
            Attack boom2 = new Attack(xPos+width-boom.width/2,yPos+height-boom.height/2,0,"explosion",attacker);
            boom2.multiplySize(3);
            game.getAttacks().add(boom2);
            moveCounter=moveMax;
        }
        if(type.equals("breadMissile") && (moveCounter>=moveMax || !peopleHit.isEmpty())){
            Attack boom = new Attack(xPos+80,yPos+20,0,"explosion",attacker);
            boom.multiplySize(2);
            Attack boom2 = new Attack(xPos+width-boom.width/2,yPos+height-boom.height/2,0,"explosion",attacker);
            boom2.multiplySize(2);
            game.getAttacks().add(boom2);
            moveCounter=moveMax;
        }
        if(type.equals("pillarManager") && moveCounter < moveMax) {
            if (moveCounter < 85) {
                int[] xs = {0, 15, -1, 16, 20, 18, 5, -7, 20, 23, 21, 15, -10, 7, 15, 16, 15, 12, 10, 4, -35};
                double[] sizes = {1, 1.2, 1.3, 1.2, 0.8, 1, 1.1, 1.5, 1, 0.9, 0.9, 1.3, 1.5, 1, 0.8, 0.8, 1.1, 1.1, 1.3, 1.5, 2};
                int[] rotations = {0, 15, -15, 15, -15, 15, 15, 15, 20, 15, 0, 5, 15, 15, 15, 10, 15, 15, 15, 15, -15};
                Attack fireRing = new Attack(xPos, yPos, 0, "fireRing", attacker);
                fireRing.xPos += xs[moveCounter % 21] / 2;
                fireRing.yPos -= 5 * moveCounter + Math.abs(rotations[moveCounter % 21] / 5) * (sizes[moveCounter%21]*3);
                fireRing.width *= sizes[moveCounter % 21];
                fireRing.height *= sizes[moveCounter % 21];
                fireRing.rotate(rotations[moveCounter % 21] / 2);
                fireRing.rotateImages();
                game.getAttacks().add(fireRing);
            }
        }
        return moveCounter>=moveMax;
    }
    public int getMoveCounter(){return moveCounter;}
    public int getMoveMax(){return moveMax;}
    public void multiplySize(int m){
        width*=m;
        height*=m;
    }
    public void flip(){
        if(direction.equals("left")){
            direction = "right";
        }else if(direction.equals("right")){
            direction = "left";
        }
        if(type.equals("searingSlash")){
            width*=-1;
        }
        else if(type.equals("breadMissile")){
            for(int i = 0; i < images.length; i++){
                rotateImage(images[i], 180, i);
            }
        }
    }
    public void shove(Person enemy, int magnitude){
        if(enemy.canGetHurt()) {
            if (direction.equals("left")) {
                game.getGameScreen().shovePerson(-magnitude, 0, enemy);
            } else if (direction.equals("right")) {
                game.getGameScreen().shovePerson(magnitude, 0, enemy);
            } else if (direction.equals("down")) {
                game.getGameScreen().shovePerson(0, magnitude, enemy);
            } else if (direction.equals("up")) {
                game.getGameScreen().shovePerson(0, -magnitude, enemy);
            }
            else{
                double rad = Math.toRadians(degrees);
                if(degrees>0 && degrees<90){
                    game.getGameScreen().shovePerson((int)(Math.cos(rad)*magnitude), -(int)(Math.sin(rad)*magnitude), enemy);
                }
                else if(degrees>90 && degrees<180){
                    game.getGameScreen().shovePerson(-(int)(Math.cos(rad)*magnitude), -(int)(Math.sin(rad)*magnitude), enemy);
                }
                else if(degrees>180 && degrees<270){
                    game.getGameScreen().shovePerson(-(int)(Math.cos(rad)*magnitude), (int)(Math.sin(rad)*magnitude), enemy);
                }
                else if(degrees>270 && degrees<360){
                    game.getGameScreen().shovePerson((int)(Math.cos(rad)*magnitude), (int)(Math.sin(rad)*magnitude), enemy);
                }
            }
        }
    }
    public void rotateImage(int degree){
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
        image = rotated;
    }
    public void rotateImage(BufferedImage image, int degrees, int index){
        double rad = Math.toRadians(degrees);
        double sine = Math.abs(Math.sin(rad));
        double cosine = Math.abs(Math.cos(rad));
        int w = /*image.getWidth();*/(int)(image.getWidth() * cosine + image.getHeight() * sine);
        int h = /*image.getHeight();*/(int)(image.getHeight() * cosine + image.getWidth() * sine);
        BufferedImage rotated = new BufferedImage(w,h,image.getType());
        AffineTransform a = new AffineTransform();
        a.translate(w /2, h /2);
        a.rotate(rad,0,0);
        a.translate(-image.getWidth() /2, -image.getHeight() /2);
        AffineTransformOp rotate = new AffineTransformOp(a,AffineTransformOp.TYPE_BILINEAR);
        rotate.filter(image,rotated);
        images[index] = rotated;
    }
    public void redirectTrajectory(){
        int yDiff = attacker.getyPos()-attacker.getEnemy().getyPos();
        int xDiff = attacker.getxPos()-attacker.getEnemy().getxPos();
        direction = "";
        degrees = (int)Math.toDegrees(Math.atan((double) yDiff /xDiff));
        if(xDiff>0){
            degrees+=180;
        }
        if(degrees==90 || degrees==-90){
            degrees-=degrees/90;
        }
    }
    public void facePerson(Person person){
        int yDiff = yPos-person.getyPos();
        int xDiff = xPos-person.getxPos();
        direction = "";
        degrees = (int)Math.toDegrees(Math.atan((double) yDiff /xDiff));
        if(xDiff>0){
            degrees+=180;
        }
        if(degrees==90 || degrees==-90){
            degrees*=-1;
        }
    }
    public void rotateImages(){
        for(int i = 0; i < images.length; i++){
            rotateImage(images[i], degrees, i);
        }
    }
    public void rotate(int degree){
        direction = "";
        degrees += degree;
    }
    private void setDegrees(){
        if(direction.equals("left")){
            degrees = 180;
        }
        if(direction.equals("up")){
            degrees = 270;
        }
        if(direction.equals("down")){
            degrees = 90;
        }
    }
    public void setDegrees(int xDiff, int yDiff){
        direction = "";
        degrees = (int)Math.toDegrees(Math.atan((double) yDiff /xDiff));
        if((xDiff<0 && yDiff>0) || (xDiff>0 && yDiff<0)){
            degrees+=90;
        }
        if(degrees==90 || degrees==-90){
            degrees-=degrees/90;
        }
        rotateImages();
    }
    public void setDimensions(int w, int h){
        width = w;
        height = h;
    }
    public boolean equals(Object other){
        return this==other;
    }
    public String toString(){
        return type;
    }
    public boolean compareTo(Attack other){
        return this.getType().equals(other.getType());
    }
}
