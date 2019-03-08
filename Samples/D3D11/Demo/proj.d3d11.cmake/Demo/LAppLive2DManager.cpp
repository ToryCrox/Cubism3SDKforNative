﻿/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */


#include "LAppLive2DManager.hpp"
#include <string>
#include <Rendering/D3D11/CubismRenderer_D3D11.hpp>
#include "LAppPal.hpp"
#include "LAppDefine.hpp"
#include "LAppDelegate.hpp"
#include "LAppModel.hpp"
#include "LAppView.hpp"

using namespace Csm;
using namespace LAppDefine;
using namespace std;

namespace {
    LAppLive2DManager* s_instance = NULL;
}

LAppLive2DManager* LAppLive2DManager::GetInstance()
{
    if (s_instance == NULL)
    {
        s_instance = new LAppLive2DManager();
    }

    return s_instance;
}

void LAppLive2DManager::ReleaseInstance()
{
    if (s_instance != NULL)
    {
        delete s_instance;
    }

    s_instance = NULL;
}

LAppLive2DManager::LAppLive2DManager()
    : _viewMatrix(NULL)
    , _sceneIndex(0)
{
    ChangeScene(_sceneIndex);
}

LAppLive2DManager::~LAppLive2DManager()
{
    ReleaseAllModel();
    // カウンターを待たず速攻破棄 
    for (int i = _releaseModel.GetSize() - 1; i >= 0; i--)
    {
        if (_releaseModel[i]._model)
        {
            delete _releaseModel[i]._model;
        }
    }
    _releaseModel.Clear();

    CubismFramework::Dispose();
}

void LAppLive2DManager::ReleaseAllModel()
{
    for (csmUint32 i = 0; i < _models.GetSize(); i++)
    {
        //delete _models[i]; ここでは消さない 

        // 削除予定の印 
        _models[i]->DeleteMark();

        // 2フレーム後削除 
        ReleaseModel rel;
        rel._model = _models[i];
        rel._counter = 2;
        _releaseModel.PushBack(rel);
    }

    _models.Clear();
}

LAppModel* LAppLive2DManager::GetModel(csmUint32 no) const
{
    if (no < _models.GetSize())
    {
        return _models[no];
    }

    return NULL;
}

void LAppLive2DManager::OnDrag(csmFloat32 x, csmFloat32 y) const
{
    for (csmUint32 i = 0; i < _models.GetSize(); i++)
    {
        LAppModel* model = GetModel(i);

        model->SetDragging(x, y);
    }
}

void LAppLive2DManager::OnTap(csmFloat32 x, csmFloat32 y)
{
    if (DebugLogEnable)
    {
        LAppPal::PrintLog("[APP]tap point: {x:%.2f y:%.2f}", x, y);
    }

    for (csmUint32 i = 0; i < _models.GetSize(); i++)
    {
        if (_models[i]->HitTest(HitAreaNameHead, x, y))
        {
            if (DebugLogEnable) 
            {
                LAppPal::PrintLog("[APP]hit area: [%s]", HitAreaNameHead);
            }
            _models[i]->SetRandomExpression();
        }
        else if (_models[i]->HitTest(HitAreaNameBody, x, y))
        {
            if (DebugLogEnable)
            {
                LAppPal::PrintLog("[APP]hit area: [%s]", HitAreaNameBody);
            }
            _models[i]->StartRandomMotion(MotionGroupTapBody, PriorityNormal);
        }
    }
}

void LAppLive2DManager::OnUpdate() const
{
    int windowWidth, windowHeight;
    LAppDelegate::GetInstance()->GetClientSize(windowWidth, windowHeight);

    // 投影用マトリックス 
    CubismMatrix44 projection = CubismMatrix44();
    if (windowWidth != 0 && windowHeight != 0)
    {
        projection.Scale(1.0f, static_cast<float>(windowWidth) / static_cast<float>(windowHeight));
    }

    // 必要があればここで乗算 
    if (_viewMatrix != NULL)
    {
        projection.MultiplyByMatrix(_viewMatrix);
    }

    // D3D11 フレーム先頭処理 
    // 各フレームでの、Cubismの処理前にコール 
    Rendering::CubismRenderer_D3D11::StartFrame(LAppDelegate::GetInstance()->GetD3dDevice(), LAppDelegate::GetInstance()->GetD3dContext(), windowWidth, windowHeight);

    const CubismMatrix44    saveProjection = projection;
    const csmUint32 modelCount = _models.GetSize();
    for (csmUint32 i = 0; i < modelCount; ++i)
    {
        LAppModel* model = GetModel(i);
        projection = saveProjection;

        // モデル1体描画前コール 
        LAppDelegate::GetInstance()->GetView()->PreModelDraw(*model);

        // Cubismモデルの描画 
        model->Update();
        model->Draw(projection);///< 参照渡しなのでprojectionは変質する

        // モデル1体描画後コール 
        LAppDelegate::GetInstance()->GetView()->PostModelDraw(*model);
    }

    // D3D11 フレーム終了処理 
    // 各フレームでの、Cubismの処理後にコール 
    Rendering::CubismRenderer_D3D11::EndFrame(LAppDelegate::GetInstance()->GetD3dDevice());
}

