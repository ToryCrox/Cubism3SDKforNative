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
    _changeModel(false),
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

    delete _viewMatrix;
    delete _deviceToScreen;
    delete _touchManager;
}

void LAppView::Initialize()
{
}

void LAppView::InitializeShader()
{
    _programId = LAppDelegate::GetInstance()->CreateShader();
}

void LAppView::InitializeSprite()
{
    int width = LAppDelegate::GetInstance()->GetWindowWidth();
    int height = LAppDelegate::GetInstance()->GetWindowHeight();

    // 画面全体を覆うサイズ
    float x = width * 0.5f;
    float y = height * 0.5f;

    if (_renderSprite == NULL)
    {
        _renderSprite = new LAppSprite(x, y, width, height, 0, _programId);
    }
    else
    {
        _renderSprite->ReSize(x, y, width, height);
    }
}

void LAppView::Render()
{

    LAppLive2DManager* Live2DManager = LAppLive2DManager::GetInstance();

    // 各モデルが持つ描画ターゲットをテクスチャとする場合 
    if (_renderTarget == SelectTarget_ModelFrameBuffer && _renderSprite)
    {
        const GLfloat uvVertex[] =
        {
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
        };

        for (csmUint32 i = 0; i < Live2DManager->GetModelNum(); i++)
        {
            float alpha = GetSpriteAlpha(i); // サンプルとしてαに適当な差をつける 
            _renderSprite->SetColor(1.0f, 1.0f, 1.0f, alpha);

            LAppModel *model = Live2DManager->GetModel(i);
            if (model)
            {
                _renderSprite->RenderImmidiate(model->GetRenderBuffer().GetColorBuffer(), uvVertex);
            }
        }
    }
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

    LAppLive2DManager::GetInstance()->OnDrag(viewX, viewY);
}

void LAppView::OnTouchesBegan(float x1, float y1, float x2, float y2) {
    _touchManager->TouchesBegan(x1, y1, x2, y2);
    float viewX = transformViewX(_touchManager->GetX());
    float viewY = transformViewY(_touchManager->GetY());

    LAppLive2DManager::GetInstance()->OnDrag(viewX, viewY);
}

void LAppView::OnTouchesMoved(float x1, float y1, float x2, float y2) {
    _touchManager->TouchesMoved(x1, y1, x2, y2);

    float dx = _touchManager->GetDeltaX() * getDeviceToScreen()->GetScaleX();
    float dy = _touchManager->GetDeltaY() * getDeviceToScreen()->GetScaleY();
    float cx = transformScreenX(_touchManager->GetX()) * _touchManager->GetScale();
    float cy = transformScreenY(_touchManager->GetY()) * _touchManager->GetScale();

    float scale = _touchManager->GetScale();
    getViewMatrix()->AdjustTranslate(dx, -dy);
    getViewMatrix()->AdjustScale(0.0f, 0.0f, scale);
    LAppPal::PrintLog("[APP]OnTouchesMoved dx:%.6f, %.6f", dx, dy);

    float viewX = transformViewX(_touchManager->GetX());
    float viewY = transformViewY(_touchManager->GetY());
    LAppLive2DManager::GetInstance()->OnDrag(viewX, viewY);
}

