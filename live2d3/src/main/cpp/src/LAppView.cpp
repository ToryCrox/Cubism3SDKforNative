/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#include "LAppView.hpp"
#include <math.h>
#include <string>
#include "LAppPal.hpp"
#include "LAppDelegate.hpp"
#include "LAppLive2DManager.hpp"
#include "LAppTextureManager.hpp"
#include "LAppDefine.hpp"
#include "TouchManager.hpp"
#include "LAppSprite.hpp"
#include "LAppModel.hpp"

#include <Rendering/OpenGL/CubismOffscreenSurface_OpenGLES2.hpp>
#include <Rendering/OpenGL/CubismRenderer_OpenGLES2.hpp>

#include "../JniBridgeC.hpp"

using namespace std;
using namespace LAppDefine;

LAppView::LAppView():
    _programId(0),
   // _gear(NULL),
    //_power(NULL),
   // _changeModel(false),
    _renderSprite(NULL),
    _renderTarget(SelectTarget_None)
{
    _clearColor[0] = 1.0f;
    _clearColor[1] = 1.0f;
    _clearColor[2] = 1.0f;
    _clearColor[3] = 0.0f;

    // タッチ関係のイベント管理
    _touchManager = new TouchManager();

}

LAppView::~LAppView()
{
    _renderBuffer.DestroyOffscreenFrame();
    delete _renderSprite;

    //delete _viewMatrix;
    //delete _deviceToScreen;
    delete _touchManager;
}

void LAppView::Initialize()
{
}

void LAppView::OnTouchesBegan(float pointX, float pointY)
{
    _touchManager->TouchesBegan(pointX, pointY);
}

void LAppView::OnTouchesMoved(float pointX, float pointY)
{
    _touchManager->TouchesMoved(pointX, pointY);
    float viewX = transformViewX(_touchManager->GetX());
    float viewY = transformViewY(_touchManager->GetY());

    _live2dManager->OnDrag(viewX, viewY);
}

void LAppView::OnTouchesBegan(float x1, float y1, float x2, float y2) {
    _touchManager->TouchesBegan(x1, y1, x2, y2);
    float viewX = transformViewX(_touchManager->GetX());
    float viewY = transformViewY(_touchManager->GetY());

    _live2dManager->OnDrag(viewX, viewY);
}

void LAppView::OnTouchesMoved(float x1, float y1, float x2, float y2) {
    _touchManager->TouchesMoved(x1, y1, x2, y2);

    float dx = _touchManager->GetDeltaX() * getDeviceToScreen()->GetScaleX() / getViewMatrix()->GetScaleX();
    float dy = _touchManager->GetDeltaY() * getDeviceToScreen()->GetScaleY() / getViewMatrix()->GetScaleY();
    float cx = transformScreenX(_touchManager->GetX()) * _touchManager->GetScale();
    float cy = transformScreenY(_touchManager->GetY()) * _touchManager->GetScale();

    float scale = _touchManager->GetScale();
    getViewMatrix()->AdjustScale(0.0f, 0.0f, scale);
    getViewMatrix()->AdjustTranslate(dx, dy);
    LAppPal::PrintLog("[APP]OnTouchesMoved dx:%.6f, %.6f", dx, dy);

    float viewX = transformViewX(_touchManager->GetX());
    float viewY = transformViewY(_touchManager->GetY());
    _live2dManager->OnDrag(viewX, viewY);
}

void LAppView::OnTouchesEnded(float pointX, float pointY)
{
    // タッチ終了
    LAppLive2DManager* live2DManager = _live2dManager;
    live2DManager->OnDrag(0.0f, 0.0f);
    if (_touchManager->IsSingleTouch()){
        if (DebugLogEnable) {
            LAppPal::PrintLog("[APP]touchesEnded x:%.2f y:%.2f", _touchManager->GetX(),
                    _touchManager->GetY());
        }

        // シングルタップ
        float x = transformViewX(_touchManager->GetX()); // 論理座標変換した座標を取得。
        float y = transformViewY(_touchManager->GetY()); // 論理座標変換した座標を取得。
        if (DebugLogEnable) {
            LAppPal::PrintLog("[APP]touchesEnded x:%.2f y:%.2f", x, y);
        }
        live2DManager->OnTap(x, y);

    }
}
Csm::CubismMatrix44* LAppView::getDeviceToScreen(){
    return _live2dManager->getDeviceToScreen();
}

Csm::CubismViewMatrix* LAppView::getViewMatrix(){
    return _live2dManager->getViewMatrix();
}


float LAppView::transformViewX(float deviceX)
{
    float screenX = getDeviceToScreen()->TransformX(deviceX); // 論理座標変換した座標を取得。
    return getViewMatrix()->InvertTransformX(screenX); // 拡大、縮小、移動後の値。
}

float LAppView::transformViewY(float deviceY)
{
    float screenY = getDeviceToScreen()->TransformY(deviceY); // 論理座標変換した座標を取得。
    return getViewMatrix()->InvertTransformY(screenY); // 拡大、縮小、移動後の値。
}

float LAppView::transformScreenX(float deviceX)
{
    return getDeviceToScreen()->TransformX(deviceX);
}

float LAppView::transformScreenY(float deviceY)
{
    return getDeviceToScreen()->TransformY(deviceY);
}

void LAppView::setLive2dManager(LAppLive2DManager *live2DManager) {
    _live2dManager = live2DManager;
}

