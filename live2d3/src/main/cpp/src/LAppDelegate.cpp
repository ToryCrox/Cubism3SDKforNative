/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#include "LAppDelegate.hpp"
#include <iostream>
#include <GLES2/gl2.h>
#include <CubismFramework.hpp>
#include "LAppView.hpp"
#include "LAppPal.hpp"
#include "LAppDefine.hpp"
#include "LAppLive2DManager.hpp"
#include "LAppTextureManager.hpp"
#include "../JniBridgeC.hpp"

using namespace Csm;
using namespace std;
using namespace LAppDefine;

namespace {
    LAppDelegate* s_instance = NULL;
    int MAX_ID = 0;
}

LAppDelegate* LAppDelegate::GetInstance()
{
    if (s_instance == NULL)
    {
        s_instance = new LAppDelegate();
    }

    return s_instance;
}

void LAppDelegate::ReleaseInstance()
{
    LAppPal::PrintLog("ReleaseInstance id=%d", _current_id);
    if (s_instance != NULL)
    {
        delete s_instance;
    }

    s_instance = NULL;
}

LAppDelegate::LAppDelegate():
        _cubismOption(),
        _captured(false),
        //_mouseX(0.0f),
        //_mouseY(0.0f),
        _isActive(true),
        //_textureManager(NULL),
        _view(NULL)
{
    // Setup Cubism
    _cubismOption.LogFunction = LAppPal::PrintMessage;
    _cubismOption.LoggingLevel = LAppDefine::CubismLoggingLevel;
    CubismFramework::CleanUp();
    CubismFramework::StartUp(&_cubismAllocator, &_cubismOption);

    //Initialize cubism
    CubismFramework::Initialize();
    _current_id = MAX_ID;
    MAX_ID ++;
}

LAppDelegate::~LAppDelegate()
{
    LAppPal::PrintMessage("LAppDelegate 释放内存");
    // リソースを解放
    LAppLive2DManager::ReleaseInstance();
    CubismFramework::Dispose();
}

void LAppDelegate::OnStart()
{
    _view = new LAppView();
    LAppPal::UpdateTime();
}

void LAppDelegate::OnPause()
{

}

void LAppDelegate::OnStop()
{
    if (_view) {
        delete _view;
        _view = NULL;
    }
}

void LAppDelegate::OnDestroy()
{
    ReleaseInstance();
}

void LAppDelegate::Run()
{
    // 時間更新
    LAppPal::UpdateTime();

    // 画面の初期化
    //设置透明
    //glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClearDepthf(1.0f);

    // Cubism更新・描画
    LAppLive2DManager::GetInstance()->OnUpdate();
}

void LAppDelegate::OnSurfaceCreate()
{
    //テクスチャサンプリング設定
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    //透過設定
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glClearColor(0.0f,0.0f,0.0f,0.0f);

    _view->InitializeShader();
    //LAppLive2DManager::GetInstance()->ReLoadModel(_modelPath);
}

void LAppDelegate::OnSurfaceChanged(int width, int height)
{
    glViewport(0, 0, width, height);
    _width = width;
    _height = height;

    //AppViewの初期化
    //_view->Initialize();
    //_view->InitializeSprite();
    LAppPal::PrintLog("LApp.OnSurfaceChanged LApp.id=%d", _current_id);

    LAppLive2DManager::GetInstance()->setUpView(width, height);
    LAppLive2DManager::GetInstance()->tryLoadModel();

    _isActive = true;
}


void LAppDelegate::OnTouchBegan(float x, float y)
{

    if (_view != NULL) {
        _captured = true;
        _view->OnTouchesBegan(x, y);
    }
}

void LAppDelegate::OnTouchEnded(float x, float y)
{

    if (_view != NULL) {
        _captured = false;
        _view->OnTouchesEnded(x, y);
    }
}

void LAppDelegate::OnTouchMoved(float x, float y)
{

    if (_captured && _view != NULL) {
        _view->OnTouchesMoved(x, y);
    }
}

void LAppDelegate::OnTouchBegan(float x1, float y1, float x2, float y2) {
    if (_view != NULL){
        _captured = true;
        _view->OnTouchesBegan(x1, y1, x2, y2);
    }
}

void LAppDelegate::OnTouchMoved(float x1, float y1, float x2, float y2) {
    if (_captured && _view != NULL){
        _view->OnTouchesMoved(x1, y1, x2, y2);
    }
}

GLuint LAppDelegate::CreateShader()
{
    //バーテックスシェーダのコンパイル
    GLuint vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
    const char* vertexShader =
        "attribute vec3 position;"
        "attribute vec2 uv;"
        "varying vec2 vuv;"
        "void main(void){"
        "    gl_Position = vec4(position, 1.0);"
        "    vuv = uv;"
        "}";
    glShaderSource(vertexShaderId, 1, &vertexShader, NULL);
    glCompileShader(vertexShaderId);

    //フラグメントシェーダのコンパイル
    GLuint fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
    const char* fragmentShader =
        "precision mediump float;"
        "varying vec2 vuv;"
        "uniform sampler2D texture;"
        "uniform vec4 baseColor;"
        "void main(void){"
        "    gl_FragColor = texture2D(texture, vuv) * baseColor;"
        "}";
    glShaderSource(fragmentShaderId, 1, &fragmentShader, NULL);
    glCompileShader(fragmentShaderId);

    //プログラムオブジェクトの作成
    GLuint programId = glCreateProgram();
    glAttachShader(programId, vertexShaderId);
    glAttachShader(programId, fragmentShaderId);

    // リンク
    glLinkProgram(programId);

    glUseProgram(programId);

    return programId;
}


void LAppDelegate::LoadModel(const std::string modelPath, csmFloat32* matrixArr) {
    _modelPath = modelPath;
    LAppLive2DManager::GetInstance()->ReLoadModel(_modelPath, matrixArr);
}
