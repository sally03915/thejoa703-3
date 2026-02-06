import { runSaga } from 'redux-saga'; 
import axios from '../../api/axios';   
 
import {
  fetchCommentsRequest, fetchCommentsSuccess, fetchCommentsFailure,
  createCommentRequest, createCommentSuccess, createCommentFailure,
  updateCommentRequest, updateCommentSuccess, updateCommentFailure,
  deleteCommentRequest, deleteCommentSuccess, deleteCommentFailure,
} from '../../reducers/commentReducer';
 
import { fetchComments, createComment, updateComment, deleteComment } from '../commentSaga';
 
jest.mock('../../api/axios');

describe('commentSaga', () => { 
  // 댓글 조회
  it('fetchCommentsSuccess', async () => {
    axios.get.mockResolvedValue({ data: [{ id: 1, content: 'hi' }] });
    const dispatched = [];
 
    const action = fetchCommentsRequest({ postId: 1 });  // react - view 
    await runSaga({ dispatch: (a) => dispatched.push(a) }, fetchComments, action).toPromise();

    expect(dispatched).toContainEqual(
      fetchCommentsSuccess({ postId: 1, comments: [{ id: 1, content: 'hi' }] })
    );
  });
 
  // 댓글 조회 실패
  it('fetchCommentsFailure', async () => {
    axios.get.mockRejectedValue(new Error('fail'));
    const dispatched = [];

    const action = fetchCommentsRequest({ postId: 1 });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, fetchComments, action).toPromise();

    expect(dispatched).toContainEqual(fetchCommentsFailure('fail'));
  });
 
  // 댓글 생성
  it('createCommentSuccess', async () => {
    axios.post.mockResolvedValue({ data: { id: 2, content: 'new' } });
    const dispatched = [];

    //  postId 포함
    const action = createCommentRequest({ postId: 1, dto: { content: 'new' } });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, createComment, action).toPromise();

    expect(dispatched).toContainEqual(
      createCommentSuccess({ postId: 1, comment: { id: 2, content: 'new' } })
    );
  });
  // 댓글 생성 실패
  it('createCommentFailure', async () => {
    axios.post.mockRejectedValue(new Error('fail'));
    const dispatched = [];

    const action = createCommentRequest({ postId: 1, dto: { content: 'bad' } });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, createComment, action).toPromise();

    expect(dispatched).toContainEqual(createCommentFailure('fail'));
  });
 
  // 댓글 수정
  it('updateCommentSuccess', async () => {
    axios.patch.mockResolvedValue({ data: { id: 1, content: 'updated' } });
    const dispatched = [];

    //  postId 포함
    const action = updateCommentRequest({ postId: 1, commentId: 1, dto: { content: 'updated' } });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, updateComment, action).toPromise();

    expect(dispatched).toContainEqual(
      updateCommentSuccess({ postId: 1, comment: { id: 1, content: 'updated' } })
    );
  });
 
  // 댓글 수정
  it('updateCommentFailure', async () => {
    const dispatched = [];
    axios.patch.mockRejectedValue(new Error('fail'));

    const action = updateCommentRequest({ postId: 1, commentId: 1, dto: { content: 'bad' } });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, updateComment, action).toPromise();

    expect(dispatched).toContainEqual(updateCommentFailure('fail'));
  });
 
  // 댓글삭제
  it('deleteCommentSuccess', async () => {
    axios.delete.mockResolvedValue({});
    const dispatched = [];

    //   postId 포함
    const action = deleteCommentRequest({ postId: 1, commentId: 1 });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, deleteComment, action)
      .toPromise();

    expect(dispatched)
      .toContainEqual(deleteCommentSuccess({ postId: 1, commentId: 1 }));
  });
 
  it('deleteCommentFailure', async () => {
    axios.delete.mockRejectedValue(new Error('fail'));
    const dispatched = [];

    const action = deleteCommentRequest({ postId: 1, commentId: 1 });
    await runSaga({ dispatch: (a) => dispatched.push(a) }, deleteComment, action).toPromise();

    expect(dispatched).toContainEqual(deleteCommentFailure('fail'));
  });
});
