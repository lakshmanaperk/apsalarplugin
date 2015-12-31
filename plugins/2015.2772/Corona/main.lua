local Apsalar = require "plugin.apsalar"
local widget = require "widget"
local defaultFontSize = 12
local defaultFill = {default = {0.5, 0.5, 0.5, 0.8}, over = {0.5, 0.5, 0.5, 1}}
local defaultLabelColor = {default = {0, 0, 0, 1}, over = {0, 0, 0, 1}}
local appkey = "perkmobile"
local appsecret = "ycoJRRsZ"
local botlabel = "BOTTOM BUTTON PRESSED"
local toplabel = "TOP BUTTON PRESSED"
local welcomeText = display.newText({
    text = "Corona Apsalar Demo",
    x = display.contentWidth / 2,
    y = 10,
    fontSize = 12
})

local getBottomAdButton = widget.newButton({
    shape = "roundedRect",
    x = display.contentWidth / 2,
    y = display.contentHeight / 2 + 50,
    cornerRadius = 2,
    label = "BOTTOM",
    fillColor = defaultFill,
    labelColor = defaultLabelColor,
    onRelease = function()
      Apsalar.logEvent(botlabel)
    end,
    fontSize = defaultFontSize,
    height = 25
})

local getTopButton = widget.newButton({
    shape = "roundedRect",
    x = display.contentWidth / 2,
    y = display.contentHeight / 2 - 5,
    cornerRadius = 2,
    label = "TOP",
    fillColor = defaultFill,
    labelColor = defaultLabelColor,
    onRelease = function()
      Apsalar.logEvent(toplabel)
    end,
    fontSize = defaultFontSize,
    height = 25
})

Apsalar.init(appkey,appsecret)


