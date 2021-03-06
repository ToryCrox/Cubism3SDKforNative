﻿/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <Math/CubismMatrix44.hpp>
#include <Math/CubismViewMatrix.hpp>
#include "CubismFramework.hpp"
#include "LAppLive2DManager.hpp"
#include <Rendering/OpenGL/CubismOffscreenSurface_OpenGLES2.hpp>

class TouchManager;
class LAppSprite;
class LAppModel;

/**
* @brief 描画クラス
*/
class LAppView
{
public:

    /**
     * @brief LAppModelのレンダリング先
     */
    enum SelectTarget
    {
        SelectTarget_None,                ///< デフォルトのフレームバッファにレンダリング 
        SelectTarget_ModelFrameBuffer,    ///< LAppModelが各自持つフレームバッファにレンダリング 
        SelectTarget_ViewFrameBuffer,     ///< LAppViewの持つフレームバッファにレンダリング 
    };

    /**
    * @brief コンストラクタ
    */
    LAppView();
    
    /**
    * @brief デストラクタ
    */
    ~LAppView();

    /**
    * @brief 初期化する。
    */
    void Initialize();

    /**
    * @brief タッチされたときに呼ばれる。
    *
    * @param[in]       pointX            スクリーンX座標
    * @param[in]       pointY            スクリーンY座標
    */
    void OnTouchesBegan(float pointX, float pointY);

    /**
    * @brief タッチしているときにポインタが動いたら呼ばれる。
    *
    * @param[in]       pointX            スクリーンX座標
    * @param[in]       pointY            スクリーンY座標
    */
    void OnTouchesMoved(float pointX, float pointY);

    /**
    * @brief タッチが終了したら呼ばれる。
    *
    * @param[in]       pointX            スクリーンX座標
    * @param[in]       pointY            スクリーンY座標
    */
    void OnTouchesEnded(float pointX, float pointY);

    /**
    * @brief X座標をView座標に変換する。
    *
    * @param[in]       deviceX            デバイスX座標
    */
    float transformViewX(float deviceX);

    /**
    * @brief Y座標をView座標に変換する。
    *
    * @param[in]       deviceY            デバイスY座標
    */
    float transformViewY(float deviceY);

    /**
    * @brief X座標をScreen座標に変換する。
    *
    * @param[in]       deviceX            デバイスX座標
    */
    float transformScreenX(float deviceX);

    /**
    * @brief Y座標をScreen座標に変換する。
    *
    * @param[in]       deviceY            デバイスY座標
    */
    float transformScreenY(float deviceY);

    void OnTouchesBegan(float d, float d1, float d2, float d3);

    void OnTouchesMoved(float d, float d1, float d2, float d3);

    void setLive2dManager(LAppLive2DManager* live2DManager);

private:
    TouchManager* _touchManager;                 ///< タッチマネージャー
    //Csm::CubismMatrix44* _deviceToScreen;    ///< デバイスからスクリーンへの行列
    //Csm::CubismViewMatrix* _viewMatrix;      ///< viewMatrix
    GLuint _programId;                       ///< シェーダID
    //LAppSprite* _back;                       ///< 背景画像
    //LAppSprite* _gear;                       ///< ギア画像
    //LAppSprite* _power;                      ///< 電源画像
    //bool _changeModel;                       ///< モデル切り替えフラグ

    // レンダリング先を別ターゲットにする方式の場合に使用 
    LAppSprite* _renderSprite;                                      ///< モードによっては_renderBufferのテクスチャを描画 
    Csm::Rendering::CubismOffscreenFrame_OpenGLES2 _renderBuffer;   ///< モードによってはCubismモデル結果をこっちにレンダリング 
    SelectTarget _renderTarget;     ///< レンダリング先の選択肢 
    float _clearColor[4];           ///< レンダリングターゲットのクリアカラー 
    Live2D::Cubism::Framework::CubismMatrix44 *getDeviceToScreen();

    Live2D::Cubism::Framework::CubismViewMatrix *getViewMatrix();

    LAppLive2DManager* _live2dManager;
};