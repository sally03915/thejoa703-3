import reducer, {
  addLikeRequest, addLikeSuccess, addLikeFailure,
  removeLikeRequest, removeLikeSuccess, removeLikeFailure,
  countLikesRequest, countLikesSuccess, countLikesFailure,
  fetchMyLikesRequest, fetchMyLikesSuccess, fetchMyLikesFailure,
} from  '../likeReducer';

describe('likeReducer' , ()=>{
    const initialState = {   likes: {},  likesCount: {},    loading: false,  error: null,  };
    // 좋아요 추가
    it( ('addLikeSuccess') , ()=>{  
        const state = reducer( initialState , addLikeSuccess({ postId:1, count: 5 }) ); 
        expect(  state.likes[1]  ).toBe(true);
        expect(  state.likesCount[1]  ).toBe(5);
    });
    it( ('addLikeFailure') , ()=>{ 
        const state = reducer( initialState , addLikeFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    });
    // 좋아요 취소  
    it( ('removeLikeSuccess') , ()=>{  
        const prev = {   likes: { 1: true},  likesCount: {1 : 5} }
        const state = reducer( prev , removeLikeSuccess({ postId:1, count: 4 }) ); 
        expect(  state.likes[1]  ).toBe(false);
        expect(  state.likesCount[1]  ).toBe(4);
    });
    
    // 좋아요 카운트
    it( ('countLikesSuccess') , ()=>{  
        const state = reducer( initialState , countLikesSuccess({ postId:2, count: 10 }) );  
        expect(  state.likesCount[2]  ).toBe(10);
    });

    
    // 내가 한   좋아요 
    it( ('fetchMyLikesSuccess') , ()=>{  
        const state = reducer( initialState , fetchMyLikesSuccess([1,2,3]) );  
        expect(  state.likes[1]  ).toBe(true);  
        expect(  state.likes[2]  ).toBe(true);  
        expect(  state.likes[3]  ).toBe(true);
    });
});
// npm run test