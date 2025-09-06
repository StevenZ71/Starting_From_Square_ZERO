public class Quest {
    private boolean questDone;
    private int numberOfEnemies;
    private int enemiesDefeated;
    private int levelNeeded;
    private String questName;
    private Person player = game.getPlayer();
    private boolean specialQuest;
    private String description;
    private static Game game;
    public Quest(String name){//special quests
        questName = name;
        specialQuest = true;
        setDescription();
        check();
    }
    private void check(){
        if(questName.equals("Rss test")){
            numberOfEnemies = 5;
            levelNeeded = 0;
        }
        if(questName.equals("An errand")){
            numberOfEnemies = 10;
            levelNeeded = 0;
        }
        if(numberOfEnemies!=0 || levelNeeded!=0){
            specialQuest = false;
        }
    }
    public static void setGame(Game g){
        game = g;
    }
    public String getName(){return questName;}
    public void enemyDefeated(){
        enemiesDefeated++;
    }
    public void checkQuest(){
        if(!questDone) {
            if(specialQuest){
                if(questName.equals("Find a ribbon")){
                    checkBagRewards("ribbon");
                }
                if(questName.equals("Find some bread")){
                    checkBagRewards("bread");
                }
            }
            else{
                if(enemiesDefeated>=numberOfEnemies && player.getLevel() >= levelNeeded){
                    giveRewards();
                }
            }
        }
    }
    private void checkBagRewards(String item){
        for(int i = 0; i < game.getInventory().size(); i++){
            if (game.getInventory().get(i).equals(item)){
                giveRewards();
            }
        }
    }
    private boolean checkBag(String item){
        for(int i = 0; i < game.getInventory().size(); i++){
            if (game.getInventory().get(i).equals(item)){
                return true;
            }
        }
        return false;
    }
    private void giveRewards(){
        if(questName.equals("Rss test")){
            game.earnMoney(10);
            player.getXp(50);
        }
        if(questName.equals("An errand")){
            game.earnMoney(15);
            player.getXp(150);
        }
        questDone = true;
    }
    public boolean deleteQuest(){
        return questDone;
    }
    public String getDescription(){
        return description;
    }
    public String getRequirements(){
        if(questName.equals("Find a ribbon")){
            if(checkBag("ribbon")){
                return "Ribbon found";
            }
            else{
                return "Ribbon not found";
            }
        }
        if(questName.equals("Find some bread")){
            if(checkBag("bread")){
                return "You have bread";
            }
            else{
                return "You have no bread";
            }

        }
        return enemiesDefeated + "/" + numberOfEnemies + " enemies defeated";
    }
    private void setDescription(){
        if(questName.equals("Find a ribbon")){
            description = "You have been tasked to find a ribbon that was lost. If you find it, you will obtain the power of fire.";
        }
        else  if(questName.equals("Rss test")){
            description = "You have been tasked to defeat five red squares by a Rss Leader.";
        }
        else  if(questName.equals("An errand")){
            description = "You have been tasked to defeat ten red squares by a Rss Leader.";
        }
        else if (questName.equals("Find some bread")){
            description = "A duck tells you he has been craving bread. He may give you something in return if you get him it...";
        }
    }
    public String toString(){return questName;}
    public boolean equals(Object other){return other==this;}
}
