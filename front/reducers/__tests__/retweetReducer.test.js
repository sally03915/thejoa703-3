import reducer, {
  addRetweetRequest, addRetweetSuccess, addRetweetFailure,
  removeRetweetRequest, removeRetweetSuccess, removeRetweetFailure,
  hasRetweetedRequest, hasRetweetedSuccess, hasRetweetedFailure,
  fetchMyRetweetsRequest, fetchMyRetweetsSuccess, fetchMyRetweetsFailure,
} from  '../retweetReducer';

describe('retweetReducer' , ()=>{
   const initialState = { retweets: {},    retweetsCount: {},  loading: false,   error: null, };
    // 리트윗 추가
    it( ('addRetweetSuccess') , ()=>{  
        const state = reducer( initialState , addRetweetSuccess({ postId:1, retweetCount: 5 }) ); 
        expect(  state.retweets[1]  ).toBe(true);
        expect(  state.retweetsCount[1]  ).toBe(5);
        expect(  state.loading  ).toBe(false);
    });
    it( ('addRetweetFailure') , ()=>{ 
        const state = reducer( initialState , addRetweetFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    });

    // 리트윗 삭제
    it( ('removeRetweetSuccess') , ()=>{
        const prev  = { ...initialState , retweets : { 1:true} , retweetsCount:{1:5} };   //기존의 react
        const state = reducer( prev , removeRetweetSuccess({ postId:1, retweetCount: 4 }) ); // 서버에서 응답
        expect(  state.retweets[1]  ).toBe(false);
        expect(  state.retweetsCount[1]  ).toBe(4); 
    });
    
    // 특정게시글에 내가 리트윗했는지  hasRetweetedSuccess
    it( ('hasRetweetedSuccess') , ()=>{ 
        const state = reducer( initialState , hasRetweetedSuccess({ postId:1, hasRetweeted: true }) ); // 서버에서 응답
        expect(  state.retweets[1]  ).toBe(true); 
    });

    // 내가 리트윗한 전체 글목록 요청  fetchMyRetweetsSuccess
    it( ('fetchMyRetweetsSuccess') , ()=>{ 
        const state = reducer( initialState , fetchMyRetweetsSuccess({ 1:true , 2:true }) ); // 서버에서 응답
        expect(  state.retweets[1]  ).toBe(true);
        expect(  state.retweets[2]  ).toBe(true); 
    });
});
// npm run test
