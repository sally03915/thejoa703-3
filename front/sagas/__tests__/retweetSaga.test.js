import { runSaga } from 'redux-saga';
import api from '../../api/axios';
import {
  addRetweetRequest, addRetweetSuccess, addRetweetFailure,
  removeRetweetRequest, removeRetweetSuccess, removeRetweetFailure,
  hasRetweetedRequest, hasRetweetedSuccess, hasRetweetedFailure,
  fetchMyRetweetsRequest, fetchMyRetweetsSuccess, fetchMyRetweetsFailure,
} from '../../reducers/retweetReducer';   
import { addRetweet, removeRetweet, hasRetweeted, fetchMyRetweets } from '../retweetSaga';

jest.mock('../../api/axios'); 

describe('retweetSaga', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  // --- 리트윗 추가 --- addRetweetRequest, addRetweetSuccess, addRetweetFailure,
  it('addRetweetSuccess', async () => {
  api.post.mockResolvedValue({ data: { originalPostId: 1, retweetCount: 5 } });  
  const dispatched = [];

  const action = addRetweetRequest({ postId: 0 });   // react - view
  await runSaga({ dispatch: (a) => dispatched.push(a) }, addRetweet, action).toPromise();

  expect(dispatched).toContainEqual(
    addRetweetSuccess({ postId: 1, retweetCount: 5 })   
  );
  });

  it('addRetweetFailure', async () => {
    api.post.mockRejectedValue(new Error('fail'));   
    const dispatched = [];

    const action = addRetweetRequest({ postId: 0 });    
    await runSaga({ dispatch: (a) => dispatched.push(a) }, addRetweet, action).toPromise();

    expect(dispatched).toContainEqual(addRetweetFailure('fail'));
  });


  // --- 리트윗 삭제 --- removeRetweetRequest, removeRetweetSuccess, removeRetweetFailure,
  it('removeRetweetSuccess', async () => {
    api.delete.mockResolvedValue({ data: { retweetCount: 4 } });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const action = removeRetweetRequest({ postId : 1 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) },removeRetweet, action).toPromise();

    expect(dispatched).toContainEqual(
      removeRetweetSuccess({ postId: 1, retweetCount: 4 })
    );
  });
  
  // --- 리트윗 있는지 여부 --- hasRetweetedRequest, hasRetweetedSuccess, hasRetweetedFailure,
  it('hasRetweetedSuccess', async () => {
    api.get.mockResolvedValue({ data: true });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const action = hasRetweetedRequest({ postId : 1 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) },hasRetweeted, action).toPromise();

    expect(dispatched).toContainEqual(
      hasRetweetedSuccess({ postId: 1, hasRetweeted: true })
    );
  });  
 
  // --- 내 리트윗한 글 목록 --- fetchMyRetweetsRequest, fetchMyRetweetsSuccess, fetchMyRetweetsFailure,
  it('fetchMyRetweetsSuccess', async () => {
    api.get.mockResolvedValue({ data: [1,2] });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const action = fetchMyRetweetsRequest({ userId  : 1 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) },  fetchMyRetweets , action)
           .toPromise();

    expect(dispatched).toContainEqual(
      fetchMyRetweetsSuccess({1: true, 2: true })
    );
  });  

});
