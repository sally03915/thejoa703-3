import reducer, {
  fetchCommentsRequest, fetchCommentsSuccess, fetchCommentsFailure,
  createCommentRequest, createCommentSuccess, createCommentFailure,
  updateCommentRequest, updateCommentSuccess, updateCommentFailure,
  deleteCommentRequest, deleteCommentSuccess, deleteCommentFailure,
} from  '../commentReducer';

describe('comment reducer' , ()=>{
    const initialState = {   comments: {},   loading: false,   error: null,   };
    // 조회 성공
    it( ('fetchCommentsSuccess') , ()=>{ 
        const comments = [{ id : 1 }];
        const state = reducer( initialState , fetchCommentsSuccess({ postId:1, comments }) ); 
        expect(  state.comments[1]  ).toEqual(comments);
    });
    it( ('fetchCommentsFailure') , ()=>{ 
        const state = reducer( initialState , fetchCommentsFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    });

    // 댓글작성
    it( ('createCommentSuccess') , ()=>{ 
        const comment = [{ id : 2 }];
        const state = reducer( initialState , createCommentSuccess({ postId:1, comment }) ); 
        expect(  state.comments[1][0]  ).toEqual(comment);
    });
    it( ('createCommentFailure') , ()=>{ 
        const state = reducer( initialState , createCommentFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    });

    // 댓글수정
    it( ('updateCommentSuccess') , ()=>{ 
        const prev = {...initialState , comments: { 1 : [{id:1,  content:'old'}]}};
        const updated = {  id: 1 ,  content:'new'};

        const state = reducer( prev , updateCommentSuccess({ postId:1, comment:updated }) ); 
        expect(  state.comments[1][0].content  ).toBe('new');
    });
    it( ('updateCommentFailure') , ()=>{ 
        const state = reducer( initialState , updateCommentFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    });

    // 댓글삭제
    it( ('deleteCommentSuccess') , ()=>{ 
        const prev = {...initialState , comments: { 1 : [{id:1} , {id:2}]}}; 
        const state = reducer( prev , deleteCommentSuccess({ postId:1, commentId:1 }) ); 
        expect(  state.comments[1]  ).toEqual([{id:2}]);
    });
    it( ('deleteCommentFailure') , ()=>{ 
        const state = reducer( initialState , deleteCommentFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    });

});
// npm run test