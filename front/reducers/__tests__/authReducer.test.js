// __test__.js/authReducer.test.js

import reducer,{ 
        signupRequest,              signupSuccess,  signupFailure, 
        resetAuthState,  
        loginRequest,               loginSuccess ,  loginFailure , 
        refreshTokenRequest,        refreshTokenSuccess,  refreshTokenFailure, 
        logoutRequest,              logout,  logoutFailure, 
        updateNicknameRequest,      updateNicknameSuccess,   updateNicknameFailure,
        updateProfileImageRequest,  updateProfileImageSuccess,   updateProfileImageFailure,
} from '../../reducers/authReducer';

describe( 'auth reducer' , ()=>{
    const initialState = { user: null, accessToken: null, loading: false,  error: null, success: false, }; 
    // --- 회원가입 ---
    it( ('signupRequest') , ()=>{
        const state = reducer( initialState , signupRequest() );
        expect(state.loading).toBe(true);
        expect(state.success).toBe(false);
        expect(state.error).toBeNull();
    }); 
    it('signupSuccess', () => {
        const state = reducer(initialState, signupSuccess());
        expect(state.loading).toBe(false);
        expect(state.success).toBe(true);
        expect(state.user).toBeNull();
    }); 
    it( ('signupFailure') , ()=>{
        const state = reducer( initialState , signupFailure('fail') );
        expect(state.loading).toBe(false);
        expect(state.success).toBe(false);
        expect(state.error).toBe('fail');
    });
    // --- 상태 초기화 ---
    it( ('resetAuthState') , ()=>{
        const prev = { ...initialState , success: true, error: 'err' , loading: true};
        const state = reducer( prev , resetAuthState() );
        expect(state.loading).toBe(false);
        expect(state.success).toBe(false);
        expect(state.error).toBeNull();
    });
    // --- 로그인 ---
    it( ('loginRequest') , ()=>{
        const state = reducer( initialState , loginRequest() );
        expect(state.loading).toBe(true);
        expect(state.error).toBeNull();
    }); 
    it( ('loginSuccess') , ()=>{
        const payload = { user:{ id:1, email:'1@1'} , accessToken: 'ACCESS' };
        const state = reducer( initialState , loginSuccess(payload) );
        expect(state.user).toEqual(payload.user);
        expect(state.accessToken).toEqual('ACCESS');
        expect(state.loading).toBe(false);
    });

    it( ('loginFailure') , ()=>{ 
        const state = reducer( initialState , loginFailure('fail') );
        expect(state.error).toBe('fail');
        expect(state.user).toBeNull();
        expect(state.loading).toBe(false);
    });

    // --- 토큰재발급 ---
    it( ('refreshTokenRequest') , ()=>{
        const state = reducer( initialState , refreshTokenRequest() );
        expect(state.loading).toBe(true); 
    }); 
    it( ('refreshTokenSuccess') , ()=>{
        const state = reducer( initialState , refreshTokenSuccess({accessToken:'ACCESS'}) );
        expect(state.accessToken).toBe('ACCESS');   expect(state.loading).toBe(false); 
    }); 
    it( ('refreshTokenFailure') , ()=>{
        const state = reducer( initialState , refreshTokenFailure('fail') );
        expect(state.error).toBe('fail'); 
        expect(state.loading).toBe(false); 
    });  
    // --- 로그아웃 ---    
    it( ('logoutRequest') , ()=>{
        const state = reducer( initialState , logoutRequest() );
        expect(state.loading).toBe(true); 
    }); 
    it( ('logout') , ()=>{
        const prev = { ...initialState , user: { id: 1 } , accessToken: 'ACCESS'}
        const state = reducer( prev , logout() );
        expect(state.user).toBeNull(); 
        expect(state.accessToken).toBeNull(); 
        expect(state.error).toBeNull(); 
        expect(state.loading).toBe(false); 
    });
    it( ('logoutFailure') , ()=>{
        const state = reducer( initialState , logoutFailure('fail') );
        expect(state.error).toBe('fail'); 
        expect(state.loading).toBe(false); 
    }); 
    // --- 닉네임 변경 ---
    it( ('updateNicknameRequest') , ()=>{
        const state = reducer( initialState , updateNicknameRequest() );
        expect(state.loading).toBe(true); 
    }); 
    it( ('updateNicknameSuccess') , ()=>{
        const state = reducer( initialState , 
                            updateNicknameSuccess({ user: {id:1, nickname:"new"}}) );
        expect(state.user).toEqual({id:1, nickname:"new"}); 
        expect(state.loading).toBe(false); 
    });
    it( ('updateNicknameFailure') , ()=>{
        const state = reducer( initialState , updateNicknameFailure('fail') );
        expect(state.error).toBe('fail');    expect(state.loading).toBe(false); 
    }); 
    // --- 프로필 이미지 변경 ---
    it( ('updateProfileImageRequest') , ()=>{
        const state = reducer( initialState , updateProfileImageRequest() );
        expect(state.loading).toBe(true); 
    });     
    it( ('updateProfileImageSuccess') , ()=>{
        const state = reducer( initialState , 
                            updateProfileImageSuccess({ user: {id:1, ufile:"1.png"}}) );
        expect(state.user).toEqual({id:1, ufile:"1.png"}); 
        expect(state.loading).toBe(false); 
    });
    it( ('updateProfileImageFailure') , ()=>{
        const state = reducer( initialState , updateProfileImageFailure('fail') );
        expect(state.error).toBe('fail');   expect(state.loading).toBe(false); 
    }); 
});
