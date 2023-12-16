package com.dy.app.gameplay.loot;

import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.ui.dialog.P2pGameResultDialog;

public class GameLootCalculator {
    private long playerTrophyDiff = 0;
    private long opponentTrophyDiff = 0;
    private long playerGoldDiff = 0;
    private long playerGemDiff = 0;
    private Long playerTrophyBefore;
    private Long rivalTrophyBefore;
    public GameLootCalculator(Player player, Rival rival, int gameResult) {
        this.playerTrophyBefore = (long)player.profile.get(PlayerProfile.KEY_ELO);
        this.rivalTrophyBefore = rival.getElo();

        switch (gameResult){
            case P2pGameResultDialog.DRAW:
                evaluateDraw();
            break;
            case P2pGameResultDialog.WIN:
                evaluateWin();
            break;
            case P2pGameResultDialog.LOSE:
                evaluateLose();
            break;
            default:
                throw new RuntimeException("Invalid game result");
        }
    }

    private void evaluateDraw() {
        if(playerTrophyBefore*1.1 <= rivalTrophyBefore){
            playerTrophyDiff = ((long)(rivalTrophyBefore - playerTrophyBefore)<10?10:(long)(rivalTrophyBefore - playerTrophyBefore));
            opponentTrophyDiff = -playerTrophyDiff/2;
            playerGoldDiff = playerTrophyDiff*10;
            playerGemDiff = playerTrophyDiff*2;
        }else if(playerTrophyBefore/1.1 > rivalTrophyBefore){
            opponentTrophyDiff = ((long)(playerTrophyBefore - rivalTrophyBefore)<10?10:(long)(playerTrophyBefore - rivalTrophyBefore));
            playerTrophyDiff = -opponentTrophyDiff/2;
            playerGoldDiff = 0;
            playerGemDiff = 0;
        }
    }

    private void evaluateWin() {
        if(playerTrophyBefore*1.1 <= rivalTrophyBefore){
            playerTrophyDiff = ((long)(rivalTrophyBefore - playerTrophyBefore)<10?10:(long)(rivalTrophyBefore - playerTrophyBefore))*3/2;
            opponentTrophyDiff = -playerTrophyDiff;
            playerTrophyDiff += 3;
            playerGoldDiff = playerTrophyDiff*10*3/2;
            playerGemDiff = playerTrophyDiff*2*3/2;
        }else if(playerTrophyBefore/1.1 > rivalTrophyBefore){
            //increase trophy a little bit
            playerTrophyDiff = ((long)(playerTrophyBefore - rivalTrophyBefore)<3?3:(long)(playerTrophyBefore - rivalTrophyBefore))*3/2;
            opponentTrophyDiff = -playerTrophyDiff;
            playerGoldDiff = playerTrophyDiff*10;
            playerGemDiff = playerTrophyDiff*2;
        }
    }

    private void evaluateLose() {
        if(playerTrophyBefore*1.1 < rivalTrophyBefore){
            playerTrophyDiff = -((long)(rivalTrophyBefore - playerTrophyBefore)<10?10:(long)(rivalTrophyBefore - playerTrophyBefore));
            opponentTrophyDiff = -playerTrophyDiff;
            playerGoldDiff = playerTrophyDiff*10;
            playerGemDiff = playerTrophyDiff*2;
        }else if(playerTrophyBefore/1.1 >= rivalTrophyBefore){
            //decrease trophy a little bit
            playerTrophyDiff = -((long)(playerTrophyBefore - rivalTrophyBefore)<3?3:(long)(playerTrophyBefore - rivalTrophyBefore));
            opponentTrophyDiff = -playerTrophyDiff;
            playerGoldDiff = playerTrophyDiff*10;
            playerGemDiff = playerTrophyDiff*2;
        }
    }

    public long getPlayerTrophyDiff() {
        return playerTrophyDiff;
    }

    public long getOpponentTrophyDiff() {
        return opponentTrophyDiff;
    }

    public long getPlayerGoldDiff() {
        return playerGoldDiff;
    }

    public long getPlayerGemDiff() {
        return playerGemDiff;
    }
}
