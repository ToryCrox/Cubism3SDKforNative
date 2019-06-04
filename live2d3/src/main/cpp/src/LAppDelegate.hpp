/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <Type/csmString.hpp>
#include <string>
#include "LAppAllocator.hpp"
#include "LAppLive2DManager.hpp"

class LAppView;
class LAppTextureManager;

/**
* @brief   アプリケーションクラス。
*   Cubism3の管理を行う。
*/
class LAppDelegate
{
public:

    /**
        * @brief   クラスのインスタンス（シングルトン）を返す。<br>
        *           インスタンスが生成されていない場合は内部でインスタンを生成する。
        *
        * @return  クラスのインスタンス
        */
    static LAppDelegate* GetInstance(int handlerId);

    static LAppDelegate* GetDelegate(int handlerId);

    /**
    * @brief   クラスのインスタンス（シングルトン）を解放する。
    *
    */
    void ReleaseInstance();

    /**
    * @brief JavaのActivityのOnStart()のコールバック関数。
    */
    void OnStart();

    /**
    * @brief JavaのActivityのOnStop()のコールバック関数。
    */
    void OnStop();

    /**
    * @brief JavaのActivityのOnDestroy()のコールバック関数。
    */
    void OnDestroy();

    /**
    * @brief   JavaのGLSurfaceviewのOnSurfaceCreate()のコールバック関数。
    */
    void OnSurfaceCreate();

    /**
     * @brief JavaのGLSurfaceviewのOnSurfaceChanged()のコールバック関数。
     * @param width
     * @param height
     */
    void OnSurfaceChanged(int width, int height);

    /**
    * @brief   実行処理。
    */
    void Run();

    /**
    * @brief Touch開始。
    *
    * @param[in] x x座標
    * @param[in] y x座標
    */
    void OnTouchBegan(float x, float y);

    /**
    * @brief Touch終了。
    *
    * @param[in] x x座標
    * @param[in] y x座標
    */
    void OnTouchEnded(float x, float y);

    /**
    * @brief Touch移動。
    *
    * @param[in] x x座標
    * @param[in] y x座標
    */
    void OnTouchMoved(float x, float y);

    /**
    * @brief　シェーダーを登録する。
    */
    GLuint CreateShader();

    /**
    * @brief テクスチャマネージャーの取得
    */
    //LAppTextureManager* GetTextureManager() { return _textureManager; }

    /**
    * @brief ウインドウ幅の設定
    */
    int GetWindowWidth() { return _width; }

    /**
    * @brief ウインドウ高さの取得
    */
    int GetWindowHeight() { return _height; }

    /**
    * @brief   アプリケーションを非アクティブにする。
    */
    void DeActivateApp() { _isActive = false; }

    /**
    * @brief   View情報を取得する。
    */
    LAppView* GetView() { return _view; }

    void OnTouchBegan(float d, float d1, float d2, float d3);

    void OnTouchMoved(float d, float d1, float d2, float d3);

    void LoadModel(
            const std::string modelPath, Csm::csmFloat32* matrixArr);

    void startMotion(const char *string, float d, float d1);

    float *getViewMatrixArray();

    void startLipSyncMotion(const char *string, float d, float d1);

    int GetHandlerId() {
        return _handlerId;
    }

private:
    /**
    * @brief   コンストラクタ
    */
    //LAppDelegate();

    LAppDelegate(int handlerId);

    /**
    * @brief   デストラクタ
    */
    ~LAppDelegate();

    /**
    * @brief   Cubism3の初期化
    */
    void InitializeCubism();

    LAppAllocator _cubismAllocator;              ///< Cubism3 Allocator
    Csm::CubismFramework::Option _cubismOption;  ///< Cubism3 Option
    //LAppTextureManager* _textureManager;         ///< テクスチャマネージャー
    LAppView* _view;                             ///< View情報
    int _width;                                  ///< Windowの幅
    int _height;                                 ///< windowの高さ
    //int _SceneIndex;                             ///< モデルシーンインデックス
    bool _captured;                              ///< クリックしているか
    bool _isActive;                              ///< アプリがアクティブ状態なのか
    //float _mouseY;                               ///< マウスY座標
    //float _mouseX;                               ///< マウスX座標
    std::string _modelPath;
    int _current_id;

    LAppLive2DManager* _l2dManager;
    int _handlerId;
};
