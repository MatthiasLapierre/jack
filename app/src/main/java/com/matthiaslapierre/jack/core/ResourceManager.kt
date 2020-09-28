package com.matthiaslapierre.jack.core

import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Image

interface ResourceManager: GameResources {

    var logoJumperJack: Image?
    var bgJump: Image?
    var btnSound: Image?
    var btnSoundPressed: Image?
    var btnMusic: Image?
    var btnMusicPressed: Image?
    var btnFacebook: Image?
    var btnFacebookPressed: Image?
    var btnTwitter: Image?
    var btnTwitterPressed: Image?
    var btnPlay: Image?
    var btnPlayPressed: Image?
    var btnPause: Image?
    var btnMoreGames: Image?
    var btnMoreGamesPressed: Image?
    var btnShop: Image?
    var btnScores: Image?
    var btnScoresPressed: Image?
    
}