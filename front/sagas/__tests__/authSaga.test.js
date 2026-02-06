// __tests__/authSaga.test.js 
import { runSaga } from 'redux-saga';
import api from '../../api/axios';  
import {
  signup, login, refresh, logoutFlow, updateNickname, updateProfileImage,
} from '../authSaga';
import {
  signupSuccess, signupFailure,
  loginSuccess, loginFailure,
  refreshTokenSuccess, refreshTokenFailure,
  logout, logoutFailure,
  updateNicknameSuccess, updateNicknameFailure,
  updateProfileImageSuccess, updateProfileImageFailure,
} from '../../reducers/authReducer';

jest.mock('../../api/axios'); 

describe('auth saga', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  // --- 회원가입 ---   
  it('signup', async () => {
    api.post.mockResolvedValue({ data: {} });  // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({dispatch: (a) => dispatched.push(a) }  , signup , { payload: {} }  ).toPromise();
    expect(dispatched).toContainEqual(signupSuccess());
  });

  it('signup failure', async () => {
    api.post.mockRejectedValue({ message: 'fail' });   // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({ dispatch: (a) => dispatched.push(a) }, signup, { payload: {} }).toPromise();
    expect(dispatched).toContainEqual(signupFailure('fail'));
  });

  // --- 로그인 ---  
   it('login', async () => {
    api.post.mockResolvedValue({ data: {  user:{id:1} , accessToken:'A' } });  // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({dispatch: (a) => dispatched.push(a) }  , login , { payload: {} }  ).toPromise();
    expect(dispatched).toContainEqual(  loginSuccess({  user:{id:1} , accessToken:'A' }));
  });

  it('login failure', async () => {
    api.post.mockRejectedValue({ message: 'fail' });   // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({ dispatch: (a) => dispatched.push(a) }, login, { payload: {} }).toPromise();
    expect(dispatched).toContainEqual(loginFailure('fail'));
  });
  
  // --- 토큰 재발급 ---  
  it('refresh', async () => {
    api.post.mockResolvedValue({ data: { accessToken:'A' } });  // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({dispatch: (a) => dispatched.push(a) }  , refresh , { payload: {} }  ).toPromise();
    expect(dispatched).toContainEqual(  refreshTokenSuccess({  accessToken:'A' }));
  });

  it('refresh failure', async () => {
    api.post.mockRejectedValue({ message: 'fail' });   // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({ dispatch: (a) => dispatched.push(a) }, refresh, { payload: {} }).toPromise();
    expect(dispatched).toContainEqual(  refreshTokenFailure('fail'));
  });
  
  // --- 로그아웃 ---  
  it('logout', async () => {
    api.post.mockResolvedValue({ });  // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({dispatch: (a) => dispatched.push(a) }  , logoutFlow , { payload: {} }  ).toPromise();
    expect(dispatched).toContainEqual(   logout()  );
  });

  it('logout failure', async () => {
    api.post.mockRejectedValue({ message: 'fail' });   // 서버에서 받아오는 값
    const dispatched = [];
    await runSaga({ dispatch: (a) => dispatched.push(a) }, logoutFlow, { payload: {} }).toPromise();
    expect(dispatched).toContainEqual(  logoutFailure('fail'));
  });

  
  // --- 닉네임 변경 ---  
  it('updateNickname', async () => {
    api.patch.mockResolvedValue({ data: { id: 1, nickname: 'new' } });
    const dispatched = [];
    await runSaga(  { dispatch: (a) => dispatched.push(a) }
     ,   updateNickname,  { payload: { userId: 1, nickname: 'new' } } ).toPromise();
    expect(dispatched).toContainEqual( updateNicknameSuccess({ user: { id: 1, nickname: 'new' } }) );
  });

  it('updateNickname failure', async () => {
    api.patch.mockRejectedValue({ message: 'fail' });
    const dispatched = [];
    await runSaga(   { dispatch: (a) => dispatched.push(a) }
      ,  updateNickname,   { payload: { userId: 1, nickname: 'new' } }  ).toPromise();
    expect(dispatched).toContainEqual(updateNicknameFailure('fail'));
  });
  
  // --- 프로필 이미지 변경 --- 
  it('updateProfileImage', async () => {
    api.post.mockResolvedValue({ data: { id: 1, ufile: 'file' } }); //응답데이터
    const dispatched = [];
    // saga 에서 직적 실행 → dispatch 
    // 첫 번째 인자 :  dispatch  ,  두 번째 인자 :  실행할 saga , 세 번째 인자 :  saga에 전달할 action 객체
    await runSaga( { dispatch: (a) => dispatched.push(a) }
        ,   updateProfileImage, { payload: { userId: 1, file: new Blob(['test']) } } ).toPromise();
    expect(dispatched).toContainEqual(updateProfileImageSuccess({ user: { id: 1, ufile: 'file' } }));
  });

  it('updateProfileImage failure', async () => {
    api.post.mockRejectedValue({ message: 'fail' });
    const dispatched = [];
    await runSaga( { dispatch: (a) => dispatched.push(a) }
      , updateProfileImage,   { payload: { userId: 1, file: new Blob(['test']) } }
    ).toPromise();
    expect(dispatched).toContainEqual(updateProfileImageFailure('fail'));
  });

}); 
////////////// npm run test



