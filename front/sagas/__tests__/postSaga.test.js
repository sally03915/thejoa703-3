// __tests__/postSaga.test.js 
import { runSaga } from 'redux-saga';
import api from '../../api/axios';
import {
  fetchPostsRequest, fetchPostsSuccess, fetchPostsFailure,
  fetchPostRequest, fetchPostSuccess, fetchPostFailure,
  fetchPostsPagedRequest, fetchPostsPagedSuccess, fetchPostsPagedFailure,
  fetchLikedPostsRequest, fetchLikedPostsSuccess, fetchLikedPostsFailure,
  fetchMyAndRetweetsRequest, fetchMyAndRetweetsSuccess, fetchMyAndRetweetsFailure,
  createPostRequest, createPostSuccess, createPostFailure,
  updatePostRequest, updatePostSuccess, updatePostFailure,
  deletePostRequest, deletePostSuccess, deletePostFailure,
} from '../../reducers/postReducer';  //치킨집 전역 상태 - 액션
import {
  fetchPosts, fetchPost, fetchPostsPaged, fetchLikedPosts,
  fetchMyAndRetweets,
  createPost, updatePost, deletePost,
} from '../postSaga';  // 글쓰기 사가 - 배달기사

jest.mock('../../api/axios'); 

describe('postSaga', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  // --- 전체조회 ---   
  it('fetchPostsSuccess', async () => {
    api.get.mockResolvedValue({ data: [{id:1}] });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = fetchPostsRequest();   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , fetchPosts , action ).toPromise();

    expect(dispatched).toContainEqual(fetchPostsSuccess( [{id:1}]));
  });

  it('fetchPostsFailure', async () => {
    api.get.mockRejectedValue(new Error('fail'));   // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = fetchPostsRequest();   // react - view 
    await runSaga({ dispatch: (a) => dispatched.push(a) },   fetchPosts , action   ).toPromise();
    expect(dispatched).toContainEqual(fetchPostsFailure('fail'));
  });

  // --- 단건조회 ---   
  it('fetchPostSuccess', async () => {
    api.get.mockResolvedValue({ data: {id:1} });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = fetchPostRequest({ postId: 1 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , fetchPost , action ).toPromise();

    expect(dispatched).toContainEqual(fetchPostSuccess( {id:1}));
  });

  // --- 페이징 조회 ---      fetchPostsPagedRequest, fetchPostsPagedSuccess, fetchPostsPagedFailure,
  it('fetchPostsPagedSuccess', async () => {
    api.get.mockResolvedValue({ data: [{id:10} , {id:11}] });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = fetchPostRequest({ page: 1 , size:2 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , fetchPostsPaged , action )
          .toPromise();

    expect(dispatched).toContainEqual(fetchPostsPagedSuccess( [{id:10} , {id:11}]));
  });

  // --- 좋아요 조회 ---      fetchLikedPostsRequest, fetchLikedPostsSuccess, fetchLikedPostsFailure,
   it('fetchLikedPostsSuccess', async () => {
    api.get.mockResolvedValue({ data: [{id:10} , {id:11}] });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = fetchLikedPostsRequest({ page: 1 , size:2 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , fetchLikedPosts , action )
          .toPromise();

    expect(dispatched).toContainEqual(fetchLikedPostsSuccess( [{id:10} , {id:11}]));
  });
  // --- 내글+리트윗 조회 ---  fetchMyAndRetweetsRequest, fetchMyAndRetweetsSuccess, fetchMyAndRetweetsFailure,
   it('fetchMyAndRetweetsSuccess', async () => {
    api.get.mockResolvedValue({ data: [{id:10} , {id:11}] });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = fetchMyAndRetweetsRequest({ page: 1 , size:2 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , fetchMyAndRetweets , action )
          .toPromise();

    expect(dispatched).toContainEqual(fetchMyAndRetweetsSuccess( [{id:10} , {id:11}]));
  });

  // --- 글 생성 ---         createPostRequest, createPostSuccess, createPostFailure,
   it('createPostSuccess', async () => {
    api.post.mockResolvedValue({ data:  {id:10 , content:'new'}  });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = createPostRequest({ dto: {content:'new'} , files:[] });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , createPost , action ).toPromise();

    expect(dispatched).toContainEqual(createPostSuccess( {id:10 , content:'new'} ));
  });

  // --- 수정  ---           updatePostRequest, updatePostSuccess, updatePostFailure,
   it('updatePostSuccess', async () => {
    api.put.mockResolvedValue({ data:  {id:10 , content:'updated'}  });  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = updatePostRequest({ dto: {content:'updated'} , files:[] });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , updatePost , action ).toPromise();

    expect(dispatched).toContainEqual(updatePostSuccess( {id:10 , content:'updated'} ));
  });
  
  // --- 삭제  ---           deletePostRequest, deletePostSuccess, deletePostFailure, 
   it('deletePostSuccess', async () => {
    api.delete.mockResolvedValue({});  // 서버에서 받아오는 값 - 덤프
    const dispatched = [];

    const  action = deletePostRequest({ postId:1 });   // react - view
    await runSaga({ dispatch: (a) => dispatched.push(a) }  , deletePost , action ).toPromise();

    expect(dispatched).toContainEqual(deletePostSuccess( 1 )); // reducer 에 넘길 값
  });
  

}); 
////////////// npm run test



