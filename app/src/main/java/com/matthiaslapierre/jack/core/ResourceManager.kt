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
    var btnPausePressed: Image?
    var btnMoreGames: Image?
    var btnMoreGamesPressed: Image?
    var btnShop: Image?
    var btnScores: Image?
    var btnScoresPressed: Image?
    var bgTop: Image?
    var candyIndicator: Image?
    var digits: Array<Image>?
    var bgGate: Image?
    var bgGraveyardBottom: Image?
    var bgGraveyardTop: Image?
    var bgGraveyardFar: Image?
    var bgHills1: Image?
    var bgHills2: Image?
    var bgHills3: Image?
    var bgHills4: Image?
    var bgHills5: Image?
}