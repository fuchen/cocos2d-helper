--- Node/widget selector
--  Usage: selectWidget(root, 123, 'widgetName')
fc = {}

fc.widget = function (node, ...)
    local n = select('#', ...)
    local i, k
    for i = 1, n do
        k = select(i, ...)
        if type(k) == 'number' then
            node = node:getChildByTag(k)
        else
            node = node:getChildByName(k)
        end
    end
    return node
end

fc.rate = function ()
    if LuaJavaBridge then
        LuaJavaBridge.callStaticMethod("me/fuchen/ccutil/GameButtons", "rate", {}, "()V");
    end
end

fc.share = function (msg, withScreenshot)
    local img = "score.jpg"
    local imgfile = ''
    if LuaJavaBridge then
        local function takeScreenshot(filename)
            local size = cc.Director:getInstance():getVisibleSize()
            local texture = cc.RenderTexture:create(size.width, size.height)
            texture:setPosition(size.width / 2, size.height / 2)

            texture:begin()
            cc.Director:getInstance():getRunningScene():visit()
            texture:endToLua()
            texture:saveToFile(filename, cc.IMAGE_FORMAT_JPEG)
        end

        if withScreenshot then
            --takeScreenshot(img)
            --collectgarbage()
            --imgfile = cc.FileUtils:getInstance():getWritablePath() .. img
        end

        local args = {msg, imgfile}
        LuaJavaBridge.callStaticMethod("me/fuchen/ccutil/GameButtons", "share", args, "(Ljava/lang/String;Ljava/lang/String;)V");
    end
end

fc.displayFullscreenAd = function ()
    if LuaJavaBridge then
        LuaJavaBridge.callStaticMethod("me/fuchen/ccutil/AdMobHelper", "displayFullscreenAd", {}, "()V");
    end
end

fc.lastFullscreenAdTime = 0

fc.autoFullscreenAd = function ()
    if os.time() - fc.lastFullscreenAdTime > 20 then
        fc.displayFullscreenAd()
        fc.lastFullscreenAdTime = os.time()
    end
end