void LAppView::OnTouchesEnded(float pointX, float pointY)
{
    // タッチ終了
    LAppLive2DManager* live2DManager = LAppLive2DManager::GetInstance();
    live2DManager->OnDrag(0.0f, 0.0f);
    {
        if (DebugLogEnable) {
            LAppPal::PrintLog("[APP]touchesEnded x:%.2f y:%.2f", _touchManager->GetX(),
                    _touchManager->GetY());
        }

        // シングルタップ
        if (DebugLogEnable) {
            LAppPal::PrintLog("[APP]touchesEnded screen:%s ",
                    LAppPal::GetArrayString(getDeviceToScreen()->GetArray()));
        }
        //float x = deviceToScreen->TransformX(_touchManager->GetX()); // 論理座標変換した座標を取得。
        //float y = deviceToScreen->TransformY(_touchManager->GetY()); // 論理座標変換した座標を取得。
        float x = transformScreenX(_touchManager->GetX()); // 論理座標変換した座標を取得。
        float y = transformScreenX(_touchManager->GetY()); // 論理座標変換した座標を取得。
        if (DebugLogEnable) {
            LAppPal::PrintLog("[APP]touchesEnded x:%.2f y:%.2f", x, y);
        }
        live2DManager->OnTap(x, y);

    }
}
Csm::CubismMatrix44* LAppView::getDeviceToScreen(){
    return LAppLive2DManager::GetInstance()->getDeviceToScreen();
}

Csm::CubismViewMatrix* LAppView::getViewMatrix(){
    return LAppLive2DManager::GetInstance()->getViewMatrix();
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

void LAppView::PreModelDraw(LAppModel &refModel)
{
    // 別のレンダリングターゲットへ向けて描画する場合の使用するフレームバッファ 
    Csm::Rendering::CubismOffscreenFrame_OpenGLES2* useTarget = NULL;

    if (_renderTarget != SelectTarget_None)
    {// 別のレンダリングターゲットへ向けて描画する場合 

        // 使用するターゲット 
        useTarget = (_renderTarget == SelectTarget_ViewFrameBuffer) ? &_renderBuffer : &refModel.GetRenderBuffer();

        if (!useTarget->IsValid())
        {// 描画ターゲット内部未作成の場合はここで作成 
            int width = LAppDelegate::GetInstance()->GetWindowWidth();
            int height = LAppDelegate::GetInstance()->GetWindowHeight();

            // モデル描画キャンバス 
            useTarget->CreateOffscreenFrame(static_cast<csmUint32>(width), static_cast<csmUint32>(height));
        }

        // レンダリング開始 
        useTarget->BeginDraw();
        useTarget->Clear(_clearColor[0], _clearColor[1], _clearColor[2], _clearColor[3]); // 背景クリアカラー 
    }
}

void LAppView::PostModelDraw(LAppModel &refModel)
{
    // 別のレンダリングターゲットへ向けて描画する場合の使用するフレームバッファ 
    Csm::Rendering::CubismOffscreenFrame_OpenGLES2* useTarget = NULL;

    if (_renderTarget != SelectTarget_None)
    {// 別のレンダリングターゲットへ向けて描画する場合 

        // 使用するターゲット 
        useTarget = (_renderTarget == SelectTarget_ViewFrameBuffer) ? &_renderBuffer : &refModel.GetRenderBuffer();

        // レンダリング終了 
        useTarget->EndDraw();

        // LAppViewの持つフレームバッファを使うなら、スプライトへの描画はここ 
        if (_renderTarget == SelectTarget_ViewFrameBuffer && _renderSprite)
        {
            const GLfloat uvVertex[] =
            {
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
            };

            _renderSprite->SetColor(1.0f, 1.0f, 1.0f, GetSpriteAlpha(0));
            _renderSprite->RenderImmidiate(useTarget->GetColorBuffer(), uvVertex);
        }
    }
}

void LAppView::SwitchRenderingTarget(SelectTarget targetType)
{
    _renderTarget = targetType;
}

void LAppView::SetRenderTargetClearColor(float r, float g, float b)
{
    _clearColor[0] = r;
    _clearColor[1] = g;
    _clearColor[2] = b;
}

float LAppView::GetSpriteAlpha(int assign) const
{
    // assignの数値に応じて適当に決定 
    float alpha = 0.25f + static_cast<float>(assign) * 0.5f; // サンプルとしてαに適当な差をつける 
    if (alpha > 1.0f)
    {
        alpha = 1.0f;
    }
    if (alpha < 0.1f)
    {
        alpha = 0.1f;
    }

    return alpha;
}

