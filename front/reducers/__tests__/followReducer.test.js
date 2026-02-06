import reducer, {
  followRequest, followSuccess, followFailure,
  unfollowRequest, unfollowSuccess, unfollowFailure,
  loadFollowersRequest, loadFollowersSuccess, loadFollowersFailure,
  loadFollowingsRequest, loadFollowingsSuccess, loadFollowingsFailure, 
  toggleFollowRequest,
} from  '../followReducer';

describe('followReducer' , ()=>{
   const initialState = {  followersMap: {},   followingsMap: {},   followersList: [],  followingsList: [],   
                        loading: false,         error: null,  
   }; 
    // 팔로우 추가
    it( ('followSuccess') , ()=>{  
        const state = reducer(initialState, followSuccess({ followeeId: 1, nickname: "user1" })); 
        expect(state.followingsMap["1"]).toBe(true);  
        expect(state.followingsList[0].followeeId).toBe("1");  
    });
    it( ('followFailure') , ()=>{ 
        const state = reducer( initialState , followFailure('fail') ); 
        expect( state.error  ).toBe('fail');
    }); 
    // 언팔로우
    it( ('unfollowSuccess') , ()=>{  
        const prev  = {...initialState ,followingsMap:{"1":true}  ,  followingsList:[{ followeeId: "1"}] };
        const state = reducer(   prev, unfollowSuccess( 1 )); 
        expect(state.followingsMap["1"]).toBeUndefined();  
        expect(state.followingsList.length).toBe(0);  
    });
    // 팔로워들 loadFollowersSuccess
    it( ('loadFollowersSuccess') , ()=>{   
        const state = reducer(   initialState, loadFollowersSuccess( [{ followerId: 1}] )); 
        expect(state.followersMap["1"]).toBe(true);  
        expect(state.followersList.length).toBe(1);  
    });
    // 팔로윙들 loadFollowingsSuccess
    it( ('loadFollowingsSuccess') , ()=>{   
        const state = reducer(   initialState, loadFollowingsSuccess( [{ followeeId: 1}] )); 
        expect(state.followingsMap["1"]).toBe(true);  
        expect(state.followingsList.length).toBe(1);  
        expect(state.followingsList[0].followeeId).toBe("1");  
    });
    // 토글    toggleFollowRequest
    it( ('toggleFollowRequest') , ()=>{   
        const state = reducer(   initialState, toggleFollowRequest());  
        expect(state.error).toBeNull();  
    }); 
});
// npm run test
