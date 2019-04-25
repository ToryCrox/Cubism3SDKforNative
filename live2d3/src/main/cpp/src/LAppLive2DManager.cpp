/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#include "LAppLive2DManager.hpp"
#include <string>
#include <GLES2/gl2.h>
#include <Rendering/CubismRenderer.hpp>
#include "LAppPal.hpp"
#include "LAppDefine.hpp"
#include "LAppDelegate.hpp"
#include "LAppModel.hpp"
#include "LAppView.hpp"
#include "../JniBridgeC.hpp"

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
    : _viewMatrix(NULL),
    _sceneIndex(0)
{
    _changeModel = false;
    initialMatrix();
    //ChangeScene(_sceneIndex);
}

LAppLive2DManager::~LAppLive2DManager()
{
    ReleaseAllModel();
}

void LAppLive2DManager::initialMatrix() {
    // デバイス座標からスクリーン座標に変換するための
    _deviceToScreen = new CubismMatrix44();

    // 画面の表示の拡大縮小や移動の変換を行う行列
    _viewMatrix = new CubismViewMatrix();
    _viewMatrix->LoadIdentity();

    // 表示範囲の設定
    _viewMatrix->SetMaxScale(ViewMaxScale); // 限界拡大率
    _viewMatrix->SetMinScale(ViewMinScale); // 限界縮小率

    // 表示できる最大範囲
    _viewMatrix->SetMaxScreenRect(
            ViewLogicalMaxLeft,
            ViewLogicalMaxRight,
            ViewLogicalMaxBottom,
            ViewLogicalMaxTop
    );
}

void LAppLive2DManager::setUpView(int width, int height) {
    float ratio = static_cast<float>(height) / static_cast<float>(width);
    float left = ViewLogicalLeft;
    float right = ViewLogicalRight;
    float bottom = -ratio;
    float top = ratio;

    _viewMatrix->SetScreenRect(left, right, bottom, top); // デバイスに対応する画面の範囲。 Xの左端, Xの右端, Yの下端, Yの上端
    //_viewMatrix->Scale(1.0f, static_cast<float>(width) / static_cast<float>(height)); // デバイスに対応する画面の範囲。 Xの左端, Xの右端, Yの下端, Yの上端

    float screenW = fabsf(left - right);
    _deviceToScreen->LoadIdentity();
    _deviceToScreen->ScaleRelative(screenW / width, -screenW / width);
    _deviceToScreen->TranslateRelative(-width * 0.5f, -height * 0.5f);
}

float* LAppLive2DManager::getViewMatrixArray() {
    return _viewMatrix->GetArray();
}


void LAppLive2DManager::ReleaseAllModel()
{
    for (csmUint32 i = 0; i < _models.GetSize(); i++)
    {
        delete _models[i];
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
        const csmChar* hitArea = _models[i]->GetHitArea(x, y);
        if (hitArea != NULL){
            if (strcmp(hitArea, HitAreaNameHead) == 0){
                _models[i]->SetRandomExpression();
            } else if (strcmp(hitArea, HitAreaNameBody) == 0){
                _models[i]->StartRandomMotion(MotionGroupTapBody, PriorityNormal);
            }
            JniBridgeC::hitTest(hitArea);
        } else {
            const csmChar* hitAreaId = _models[i]->GetHitAreaId(x, y);
            JniBridgeC::hitTest(hitAreaId);
        }
    }
}

void LAppLive2DManager::startMotion(const csmChar* filePath,
                                    csmFloat32 fadeInSeconds, csmFloat32 fadeOutSeconds){
    csmUint32 modelCount = _models.GetSize();
    for (csmUint32 i = 0; i < modelCount; ++i)
    {
        _models[i]->StartMotion(filePath, fadeInSeconds, fadeOutSeconds);
    }
}

void LAppLive2DManager::OnUpdate()
{
    tryLoadModel();

    int width = LAppDelegate::GetInstance()->GetWindowWidth();
    int height = LAppDelegate::GetInstance()->GetWindowHeight();
    CubismMatrix44 projection;
    projection.Scale(1.0f, static_cast<float>(width) / static_cast<float>(height)); // デバイスに対応する画面の範囲。 Xの左端, Xの右端, Yの下端, Yの上端
    if (_viewMatrix != NULL) {
        projection.MultiplyByMatrix(_viewMatrix);
    }

    const CubismMatrix44    saveProjection = projection;
    csmUint32 modelCount = _models.GetSize();
    for (csmUint32 i = 0; i < modelCount; ++i)
    {
        LAppModel* model = GetModel(i);
        projection = saveProjection;

        // モデル1体描画前コール 
        //LAppDelegate::GetInstance()->GetView()->PreModelDraw(*model);

        model->Update();
        model->Draw(projection);///< 参照渡しなのでprojectionは変質する

        // モデル1体描画後コール 
        //LAppDelegate::GetInstance()->GetView()->PostModelDraw(*model);
    }
}

void LAppLive2DManager::tryLoadModel(){
    if (!_modelPath.empty() && (_changeModel
                                || _models.GetSize() <= 0)){
        _changeModel = false;
        LoadModel(_modelPath);
    }
}

void LAppLive2DManager::LoadModel(const std::string modePath){
    double startTime = LAppPal::GetSystemTime();
    unsigned long i = modePath.find_last_of("/");
    string parentPath = modePath.substr(0, i + 1);
    string modelName = modePath.substr(i+1, modePath.length());

    LAppPal::PrintLog("[APP]LoadModel parent: %s, name: %s", parentPath.c_str(), modelName.c_str());

    ReleaseAllModel();
    LAppPal::PrintLog("[APP]LoadModel after ReleaseAllModel");
    _models.PushBack(new LAppModel());
    LAppPal::PrintLog("[APP]LoadModel after PushBack");
    _models[0]->LoadAssets(parentPath.c_str(), modelName.c_str());
    LAppPal::PrintLog("[APP]LoadModel after LoadAssets");
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

        //LAppDelegate::GetInstance()->GetView()->SwitchRenderingTarget(useRenderTarget);

        // 別レンダリング先を選択した際の背景クリア色
        //float clearColor[3] = { 1.0f, 1.0f, 1.0f };
        //LAppDelegate::GetInstance()->GetView()->SetRenderTargetClearColor(clearColor[0], clearColor[1], clearColor[2]);
    }
    LAppPal::PrintLog("LoadModel has spent %f", (LAppPal::GetSystemTime() - startTime));
}

csmUint32 LAppLive2DManager::GetModelNum() const
{
    return _models.GetSize();
}

void LAppLive2DManager::ReLoadModel(const std::string modelPath, csmFloat32* matrixArr) {
    _modelPath = modelPath;
    _changeModel = !modelPath.empty();
    if (matrixArr != NULL){
        _viewMatrix->SetMatrix(matrixArr);
    }
}
