﻿/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include <CubismFramework.hpp>
#include <Math/CubismMatrix44.hpp>
#include <Type/csmVector.hpp>
#include <Math/CubismViewMatrix.hpp>
#include <string>

class LAppModel;

/**
* @brief サンプルアプリケーションにおいてCubismModelを管理するクラス<br>
*         モデル生成と破棄、タップイベントの処理、モデル切り替えを行う。
*
*/
class LAppLive2DManager
{

public:

    /**
    * @brief  コンストラクタ
    */
    LAppLive2DManager();

    /**
    * @brief  デストラクタ
    */
    virtual ~LAppLive2DManager();

    /**
    * @brief   現在のシーンで保持しているモデルを返す
    *
    * @param[in]   no  モデルリストのインデックス値
    * @return      モデルのインスタンスを返す。インデックス値が範囲外の場合はNULLを返す。
    */
    LAppModel* GetModel(Csm::csmUint32 no) const;

    /**
    * @brief   現在のシーンで保持しているすべてのモデルを解放する
    *
    */
    void ReleaseAllModel();

    /**
    * @brief   画面をドラッグしたときの処理
    *
    * @param[in]   x   画面のX座標
    * @param[in]   y   画面のY座標
    */
    void OnDrag(Csm::csmFloat32 x, Csm::csmFloat32 y) const;

    /**
    * @brief   画面をタップしたときの処理
    *
    * @param[in]   x   画面のX座標
    * @param[in]   y   画面のY座標
    */
    void OnTap(Csm::csmFloat32 x, Csm::csmFloat32 y);

    /**
    * @brief   画面を更新するときの処理
    *          モデルの更新処理および描画処理を行う
    */
    void OnUpdate();

    /**
    * @brief   次のシーンに切り替える<br>
    *           サンプルアプリケーションではモデルセットの切り替えを行う。
    */
    //void NextScene();

    /**
    * @brief   シーンを切り替える<br>
    *           サンプルアプリケーションではモデルセットの切り替えを行う。
    */
    //void ChangeScene(Csm::csmInt32 index);


    /**
    * @brief   シーンインデックスの取得
    * @return  シーンインデックスを返す
    */
    //Csm::csmInt32 GetSceneIndex() { return _sceneIndex; }

    /**
     * @brief   モデル個数を得る
     * @return  所持モデル個数
     */
    Csm::csmUint32 GetModelNum() const;

    void initialMatrix();
    void setUpView(int width, int height);

    Csm::CubismMatrix44* getDeviceToScreen(){
        return _deviceToScreen;
    }

    Csm::CubismViewMatrix* getViewMatrix(){
        return _viewMatrix;
    }

    void LoadModel(std::string modePath);

    void ReLoadModel(const std::string modePath, Csm::csmFloat32 *matrixArr);

    void startMotion(const Csm::csmChar *filePath,
                     Csm::csmFloat32 fadeInSeconds,
                     Csm::csmFloat32 fadeOutSeconds);

    void startLipSyncMotion(const Csm::csmChar *filePath,
                     Csm::csmFloat32 fadeInSeconds,
                     Csm::csmFloat32 fadeOutSeconds);

    float *getViewMatrixArray();

    void tryLoadModel();

    void setModelScale(float modelScale){
        this->_modelScale = modelScale;
    }

    void setMatrixTr(Live2D::Cubism::Framework::csmFloat32 *matrixArr);

    void setHandlerId(int handlerId){
        _handlerId = handlerId;
    }

private:

    Csm::CubismMatrix44* _deviceToScreen;    ///< デバイスからスクリーンへの行列
    Csm::CubismViewMatrix*        _viewMatrix; ///< モデル描画に用いるView行列
    Csm::csmVector<LAppModel*>  _models; ///< モデルインスタンスのコンテナ
    //LAppModel*  _model; ///< モデルインスタンスのコンテナ
    //Csm::csmInt32               _sceneIndex; ///< 表示するシーンのインデックス値

    std::string _modelPath;
    bool _changeModel;
    int _viewWidth;
    int _viewHeight;

    float _modelScale = 1.0f;
    int _handlerId;
};
