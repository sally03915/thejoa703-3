// reducers/authReducer.js
import { createSlice } from "@reduxjs/toolkit";  // reducer와 액션을 한번에 생성해주는 역할
const initialState = {
    user: null,
    accessToken: null,
    loading: false,
    error: null,
    success: false,
};

const authSlice = createSlice({
    name: "auth",
    initialState, 
    reducers: {
        // --- 회원가입 ---
        signupRequest : (state)=>{  
            state.loading  = true;
            state.error    = null;
            state.success  = false;
        },
        signupSuccess : (state)=>{  
            state.loading  = false;
            state.success  = true;
        },
        signupFailure : (state, action)=>{ 
            state.loading  = false;
            state.error    = action.payload;
        }, 
        // --- 상태 초기화 액션 추가 ---
        resetAuthState : (state)=>{
            state.loading  = false;
            state.error    = null;
            state.success  = false;
        },
        // --- 로그인 ---
        loginRequest : (state)=>{
            state.loading  = true;
            state.error    = null; 
        },
        loginSuccess : (state, action)=>{
            state.loading     = false;
            state.user        = action.payload.user  || null;
            state.accessToken = action.payload.accessToken || null;
        },
        loginFailure : (state, action)=>{
            state.loading     = false;
            state.error       = action.payload;
            state.user        = null;
            state.accessToken = null;
        },
        // --- 토큰 재발급 ---
        refreshTokenRequest : (state)=>{   
            state.loading  = true;      
        },
        refreshTokenSuccess : (state, action)=>{
            state.loading     = false;
            state.accessToken = action.payload?.accessToken || null;
        },
        refreshTokenFailure : (state, action)=>{
            state.loading     = false;     
            state.error       = action.payload;
        },
        // --- 로그아웃 ---
        logoutRequest  : (state)=>{   
            state.loading  = true;   
        },
        logout : (state)=>{
            state.loading     = false;
            state.error       = null;
            state.user        = null;
            state.accessToken = null;
            state.success     = false;
        },
        logoutFailure :  (state, action)=>{
            state.loading     = false;   
            state.error       = action.payload;
        },
        // --- 닉네임 변경 ---
        updateNicknameRequest : (state)=>{   
            state.loading  = true;      
        },
        updateNicknameSuccess : (state, action)=>{
            state.loading     = false;
            state.user = action.payload.user;
        },
        updateNicknameFailure : (state, action)=>{
            state.loading     = false;   
            state.error       = action.payload;
        },
        // --- 프로필 이미지 변경 ---
        updateProfileImageRequest : (state)=>{   
            state.loading  = true;      
        },
        updateProfileImageSuccess : (state, action)=>{
            state.loading     = false;
            state.user = action.payload.user;
        },
        updateProfileImageFailure : (state, action)=>{
            state.loading     = false;  
            state.error       = action.payload;
        },
    },
});

export const { 
        signupRequest,              signupSuccess,  signupFailure, 
        resetAuthState,  
        loginRequest,               loginSuccess ,  loginFailure , 
        refreshTokenRequest,        refreshTokenSuccess,  refreshTokenFailure, 
        logoutRequest,              logout,  logoutFailure, 
        updateNicknameRequest,      updateNicknameSuccess,   updateNicknameFailure,
        updateProfileImageRequest,  updateProfileImageSuccess,   updateProfileImageFailure,
} = authSlice.actions;

export default authSlice.reducer;
