import { runSaga } from 'redux-saga';
import axios from '../../api/axios';
 
import {
  followRequest, followSuccess, followFailure,
  unfollowRequest, unfollowSuccess, unfollowFailure,
  loadFollowersRequest, loadFollowersSuccess, loadFollowersFailure,
  loadFollowingsRequest, loadFollowingsSuccess, loadFollowingsFailure,
 // updateBlockRequest, updateBlockSuccess, updateBlockFailure,
  toggleFollowRequest,
} from '../../reducers/followReducer';
 
import { follow, unfollow, loadFollowers, loadFollowings , toggleFollow
    //    , updateBlock 
} from '../followSaga';
 
jest.mock('../../api/axios');

describe('followSaga CRUD', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  // 팔로우 추가
  it('follow success', async () => {
    const dispatched = [];
    axios.post.mockResolvedValue({ data: { followeeId: 2, blocked: false } }); //(2) 서버에서 결과

    const action = followRequest({ followeeId: 2 });  //react-view (1)
    await runSaga({ dispatch: (a) => dispatched.push(a) }, follow, action).toPromise();  //(3)

    expect(dispatched).toContainEqual(followSuccess({ followeeId: "2", blocked: false }));
    expect(dispatched).toContainEqual(loadFollowingsRequest());
  });
 
  it('follow failure', async () => {
    const dispatched = [];
    axios.post.mockRejectedValue(new Error('fail'));

    const action = followRequest({ followeeId: 2 });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, follow, action).toPromise();

    expect(dispatched).toContainEqual(followFailure('fail'));
  });
 
  // 언팔로우 
  it('unfollow success', async () => {
    const dispatched = [];
    axios.delete.mockResolvedValue({ data: { followeeId: 2 } });  //서버에서 결과값

    const action = unfollowRequest({ followeeId: 2 }); // react-view
    await runSaga({ dispatch: (a) => dispatched.push(a) }, unfollow, action).toPromise();

    expect(dispatched).toContainEqual(unfollowSuccess("2"));
    expect(dispatched).toContainEqual(loadFollowingsRequest());
  });
 
  it('unfollow failure', async () => {
    const dispatched = [];
    axios.delete.mockRejectedValue(new Error('fail'));

    const action = unfollowRequest({ followeeId: 2 });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, unfollow, action).toPromise();

    expect(dispatched).toContainEqual(unfollowFailure('fail'));
  });
 
  // 팔로워 목록들 
  it('loadFollowers success', async () => {
    const dispatched = [];
    axios.get.mockResolvedValue({ data: [{ id: 1 }, { id: 2 }] });

    const action = loadFollowersRequest();
    await runSaga({ dispatch: (a) => dispatched.push(a) }, loadFollowers, action).toPromise();

    expect(dispatched).toContainEqual(loadFollowersSuccess([{ id: 1 }, { id: 2 }]));
  });
 
  it('loadFollowers failure', async () => {
    const dispatched = [];
    axios.get.mockRejectedValue(new Error('fail'));

    const action = loadFollowersRequest();
    await runSaga({ dispatch: (a) => dispatched.push(a) }, loadFollowers, action).toPromise();

    expect(dispatched).toContainEqual(loadFollowersFailure('fail'));
  });
 
  // 팔로잉 목록들 
  it('loadFollowings success', async () => {
    const dispatched = [];
    axios.get.mockResolvedValue({ data: [{ id: 3 }, { id: 4 }] });

    const action = loadFollowingsRequest();
    await runSaga({ dispatch: (a) => dispatched.push(a) }, loadFollowings, action).toPromise();

    expect(dispatched).toContainEqual(loadFollowingsSuccess([{ id: 3 }, { id: 4 }]));
  });
 
  it('loadFollowings failure', async () => {
    const dispatched = [];
    axios.get.mockRejectedValue(new Error('fail'));

    const action = loadFollowingsRequest();
    await runSaga({ dispatch: (a) => dispatched.push(a) }, loadFollowings, action).toPromise();

    expect(dispatched).toContainEqual(loadFollowingsFailure('fail'));
  });
 
//   it('updateBlock success', async () => {
//     const dispatched = [];
//     axios.patch.mockResolvedValue({ data: { blockerId: 1, targetUserId: 2, blocked: true } });

//     const action = updateBlockRequest({ followeeId: 2, blocked: true });
//     await runSaga({ dispatch: (a) => dispatched.push(a) }, updateBlock, action).toPromise();

//     expect(dispatched).toContainEqual(updateBlockSuccess({
//       followerId: "1",
//       followeeId: "2",
//       blocked: true,
//     }));
//     expect(dispatched).toContainEqual(loadFollowersRequest());
//     expect(dispatched).toContainEqual(loadFollowingsRequest());
//   });
 
//   it('updateBlock failure', async () => {
//     const dispatched = [];
//     axios.patch.mockRejectedValue(new Error('fail'));

//     const action = updateBlockRequest({ followeeId: 2, blocked: true });
//     await runSaga({ dispatch: (a) => dispatched.push(a) }, updateBlock, action).toPromise();

//     expect(dispatched).toContainEqual(updateBlockFailure('fail'));
//   });
 
  it('toggleFollow unfollow path', async () => {
    const dispatched = [];
    axios.delete.mockResolvedValue({ data: {} });

    const action = toggleFollowRequest(2);
    const fakeState = { follow: { followingsMap: { "2": true } } };

    await runSaga(
      { dispatch: (a) => dispatched.push(a), getState: () => fakeState },
      toggleFollow,
      action
    ).toPromise();

    expect(dispatched).toContainEqual(unfollowSuccess("2"));
  });
 
  it('toggleFollow follow path', async () => {
    const dispatched = [];
    axios.post.mockResolvedValue({ data: {} });

    const action = toggleFollowRequest(2);
    const fakeState = { follow: { followingsMap: {} } };

    await runSaga(
      { dispatch: (a) => dispatched.push(a), getState: () => fakeState },
      toggleFollow,
      action
    ).toPromise();

    expect(dispatched).toContainEqual(followSuccess({ followeeId: "2", blocked: false }));
  });
});
