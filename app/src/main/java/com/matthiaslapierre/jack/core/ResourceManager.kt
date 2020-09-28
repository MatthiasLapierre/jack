package com.matthiaslapierre.jack.core

import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Image

interface ResourceManager: GameResources {

    var logoJumperJack: Image?
    var bgJump: Image?
    var btnSoundEnabled: Image?
    var btnMusicEnabled: Image?
    var btnFacebook: Image?
    var btnTwitter: Image?
    var btnPlay: Image?
    var btnPause: Image?
    var btnMoreGames: Image?
    var btnShop: Image?
    var btnScores: Image?
    
}