void LAppLive2DManager::NextScene()
{
    csmInt32 no = (_sceneIndex + 1) % ModelDirSize;
    ChangeScene(no);
}

void LAppLive2DManager::ChangeScene(Csm::csmInt32 index)
{
    _sceneIndex = index;
    if (DebugLogEnable)
    {
        LAppPal::PrintLog("[APP]model index: %d", _sceneIndex);
    }

    // ModelDir[]に保持したディレクトリ名から
    // model3.jsonのパスを決定する.
    // ディレクトリ名とmodel3.jsonの名前を一致させておくこと.
    std::string model = ModelDir[index];
    std::string modelPath = ResourcesPath + model + "/";
    std::string modelJsonName = ModelDir[index];
    modelJsonName += ".model3.json";

    ReleaseAllModel();
    _models.PushBack(new LAppModel());
    _models[0]->LoadAssets(modelPath.c_str(), modelJsonName.c_str());

    /*
     * モデル半透明表示を行うサンプルを提示する。
     * ここでUSE_RENDER_TARGET、USE_MODEL_RENDER_TARGETが定義されている場合
     * 別のレンダリングターゲットにモデルを描画し、描画結果をテクスチャとして別のスプライトに張り付ける。
     */
    {
#if defined(USE_RENDER_TARGET)
        // LAppViewの持つターゲットに描画を行う場合、こちらを選択 
        LAppView::SelectTarget useRenderTarget = LAppView::SelectTarget_ViewFrameBuffer;
#elif defined(USE_MODEL_RENDER_TARGET)
        // 各LAppModelの持つターゲットに描画を行う場合、こちらを選択 
        LAppView::SelectTarget useRenderTarget = LAppView::SelectTarget_ModelFrameBuffer;
#else
        // デフォルトのメインフレームバッファへレンダリングする(通常) 
        LAppView::SelectTarget useRenderTarget = LAppView::SelectTarget_None;
#endif

#if defined(USE_RENDER_TARGET) || defined(USE_MODEL_RENDER_TARGET)
        // モデル個別にαを付けるサンプルとして、もう1体モデルを作成し、少し位置をずらす 
        _models.PushBack(new LAppModel());
        _models[1]->LoadAssets(modelPath.c_str(), modelJsonName.c_str());
        _models[1]->GetModelMatrix()->TranslateX(0.2f);
#endif

        LAppDelegate::GetInstance()->GetView()->SwitchRenderingTarget(useRenderTarget);

        // 別レンダリング先を選択した際の背景クリア色 
        float clearColor[3] = { 1.0f, 1.0f, 1.0f };
        LAppDelegate::GetInstance()->GetView()->SetRenderTargetClearColor(clearColor[0], clearColor[1], clearColor[2]);
    }
}

csmUint32 LAppLive2DManager::GetModelNum() const
{
    return _models.GetSize();
}

void LAppLive2DManager::EndFrame()
{
    // モデル解放監視 
    for( int i= _releaseModel.GetSize()-1; i>=0; i--)
    {
        _releaseModel[i]._counter--;

        if (_releaseModel[i]._counter <= 0)
        {// モデル削除 
            if (_releaseModel[i]._model)
            {
                delete _releaseModel[i]._model;
            }
            // コンテナも削除 
            _releaseModel.Remove(i);
            continue;
        }
    }
}

void LAppLive2DManager::ResizedWindow()
{
    const csmUint32 modelCount = _models.GetSize();
    for (csmUint32 i = 0; i < modelCount; ++i)
    {
        _models[i]->GetRenderBuffer().DestroyOffscreenFrame();
    }
}
